package pi.event;

import pi.Vec;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class BlockAddedEvent extends BlockEvent {

    public BlockAddedEvent(Vec position) {
        super(position);
    }
}
