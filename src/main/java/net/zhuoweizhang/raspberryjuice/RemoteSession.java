package net.zhuoweizhang.raspberryjuice;

import java.io.*;
import java.net.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class RemoteSession {

	private Location origin;

	private Socket socket;

	private BufferedReader in;

	private BufferedWriter out;

	private ArrayDeque<String> inQueue = new ArrayDeque<String>();

	private ArrayDeque<String> outQueue = new ArrayDeque<String>();

	public boolean running = true;

	public boolean pendingRemoval = false;

	public RaspberryJuicePlugin plugin;

	protected ArrayDeque<PlayerInteractEvent> interactEventQueue = new ArrayDeque<PlayerInteractEvent>();

	private int maxCommandsPerTick = 9000;

	private boolean closed = false;

	private Player attachedPlayer = null;

	public RemoteSession(RaspberryJuicePlugin plugin, Socket socket) throws IOException {
		this.socket = socket;
		this.plugin = plugin;
		init();
	}

	public void init() throws IOException {
		socket.setTcpNoDelay(true);
		socket.setKeepAlive(true);
		socket.setTrafficClass(0x10);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		startThreads();
		plugin.getLogger().info("Opened connection to" + socket.getRemoteSocketAddress() + ".");
	}

	protected void startThreads() {
		new Thread(new InputThread()).start();
		new Thread(new OutputThread()).start();
	}


	public Location getOrigin() {
		return origin;
	}

	public void setOrigin(Location origin) {
		this.origin = origin;
	}

	public Socket getSocket() {
		return socket;
	}

	public void queuePlayerInteractEvent(PlayerInteractEvent event) {
		plugin.getLogger().info(event.toString());
		interactEventQueue.add(event);
	}


	/** called from the server main thread */
	public void tick() {
		if (origin == null) this.origin = plugin.getServer().getWorlds().get(0).getSpawnLocation();
		int processedCount = 0;
		String message;
		while ((message = inQueue.poll()) != null) {
			handleLine(message);
			processedCount++;
			if (processedCount >= maxCommandsPerTick) {
				plugin.getLogger().warning("Over " + maxCommandsPerTick + 
					" commands were queued - deferring " + inQueue.size() + " to next tick");
				break;
			}
		}

		if (!running && inQueue.size() <= 0) {
			pendingRemoval = true;
		}
	}

	protected void handleLine(String line) {
		//System.out.println(line);
		String methodName = line.substring(0, line.indexOf("("));
		String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
		//System.out.println(methodName + ":" + Arrays.toString(args));
		handleCommand(methodName, args);
	}

	protected void handleCommand(String c, String[] args) {
		World world = origin.getWorld();
		Server server = plugin.getServer();
		if (c.equals("world.getBlock")) {
			Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
			//System.out.println(loc);
			send(world.getBlockTypeIdAt(loc));
		} else if (c.equals("world.getBlocks")) {
			Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
			Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
			send(getBlocks(loc1, loc2));
		} else if (c.equals("world.getBlockWithData")) {
			Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
			send(world.getBlockTypeIdAt(loc) + "," + world.getBlockAt(loc).getData());
		} else if (c.equals("world.setBlock")) {
			Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
			//System.out.println(loc);
			world.getBlockAt(loc).setTypeIdAndData(Integer.parseInt(args[3]), 
				(args.length > 4? Byte.parseByte(args[4]) : (byte) 0), true);
		} else if (c.equals("world.setBlocks")) {
			Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
			Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
			int blockType = Integer.parseInt(args[6]);
			byte data = args.length > 7? Byte.parseByte(args[7]) : (byte) 0;
			setCuboid(loc1, loc2, blockType, data);
		} else if (c.equals("world.getPlayerIds")) {
			StringBuilder bdr = new StringBuilder();
			for (Player p: server.getOnlinePlayers()) {
				bdr.append(p.getEntityId());
				bdr.append("|");
			}
			send(bdr.toString());
		} else if (c.equals("chat.post")) {
			server.broadcastMessage(args[0]);
		} else if (c.equals("events.clear")) {
			interactEventQueue.clear();
		} else if (c.equals("events.block.hits")) {
			StringBuilder b = new StringBuilder();
			PlayerInteractEvent event;
			while ((event = interactEventQueue.poll()) != null) {
				Block block = event.getClickedBlock();
				Location loc = block.getLocation();
				b.append(blockLocationToRelative(loc));
				b.append(",");
				b.append(blockFaceToNotch(event.getBlockFace()));
				b.append(",");
				b.append(event.getPlayer().getEntityId());
				if (interactEventQueue.size() > 0) {
					b.append("|");
				}
			}
			System.out.println(b.toString());
			send(b.toString());
		} else if (c.equals("player.getTile")) {
			Player currentPlayer = getCurrentPlayer();
			send(blockLocationToRelative(currentPlayer.getLocation()));
		} else if (c.equals("player.setTile")) {
			Player currentPlayer = getCurrentPlayer();
			currentPlayer.teleport(parseRelativeBlockLocation(args[0], args[1], args[2]));
		} else if (c.equals("player.getPos")) {
			Player currentPlayer = getCurrentPlayer();
			send(locationToRelative(currentPlayer.getLocation()));
		} else if (c.equals("player.setPos")) {
			Player currentPlayer = getCurrentPlayer();
			currentPlayer.teleport(parseRelativeLocation(args[0], args[1], args[2]));
		} /*else if (c.equals("entity.getTile")) {
			Entity entity = world.getEntities()
			send(blockLocationToRelative(entity.getLocation()));
		} else if (c.equals("entity.setTile")) {
			entity.setLocation(parseRelativeBlockLocation(args[0], args[1], args[2]);
		} else if (c.equals("entity.getPos")) {

			send(locationToRelative(entity.getLocation()));
		} else if (c.equals("entity.setPos")) {

			entity.setLocation(parseRelativeLocation(args[0], args[1], args[2]);
		}*/ else {
			System.err.println(c + " has not been implemented.");
			send("Fail");
		}
	}

	private void setCuboid(Location pos1, Location pos2, int blockType, byte data) {
		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();

		for (int x = minX; x <= maxX; ++x) {
			for (int z = minZ; z <= maxZ; ++z) {
				for (int y = minY; y <= maxY; ++y) {
					world.getBlockAt(x, y, z).setTypeIdAndData(blockType, data, true);
				}
			}
		}
	}

	private String getBlocks(Location pos1, Location pos2) {
		StringBuilder blockData = new StringBuilder();

		int minX, maxX, minY, maxY, minZ, maxZ;
		World world = pos1.getWorld();
		minX = pos1.getBlockX() < pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		maxX = pos1.getBlockX() >= pos2.getBlockX() ? pos1.getBlockX() : pos2.getBlockX();
		minY = pos1.getBlockY() < pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		maxY = pos1.getBlockY() >= pos2.getBlockY() ? pos1.getBlockY() : pos2.getBlockY();
		minZ = pos1.getBlockZ() < pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();
		maxZ = pos1.getBlockZ() >= pos2.getBlockZ() ? pos1.getBlockZ() : pos2.getBlockZ();

		for (int y = minY; y <= maxY; ++y) {
			for (int x = minX; x <= maxX; ++x) {
				for (int z = minZ; z <= maxZ; ++z) {
					blockData.append(new Integer(world.getBlockTypeIdAt(x, y, z)).toString() + ",");
				}
			}
		}

		return blockData.substring(0, blockData.length() > 0 ? blockData.length() - 1 : 0);	// We don't want last comma
	}

	public Player getCurrentPlayer() {
		if (attachedPlayer != null) return attachedPlayer;
		Player retval = plugin.getHostPlayer();
		if (retval != null) return retval;
		return null;
	}

	public Location parseRelativeBlockLocation(String xstr, String ystr, String zstr) {
		int x = (int) Double.parseDouble(xstr);
		int y = (int) Double.parseDouble(ystr);
		int z = (int) Double.parseDouble(zstr);
		return new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);
	}

	public Location parseRelativeLocation(String xstr, String ystr, String zstr) {
		double x = Double.parseDouble(xstr);
		double y = Double.parseDouble(ystr);
		double z = Double.parseDouble(zstr);
		return new Location(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() + z);
	}

	public String blockLocationToRelative(Location loc) {
		return (loc.getBlockX() - origin.getBlockX()) + "," + (loc.getBlockY() - origin.getBlockY()) + "," + 
			(loc.getBlockZ() - origin.getBlockZ());
	}

	public String locationToRelative(Location loc) {
		return (loc.getX() - origin.getX()) + "," + (loc.getY() - origin.getY()) + "," + 
			(loc.getZ() - origin.getZ());
	}

	public void send(Object a) {
		send(a.toString());
	}

	public void send(String a) {
		if (pendingRemoval) return;
		synchronized(outQueue) {
			outQueue.add(a);
		}
	}

	public void close() {
		if (closed) return;
		running = false;
		pendingRemoval = true;

		try {
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		plugin.getLogger().info("Closed connection to" + socket.getRemoteSocketAddress() + ".");
	}

	public void kick(String reason) {
		try {
			out.write(reason);
			out.flush();
		} catch (Exception e) {
		}
		close();
	}

	/** socket listening thread */
	private class InputThread implements Runnable {
		public void run() {
			System.out.println("Starting input thread!");
			while (running) {
				try {
					String newLine = in.readLine();
					//System.out.println(newLine);
					if (newLine == null) {
						running = false;
					} else {
						inQueue.add(newLine);
						//System.out.println("Added to in queue");
					}
				} catch (Exception e) {
					e.printStackTrace();
					running = false;
				}
			}
		}
	}

	private class OutputThread implements Runnable {
		public void run() {
			System.out.println("Starting output thread!");
			while (running) {
				try {
					String line;
					while((line = outQueue.poll()) != null) {
						out.write(line);
						out.write('\n');
					}
					out.flush();
					Thread.yield();
					Thread.sleep(1L);
				} catch (Exception e) {
					e.printStackTrace();
					running = false;
				}
			}
		}
	}

	/** from CraftBukkit's org.bukkit.craftbukkit.block.CraftBlock.blockFactToNotch */
	public static int blockFaceToNotch(BlockFace face) {
		switch (face) {
		case DOWN:
			return 0;
		case UP:
			return 1;
		case NORTH:
			return 2;
		case SOUTH:
			return 3;
		case WEST:
			return 4;
		case EAST:
			return 5;
		default:
			return 7; // Good as anything here, but technically invalid
		}
	}



}
