package net.zhuoweizhang.raspberryjuice;

import java.io.*;
import java.net.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.event.block.BlockBreakEvent;

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

	protected ArrayDeque<BlockBreakEvent> blockBreakQueue = new ArrayDeque<BlockBreakEvent>();

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

	public void queueBlockBreakEvent(BlockBreakEvent event) {
		blockBreakQueue.add(event);
	}


	/** called from the server main thread */
	public void tick() {
		if (origin == null) this.origin = plugin.getServer().getWorlds().get(0).getSpawnLocation();
		String message;
		while ((message = inQueue.poll()) != null) {
			handleLine(message);
		}
	}

	protected void handleLine(String line) {
		//System.out.println(line);
		String methodName = line.substring(0, line.indexOf("("));
		String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
		System.out.println(methodName + ":" + Arrays.toString(args));
		handleCommand(methodName, args);
	}

	protected void handleCommand(String c, String[] args) {
		World world = origin.getWorld();
		Server server = plugin.getServer();
		if (c.equals("world.getBlock")) {
			Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
			System.out.println(loc);
			send(world.getBlockTypeIdAt(loc));
		} else if (c.equals("world.setBlock")) {
			Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
			System.out.println(loc);
			world.getBlockAt(loc).setTypeIdAndData(Integer.parseInt(args[3]), 
				(args.length > 4? Byte.parseByte(args[5]) : (byte) 0), true);
		} else if (c.equals("world.setBlocks")) {
			Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
			Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
			int blockType = Integer.parseInt(args[6]);
			byte data = args.length > 7? Byte.parseByte(args[7]) : (byte) 0;
			setCuboid(loc1, loc2, blockType, data);
		} else {
			System.err.println(c + " has not been implemented.");
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

	public Location parseRelativeBlockLocation(String xstr, String ystr, String zstr) {
		int x = Integer.parseInt(xstr);
		int y = Integer.parseInt(ystr);
		int z = Integer.parseInt(zstr);
		return new Location(origin.getWorld(), origin.getBlockX() + x, origin.getBlockY() + y, origin.getBlockZ() + z);
	}

	public Location parseRelativeLocation(String xstr, String ystr, String zstr) {
		double x = Double.parseDouble(xstr);
		double y = Double.parseDouble(ystr);
		double z = Double.parseDouble(zstr);
		return new Location(origin.getWorld(), origin.getX() + x, origin.getY() + y, origin.getZ() + z);
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
			pendingRemoval = true;
		}
	}

	private class OutputThread implements Runnable {
		public void run() {
			System.out.println("Starting output thread!");
			while (running) {
				try {
					String line;
					while((line = outQueue.poll()) != null) {
						out.write(outQueue.poll());
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
			pendingRemoval = true;
		}
	}
	
}
