package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.Getter;
import party.detection.unknown.hook.MethodProxy;
import party.detection.unknown.hook.Setter;

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
