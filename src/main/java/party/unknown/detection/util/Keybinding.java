package party.unknown.detection.util;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

/**
 * @author GenericSkid
 * @since 12/25/2017
 */
public class Keybinding {
	private static final String FANCY_SPLIT = " + ";
	private final int key;
	private Keybinding modifier;

	public Keybinding(int key) {
		this.key = key;
	}

	/**
	 * @return LWJGL key value.
	 */
	public int getKey() {
		return key;
	}

	/**
	 * @return Array of LWJGL key values of the current key and all its linked
	 *         modifiers.
	 */
	public int[] getKeys() {
		Keybinding keybind = this;
		int i = 0;
		int[] keys = new int[getSize()];
		while (i < keys.length) {
			keys[i++] = keybind.getKey();
			keybind = keybind.getModifier();
		}
		return keys;
	}

	/**
	 * Checks if a given key is either {@link #getKey() the key} or a key of one of
	 * the linked {@link #getModifier() modifiers}.
	 * 
	 * @param key
	 *            Key to check.
	 * @return {@code true} if key is contained by {@link #getKeys() the keybind}.
	 */
	public boolean match(int key) {
		return Arrays.stream(getKeys()).anyMatch((i) -> i == key);
	}

	/**
	 * Additional key-down requirement. Used
	 * 
	 * @return
	 */
	public Keybinding getModifier() {
		return modifier;
	}

	/**
	 * Set an additional key-down requirement.
	 * 
	 * @param modifier
	 *            Additional key to bind to current bind.
	 */
	public Keybinding setModifier(int modifier) {
		return setModifier(new Keybinding(modifier));
	}

	/**
	 * Set an additional key-down requirement.
	 * 
	 * @param modifier
	 *            Additional keybind to bind to current bind.
	 */
	public Keybinding setModifier(Keybinding modifier) {
		this.modifier = modifier;
		return this;
	}

	/**
	 * @return Number of attached {@link #getModifier() modifiers}. If no modifiers,
	 *         value is {@code 1}.
	 */
	public int getSize() {
		Keybinding mod = getModifier();
		if (mod == null)
			return 1;
		// Recursive addition
		return 1 + getModifier().getSize();
	}

	/**
	 * @return {@code true} if the {@link #getKey() key} is pressed. If
	 *         {@link #getModifier() a modifier} exists, it must also be down to
	 *         remain {@code true}.
	 */
	public boolean isDown() {
		if (getModifier() != null) {
			return getModifier().isDown() && Keyboard.isKeyDown(getKey());
		}
		return Keyboard.isKeyDown(getKey());
	}

	@Override
	public String toString() {
		return toFancyString();
	}

	/**
	 * @return String used for identification / equality checking.
	 */
	public String toIDString() {
		int[] keys = getKeys();
		// Sort so any combination of the same keys is printed in the same order.
		Arrays.sort(keys);
		return Arrays.toString(keys);
	}

	/**
	 * @return String used for displaying what the keybind is.
	 */
	public String toFancyString() {
		int[] keys = getKeys();
		// Sort so any combination of the same keys is printed in the same order.
		Arrays.sort(keys);
		// Map int-->string
		String[] names = Arrays.stream(keys).mapToObj(Keyboard::getKeyName).toArray(String[]::new);
		// Construct formatted text
		StringBuilder sb = new StringBuilder();
		for (String name : names) {
			sb.append(name + FANCY_SPLIT);
		}
		return sb.substring(0, sb.length() - FANCY_SPLIT.length()).toUpperCase();
	}

	/**
	 * Checks:
	 * <ul>
	 * <li>{@code obj == null --> false}</li>
	 * <li>{@code obj == int --> getKey == int}</li>
	 * <li>
	 * {@code obj == keybind --> getKey == obj.getKey && getModifier == obj.getModifier}
	 * </li>
	 * </ul>
	 * 
	 * @param obj
	 *            Object to compare keybind to.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj instanceof Keybinding) {
			Keybinding other = (Keybinding) obj;
			return toIDString().equals(other.toIDString());
		} else if (obj instanceof Number && getModifier() == null) {
			Number num = (Number) obj;
			return num.intValue() == key;
		}
		return obj.equals(this);
	}

	/**
	 * Hash of {@link #toIDString()}.
	 */
	@Override
	public int hashCode() {
		return toIDString().hashCode();
	}
}
