package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.MethodProxy;
import party.unknown.detection.hook.StaticHandler;

/**
 * @author GenericSkid
 * @since 12/27/2017
 */
@StaticHandler(Gui.class)
public interface SHGui {
	@MethodProxy("sa")
	void drawRect(int left, int top, int right, int bottom, int color);
}
