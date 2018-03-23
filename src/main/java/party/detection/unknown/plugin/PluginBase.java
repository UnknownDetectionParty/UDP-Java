package party.detection.unknown.plugin;

import party.detection.unknown.io.config.Settings;
import party.detection.unknown.io.config.SettingsManager;
import party.detection.unknown.plugin.annotations.Plugin;

/**
 * @author GenericSkid
 * @since 8/19/2017
 */
public abstract class PluginBase {
	/**
	 * Persistent settings.
	 */
	protected final Settings settings;

	public PluginBase() {
		settings = SettingsManager.INSTANCE.create(this);
	}

	/** Called when the plugin is loaded. */
	public void onLoad() {
		settings.load();
	}

	/** Called when the plugin is unloaded. */
	public void onUnload() {
		settings.save();
	}
	
	/**
	 * @return '@Plugin' annotation on the current class.
	 */
	public Plugin getAnnotation(){
		return getClass().getDeclaredAnnotation(Plugin.class);
	}
}
