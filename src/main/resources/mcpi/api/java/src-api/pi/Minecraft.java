package pi;

import java.util.Arrays;
import java.util.List;
import pi.event.BlockHitEvent;
import pi.tool.Tools;

/**
 * The main class to interact with a running instance of Minecraft Pi.
 *
 * <br/><br/>Example:&nbsp;&nbsp;
 * <code>Minecraft.connect().setBlock(0, 2, 0, Block.GOLD_ORE)</code>
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Minecraft {

    Connection connection;
    static final int DEFAULT_PORT = 4711;
    //
    public final Camera camera = new Camera();
    public final Player player = new Player();
    public final Entities entities = new Entities();
    public final Events events = new Events();
    public final Tools tools = new Tools(this);

    Minecraft(Connection connection) {
        this.connection = connection;
    }

    /**
     * Connect to a local mcpi game
     */
    public static Minecraft connect() {
        return connect("127.0.0.1");
    }

    /**
     * Connect to a remote mcpi game
     */
    public static Minecraft connect(String host) {
        return connect(host, DEFAULT_PORT);
    }

    /**
     * Connect to mcpi on a specific host/port
     */
    static Minecraft connect(String host, int port) {
        return new Minecraft(new Connection(host, port));
    }

    /**
     * Connect with string args
     *
     * @param args an array with optional host, port
     */
    public static Minecraft connect(String[] args) {
        System.err.println(Arrays.asList(args));
        String host = args.length >= 1 ? args[0] : "127.0.0.1";
        int port = args.length >= 2 ? Integer.parseInt(args[1]) : Minecraft.DEFAULT_PORT;
        return Minecraft.connect(host, port);
    }

    /**
     * Get a block
     */
    public Block getBlock(Vec position) {
        send("world.getBlock", position);
        return Block.decode(receive());
    }

    /**
     * Get a block
     */
    public Block getBlockWithData(Vec position) {
        send("world.getBlock", position);
        return Block.decodeWithData(receive());
    }

    /**
     * Set a block
     */
    public void setBlock(int x, int y, int z, Block block) {
        setBlock(Vec.xyz(x, y, z), block);
    }

    /**
     * Set a block
     */
    public void setBlock(Vec position, Block block) {
        send("world.setBlock", position, block);
    }

    /**
     * Set a cuboid of blocks
     */
    public void setBlocks(int x1, int y1, int z1, int x2, int y2, int z2, Block block) {
        setBlocks(Vec.xyz(x1, y1, z1), Vec.xyz(x2, y2, z2), block);
    }

    /**
     * Set a cuboid of blocks
     */
    public void setBlocks(Vec begin, Vec end, Block block) {
        send("world.setBlocks", begin, end, block);
    }

    /**
     * Get the height of the world (last Y that isn't solid from top-down)
     */
    public int getHeight(int x, int z) {
        send("world.getHeight", x, z);
        return Integer.parseInt(receive());
    }

    /**
     * Get the entity ids of the connected players
     */
    public int[] getPlayerEntityIds() {
        send("world.getPlayerIds");
        String[] strIds = receive().split("|");
        int[] ids = new int[strIds.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = Integer.parseInt(strIds[i]);
        }
        return ids;
    }

    /**
     * Keys: "world_immutable", "nametags_visible"
     */
    public void setting(String key, boolean value) {
        send("world.setting", key, value == false ? 0 : 1);
    }

    /**
     * Save a checkpoint that can be used for restoring the world
     */
    public void saveCheckpoint() {
        send("world.checkpoint.save");
    }

    /**
     * Restore the world state to the checkpoint
     */
    public void restoreCheckpoint() {
        send("world.checkpoint.restore");
    }

    /**
     * Post a message to the game chat
     */
    public void postToChat(String message) {
        send("chat.post", message);
    }

    /**
     * If auto is false, commands are kept in a buffer until they are flushed
     * with flush() or the buffer fills up.<br/> Default is to automatically
     * flush each command separately.
     */
    public void autoFlush(boolean auto) {
        connection.autoFlush(auto);
    }

    /**
     * Flush commands that are buffered, not needed, unless autoFlush(false).
     */
    public void flush() {
        connection.flush();
    }

    /**
     * Methods for the player in the connected game
     */
    public class Player {

        public Vec getPosition() {
            send("player.getTile");
            return Vec.decode(receive());
        }

        public void setPosition(Vec position) {
            send("player.setTile", position);
        }

        public VecFloat getExactPosition() {
            send("player.getPos");
            return VecFloat.decode(receive());
        }

        public void setExactPosition(VecFloat position) {
            send("player.setPos", position);
        }

        /**
         * Keys: autojump, Values: true/false<br/> For example to disable
         * automatic jumping:
         * <code>mc.player.setting("autojump", false);</code>
         */
        public void setting(String key, boolean value) {
            send("player.setting", key, value == false ? 0 : 1);
        }
    }

    /**
     * Methods for entities
     */
    public class Entities {

        public Vec getPosition(int entityId) {
            send("entity.getTile", entityId);
            return Vec.decode(receive());
        }

        public void setPosition(int entityId, Vec tile) {
            send("entity.setTile", entityId, tile);
        }

        public VecFloat getExactPosition(int entityId) {
            send("entity.getPos", entityId);
            return VecFloat.decode(receive());
        }

        public void setExactPosition(VecFloat pos) {
            send("entity.setPos");
        }
    }

    /**
     * Control the camera in the game we're connected to
     */
    public class Camera {

        public void setNormal() {
            send("camera.mode.setNormal");
        }

        public void setNormal(int mobEntityId) {
            send("camera.mode.setNormal", mobEntityId);
        }

        public void setThirdPerson() {
            send("camera.mode.setFollow");
        }

        public void setThirdPerson(int entityId) {
            send("camera.mode.setFollow", entityId);
        }

        public void setFixed() {
            send("camera.mode.setFixed");
        }

        public void setPosition(VecFloat position) {
            send("camera.setPos", position);
        }
    }

    /**
     * Events
     */
    public class Events {

        /**
         * Clear all old events
         */
        public void clearAll() {
            send("events.clear");
        }

        /**
         * Only triggered by sword
         */
        public List<BlockHitEvent> pollBlockHits() {
            send("events.block.hits");
            return EventFactory.createBlockHitEvents(receive());
        }
    }

    void send(Object... parts) {
        connection.send(parts);
    }

    String receive() {
        if (!connection.autoFlush) {
            throw new IllegalStateException("Methods that return data aren't supported with autoflush off!");
        }
        return connection.receive();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
