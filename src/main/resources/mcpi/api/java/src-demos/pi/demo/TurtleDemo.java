package pi.demo;

import pi.*;
import pi.tool.Turtle;
import static pi.Block.*;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class TurtleDemo {
    public static void main(String[] args) {
        Turtle turtle = Minecraft.connect(args).tools.turtle;

        int width = 9, depth = 5, height = 3;
        drawHouse(turtle, width, depth, height);
    }

    /**
     * Draw a square house at turtle setHome
     *
     * @param turtle the turtle to use for drawing
     * @param width the width of the house
     * @param height the height of the walls
     */
    private static void drawHouse(Turtle turtle, int width, int depth, int height) {
        width--;

        // Floor

        // Walls
        turtle.home().off().up(1).block(BRICK_BLOCK).on();
        for (int i = 0; i < height; i++) {
            turtle.forward(depth).right().
                    forward(width).right().
                    forward(depth).right().
                    forward(width).right();
            turtle.up(1);
        }

        // Roof
        turtle.jump(0, 0, -1).block(WOOD);
        for (int s = depth; s >= 0; s -= 2) {
            turtle.forward(s).right().forward(width + 2).right()
                    .forward(s).right().forward(width + 2).right();
            turtle.jump(1, 1, 0);
        }

        // Door
        turtle.home().block(AIR).off().up(1).right().forward(width / 2).on().up(1);
    }
}
