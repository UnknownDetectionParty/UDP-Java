package party.detection.unknown.plugin.internal;

import party.detection.unknown.event.EventListener;
import party.detection.unknown.event.impl.external.KeyDispatchEvent;
import party.detection.unknown.hook.impl.Wrapper;
import party.detection.unknown.plugin.KeyPlugin;
import party.detection.unknown.plugin.PluginManager;


/**
 * Event listener used for listining to keybinds of plugins.
 * 
 * @author GenericSkid
 * @since 1/2/2018
 */
public enum Keys {
	INSTANCE;
	
	@SuppressWarnings("static-method")
	@EventListener
	public void onKeyDispatch(KeyDispatchEvent e) {
		// Prevent repeat events, easier use in toggling.
		if (e.isRepeat()) {
			return;
		}
		// Prevent toggles in menus.
		if (Wrapper.getMinecraft().getCurrentScreen() != null) {
			return;
		}
		int key = e.getKey();
		PluginManager.INSTANCE.getPlugins().stream()
			.filter(data -> data.getInstance() instanceof KeyPlugin)
			.map(data -> (KeyPlugin) data.getInstance())
			.forEach(plugin -> plugin.updateKey(key));
	}
}
