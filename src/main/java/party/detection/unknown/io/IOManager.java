package party.detection.unknown.io;

import java.io.File;

import party.detection.unknown.plugin.PluginPack;

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
	 *            The plugin-pack.
	 * @return Config folder for given plugin-pack.
	 */
	public static File getPluginDirectory(PluginPack pack) {
		return new File(getPluginsDirectory(), pack.getUniqueID());
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
