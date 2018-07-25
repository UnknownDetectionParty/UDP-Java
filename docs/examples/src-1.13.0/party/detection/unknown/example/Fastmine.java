package party.detection.unknown.example;

import party.detection.unknown.event.EventListener;
import party.detection.unknown.event.impl.external.*;
import party.detection.unknown.hook.impl.*;
import party.detection.unknown.io.config.Setting;
import party.detection.unknown.plugin.*;
import party.detection.unknown.plugin.annotations.*;
import party.detection.unknown.util.Keyboard;

/**
 * @author GenericSkid
 * @since 11/21/2017
 */
@Plugin(
	name = "Fastmine",								
	description = "Dig faster.",	
	author = "GenericSkid",
	versions = {
			"1.13*"
	}
)
public class Fastmine extends KeyPlugin.Toggle {
	@Setting(name = "Begin progress")
	private float startLevel = 0.20f;
	@Setting(name = "Per-tick boost")
	private float perTickBoost = 0.023f;
	
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
