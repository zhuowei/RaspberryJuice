package net.zhuoweizhang.raspberryjuice;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.Vector;

public class RemoteSession {

	private final LocationType locationType;

	private Location origin;

	private Socket socket;

	private BufferedReader in;

	private BufferedWriter out;
	
	private Thread inThread;
	
	private Thread outThread;

	private ArrayDeque<String> inQueue = new ArrayDeque<String>();

	private ArrayDeque<String> outQueue = new ArrayDeque<String>();

	public boolean running = true;

	public boolean pendingRemoval = false;

	public RaspberryJuicePlugin plugin;

	protected ArrayDeque<PlayerInteractEvent> interactEventQueue = new ArrayDeque<PlayerInteractEvent>();
	
	protected ArrayDeque<AsyncPlayerChatEvent> chatPostedQueue = new ArrayDeque<AsyncPlayerChatEvent>();

	private int maxCommandsPerTick = 9000;

	private boolean closed = false;

	private Player attachedPlayer = null;

	public RemoteSession(RaspberryJuicePlugin plugin, Socket socket) throws IOException {
		this.socket = socket;
		this.plugin = plugin;
		this.locationType = plugin.getLocationType();
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
		inThread = new Thread(new InputThread());
		inThread.start();
		outThread = new Thread(new OutputThread());
		outThread.start();
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
		//plugin.getLogger().info(event.toString());
		interactEventQueue.add(event);
	}

	public void queueChatPostedEvent(AsyncPlayerChatEvent event) {
		//plugin.getLogger().info(event.toString());
		chatPostedQueue.add(event);
	}

	/** called from the server main thread */
	public void tick() {
		if (origin == null) {
			switch (locationType) {
				case ABSOLUTE:
					this.origin = new Location(plugin.getServer().getWorlds().get(0), 0, 0, 0);
					break;
				case RELATIVE:
					this.origin = plugin.getServer().getWorlds().get(0).getSpawnLocation();
					break;
				default:
					throw new IllegalArgumentException("Unknown location type " + locationType);
			}
		}
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
		//split string into args, handles , inside " i.e. ","
		String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
		//System.out.println(methodName + ":" + Arrays.toString(args));
		handleCommand(methodName, args);
	}

