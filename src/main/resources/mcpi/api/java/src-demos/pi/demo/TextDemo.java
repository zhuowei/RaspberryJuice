package pi.demo;

import pi.Minecraft;
import pi.tool.Text;
import static pi.Block.*;
import static pi.Vec.Unit.*;
import static pi.Vec.xyz;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class TextDemo {

    public static void main(String[] args) {
        Text textTool = Minecraft.connect(args).tools.text;

        // Just draw a text
        textTool.at(xyz(0, 0, 20)).draw("pi");

        // Symbols are fun (skull and bones, arrr!)
        textTool.at(xyz(0, 0, 35)).with("Wingdings", 50).with(STONE).draw("\u2620");

        // More settings
        textTool.at(xyz(0, 10, 0)).with("Arial", 12).with(SAND).withOrientation(X, Z).draw("fun!");
    }
}
