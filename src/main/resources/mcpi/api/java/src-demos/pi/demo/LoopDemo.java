package pi.demo;

import static pi.Block.*;
import pi.Minecraft;
import pi.Vec;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class LoopDemo {
    public static void main(String[] args) {
        Minecraft mc = Minecraft.connect(args);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mc.setBlock(Vec.xyz(i, 2, j), IRON_BLOCK);
            }
        }
    }
}
