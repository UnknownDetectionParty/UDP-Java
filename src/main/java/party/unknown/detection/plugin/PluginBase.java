package party.unknown.detection.plugin;

import party.unknown.detection.io.config.Settings;
import party.unknown.detection.io.config.SettingsManager;

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
}
