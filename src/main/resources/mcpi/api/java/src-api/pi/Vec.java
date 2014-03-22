package pi;

import java.util.Scanner;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Vec {

    public final static Vec ZERO = new Vec(0, 0, 0);
    public final static int MIN_Y = -128, MAX_Y = 127;
    public final int x, y, z;

    Vec(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create
     */
    public static Vec xyz(int x, int y, int z) {
        return new Vec(x, y, z);
    }

    /**
     * Add
     */
    public Vec add(Vec v) {
        return xyz(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Add
     */
    public Vec add(int x, int y, int z) {
        return xyz(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Subtract
     */
    public Vec sub(Vec v) {
        return xyz(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Multiply with integer (scale)
     */
    public Vec mul(int s) {
        return xyz(s * x, s * y, s * z);
    }

    /**
     * Negate (multiply with -1)
     */
    public Vec neg() {
        return xyz(-x, -y, -z);
    }

    /**
     * Scalar product
     */
    public int dot(Vec v) {
        return x * v.x + y * v.y + z * v.z;
    }

    @Override
    public int hashCode() {
        return x | (y << 20) | (z << 10);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Vec)) {
            return false;
        }
        return hashCode() == ((Vec) obj).hashCode();
    }

    @Override
    public final String toString() {
        return x + "," + y + "," + z;
    }

    static Vec decode(String encoded) {
        Scanner s = new Scanner(encoded).useDelimiter("\\,");
        return xyz(s.nextInt(), s.nextInt(), s.nextInt());
    }

    /**
     * A vector with length=1
     */
    public static class Unit extends Vec {

        public final static Unit X = new Unit(1, 0, 0);
        public final static Unit Y = new Unit(0, 1, 0);
        public final static Unit Z = new Unit(0, 0, 1);

        Unit(int x, int y, int z) {
            super(x, y, z);
        }

        @Override
        public Unit neg() {
            return new Unit(-x, -y, -z);
        }
    }
}
