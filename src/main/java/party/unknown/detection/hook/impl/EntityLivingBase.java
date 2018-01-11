package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.Setter;

/**
 * @author GenericSkid
 * @since 8/11/2017
 */
public interface EntityLivingBase extends Entity {
	@Getter("a")
	float getMoveStrafing();

	@Setter("a")
	void setMoveStrafing(float move);

	@Getter("b")
	float getMoveForward();

	@Setter("b")
	void setMoveForward(float move);
}
