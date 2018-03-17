package party.detection.unknown.event.impl.external;

import org.lwjgl.input.Keyboard;

import party.detection.unknown.event.Event;

/**
 * @author GenericSkid
 * @since 12/29/2017
 */
public class KeyDispatchEvent extends Event {

	/**
	 * @return LWJGL key constant.
	 */
	public int getKey() {
		return Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
	}

	/**
	 * 
	 * @return {@code true} if key is held down and down event is repeated.
	 *         {@code false} if is initial key-up/down event.
	 */
	public boolean isRepeat() {
		return Keyboard.isRepeatEvent();
	}

	/**
	 * @return {@code true} if key-press that fired the event was a down-press.
	 */
	public boolean isDown() {
		return Keyboard.getEventKeyState();
	}

	/**
	 * @return {@code true} if key-press that fired the event was a up-release.
	 */
	public boolean isUp() {
		return !isDown();
	}
}