	protected void handleCommand(String c, String[] args) {
		
		try {
			// get the server
			Server server = plugin.getServer();
			
			// get the world
			World world = origin.getWorld();
			
			// world.getBlock
			if (c.equals("world.getBlock")) {
				Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
				send(world.getBlockTypeIdAt(loc));
				
			// world.getBlocks
			} else if (c.equals("world.getBlocks")) {
				Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
				Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
				send(getBlocks(loc1, loc2));
				
			// world.getBlockWithData
			} else if (c.equals("world.getBlockWithData")) {
				Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
				send(world.getBlockTypeIdAt(loc) + "," + world.getBlockAt(loc).getData());
				
			// world.setBlock
			} else if (c.equals("world.setBlock")) {
				Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
				updateBlock(world, loc, Integer.parseInt(args[3]), (args.length > 4? Byte.parseByte(args[4]) : (byte) 0));
				
			// world.setBlocks
			} else if (c.equals("world.setBlocks")) {
				Location loc1 = parseRelativeBlockLocation(args[0], args[1], args[2]);
				Location loc2 = parseRelativeBlockLocation(args[3], args[4], args[5]);
				int blockType = Integer.parseInt(args[6]);
				byte data = args.length > 7? Byte.parseByte(args[7]) : (byte) 0;
				setCuboid(loc1, loc2, blockType, data);
				
			// world.getPlayerIds
			} else if (c.equals("world.getPlayerIds")) {
				StringBuilder bdr = new StringBuilder();
				Collection<? extends Player> players = Bukkit.getOnlinePlayers();
				if (players.size() > 0) {
					for (Player p: players) {
						bdr.append(p.getEntityId());
						bdr.append("|");
					}
					bdr.deleteCharAt(bdr.length()-1);
					send(bdr.toString());
				} else {
					send("Fail");
				}
				
			// world.getPlayerId
			} else if (c.equals("world.getPlayerId")) {
				Player p = plugin.getNamedPlayer(args[0]);
				if (p != null) {
					send(p.getEntityId());
				} else {
					plugin.getLogger().info("Player [" + args[0] + "] not found.");
					send("Fail");
				}
			// entity.getListName
			} else if (c.equals("entity.getName")) {
				Entity e = plugin.getEntity(Integer.parseInt(args[0]));
				if (e == null) {
					plugin.getLogger().info("Player (or Entity) [" + args[0] + "] not found in entity.getName.");
				} else if (e instanceof Player) {
					Player p = (Player) e;
					//sending list name because plugin.getNamedPlayer() uses list name
					send(p.getPlayerListName());
				} else if (e != null) {
					send(e.getName());
				}
				
			// chat.post
			} else if (c.equals("chat.post")) {
				//create chat message from args as it was split by ,
				String chatMessage = "";
				int count;
				for(count=0;count<args.length;count++){
					chatMessage = chatMessage + args[count] + ",";
				}
				chatMessage = chatMessage.substring(0, chatMessage.length() - 1);
				server.broadcastMessage(chatMessage);
				
			// events.clear
			} else if (c.equals("events.clear")) {
				interactEventQueue.clear();
				chatPostedQueue.clear();
				
			// events.block.hits
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
				send(b.toString());
			
			// events.chat.posts
			} else if (c.equals("events.chat.posts")) {
				StringBuilder b = new StringBuilder();
				AsyncPlayerChatEvent event;
				while ((event = chatPostedQueue.poll()) != null) {
					b.append(event.getPlayer().getEntityId());
					b.append(",");
					b.append(event.getMessage());
					if (chatPostedQueue.size() > 0) {
						b.append("|");
					}
				}
				send(b.toString());
				
			// player.getTile
			} else if (c.equals("player.getTile")) {
				Player currentPlayer = getCurrentPlayer();
				send(blockLocationToRelative(currentPlayer.getLocation()));
				
			// player.setTile
			} else if (c.equals("player.setTile")) {
				String x = args[0], y = args[1], z = args[2];
				Player currentPlayer = getCurrentPlayer();
				//get players current location, so when they are moved we will use the same pitch and yaw (rotation)
				Location loc = currentPlayer.getLocation();
				currentPlayer.teleport(parseRelativeBlockLocation(x, y, z, loc.getPitch(), loc.getYaw()));
				
			// player.getAbsPos
			} else if (c.equals("player.getAbsPos")) {
				Player currentPlayer = getCurrentPlayer();
				send(currentPlayer.getLocation());
				
			// player.setAbsPos
			} else if (c.equals("player.setAbsPos")) {
				String x = args[0], y = args[1], z = args[2];
				Player currentPlayer = getCurrentPlayer();
				//get players current location, so when they are moved we will use the same pitch and yaw (rotation)
				Location loc = currentPlayer.getLocation();
				loc.setX(Double.parseDouble(x));
				loc.setY(Double.parseDouble(y));
				loc.setZ(Double.parseDouble(z));
				currentPlayer.teleport(loc);

			// player.getPos
			} else if (c.equals("player.getPos")) {
				Player currentPlayer = getCurrentPlayer();
				send(locationToRelative(currentPlayer.getLocation()));

			// player.setPos
			} else if (c.equals("player.setPos")) {
				String x = args[0], y = args[1], z = args[2];
				Player currentPlayer = getCurrentPlayer();
				//get players current location, so when they are moved we will use the same pitch and yaw (rotation)
				Location loc = currentPlayer.getLocation();
				currentPlayer.teleport(parseRelativeLocation(x, y, z, loc.getPitch(), loc.getYaw()));

			// player.setDirection
			} else if (c.equals("player.setDirection")) {
				Double x = Double.parseDouble(args[0]);
				Double y = Double.parseDouble(args[1]); 
				Double z = Double.parseDouble(args[2]);
				Player currentPlayer = getCurrentPlayer();
				Location loc = currentPlayer.getLocation();
				loc.setDirection(new Vector(x, y, z));
				currentPlayer.teleport(loc);

			// player.getDirection
			} else if (c.equals("player.getDirection")) {
			Player currentPlayer = getCurrentPlayer();
			send(currentPlayer.getLocation().getDirection().toString());

			// player.setRotation
			} else if (c.equals("player.setRotation")) {
				Float yaw = Float.parseFloat(args[0]);
				Player currentPlayer = getCurrentPlayer();
				Location loc = currentPlayer.getLocation();
				loc.setYaw(yaw);
				currentPlayer.teleport(loc);

			// player.getRotation
			} else if (c.equals("player.getRotation")) {
				Player currentPlayer = getCurrentPlayer();
				float yaw = currentPlayer.getLocation().getYaw();
				// turn bukkit's 0 - -360 to positive numbers 
				if (yaw < 0) yaw = yaw * -1;
				send(yaw);

			// player.setPitch
			} else if (c.equals("player.setPitch")) {
				Float pitch = Float.parseFloat(args[0]);
				Player currentPlayer = getCurrentPlayer();
				Location loc = currentPlayer.getLocation();
				loc.setPitch(pitch);
				currentPlayer.teleport(loc);
				
			// player.getPitch
			} else if (c.equals("player.getPitch")) {
				Player currentPlayer = getCurrentPlayer();
				send(currentPlayer.getLocation().getPitch());
				
				// world.getHeight
			} else if (c.equals("world.getHeight")) {
				send(world.getHighestBlockYAt(parseRelativeBlockLocation(args[0], "0", args[1])) - origin.getBlockY());
				
			// entity.getTile
			} else if (c.equals("entity.getTile")) {
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					send(blockLocationToRelative(entity.getLocation()));
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}
				
			// entity.setTile
			} else if (c.equals("entity.setTile")) {
				String x = args[1], y = args[2], z = args[3];
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					//get entity's current location, so when they are moved we will use the same pitch and yaw (rotation)
					Location loc = entity.getLocation();
					entity.teleport(parseRelativeBlockLocation(x, y, z, loc.getPitch(), loc.getYaw()));
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}

			// entity.getPos
			} else if (c.equals("entity.getPos")) {
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				//Player entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					send(locationToRelative(entity.getLocation()));
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}
			
