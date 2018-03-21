package party.detection.unknown.example;

import org.lwjgl.input.Keyboard;

import party.detection.unknown.event.EventListener;
import party.detection.unknown.event.impl.external.PreMotionUpdateEvent;
import party.detection.unknown.hook.impl.EntityPlayerSP;
import party.detection.unknown.hook.impl.GameSettings;
import party.detection.unknown.hook.impl.Wrapper;
import party.detection.unknown.io.config.Setting;
import party.detection.unknown.plugin.KeyPlugin;
import party.detection.unknown.plugin.annotations.Plugin;

/**
 * @author bloo
 * @since 8/14/2017
 */
@Plugin(
	id = "SkidFly",
	name = "Fly",
	author = "GenericSkid",
	description = "Enable client-side flying.", 
	versions = { "1.8", "1.9", "1.10", "1.11", "1.12","1.12.2", "1.12.2-OptiFine_HD_U_C9" }
)
public class Fly extends KeyPlugin.Toggle {
	@Setting(name = "Base speed")
	private float baseModifier = 0.6f;
	@Setting(name = "Sprinting speed multiplier")
	private float sprintModifier = 2f;

	public Fly() {
		setKey(Keyboard.KEY_F);
	}

	@EventListener
	public void onMotionUpdate(PreMotionUpdateEvent e) {
		EntityPlayerSP player = Wrapper.getPlayer();
		player.setMotionX(0);
		player.setMotionY(0);
		player.setMotionZ(0);
		GameSettings settings = Wrapper.getSettings();
		boolean right = settings.getKeyRight().isPressed();
		boolean left = settings.getKeyLeft().isPressed();
		boolean forward = settings.getKeyForward().isPressed();
		boolean back = settings.getKeyBack().isPressed();
		boolean up = settings.getKeyJump().isPressed();
		boolean down = settings.getKeySneak().isPressed();
		boolean sprint = settings.getKeySprint().isPressed();
		boolean noHorz = !forward && !back && !left && !right;
		boolean noVert = !up && !down;
		// No movement
		if (noHorz && noVert) {
			return;
		}

		float yaw = player.getRotationYaw();
		float pitch = 0;
		float angleConst = 0.017453292F, vertConst = noHorz ? 90f : 45f;
		// Flip vector
		if (back) {
			yaw -= 180;
		}
		// Rotate vector, mix with forward/backward momentem if exists
		if (left) {
			yaw -= forward ? 45 : back ? -45 : 90;
		}
		if (right) {
			yaw += forward ? 45 : back ? -45 : 90;
		}
		// Cancel movement if horizontal movement is conflicting unless there is
		// vertical momentum to account for.
		if (!forward && left && right) {
			if (back || up || down) {

			} else {
				return;
			}
		}
		// Move pitch based on up/down velocity
		if (down) {
			pitch += vertConst;
		}
		if (up) {
			pitch -= vertConst;
		}
		if (pitch >= vertConst) {
			pitch = vertConst;
		} else if (pitch <= -vertConst) {
			pitch = -vertConst;
		}
		// Do the math shit, pasted from the sprit jump code that generates momentum
		// boost and modified to work for flying.
		double f = Math.cos(-yaw * angleConst - (float) Math.PI);
		double f1 = Math.sin(-yaw * angleConst - (float) Math.PI);
		double f2 = -Math.cos(-pitch * angleConst);
		double f3 = Math.sin(-pitch * angleConst);
		float x = (float) (f1 * f2), y = (float) f3, z = (float) (f * f2);
		// Vector strength
		if (sprint) {
			x *= sprintModifier;
			y *= sprintModifier;
			z *= sprintModifier;
		}
		x *= baseModifier;
		y *= baseModifier;
		z *= baseModifier;
		// Update motion
		player.setMotionX(x);
		player.setMotionY(y);
		player.setMotionZ(z);
	}
}
