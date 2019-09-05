package net.zhuoweizhang.raspberryjuice;

import net.zhuoweizhang.raspberryjuice.cmd.CmdEntity;
import net.zhuoweizhang.raspberryjuice.cmd.CmdEvent;
import net.zhuoweizhang.raspberryjuice.cmd.CmdPlayer;
import net.zhuoweizhang.raspberryjuice.cmd.CmdWorld;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;

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

    public ArrayDeque<PlayerInteractEvent> interactEventQueue = new ArrayDeque<PlayerInteractEvent>();

    public ArrayDeque<AsyncPlayerChatEvent> chatPostedQueue = new ArrayDeque<AsyncPlayerChatEvent>();

    private int maxCommandsPerTick = 9000;

    private boolean closed = false;

    private Player attachedPlayer = null;

    private CmdEntity cmdEntity = new CmdEntity(this);
    private CmdEvent cmdEvent = new CmdEvent(this);
    private CmdPlayer cmdPlayer = new CmdPlayer(this);
    private CmdWorld cmdWorld = new CmdWorld(this);

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
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
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

    /**
     * called from the server main thread
     */
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

            // 分割命令
            String[] cmd = c.split("[.]", 2);

            if (cmd[0].equals("player")) {
                cmdPlayer.execute(cmd[1], args);

            } else if (cmd[0].equals("entity")) {
                cmdEntity.execute(cmd[1], args);

            } else if (cmd[0].equals("world")) {
                cmdWorld.execute(world, cmd[1], args);

            } else if (cmd[0].equals("events")) {
                cmdEvent.execute(cmd[1], args);

                // chat.post
            } else if (c.equals("chat.post")) {
                //create chat message from args as it was split by ,
                String chatMessage = "";
                int count;
                for (count = 0; count < args.length; count++) {
                    chatMessage = chatMessage + args[count] + " ";
                }
                chatMessage = chatMessage.substring(0, chatMessage.length() - 1);
                server.broadcastMessage(chatMessage);

                // not a command which is supported
            } else {
                plugin.getLogger().warning(c + " is not supported.");
                send("Fail," + c + " is not supported.");
            }
        } catch (Exception e) {

            plugin.getLogger().warning("Error occured handling command");
            e.printStackTrace();
            send("Fail,Please check out minecraft server console");

        }
    }

    // create a cuboid of lots of blocks
    private void setCuboid(Location pos1, Location pos2, String blockType, byte data) {
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
                    blockData.append(world.getBlockAt(x, y, z).getType().name() + ",");
                }
            }
        }

        return blockData.substring(0, blockData.length() > 0 ? blockData.length() - 1 : 0);    // We don't want last comma
    }

    // updates a block
    private void updateBlock(World world, Location loc, String blockType, byte blockData) {
        Block thisBlock = world.getBlockAt(loc);
        updateBlock(thisBlock, blockType, blockData);
    }

    private void updateBlock(World world, int x, int y, int z, String blockType, byte blockData) {
        Block thisBlock = world.getBlockAt(x, y, z);
        updateBlock(thisBlock, blockType, blockData);
    }

    private void updateBlock(Block thisBlock, String blockType, byte blockData) {
        // check to see if the block is different - otherwise leave it
        blockType = blockType.toUpperCase();
        if ((thisBlock.getType() != Material.valueOf(blockType))) {
            thisBlock.setType(Material.valueOf(blockType.toUpperCase()));
//			thisBlock.setTypeIdAndData(blockType, blockData, true);
        }
    }

    // gets the current player
    public Player getCurrentPlayer() {
        if (!serverHasPlayer()) {
            send("Fail,There are no players in the server.");
            return null;
        }
        Player player = attachedPlayer;
        // if the player hasnt already been retreived for this session, go and get it.
        if (player == null) {
            player = plugin.getHostPlayer();
            attachedPlayer = player;
        }
        return player;
    }

    private boolean serverHasPlayer() {
        return !Bukkit.getOnlinePlayers().isEmpty();
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
        synchronized (outQueue) {
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
        } catch (InterruptedException e) {
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

    /**
     * socket listening thread
     */
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
                    while ((line = outQueue.poll()) != null) {
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

    /**
     * from CraftBukkit's org.bukkit.craftbukkit.block.CraftBlock.blockFactToNotch
     */
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
