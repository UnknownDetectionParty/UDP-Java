package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 12/27/2017
 */
public interface FontRenderer {
	@MethodProxy("a")
	int drawString(String text, float x, float y, int color, boolean shadow);
	
	@MethodProxy("b")
	int drawStringWithShadow(String text, float x, float y, int color);
}
