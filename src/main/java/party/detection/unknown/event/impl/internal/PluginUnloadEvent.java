package party.detection.unknown.event.impl.internal;

import party.detection.unknown.event.Event;
import party.detection.unknown.plugin.PluginPack;

/**
 * Called when a plugin-pack is unloaded.
 * 
 * @author GenericSkid
 * @since 8/19/2017
 */
public class PluginUnloadEvent extends Event {
	private final PluginPack pack;

	public PluginUnloadEvent(PluginPack data) {
		this.pack = data;
	}

	/**
	 * The plugin-pack of the plugins being unloaded.
	 * 
	 * @return
	 */
	public PluginPack getPack() {
		return pack;
	}

}
