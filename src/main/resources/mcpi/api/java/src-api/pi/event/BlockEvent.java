package pi.event;

import pi.Vec;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public abstract class BlockEvent {

    public final Vec position;

    public BlockEvent(Vec position) {
        this.position = position;
    }
}
