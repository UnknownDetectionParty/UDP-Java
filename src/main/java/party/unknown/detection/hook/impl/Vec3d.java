package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 8/11/2017
 */
public interface Vec3d {
	@Getter("a")
	double getX();

	@Getter("b")
	double getY();

	@Getter("c")
	double getZ();

	@MethodProxy("a")
	double distanceTo(Vec3d vec);

	@MethodProxy("b")
	Vec3d subtract(double x, double y, double z);

	@MethodProxy("c")
	Vec3d add(double x, double y, double z);

	@MethodProxy("d")
	double length();

	@MethodProxy("e")
	Vec3d add(Vec3d vec);

	@MethodProxy("f")
	Vec3d subtract(Vec3d vec);
}
