package party.detection.unknown.event.impl.external;

import party.detection.unknown.event.Event;
import party.detection.unknown.hook.impl.Entity;
import party.detection.unknown.hook.impl.EntityPlayer;

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
