package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;
import party.detection.unknown.hook.StaticHandler;

/**
 * @author bloo
 * @since 7/14/2017
 */
@StaticHandler(Minecraft.class)
public interface SHMinecraft {
	@MethodProxy("sa")
	Minecraft getMinecraft();
}
