package party.detection.unknown.plugin;

import java.util.*;

import org.pmw.tinylog.Logger;

import party.detection.unknown.event.EventManager;
import party.detection.unknown.event.impl.internal.PluginUnloadEvent;
import party.detection.unknown.plugin.internal.PluginLoader;
import party.detection.unknown.plugin.internal.exceptions.LoadException;

/**
 * @author bloo
 * @since 8/15/2017
 */
public enum PluginManager {
	INSTANCE;

	PluginManager() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				plugins.values().forEach(pd -> pd.getInstance().onUnload());
			}
		});
	}

	/**
	 * Map of plugin class names to plugins.
	 */
	private final Map<String, PluginData> plugins = new HashMap<>();

	/**
	 * Registers a plugin in the {@linkplain #plugins plugin map}.
	 * 
	 * @param data
	 *            Plugin to register.
	 */
	public void register(PluginData data) {
		Logger.info("Register: " + data.getAuthor() + "'s " + data.getName() + " (" + data.getJar().getName() + ")");
		plugins.put(data.getPluginClass().getName(), data);
	}

	/**
	 * Reload plugin from it's file.
	 * <hr>
	 * Issues may occur with bytecode generation of hook classes / injected method
	 * calls to event handling. If you encounter this, open a issue on github and
	 * pray one of us is still around.
	 * 
	 * @param data
	 *            Plugin container.
	 * @throws LoadException
	 *             Thrown if the plugin failed to load.
	 */
	public static void reload(PluginData data) throws LoadException {
		PluginBase pluginBase = data.getInstance();
		pluginBase.onUnload();
		EventManager.INSTANCE.invoke(new PluginUnloadEvent(data));
		EventManager.INSTANCE.destroyCache(pluginBase);
		// Reload
		PluginLoader.load(data.getJar());
	}

	/**
	 * @return Collection of plugin containers.
	 */
	public Collection<PluginData> getPlugins() {
		return plugins.values();
	}

	/**
	 * @param clazz
	 *            Plugin class.
	 * @return Plugin container for the given plugin class.
	 */
	public PluginData getPlugin(Class<?> clazz) {
		return plugins.get(clazz.getName());
	}

}
