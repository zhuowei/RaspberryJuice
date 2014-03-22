package pi.event;

/**
 *
 * @author Daniel Frisk, twitter:danfrisk
 */
class PlayerConnectEvent extends PlayerEvent {

    public final int entityId;

    public PlayerConnectEvent(int entityId) {
        this.entityId = entityId;
    }
}
