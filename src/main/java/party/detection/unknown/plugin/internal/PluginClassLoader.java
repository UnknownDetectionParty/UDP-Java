package party.detection.unknown.plugin.internal;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author bloo
 * @since 8/17/2017
 */
public class PluginClassLoader extends URLClassLoader {
	public PluginClassLoader(URL urls) {
		super(new URL[]{urls});
	}
}
