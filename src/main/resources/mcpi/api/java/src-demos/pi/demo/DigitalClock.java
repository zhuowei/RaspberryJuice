package pi.demo;

import java.text.DateFormat;
import java.util.Date;
import pi.Minecraft;
import pi.tool.Text;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class DigitalClock {
    public static void main(String[] args) {
        //Text textTool = Minecraft.connect(args).tools.text;

        String lastTime = "";
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        while (true) {
            String time = timeFormatter.format(new Date());
            if (!time.equals(lastTime)) {
                System.out.println(time);
                lastTime = time;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException _) {
            }
        }
    }
}
