package party.detection.unknown.plugin.internal;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.pmw.tinylog.Logger;

import party.detection.unknown.io.IOManager;
import party.detection.unknown.io.IOUtil;
import party.detection.unknown.plugin.PluginPack;
import party.detection.unknown.plugin.internal.exceptions.LoadException;

public class PluginLoader {
	/**
	 * Finds and loads plugins from the plugin directory.
	 */
	public static void init() {
		File[] plugins = IOManager.getPluginsDirectory().listFiles();
		if (plugins != null) {
			for (File file : plugins) {
				// Skip non-jars
				if (!file.getName().endsWith(".jar")) {
					continue;
				}
				try {
					PluginLoader.load(file);
				} catch (Exception e) {
					Logger.error(e, "Could not load plugin from: " + file.getName());
				}
			}
		}
	}

	/**
	 * Load classes from the given jar.
	 * 
	 * @param jar
	 *            Jar to read from.
	 * @throws LoadException
	 *             Thrown if loading denied.
	 */
	public static void load(File jar) throws LoadException {
		// Open jar
		final PluginPack data = readJar(jar);

		// Ensure that each plugin has the proper annotation data and supports the
		// current version of the game.
		data.checkValid();

		// Load the plugin using our custom classloader which uses the
		// PluginSecurityPolicy
		try (PluginClassLoader loader = new PluginClassLoader(jar.toURI().toURL())) {
			data.loadClasses(loader);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new LoadException("Could not construct plugin! Perhaps the constructor is private?");
		} catch (IOException e) {
			throw new LoadException("Could not read data from plugin jar!");
		}
	}

	/**
	 * @param jar
	 *            Plugin jar to read from.
	 * @return PluginPack extracted from jar.
	 * @throws LoadException
	 */
	private static PluginPack readJar(File jar) throws LoadException {
		try (JarFile jarFile = new JarFile(jar)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			PluginPack data = new PluginPack(jar);
			// Iterate entries for class files, scan for plugin data
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				// skip non-classes
				if (!entry.getName().endsWith(".class")) {
					continue;
				}
				// read the bytecode from the class file
				byte[] code = IOUtil.readBytes(jarFile.getInputStream(entry));

				// add to plugin data
				data.addClass(code);
			}
			return data;
		} catch (IOException e) {
			throw new LoadException(e);
		}
	}
}
