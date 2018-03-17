package party.detection.unknown.event.impl.external;

import party.detection.unknown.event.EventCancellable;
import party.detection.unknown.hook.impl.EntityPlayerSP;

/**
 * @author bloo
 * @since 8/3/2017
 */
public class PreMotionUpdateEvent extends EventCancellable {
    private EntityPlayerSP player;

    public PreMotionUpdateEvent(EntityPlayerSP player) {
        this.player = player;
    }

    public EntityPlayerSP getPlayer() {
        return player;
    }
}
