package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 8/11/2017
 */
public interface Vec3i {
	@Getter("a")
	int getX();

	@Getter("b")
	int getY();

	@Getter("c")
	int getZ();
	
	@MethodProxy("a")
	double distanceTo(double x, double y, double z);
}
