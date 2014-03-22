package pi.event;

import pi.Vec;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
public class BlockHitEvent extends BlockEvent {

    public final Vec.Unit surfaceDirection;
    public final int entityId;

    public BlockHitEvent(Vec position, Vec.Unit surfaceDirection, int entityId) {
        super(position);
        this.surfaceDirection = surfaceDirection;
        this.entityId = entityId;
    }
}
