package pi.demo;

import static pi.Block.*;
import pi.Minecraft;
import static pi.Vec.xyz;

/**
 * Manipulate blocks with the low level api
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class LowLevelDemo {

    public static void main(String[] args) {
        createPool(Minecraft.connect(args));
    }

    static void createPool(Minecraft mc) {
        final int height = 0;

        final int r = 7;
        // Build a kind of half sphere pool
        for (int k = -r; k <= height; k++) {
            for (int j = -r; j <= r; j++) {
                for (int i = -r; i <= r; i++) {
                    if (i * i + j * j + k * k < r * r) {
                        mc.setBlock(xyz(i, k, j), WATER_STATIONARY);
                    } else {
                        mc.setBlock(xyz(i, k, j), STONE);
                    }
                }
            }
        }

        // Set air above pool
        mc.setBlocks(xyz(r, height + 1, -r), xyz(-r, height + 30, r), AIR);

        // Drop the player in the pool
        mc.player.setPosition(xyz(0, 10, 0));
    }
}
