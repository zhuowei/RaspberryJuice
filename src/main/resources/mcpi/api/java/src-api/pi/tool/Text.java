package pi.tool;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import pi.*;

/**
 * A tool to draw text in the Minecraft world. <br>Example:
 * <code>
 * text.with("Arial", 18).xyz2(Vec.xyz(0, 2, 0)).draw("Hello, world!").
 * </code>
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class Text {

    Minecraft minecraft;
    //
    Vec basePos = Vec.xyz(0, 1, 0);
    Font font = Font.decode("SansSerif-PLAIN-9");
    Vec u = Vec.Unit.X, v = Vec.Unit.Y;
    Block block = Block.WOOD_PLANKS;

    Text(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    /**
     * Set the position of the text
     */
    public Text at(Vec pos) {
        this.basePos = pos;
        return this;
    }

    /**
     * Set the block type to use for drawing
     */
    public Text with(Block block) {
        this.block = block;
        return this;
    }

    /**
     * Set the font
     */
    public Text with(String fontName, int fontSizeInPoints) {
        this.font = new Font(fontName, Font.PLAIN, fontSizeInPoints);
        return this;
    }

    /**
     * Set the orientation of the text
     */
    public Text withOrientation(Vec.Unit u, Vec.Unit v) {
        if (u.equals(v)) {
            throw new IllegalArgumentException("u and v can't be equal!");
        }
        this.u = u;
        this.v = v;
        return this;
    }

    /**
     * Draw a text with the current settings in the Minecraft world
     */
    public void draw(String text) {
        BufferedImage img = createImage(text);
        final int w = img.getWidth(), h = img.getHeight();
        for (int j = h - 1; j >= 0; j--) {
            for (int i = 0; i < w; i++) {
                if ((img.getRGB(i, j) & 1) == 1) {
                    Vec relativeBlockPos = u.mul(i).add(v.mul((h - j)));
                    Vec blockPos = basePos.add(relativeBlockPos);
                    minecraft.setBlock(blockPos, block);
                }
            }
        }
    }

    private BufferedImage createImage(String text) {
        BufferedImage buf = new BufferedImage(1000, 200, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = buf.createGraphics();

        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, 1, 1 + fm.getAscent());
        Rectangle2D bounds = fm.getStringBounds(text, g);
        buf = buf.getSubimage(0, 0, (int) bounds.getWidth() + 1, (int) bounds.getHeight() + 1);
        g.dispose();
        return buf;
    }
}
