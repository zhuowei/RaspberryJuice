package pi;

/**
 * A Minecraft Item description (no use yet)
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Item {

    final int id;

    Item(int id) {
        this.id = id;
    }

    static Item id(int id) {
        return new Item(id);
    }

    static Item decode(String s) {
        return id(Integer.parseInt(s));
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Item)) {
            return false;
        }
        return id == ((Item) obj).id;
    }

    @Override
    public String toString() {
        return Integer.toString(id);
    }
    // Predefined items
    public static final Item //
            IRON_SHOVEL = id(256),
            IRON_PICKAXE = id(257),
            IRON_AXE = id(258),
            BOW = id(261),
            ARROW = id(262),
            COAL = id(263),
            DIAMOND = id(264),
            IRON_INGOT = id(265),
            GOLD_INGOT = id(266),
            IRON_SWORD = id(267),
            WOODEN_SWORD = id(268),
            WOODEN_SHOVEL = id(269),
            WOODEN_PICKAXE = id(270),
            WOODEN_AXE = id(271),
            STONE_SWORD = id(272),
            STONE_SHOVEL = id(273),
            STONE_PICKAXE = id(274),
            STONE_AXE = id(275),
            DIAMOND_SWORD = id(276),
            DIAMOND_SHOVEL = id(277),
            DIAMOND_PICKAXE = id(278),
            DIAMOND_AXE = id(279),
            STICK = id(280),
            BOWL = id(281),
            GOLD_SWORD = id(283),
            GOLD_SHOVEL = id(284),
            GOLD_PICKAXE = id(285),
            GOLD_AXE = id(286),
            STRING = id(287),
            FEATHER = id(288),
            GUNPOWDER = id(289),
            FLINT = id(292),
            WHEAT = id(296),
            SIGN = id(323),
            WOODEN_DOOR = id(324),
            IRON_DOOR = id(330),
            SNOWBALL = id(332),
            LEATHER = id(334),
            CLAY_BRICK = id(336),
            CLAY = id(337),
            SUGAR_CANE = id(338),
            PAPER = id(339),
            BOOK = id(340),
            SLIMEBALL = id(341),
            EGG = id(344),
            COMPASS = id(345),
            CLOCK = id(347),
            GLOWSTONE_DUST = id(348),
            DYE = id(351),
            BONE = id(352),
            SUGAR = id(353),
            SHEARS = id(359),
            CAMERA = id(456);
}
