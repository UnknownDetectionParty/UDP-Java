package party.unknown.detection.plugin.impl;

import org.lwjgl.input.Keyboard;

import party.unknown.detection.event.EventListener;
import party.unknown.detection.event.impl.external.PreMotionUpdateEvent;
import party.unknown.detection.hook.impl.Entity;
import party.unknown.detection.hook.impl.EntityLivingBase;
import party.unknown.detection.hook.impl.EntityPlayerSP;
import party.unknown.detection.hook.impl.WorldClient;
import party.unknown.detection.hook.impl.Wrapper;
import party.unknown.detection.io.config.Setting;
import party.unknown.detection.plugin.KeyPlugin;
import party.unknown.detection.plugin.annotations.Plugin;

/**
 * @author GenericSkid
 * @since 2/16/2017
 */
@Plugin(
	id = "Aura", 
	name = "Aura", 
	description = "Kill nearby entities.", 
	author = "GenericSkid", 
	versions = { "1.8", "1.9", "1.10", "1.11", "1.12","1.12.2", "1.12.2-OptiFine_HD_U_C9" })
public class Aura extends KeyPlugin.Toggle {
	@Setting(name = "Maximum range")
	private int range = 4;
	@Setting(name = "Invuln time skip")
	private int resist = 5;

	public Aura() {
		setKey(Keyboard.KEY_R);
	}

	@EventListener
	public void onMotionUpdate(PreMotionUpdateEvent e) {
		WorldClient world = Wrapper.getWorld();
		if (world == null) return;
		EntityPlayerSP player = Wrapper.getPlayer();
		double x = player.getPosX();
		double y = player.getPosY();
		double z = player.getPosZ();
		for (Entity entity : world.getLoadedEntityList()) {
			if (entity.isDead()) {
				continue;
			}
			if (entity instanceof EntityLivingBase && entity.getPosition().distanceTo(x, y, z) < (range*range) && entity.getHurtResistanceTime() <= resist) {
				Wrapper.getController().attackEntity(player, entity);
				return;
			}
		}
	}

}
