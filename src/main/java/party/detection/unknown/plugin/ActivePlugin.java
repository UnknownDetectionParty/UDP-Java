package party.detection.unknown.plugin;

/**
 * @author GenericSkid
 * @since 12/31/2017
 */
public class ActivePlugin extends StatePlugin {
	/**
	 * Active-On-Load implementation. The plugin is registered for events off the
	 * bat and is set to active. The plugin has no means of changing states.
	 * 
	 * @author GenericSkid
	 * @since 12/31/2017
	 */
	public abstract static class Startup extends ActivePlugin {
		public Startup() {
			setEnabled(true, true);
		}
	}
	
	public abstract static class Criteria extends ActivePlugin {

	}

	// TODO: Class that is toggled when an event meeting certain criteria is fired.
	// The issue here is ensuring that while the plugin is disabled it still
	// receives events of the intended kind, but not those requested by the user for
	// active-status performance.
	//
	// This will require making an adjustment to the event manager such that objects
	// can be registed to singular events at a time. Not big problem, I'm just
	// feeling lazy right now.
}
