package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.MethodProxy;
import party.unknown.detection.hook.StaticHandler;

/**
 * @author bloo
 * @since 7/14/2017
 */
@StaticHandler(Minecraft.class)
public interface SHMinecraft {
	@MethodProxy("sa")
	Minecraft getMinecraft();
}
