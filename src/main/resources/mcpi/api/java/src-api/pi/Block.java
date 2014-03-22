package pi;

/**
 * A Minecraft Block description
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Block {

    final int id, data;

    Block(int id, int data) {
        this.id = id;
        this.data = data & 0xf;
    }

    /**
     * Get a block with and withId (use a constant like Block.TNT)
     */
    public static Block id(int id) {
        return new Block(id, 0);
    }

    /**
     * Get a block with extra data
     */
    public Block withData(int data) {
        return new Block(id, data);
    }

    static Block decode(String s) {
        return id(Integer.parseInt(s));
    }

    static Block decodeWithData(String s) {
        String[] ss = s.split(",");
        int id = Integer.parseInt(ss[0]);
        int data = Integer.parseInt(ss[1]);
        return new Block(id, data);
    }

    @Override
    public int hashCode() {
        return (id << 8) + data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Block)) {
            return false;
        }
        return hashCode() == ((Block) obj).hashCode();
    }

    @Override
    public String toString() {
        return id + (data == 0 ? "" : "," + data);
    }

    /**
     * Get a wool block of a specific color
     */
    public static Block wool(Color color) {
        return WOOL.withData(color.woolColorData);
    }
    // Predefined blocks
    public static final Block //
            AIR = id(0),
            STONE = id(1),
            GRASS = id(2),
            DIRT = id(3),
            COBBLESTONE = id(4),
            WOOD_PLANKS = id(5),
            SAPLING = id(6),
            BEDROCK = id(7),
            WATER_FLOWING = id(8),
            WATER = WATER_FLOWING,
            WATER_STATIONARY = id(9),
            LAVA_FLOWING = id(10),
            LAVA = LAVA_FLOWING,
            LAVA_STATIONARY = id(11),
            SAND = id(12),
            GRAVEL = id(13),
            GOLD_ORE = id(14),
            IRON_ORE = id(15),
            COAL_ORE = id(16),
            WOOD = id(17),
            LEAVES = id(18),
            GLASS = id(20),
            LAPIS_LAZULI_ORE = id(21),
            LAPIS_LAZULI_BLOCK = id(22),
            SANDSTONE = id(24),
            BED = id(26),
            COBWEB = id(30),
            GRASS_TALL = id(31),
            WOOL = id(35),
            FLOWER_YELLOW = id(37),
            FLOWER_CYAN = id(38),
            MUSHROOM_BROWN = id(39),
            MUSHROOM_RED = id(40),
            GOLD_BLOCK = id(41),
            IRON_BLOCK = id(42),
            STONE_SLAB_DOUBLE = id(43),
            STONE_SLAB = id(44),
            BRICK_BLOCK = id(45),
            TNT = id(46),
            BOOKSHELF = id(47),
            MOSS_STONE = id(48),
            OBSIDIAN = id(49),
            TORCH = id(50),
            FIRE = id(51),
            STAIRS_WOOD = id(53),
            CHEST = id(54),
            DIAMOND_ORE = id(56),
            DIAMOND_BLOCK = id(57),
            CRAFTING_TABLE = id(58),
            FARMLAND = id(60),
            FURNACE_INACTIVE = id(61),
            FURNACE_ACTIVE = id(62),
            DOOR_WOOD = id(64),
            LADDER = id(65),
            STAIRS_COBBLESTONE = id(67),
            DOOR_IRON = id(71),
            REDSTONE_ORE = id(73),
            SNOW = id(78),
            ICE = id(79),
            SNOW_BLOCK = id(80),
            CACTUS = id(81),
            CLAY = id(82),
            SUGAR_CANE = id(83),
            FENCE = id(85),
            GLOWSTONE_BLOCK = id(89),
            BEDROCK_INVISIBLE = id(95),
            STONE_BRICK = id(98),
            GLASS_PANE = id(102),
            MELON = id(103),
            FENCE_GATE = id(107),
            GLOWING_OBSIDIAN = id(246),
            NETHER_REACTOR_CORE = id(247);
}
