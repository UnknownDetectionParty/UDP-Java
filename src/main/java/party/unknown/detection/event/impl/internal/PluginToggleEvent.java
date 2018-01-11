package party.unknown.detection.event.impl.internal;

import party.unknown.detection.event.Event;
import party.unknown.detection.plugin.PluginBase;

/**
 * Event called when a plugin switches enabled states.
 * 
 * @author GenericSkid
 * @since 8/19/2017
 */
public class PluginToggleEvent extends Event {
	private final PluginBase plugin;
	private final boolean newState;

	public PluginToggleEvent(PluginBase plugin, boolean newState) {
		this.plugin = plugin;
		this.newState = newState;
	}

	public PluginBase getPlugin() {
		return plugin;
	}

	public boolean getNewState() {
		return newState;
	}
}
