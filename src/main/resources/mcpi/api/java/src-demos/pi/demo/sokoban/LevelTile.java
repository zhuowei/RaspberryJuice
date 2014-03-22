package pi.demo.sokoban;

/**
 * Level tile
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
enum LevelTile {
    EMPTY(' '), WALL('='), PLAYER('p'), STONE('s'), TARGET('t'), TARGET_AND_STONE('T');
    private char id;

    private LevelTile(char id) {
        this.id = id;
    }

    static LevelTile from(char id) {
        for (LevelTile t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        throw new RuntimeException("Unknown id: " + id);
    }
}
