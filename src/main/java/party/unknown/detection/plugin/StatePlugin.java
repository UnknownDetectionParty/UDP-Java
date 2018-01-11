package party.unknown.detection.plugin;

import party.unknown.detection.event.EventManager;
import party.unknown.detection.event.impl.internal.PluginToggleEvent;

/**
 * @author GenericSkid
 * @since 12/25/2017
 */
abstract class StatePlugin extends PluginBase {
	private boolean enabled;

	/**
	 * @return State of the plugin. {@code true} for enabled, {@code false} for
	 *         disabled.
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets the {@link #enabled enabled status} of the plugin. If passed value
	 * is{@code true}, plugin is registered for events, otherwise is unregistered.
	 * 
	 * @param enabled
	 *            New enabled status.
	 */
	public final void setEnabled(boolean enabled) {
		setEnabled(enabled, false);
	}

	/**
	 * Sets the {@link #enabled enabled status} of the plugin. If passed value
	 * is{@code true}, plugin is registered for events, otherwise is unregistered.
	 * 
	 * @param enabled
	 *            New enabled status.
	 * @param disableNotif
	 *            If firing toggle event should be skipped.
	 */
	public final void setEnabled(boolean enabled, boolean disableNotif) {
		// Skip non-changeing values
		if (isEnabled() == enabled) {
			return;
		}
		// Fire toggle event if not disabled
		if (!disableNotif) {
			EventManager.INSTANCE.invoke(new PluginToggleEvent(this, enabled));
		}
		// Update state, subscribe to events if state demands it.
		this.enabled = enabled;
		if (enabled) {
			EventManager.INSTANCE.register(this);
			onEnable();
		} else {
			EventManager.INSTANCE.unregister(this);
			onDisable();
		}
	}

	/**
	 * Called when the state is set to {@code true}.
	 */
	protected void onEnable() {}

	/**
	 * Called when the state is set to {@code false}.
	 */
	protected void onDisable() {}

	/**
	 * Toggle the plugin state.
	 */
	public void toggle() {
		setEnabled(!isEnabled());
	}
}
