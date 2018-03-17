package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;
import party.detection.unknown.hook.StaticHandler;

/**
 * @author GenericSkid
 * @since 12/27/2017
 */
@StaticHandler(Gui.class)
public interface SHGui {
	@MethodProxy("sa")
	void drawRect(int left, int top, int right, int bottom, int color);
}
