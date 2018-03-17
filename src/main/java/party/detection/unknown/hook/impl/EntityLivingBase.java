package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.Getter;
import party.detection.unknown.hook.Setter;

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
