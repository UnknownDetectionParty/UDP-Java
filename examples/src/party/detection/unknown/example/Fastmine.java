package party.detection.unknown.example;

import org.lwjgl.input.Keyboard;

import party.detection.unknown.event.EventListener;
import party.detection.unknown.event.impl.external.*;
import party.detection.unknown.hook.impl.*;
import party.detection.unknown.io.config.Setting;
import party.detection.unknown.plugin.*;
import party.detection.unknown.plugin.annotations.*;

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