			// entity.setPos
			} else if (c.equals("entity.setPos")) {
				String x = args[1], y = args[2], z = args[3];
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					//get entity's current location, so when they are moved we will use the same pitch and yaw (rotation)
					Location loc = entity.getLocation();
					entity.teleport(parseRelativeLocation(x, y, z, loc.getPitch(), loc.getYaw()));
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}

			// entity.setDirection
			} else if (c.equals("entity.setDirection")) {
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					Double x = Double.parseDouble(args[1]);
					Double y = Double.parseDouble(args[2]); 
					Double z = Double.parseDouble(args[3]);
					Location loc = entity.getLocation();
					loc.setDirection(new Vector(x, y, z));
					entity.teleport(loc);
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
				}
				
			// entity.getDirection
			} else if (c.equals("entity.getDirection")) {
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					send(entity.getLocation().getDirection().toString());
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}

			// entity.setRotation
			} else if (c.equals("entity.setRotation")) {
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					Float yaw = Float.parseFloat(args[1]);
					Location loc = entity.getLocation();
					loc.setYaw(yaw);
					entity.teleport(loc);
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
				}

			// entity.getRotation
			} else if (c.equals("entity.getRotation")) {
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					send(entity.getLocation().getYaw());
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}
			
			// entity.setPitch
			} else if (c.equals("entity.setPitch")) {
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					Float pitch = Float.parseFloat(args[1]);
					Location loc = entity.getLocation();
					loc.setPitch(pitch);
					entity.teleport(loc);
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
				}

			// entity.getPitch
			} else if (c.equals("entity.getPitch")) {
				//get entity based on id
				Entity entity = plugin.getEntity(Integer.parseInt(args[0]));
				if (entity != null) {
					send(entity.getLocation().getPitch());
				} else {
					plugin.getLogger().info("Entity [" + args[0] + "] not found.");
					send("Fail");
				}
				
			// world.setSign
			} else if (c.equals("world.setSign")) {
				Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
				Block thisBlock = world.getBlockAt(loc);
				//blockType should be 68 for wall sign or 63 for standing sign
				int blockType = Integer.parseInt(args[3]);	
				//facing direction for wall sign : 2=north, 3=south, 4=west, 5=east
				//rotation 0 - to 15 for standing sign : 0=south, 4=west, 8=north, 12=east
				byte blockData = Byte.parseByte(args[4]); 
				if ((thisBlock.getTypeId() != blockType) || (thisBlock.getData() != blockData)) {
					thisBlock.setTypeIdAndData(blockType, blockData, true);
				}
				//plugin.getLogger().info("Creating sign at " + loc);
				if ( thisBlock.getState() instanceof Sign ) {
					Sign sign = (Sign) thisBlock.getState();
					for ( int i = 5; i-5 < 4 && i < args.length; i++) {
						sign.setLine(i-5, unescape(args[i]));
					}
					sign.update();
				}
			
			// world.addBookToChest
			} else if (c.equals("world.addBookToChest")) {
				Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
				Block thisBlock = world.getBlockAt(loc);
				BlockState thisBlockState = thisBlock.getState();
				if ( thisBlockState instanceof InventoryHolder) {
					InventoryHolder chest = (InventoryHolder) thisBlockState;
					ItemStack book = createBookFromJson(unescape(args[3]));
					chest.getInventory().addItem(book);
				} else {
					plugin.getLogger().info("addBook needs location of chest or other InventoryHolder");
					send("Fail");
				}
			
			// world.spawnEntity
			} else if (c.equals("world.spawnEntity")) {
				Location loc = parseRelativeBlockLocation(args[0], args[1], args[2]);
				Entity entity = world.spawnEntity(loc, EntityType.fromId(Integer.parseInt(args[3])));
				send(entity.getEntityId());

			// world.getEntityTypes
			} else if (c.equals("world.getEntityTypes")) {
				StringBuilder bdr = new StringBuilder();				
				for (EntityType entityType : EntityType.values()) {
					if ( entityType.isSpawnable() && entityType.getTypeId() >= 0 ) {
						bdr.append(entityType.getTypeId());
						bdr.append(",");
						bdr.append(entityType.toString());
						bdr.append("|");
					}
				}
				send(bdr.toString());

			// not a command which is supported
			} else {
				plugin.getLogger().warning(c + " is not supported.");
				send("Fail");
			}
		} catch (Exception e) {
			
			plugin.getLogger().warning("Error occured handling command");
			e.printStackTrace();
			send("Fail");
		
		}
	}
	
	public String unescape(String s) {
		if ( s == null ) return null;
		s = s.replace("&#10;", "\n");
		s = s.replace("&#40;", "(");
		s = s.replace("&#41;", ")");
		s = s.replace("&#44;", ",");
		s = s.replace("&sect;", "§");
		s = s.replace("&amp;", "&");
		return s;
	}

	// create a cuboid of lots of blocks 
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
					updateBlock(world, x, y, z, blockType, data);
				}
			}
		}
	}

	// get a cuboid of lots of blocks
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

	// updates a block
	private void updateBlock(World world, Location loc, int blockType, byte blockData) {
		Block thisBlock = world.getBlockAt(loc);
		updateBlock(thisBlock, blockType, blockData);
	}
	
	private void updateBlock(World world, int x, int y, int z, int blockType, byte blockData) {
		Block thisBlock = world.getBlockAt(x,y,z);
		updateBlock(thisBlock, blockType, blockData);
	}
	
	private void updateBlock(Block thisBlock, int blockType, byte blockData) {
		// check to see if the block is different - otherwise leave it 
		if ((thisBlock.getTypeId() != blockType) || (thisBlock.getData() != blockData)) {
			thisBlock.setTypeIdAndData(blockType, blockData, true);
		}
	}
	
	// gets the current player
	public Player getCurrentPlayer() {
		Player player = attachedPlayer;
		// if the player hasnt already been retreived for this session, go and get it.
		if (player == null) {
			player = plugin.getHostPlayer();
			attachedPlayer = player;
		}
		return player;
	}

	public Location parseRelativeBlockLocation(String xstr, String ystr, String zstr) {
		int x = (int) Double.parseDouble(xstr);
		int y = (int) Double.parseDouble(ystr);
		int z = (int) Double.parseDouble(zstr);
		return parseLocation(origin.getWorld(), x, y, z, origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
	}

	public Location parseRelativeLocation(String xstr, String ystr, String zstr) {
		double x = Double.parseDouble(xstr);
		double y = Double.parseDouble(ystr);
		double z = Double.parseDouble(zstr);
		return parseLocation(origin.getWorld(), x, y, z, origin.getX(), origin.getY(), origin.getZ());
	}

	public Location parseRelativeBlockLocation(String xstr, String ystr, String zstr, float pitch, float yaw) {
		Location loc = parseRelativeBlockLocation(xstr, ystr, zstr);
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		return loc;
	}

	public Location parseRelativeLocation(String xstr, String ystr, String zstr, float pitch, float yaw) {
		Location loc = parseRelativeLocation(xstr, ystr, zstr);
		loc.setPitch(pitch);
		loc.setYaw(yaw);
		return loc;
	}
	
	public String blockLocationToRelative(Location loc) {
		return parseLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
	}

	public String locationToRelative(Location loc) {
		return parseLocation(loc.getX(), loc.getY(), loc.getZ(), origin.getX(), origin.getY(), origin.getZ());
	}

	private String parseLocation(double x, double y, double z, double originX, double originY, double originZ) {
		return (x - originX) + "," + (y - originY) + "," + (z - originZ);
	}

	private Location parseLocation(World world, double x, double y, double z, double originX, double originY, double originZ) {
		return new Location(world, originX + x, originY + y, originZ + z);
	}

	private String parseLocation(int x, int y, int z, int originX, int originY, int originZ) {
		return (x - originX) + "," + (y - originY) + "," + (z - originZ);
	}

	private Location parseLocation(World world, int x, int y, int z, int originX, int originY, int originZ) {
		return new Location(world, originX + x, originY + y, originZ + z);
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

		//wait for threads to stop
		try {
			inThread.join(2000);
			outThread.join(2000);
		}
		catch (InterruptedException e) {
			plugin.getLogger().warning("Failed to stop in/out thread");
			e.printStackTrace();
		}

		try {
			socket.close();
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
			plugin.getLogger().info("Starting input thread");
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
					// if its running raise an error
					if (running) {
						if (e.getMessage().equals("Connection reset")) {
							plugin.getLogger().info("Connection reset");
						} else {
							e.printStackTrace();
						}
						running = false;
					}
				} 
			}
			//close in buffer
			try {
				in.close();
			} catch (Exception e) {
				plugin.getLogger().warning("Failed to close in buffer");
				e.printStackTrace();
			}
		}
	}

	private class OutputThread implements Runnable {
		public void run() {
			plugin.getLogger().info("Starting output thread!");
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
					// if its running raise an error
					if (running) {
						e.printStackTrace();
						running = false;
					}
				}
			}
			//close out buffer
			try {
				out.close();
			} catch (Exception e) {
				plugin.getLogger().warning("Failed to close out buffer");
				e.printStackTrace();
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
	
	/**
	 * Creates a WRITTEN_BOOK from JSON string including interactivity such as clicks and hovers.
	 * JSON format same as used by "/give" command without need to escape double quotes
	 * 
	 * Inspired by https://github.com/upperlevel/spigot-book-api/blob/master/src/main/java/xyz/upperlevel/spigot/book/NmsBookHelper.java
	 * 
	 * @author Tim Cummings
	 * @param json - JSON string used to define a book
	 * @return the book as an ItemStack
	 */
	public ItemStack createBookFromJson(String json) {
		BookMeta meta = (BookMeta) Bukkit.getItemFactory().getItemMeta(Material.WRITTEN_BOOK);
		JsonObject pyBook = new JsonParser().parse(json).getAsJsonObject();
		JsonElement pyTitle = pyBook.get("title");
		JsonElement pyAuthor = pyBook.get("author");
		JsonElement pyPages = pyBook.get("pages");
		JsonElement pyDisplay = pyBook.get("display");
		JsonElement pyGeneration = pyBook.get("generation");
		if (pyTitle != null) {
			try {
				meta.setTitle(pyTitle.getAsString());
			} catch (ClassCastException e) {
				plugin.getLogger().info("Book title can't be got as string because it is not JsonPrimitive. Its JSON is " + pyTitle.toString());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				plugin.getLogger().info("Book title can't be got as string because it is a multiple element array. Its JSON is " + pyTitle.toString());
				e.printStackTrace();
			}
		}
		if (pyAuthor != null) {
			try {
				meta.setAuthor(pyAuthor.getAsString());
			} catch (ClassCastException e) {
				plugin.getLogger().info("Book author can't be got as string because it is not JsonPrimitive. Its JSON is " + pyAuthor.toString());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				plugin.getLogger().info("Book author can't be got as string because it is a multiple element array. Its JSON is " + pyAuthor.toString());
				e.printStackTrace();
			}
		}
		if (pyDisplay != null) {
			JsonElement pyDisplayName = null;
			JsonElement pyDisplayLore = null;
			if ( pyDisplay.isJsonObject() ) {
				pyDisplayName = pyDisplay.getAsJsonObject().get("Name");
				pyDisplayLore = pyDisplay.getAsJsonObject().get("Lore");
			}
			if (pyDisplayName != null) {
				try {
					meta.setDisplayName(pyDisplayName.getAsString());
				} catch (ClassCastException e) {
					plugin.getLogger().info("Book display name can't be got as string because it is not JsonPrimitive. Its JSON is " + pyDisplayName.toString());
					e.printStackTrace();
				} catch (IllegalStateException e) {
					plugin.getLogger().info("Book display name can't be got as string because it is a multiple element array. Its JSON is " + pyDisplayName.toString());
					e.printStackTrace();
				}
			}
			if (pyDisplayLore != null) {
				List<String> listLore = new ArrayList<String>();
				if (pyDisplayLore.isJsonArray()) {
					for (JsonElement je : pyDisplayLore.getAsJsonArray()) {
						try {
							listLore.add(je.getAsString());
						} catch (ClassCastException e) {
							plugin.getLogger().info("Book display lore item can't be got as string because it is not JsonPrimitive. Its JSON is " + je.toString());
							e.printStackTrace();
						} catch (IllegalStateException e) {
							plugin.getLogger().info("Book display lore item can't be got as string because it is a multiple element array. Its JSON is " + je.toString());
							e.printStackTrace();
						}
					}
				} else {
					try {
						listLore.add(pyDisplayLore.getAsString());
					} catch (ClassCastException e) {
						plugin.getLogger().info("Book display lore can't be got as string because it is not JsonPrimitive. Really it should be JsonArray but if not we try this. Its JSON is " + pyDisplayLore.toString());
						e.printStackTrace();
					} catch (IllegalStateException e) {
						plugin.getLogger().info("Book display lore can't be got as string because it is a multiple element array. This should never happen because we have already checked it is not a JsonArray. Its JSON is " + pyDisplayLore.toString());
						e.printStackTrace();
					}
				}
			}
		}
		if (pyGeneration != null) {
			try {
				int g = pyGeneration.getAsInt();
				Generation[] ga = Generation.values();
				if ( g >= 0 && g < ga.length ) {
					meta.setGeneration(ga[g]);
				}
			} catch (ClassCastException e) {
				plugin.getLogger().info("Book generation item can't be got as int because it is not JsonPrimitive of int. Its JSON is " + pyGeneration.toString());
				e.printStackTrace();
			} catch (IllegalStateException e) {
				plugin.getLogger().info("Book generation item can't be got as int because it is a multiple element array rather than an int. Its JSON is " + pyGeneration.toString());
				e.printStackTrace();
			}
		}
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		Class<?> craftMetaBookClass = null;
		Field craftMetaBookField = null;
		String strCraftMetaBook = "org.bukkit.craftbukkit." + version + ".inventory.CraftMetaBook";
		try {
			craftMetaBookClass = Class.forName(strCraftMetaBook);
		} catch (ClassNotFoundException e) {
			plugin.getLogger().warning("Can't get class " + strCraftMetaBook + " required to get pages of book that we want to modify");
			e.printStackTrace();
		}
		if (craftMetaBookClass != null ) {
			try {
				craftMetaBookField = craftMetaBookClass.getDeclaredField("pages");
				craftMetaBookField.setAccessible(true);							
			} catch (NoSuchFieldException e) {
				plugin.getLogger().info("Field 'pages' missing from class " + strCraftMetaBook + " required to get pages of book we want to modify");
				e.printStackTrace();
			} catch (SecurityException se) {
				plugin.getLogger().warning("Security exception getting field 'pages' from class " + strCraftMetaBook + " required to get pages of book we want to modify");
				se.printStackTrace();
			}
		}
		Class<?> chatSerializer = null;
		String strChatSerializer1 = "net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer";
		String strChatSerializer2 = "net.minecraft.server." + version + ".ChatSerializer";
		String strChatSerializer = null;
		try {
			chatSerializer = Class.forName(strChatSerializer1);
			strChatSerializer = strChatSerializer1;
		} catch(ClassNotFoundException e) {
			plugin.getLogger().info("Can't find class " + strChatSerializer1 + ". Will try " + strChatSerializer2);
			e.printStackTrace();
		}
		if ( chatSerializer == null ) {
			try {
				chatSerializer = Class.forName(strChatSerializer2);
				strChatSerializer = strChatSerializer2;
			} catch(ClassNotFoundException e) {
				plugin.getLogger().warning("Can't find classes " + strChatSerializer1 + " or " + strChatSerializer2 + " needed to convert JSON to formatted interactive text in books");
				e.printStackTrace();
			}
		}
		Method chatSerializerA = null;
		if ( chatSerializer != null ) {
			try {
				chatSerializerA = chatSerializer.getDeclaredMethod("a", String.class);
			} catch (NoSuchMethodException e) {
				plugin.getLogger().warning("Class " + strChatSerializer + " does not have method a() required to convert JSON to formatted interactive text in books");
				e.printStackTrace();
			} catch (SecurityException se) {
				plugin.getLogger().warning("Security exception getting declared method a() from " + strChatSerializer);
				se.printStackTrace();
			}
		}
		List<Object> pages = null;
		//get the pages if required for json formatting
		if (craftMetaBookField != null ) {
			try {
				@SuppressWarnings("unchecked")
				List<Object> lo = (List<Object>) craftMetaBookField.get(meta);
				pages = lo;
			} catch (ReflectiveOperationException ex) {
				plugin.getLogger().warning("Reflection exception getting pages from book using " + strCraftMetaBook + ".pages");
				ex.printStackTrace();
			}
		}
		if (pyPages.isJsonArray()) {
			for (JsonElement jePage : pyPages.getAsJsonArray()) {
				String page = jePage.toString();
				//plugin.getLogger().info(page);
				if (chatSerializerA != null && pages != null) {
					try {
						pages.add(chatSerializerA.invoke(null, page));
					} catch (IllegalAccessException e) {
						plugin.getLogger().warning("IllegalAccessException invoking method " + strChatSerializer + ".a() using reflection");
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						plugin.getLogger().warning("IllegalArgumentException invoking method " + strChatSerializer + ".a() using reflection");
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						plugin.getLogger().warning("InvocationTargetException invoking method " + strChatSerializer + ".a() using reflection");
						e.printStackTrace();
					}
				} else {
					//something wrong with reflection methods so just add raw text to book
					meta.addPage(page);
				}
			}
		}
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		book.setItemMeta(meta);
		return book;
	}

}
