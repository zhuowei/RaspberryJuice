package pi.tool;

import pi.*;
import static pi.Vec.xyz;

/**
 * A turtle that can be used for shouldPlaceBlock
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Turtle {

    Minecraft minecraft;
    //
    Vec home = Vec.ZERO;
    Vec pos = Vec.ZERO, dir = Vec.Unit.X;
    Block block = Block.WOOD_PLANKS;
    boolean shouldPlaceBlock = false;

    public Turtle(Minecraft mc) {
        this.minecraft = mc;
    }

    /**
     * Set the turtle setHome pos
     */
    public Turtle setHome(Vec home) {
        this.home = home;
        return this;
    }

    /**
     * Move the turtle to setHome with default orientation
     */
    public Turtle home() {
        this.pos = Vec.ZERO;
        this.dir = Vec.Unit.X;
        return this;
    }

    /**
     * Start shouldPlaceBlock
     */
    public Turtle on() {
        this.shouldPlaceBlock = true;
        placeBlock();
        return this;
    }

    /**
     * Stop shouldPlaceBlock
     */
    public Turtle off() {
        this.shouldPlaceBlock = false;
        return this;
    }

    public Turtle block(Block block) {
        this.block = block;
        return this;
    }

    void placeBlock() {
        if (shouldPlaceBlock) {
            minecraft.setBlock(home.add(pos), block);
        }
    }

    /**
     * Jump without placing blocks
     */
    public Turtle jump(int dx, int dy, int dz) {
        pos = pos.add(dx, dy, dz);
        return this;
    }

    /**
     * Turn 90 degrees CCW
     */
    public Turtle left() {
        this.dir = xyz(dir.z, 0, -dir.x);
        return this;
    }

    /**
     * Turn 90 degrees CW
     */
    public Turtle right() {
        this.dir = xyz(-dir.z, 0, dir.x);
        return this;
    }

    /**
     * Turn 180 degrees
     */
    public Turtle around() {
        this.dir = xyz(-dir.x, 0, -dir.z);
        return this;
    }

    public Turtle forward(int steps) {
        return move(steps, dir);
    }

    public Turtle back(int steps) {
        return move(steps, dir.neg());
    }

    public Turtle up(int steps) {
        return move(steps, Vec.Unit.Y);
    }

    public Turtle down(int steps) {
        return move(steps, Vec.Unit.Y.neg());
    }

    Turtle move(int steps, Vec d) {
        while (steps-- > 0) {
            this.pos = pos.add(d);
            placeBlock();
        }
        return this;
    }
}
