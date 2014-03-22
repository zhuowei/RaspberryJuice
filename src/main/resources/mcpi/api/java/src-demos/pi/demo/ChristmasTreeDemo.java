package pi.demo;

import pi.*;
import static pi.Vec.xyz;

/**
 * Build a big christmas tree!
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class ChristmasTreeDemo {
    //
    static double[] tree = {//
        0.5, 1, 1.5, 2, 2.6, 3, 4, 5, 6,//
        4, 5, 6, 7, 8, 8.6,//
        7, 8, 9, 10, 10.6,//
        9, 10, 11, 12, 13};

    public static void main(String[] args) {
        Minecraft mc = Minecraft.connect(args);
        drawChristmasTree(mc, Vec.ZERO);
    }

    static void drawChristmasTree(Minecraft mc, Vec p) {
        // Clear area and add snow on the ground
        Vec v = xyz(20, 0, 20);
        mc.setBlocks(v.neg(), v.add(0, 50, 0), Block.AIR);
        mc.setBlocks(v.neg(), v, Block.SNOW_BLOCK);

        int h = 6;

        // Draw "branches"
        for (int i = 0; i < tree.length; i++) {
            drawDisc(mc, p.add(0, h, 0), tree[tree.length - i - 1], Block.LEAVES.withData(1));
            h++;
        }

        // Draw trunk
        mc.setBlocks(p.add(-1, 0, 0), p.add(1, h - 6, 0), Block.WOOD);
        mc.setBlocks(p.add(0, 0, -1), p.add(0, h - 6, 1), Block.WOOD);

        // Draw star
        drawStar(mc, p.add(0, h, 0));
    }

    static void drawDisc(Minecraft mc, Vec p, double r, Block b) {
        int rr = (int) Math.ceil(r);
        for (int j = -rr; j <= rr; j++) {
            for (int i = -rr; i <= rr; i++) {
                double diff = i * i + j * j - r * r;
                if (diff <= 0) {
                    mc.setBlock(p.add(i, 0, j), b);
                }
            }
        }
    }

    private static void drawStar(Minecraft mc, Vec p) {
        double[] radii = {0, 0, 1, 2, 1, 0};
        for (int i = 0; i < radii.length; i++) {
            drawDisc(mc, p.add(0, i, 0), radii[i], Block.GOLD_BLOCK);
        }
    }
}
