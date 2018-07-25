package party.detection.unknown.event.impl.external;

import org.lwjgl.glfw.GLFW;

import party.detection.unknown.event.Event;

/**
 * @author GenericSkid
 * @since 12/29/2017
 */
public class KeyDispatchEvent extends Event {
	private final int key, action;

	public KeyDispatchEvent(int key, int action) {
		this.key = key;
		this.action = action;
	}

	/**
	 * @return GLFW key constant.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @return GLFW action constant.
	 */
	public int getAction() {
		return action;
	}

	/**
	 * 
	 * @return {@code true} if key is held down and down event is repeated.
	 *         {@code false} if is initial key-up/down event.
	 */
	public boolean isRepeat() {
		return action == GLFW.GLFW_REPEAT;
	}

	/**
	 * @return {@code true} if key-press that fired the event was a down-press.
	 */
	public boolean isDown() {
		return action == GLFW.GLFW_PRESS;
	}

	/**
	 * @return {@code true} if key-press that fired the event was a up-release.
	 */
	public boolean isUp() {
		return action == GLFW.GLFW_RELEASE;
	}
}
