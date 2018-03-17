package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 12/27/2017
 */
public interface Gui {
	
	@MethodProxy("a")
	void drawHorizontalLine(int startX, int endX, int y, int color);

	@MethodProxy("b")
	void drawVerticalLine(int x, int startY, int endY, int color);

	@MethodProxy("c")
	void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor);

	@MethodProxy("d")
	void drawCenteredString(FontRenderer font, String text, int x, int y, int color);

	@MethodProxy("e")
	void drawString(FontRenderer font, String text, int x, int y, int color);

	/**
	 * Draws a textured rectangle at the current z-value.
	 * @param x Draw location 
	 * @param y Draw location
	 * @param u Texture x-offset
	 * @param v Texture y-offset
	 * @param width Draw size
	 * @param height Draw size
	 */
	@MethodProxy("f")
	void drawTexturedModalRect(int x, int y, int u, int v, int width, int height);
}
