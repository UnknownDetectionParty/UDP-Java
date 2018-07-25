package party.detection.unknown.util;

import party.detection.unknown.hook.impl.Wrapper;

/**
 * @author GenericSkid
 * @since 7/25/2018
 */
public class GlfwUtil {
	/**
	 * @return GLFW window ID for MC.
	 */
	public static long getMCWindow() {
		return Wrapper.getMinecraft().getMainWindow().getWindowID();
	}
}
