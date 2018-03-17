package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.Getter;
import party.detection.unknown.hook.Setter;

/**
 * @author GenericSkid
 * @since 8/10/2017
 */
public interface GameSettings {
	@Getter("a")
	KeyBinding getKeyForward();
	@Getter("b")
	KeyBinding getKeyLeft();
	@Getter("c")
	KeyBinding getKeyBack();
	@Getter("d")
	KeyBinding getKeyRight();
	@Getter("e")
	KeyBinding getKeyJump();
	@Getter("f")
	KeyBinding getKeySneak();
	@Getter("g")
	KeyBinding getKeyAttack();
	@Getter("h")
	KeyBinding getKeySprint();
	@Getter("i")
	float getGamma();
	@Setter("i")
	void setGamma(float gamma);
	@Getter("j")
	boolean isViewBobbing();
}
