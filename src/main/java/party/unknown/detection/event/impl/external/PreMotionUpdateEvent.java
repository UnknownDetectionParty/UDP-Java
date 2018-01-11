package party.unknown.detection.event.impl.external;

import party.unknown.detection.event.EventCancellable;
import party.unknown.detection.hook.impl.EntityPlayerSP;

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
