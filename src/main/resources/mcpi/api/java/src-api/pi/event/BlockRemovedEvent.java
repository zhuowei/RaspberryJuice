package pi.event;

import pi.Vec;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class BlockRemovedEvent extends BlockEvent {

    public BlockRemovedEvent(Vec position) {
        super(position);
    }
}
