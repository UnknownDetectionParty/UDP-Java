package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.Getter;
import party.detection.unknown.hook.Setter;

/**
 * @author GenericSkid
 * @since 8/10/2017
 */
public interface KeyBinding {
	@Getter("a")
	String getDescription();

	@Getter("b")
	String getCategory();

	@Getter("c")
	int getKeyDefault();

	@Getter("d")
	int getKey();

	@Getter("e")
	boolean isPressed();

	@Setter("e")
	void setPressed(boolean pressed);

	@Getter("f")
	int getTimePressed();
}
