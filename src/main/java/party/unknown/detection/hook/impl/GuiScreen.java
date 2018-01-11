package party.unknown.detection.hook.impl;

import party.unknown.detection.hook.Getter;
import party.unknown.detection.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 12/27/2017
 */
public interface GuiScreen extends Gui {
	@Getter("a")
	int getWidth();

	@Getter("b")
	int getHeight();

	@Getter("c")
	FontRenderer getFont();

	/**
	 * Print and/or send message to chat.
	 * 
	 * @param msg
	 * @param send
	 *            {@code true} if sending to public chat.
	 */
	@MethodProxy("a")
	void sendChatMessage(String msg, boolean send);
}
