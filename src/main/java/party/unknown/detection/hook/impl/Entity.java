package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.MethodProxy;
import party.unknown.detection.hook.Setter;

/**
 * @author bloo
 * @since 8/11/2017
 */
public interface Entity {
	@Getter("a")
	float getFallDistance();

	@Setter("a")
	void setFallDistance(float fallDistance);

	@Getter("b")
	double getMotionX();

	@Setter("b")
	void setMotionX(double motionX);

	@Getter("c")
	double getMotionY();

	@Setter("c")
	void setMotionY(double motionY);

	@Getter("d")
	double getMotionZ();

	@Setter("d")
	void setMotionZ(double motionZ);

	@Getter("e")
	double getPosX();

	@Setter("e")
	void setPosX(double posX);

	@Getter("f")
	double getPosY();

	@Setter("f")
	void setPosY(double posZ);

	@Getter("g")
	double getPosZ();

	@Setter("g")
	void setPosZ(double posZ);

	@Getter("q")
	double getPrevPosX();

	@Getter("r")
	double getPrevPosY();

	@Getter("s")
	double getPrevPosZ();

	@Getter("h")
	boolean isOnGround();

	@Setter("h")
	void setOnGround(boolean onGround);

	@Getter("i")
	float getRotationYaw();

	@Setter("i")
	void setRotationYaw(float yaw);

	@Getter("j")
	float getRotationPitch();

	@Setter("j")
	void setRotationPitch(float pitch);

	@Getter("k")
	int getTicksExisted();

	@Getter("l")
	boolean isInWater();

	@Getter("m")
	float getStepHeight();

	@Setter("m")
	void setStepHeight(float height);

	@Getter("n")
	boolean isNoclip();

	@Getter("n")
	void setNoclip(boolean noclip);

	@Getter("o")
	boolean isInWeb();

	@Setter("o")
	void setInWeb(boolean inWeb);

	@Getter("p")
	boolean isDead();

	@Getter("t")
	boolean isCollidedHorizontally();

	@Getter("u")
	boolean isCollidedVertically();

	@Getter("v")
	boolean isCollided();

	@Getter("w")
	int getHurtResistanceTime();

	@MethodProxy("a")
	BlockPos getPosition();

	@MethodProxy("b")
	Vec3d getVector();
	
	@MethodProxy("c")
	void setGlowing(boolean glow);
}
