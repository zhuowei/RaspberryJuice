package pi;

import java.util.Scanner;

/**
 * A vector of three floats
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class VecFloat {

    public static final VecFloat ZERO = new VecFloat(0, 0, 0);
    public final float x, y, z;

    VecFloat(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create
     */
    public static VecFloat xyz(float x, float y, float z) {
        return new VecFloat(x, y, z);
    }

    /**
     * Add
     */
    public VecFloat add(VecFloat v) {
        return xyz(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Subtract
     */
    public VecFloat sub(VecFloat v) {
        return xyz(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Multiply with a float (scale)
     */
    public VecFloat mul(float s) {
        return xyz(s * x, s * y, s * z);
    }

    /**
     * Negate (multiply with -1)
     */
    public VecFloat neg() {
        return mul(-1);
    }

    /**
     * Scalar product
     */
    public float dot(VecFloat v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Cross product
     */
    VecFloat cross(VecFloat v) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    /**
     * Get a vector in the same direction but with length 1
     */
    public VecFloat normalized() {
        return mul(1f / length());
    }

    /**
     * Length
     */
    public float length() {
        return (float) Math.sqrt(lengthSq());
    }

    /**
     * length * length
     */
    public float lengthSq() {
        return dot(this);
    }

    static VecFloat decode(String encoded) {
        Scanner s = new Scanner(encoded).useDelimiter("\\,");
        return xyz(s.nextFloat(), s.nextFloat(), s.nextFloat());
    }

    @Override
    public final String toString() {
        return x + "," + y + "," + z;
    }
}
