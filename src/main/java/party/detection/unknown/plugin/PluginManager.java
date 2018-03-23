package party.detection.unknown.plugin;

import java.util.*;

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
				packs.values().forEach(pd -> pd.getClasses().forEach(pc -> pc.getInstance().onUnload()));
			}
		});
	}

	/**
	 * Map of plugin group ids to plugin-packs.
	 */
	private final Map<String, PluginPack> packs = new HashMap<>();
	/**
	 * Map of plugin-instance names to their plugin-packs.
	 */
	private final Map<String, PluginPack> classLookup = new HashMap<>();

	/**
	 * Registers a plugin-pack in the {@linkplain #packs plugin map}.
	 * 
	 * @param data
	 *            Plugin-pack to register.
	 */
	public void register(PluginPack data) {
		data.getClasses().forEach(pc -> classLookup.put(pc.getClassName(), data));
		packs.put(data.getUniqueID(), data);
	}

	/**
	 * Registers a plugin class to its plugin-pack.
	 * 
	 * @param data
	 *            Plugin pack.
	 * @param pc
	 *            Plugin class.
	 */
	public void register(PluginPack data, Class<?> pc) {
		classLookup.put(pc.getName(), data);
	}

	/**
	 * Reload plugin from it's file.
	 * <hr>
	 * Issues may occur with bytecode generation of hook classes / injected method
	 * calls to event handling. If you encounter this, open a issue on github and
	 * pray one of us is still around.
	 * 
	 * @param pack
	 *            Plugin container.
	 * @throws LoadException
	 *             Thrown if the plugin failed to load.
	 */
	public static void reload(PluginPack pack) throws LoadException {
		pack.getClasses().forEach(pc -> {
			PluginBase pluginBase = pc.getInstance();
			pluginBase.onUnload();
			EventManager.INSTANCE.destroyCache(pc);
		});
		EventManager.INSTANCE.invoke(new PluginUnloadEvent(pack));
		// Reload
		PluginLoader.load(pack.getJar());
	}

	/**
	 * @return Collection of plugin containers.
	 */
	public Collection<PluginPack> getPlugins() {
		return packs.values();
	}

	/**
	 * @param clazz
	 *            Plugin class.
	 * @return Plugin container for the given plugin class.
	 */
	public PluginPack getPlugin(Class<?> clazz) {
		return classLookup.get(clazz.getName());
	}

}
