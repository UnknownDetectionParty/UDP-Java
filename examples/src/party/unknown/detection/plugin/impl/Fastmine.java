package party.unknown.detection.plugin.impl;

import org.lwjgl.input.Keyboard;

import party.unknown.detection.io.config.Setting;
import party.unknown.detection.event.EventListener;
import party.unknown.detection.event.impl.external.*;
import party.unknown.detection.hook.impl.*;
import party.unknown.detection.plugin.*;
import party.unknown.detection.plugin.annotations.*;

/**
 * @author GenericSkid
 * @since 11/21/2017
 */
@Plugin(
	id = "SkidBreak",
	name = "Fastmine",								
	description = "Dig faster.",	
	author = "GenericSkid",
	versions = {"1.12.2"}
)
public class Fastmine extends KeyPlugin.Toggle {
	@Setting(name = "Begin progress")
	private float startLevel = 0.32f;
	@Setting(name = "Per-tick boost")
	private float perTickBoost = 0.04f;
	
	public Fastmine(){
		setKey(Keyboard.KEY_V);
	}
	
	@EventListener
	public void onMotionUpdate(PreMotionUpdateEvent e) {
		PlayerControllerMP cont = Wrapper.getController();
		// Set no-delay
		cont.setBlockHitDelay(0);
		// Damage speedup
		float dmg = cont.getCurBlockDamageMP();
		if (dmg < startLevel) {
			dmg = startLevel;
		}
		dmg += perTickBoost;
		// Update new damage
		cont.setCurBlockDamageMP(dmg);
	}
}
