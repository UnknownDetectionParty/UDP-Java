package party.detection.unknown.io;

import java.io.File;

import party.detection.unknown.plugin.PluginData;

/**
 * IO functions key to client functions. 
 * 
 * @author GenericSkid
 * @since 1/16/2018
 */
public class IOManager {
	/**
	 * @return Current directory.
	 */
	public static File getRoot() {
		return new File(System.getProperty("user.dir"));
	}
	
	/**
	 * @return Client main directory.
	 */
	public static File getClientDirectory() {
		return new File(getRoot(), "udp");
	}

	/**
	 * @return Client plugins directory.
	 */
	public static File getPluginsDirectory() {
		return new File(getClientDirectory(), "plugins");
	}

	/**
	 * @param plugin
	 *            The plugin.
	 * @return Config folder for given plugin.
	 */
	public static File getPluginDirectory(PluginData plugin) {
		return new File(getPluginsDirectory(), plugin.getAuthor() + File.separator + plugin.getName());
	}

	/**
	 * @return Client language directory.
	 */
	public static File getLanguageDirectory() {
		return new File(getClientDirectory(), "lang");
	}
	
	/**
	 * @return Client log.
	 */
	public static File getLoggingFile() {
		return new File(getClientDirectory(), "log.txt");
	}
}
