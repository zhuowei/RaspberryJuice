package pi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static pi.Vec.Unit.*;
import pi.event.BlockHitEvent;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class EventFactory {

    static List<BlockHitEvent> createBlockHitEvents(String eventList) {
        List<BlockHitEvent> events = new ArrayList<BlockHitEvent>();

        if (!eventList.isEmpty()) {
            for (String event : eventList.split("\\|")) {
                Scanner s = new Scanner(event).useDelimiter(",");
                Vec position = Vec.xyz(s.nextInt(), s.nextInt(), s.nextInt());
                Vec.Unit surfaceDirection = faceIdxToDirection(s.nextInt());
                int entityId = s.nextInt();
                events.add(new BlockHitEvent(position, surfaceDirection, entityId));
            }
        }

        return events;
    }

    static Unit faceIdxToDirection(int faceIdx) {
        Unit[] faceDirs = {Y.neg(), Y, Z.neg(), Z, X.neg(), X};
        return faceDirs[faceIdx];
    }
}
