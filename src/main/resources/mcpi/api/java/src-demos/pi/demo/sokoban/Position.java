package pi.demo.sokoban;

import pi.Vec;

/**
 * A position in a Sokoban level
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class Position {
    final int i, j;

    private Position(int i, int j) {
        this.i = i;
        this.j = j;
    }

    static Position uv(int i, int j) {
        return new Position(i, j);
    }

    static Position fromWorld(Vec v) {
        return new Position(v.x, v.z);
    }

    Vec toWorld(int height) {
        return Vec.xyz(i, height, j);
    }

    @Override
    public int hashCode() {
        return (i << 16) + j;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof Position)) {
            return false;
        }
        return hashCode() == ((Position) obj).hashCode();
    }

    @Override
    public String toString() {
        return i + "," + j;
    }
}
