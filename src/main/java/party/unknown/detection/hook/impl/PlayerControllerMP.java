package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.MethodProxy;
import party.unknown.detection.hook.Setter;

/**
 * @author GenericSkid
 * @since 8/10/2017
 */
public interface PlayerControllerMP {
	@Getter("a")
	float getCurBlockDamageMP();

	@Setter("a")
	void setCurBlockDamageMP(float damage);

	@Getter("b")
	int getBlockHitDelay();

	@Setter("b")
	void setBlockHitDelay(int delay);
	
	@Getter("c")
	NetHandlerPlayClient getConnection();

	@MethodProxy("a")
	void attackEntity(EntityPlayer player, Entity target);
}
