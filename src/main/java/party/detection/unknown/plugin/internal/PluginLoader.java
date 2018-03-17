package party.detection.unknown.plugin.internal;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.pmw.tinylog.Logger;

import party.detection.unknown.event.EventManager;
import party.detection.unknown.event.impl.internal.PluginLoadEvent;
import party.detection.unknown.io.IOManager;
import party.detection.unknown.io.IOUtil;
import party.detection.unknown.plugin.PluginBase;
import party.detection.unknown.plugin.PluginData;
import party.detection.unknown.plugin.PluginManager;
import party.detection.unknown.plugin.internal.exceptions.LoadException;
import party.detection.unknown.util.HookGen;
import party.detection.unknown.util.MCVersion;

public class PluginLoader {
	/**
	 * Finds and loads plugins from the pllugin directory.
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
	 * Load classes from the given jar into the {@linkplain #plugins plugin map}.
	 * 
	 * @param jar
	 *            Jar to read from.
	 * @throws LoadException
	 *             Thrown if loading denied.
	 */
	public static void load(File jar) throws LoadException {
		// Open jar
		final PluginData data = readJar(jar);

		// Ensure the annotation fields we want are present.
		checkValid(data);

		// Ensure this game version is supported
		String gameVersion = MCVersion.getGameVersion();
		List<String> supportedVersions = data.getVersions();
		if (!(supportedVersions.isEmpty() || supportedVersions.contains(gameVersion))) {
			throw new LoadException(String.format("Plugin %s does not support game version %s%n", data.getName(), gameVersion));
		}

		// Load the plugin using our custom classloader which uses the
		// PluginSecurityPolicy
		try (PluginClassLoader loader = new PluginClassLoader(jar.toURI().toURL())) {
			Class<?> pluginClass = loader.loadClass(data.getPluginClassName());
			if (!PluginBase.class.isAssignableFrom(pluginClass)) {
				throw new LoadException(
						"Class '" + pluginClass.getName() + "' had @Plugin, but did not implement any plugin classes.");
			}
			data.setPluginClass(pluginClass);
			// Register before instantiation. Requires to plugins can do self-lookups.
			PluginManager.INSTANCE.register(data);
			// Instantiate the plugin
			//
			// TODO: Some plugins may require other plugins as dependencies, which get
			// declared in the constructor. There will need to be a way to account for this
			// in the near future.
			PluginBase instance = (PluginBase) pluginClass.newInstance();
			data.setInstance(instance);

			HookGen.createHooks(loader, instance);
			instance.onLoad();
			EventManager.INSTANCE.invoke(new PluginLoadEvent(data));
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new LoadException("Could not construct plugin! Perhaps the constructor is private?");
		} catch (IOException e) {
			throw new LoadException("Could not read data from plugin jar!");
		}
	}

	private static void checkValid(PluginData data) throws LoadException {
		boolean uid = data.getUniqueID() == null;
		boolean name = data.getName() == null;
		boolean desc = data.getDescription() == null;
		boolean auth = data.getAuthor() == null;
		if (uid || name || auth || desc) {
			throw new LoadException("Invalid Plugin annotation in '" + data.getJar().getName()
					+ "' : Must supply args: UID, Name, Author");
		}
	}

	/**
	 * @param jar
	 *            Plugin jar to read from.
	 * @return PluginData extracted from jar.
	 * @throws LoadException
	 */
	private static PluginData readJar(File jar) throws LoadException {
		try (JarFile jarFile = new JarFile(jar)) {
			Enumeration<JarEntry> entries = jarFile.entries();
			PluginData data = new PluginData(jar);
			// Iterate entries for class files, scan for plugin data
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				// skip non-classes
				if (!entry.getName().endsWith(".class")) {
					continue;
				}
				// read the bytecode from the class file
				byte[] code = IOUtil.readBytes(jarFile.getInputStream(entry));

				// parse the bytecode to check for a Plugin annotation
				ClassReader cr = new ClassReader(code);
				cr.accept(new AnnoVisitorData(data), ClassReader.SKIP_CODE);

				// if we found a Plugin annotation, then we can stop
				if (data.getName() != null) {
					data.setPluginClassName(cr.getClassName().replace('/', '.'));
					break;
				}
			}
			return data;
		} catch (IOException e) {
			throw new LoadException(e);
		}
	}
}
