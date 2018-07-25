package party.detection.unknown.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

/**
 * A fair bit of this is pasted from old LWJGL
 * 
 * @author GenericSkid
 * @since 7/25/2018
 */
public class Keyboard {
	public static final int KEY_NONE = -1;
	public static final int KEY_ESCAPE = 256;
	public static final int KEY_0 = 48;
	public static final int KEY_1 = 49;
	public static final int KEY_2 = 50;
	public static final int KEY_3 = 51;
	public static final int KEY_4 = 52;
	public static final int KEY_5 = 53;
	public static final int KEY_6 = 54;
	public static final int KEY_7 = 55;
	public static final int KEY_8 = 56;
	public static final int KEY_9 = 56;
	public static final int KEY_A = 65;
	public static final int KEY_B = 66;
	public static final int KEY_C = 67;
	public static final int KEY_D = 68;
	public static final int KEY_E = 69;
	public static final int KEY_F = 70;
	public static final int KEY_G = 71;
	public static final int KEY_H = 72;
	public static final int KEY_I = 73;
	public static final int KEY_J = 74;
	public static final int KEY_K = 75;
	public static final int KEY_L = 76;
	public static final int KEY_M = 77;
	public static final int KEY_N = 78;
	public static final int KEY_O = 79;
	public static final int KEY_P = 80;
	public static final int KEY_Q = 81;
	public static final int KEY_R = 82;
	public static final int KEY_S = 83;
	public static final int KEY_T = 84;
	public static final int KEY_U = 85;
	public static final int KEY_V = 86;
	public static final int KEY_W = 87;
	public static final int KEY_X = 88;
	public static final int KEY_Y = 89;
	public static final int KEY_Z = 90;
	public static final int KEY_F1 = 290;
	public static final int KEY_F2 = 291;
	public static final int KEY_F3 = 292;
	public static final int KEY_F4 = 293;
	public static final int KEY_F5 = 294;
	public static final int KEY_F6 = 295;
	public static final int KEY_F7 = 296;
	public static final int KEY_F8 = 297;
	public static final int KEY_F9 = 298;
	public static final int KEY_F10 = 299;
	public static final int KEY_F11 = 300;
	public static final int KEY_F12 = 301;
	public static final int KEY_F13 = 302;
	public static final int KEY_SPACE = 32;
	public static final int KEY_COMMA = 44;
	public static final int KEY_MINUS = 45;
	public static final int KEY_SLASH = 47;
	public static final int KEY_PERIOD = 46;
	public static final int KEY_EQUALS = 61;
	public static final int KEY_LBRACKET = 91;
	public static final int KEY_RBRACKET = 93;
	public static final int KEY_LCONTROL = 341;
	public static final int KEY_RCONTROL = 345;
	private static final String[] keyName = new String[400];
	private static final Map<String, Integer> keyMap = new HashMap<String, Integer>(keyName.length);

	static {
		// Setup name lookups
		Field[] fields = Keyboard.class.getFields();
		try {
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())
						&& Modifier.isFinal(field.getModifiers()) && field.getType().equals(int.class)
						&& field.getName().startsWith("KEY_")
						&& !field.getName().endsWith("WIN")) { /* Don't use deprecated names */
					int key = field.getInt(null);
					String name = field.getName().substring(4);
					keyName[key] = name;
					keyMap.put(name, key);
				}
			}
		} catch (Exception e) {}

	}

	/**
	 * Gets a key's name
	 * 
	 * @param key
	 *            The key
	 * @return a String with the key's human readable name in it or null if the key
	 *         is unnamed
	 */
	public static String getKeyName(int key) {
		return "";
	}

	/**
	 * Get's a key's index. If the key is unrecognised then KEY_NONE is returned.
	 * 
	 * @param keyName
	 *            The key name
	 */
	public static synchronized int getKeyIndex(String keyName) {
		Integer ret = keyMap.get(keyName);
		if (ret == null)
			return KEY_NONE;
		else
			return ret;
	}

	/**
	 * Check last known state of key.
	 * 
	 * @param key
	 * @return {@code true} if last state is {@code GLFW_PRESS}.
	 */
	public static boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(GlfwUtil.getMCWindow(), key) == GLFW.GLFW_PRESS;
	}

}
