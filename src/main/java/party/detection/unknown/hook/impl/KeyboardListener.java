package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.MethodProxy;

/**
 * @author GenericSkid
 * @since 7/25/2018
 */
public interface KeyboardListener {

	/**
	 * GLFW keyboard callback.<br>
	 * 
	 * <b>Local variable table:</b>
	 * 
	 * <pre>
	0: this
	1-2: window
	3: key
	4: scancode
	5: action
	 * </pre>
	 */
	@MethodProxy("a")
	void glfwKeyCallback(long window, int key, int scancode, int action, int mods);
}
