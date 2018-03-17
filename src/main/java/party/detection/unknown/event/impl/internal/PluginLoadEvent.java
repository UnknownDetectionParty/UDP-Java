package party.detection.unknown.event.impl.internal;

import party.detection.unknown.event.Event;
import party.detection.unknown.plugin.PluginData;

/**
 * Called when a plugin is loaded.
 * 
 * @author GenericSkid
 * @since 8/19/2017
 */
public class PluginLoadEvent extends Event {
	private final PluginData data;

	public PluginLoadEvent(PluginData data) {
		this.data = data;
	}

	/**
	 * The plugin data of the plugin being loaded.
	 * 
	 * @return
	 */
	public PluginData getData() {
		return data;
	}

}
