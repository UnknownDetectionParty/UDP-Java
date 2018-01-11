package party.unknown.detection.event.impl.external;

import party.unknown.detection.event.Event;
import party.unknown.detection.hook.impl.Entity;
import party.unknown.detection.hook.impl.EntityPlayer;

/**
 * @author GenericSkid
 * @since 8/11/2017
 */
public class AttackEntityEvent extends Event {
	private final EntityPlayer player;
	private Entity target;

	public AttackEntityEvent(EntityPlayer player, Entity target) {
		this.player = player;
		this.target = target;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public Entity getTarget() {
		return target;
	}
}
