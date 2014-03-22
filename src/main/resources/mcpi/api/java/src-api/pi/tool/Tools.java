package pi.tool;

import pi.Minecraft;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Tools {

    public final Text text;
    public final Turtle turtle;

    public Tools(Minecraft world) {
        this.text = new Text(world);
        this.turtle = new Turtle(world);
    }
}
