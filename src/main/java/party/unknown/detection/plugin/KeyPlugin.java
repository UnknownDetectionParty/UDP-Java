package party.unknown.detection.plugin;

import party.unknown.detection.util.Keybinding;

/**
 * @author GenericSkid
 * @since 12/25/2017
 */
public abstract class KeyPlugin extends StatePlugin {
	private Keybinding key;

	/**
	 * Set {@link #getKey() keybind}.
	 * 
	 * @param key
	 *            Keybind to sey.
	 */
	public final void setKey(int key) {
		setKey(new Keybinding(key));
	}

	/**
	 * Set {@link #getKey() keybind}.
	 * 
	 * @param key
	 *            Keybind to sey.
	 */
	public final void setKey(Keybinding key) {
		this.key = key;
	}

	/**
	 * @return Keybind associated with state changes to the mod.
	 */
	public final Keybinding getKey() {
		return key;
	}

	/**
	 * How to handle keybind logic pertaining to the {@link #isEnabled() enabled
	 * state}.
	 */
	public abstract void updateKey(int key);

	/**
	 * Toggle implementation. State changes on key press.
	 * 
	 * @author GenericSkid
	 * @since 12/25/2017
	 */
	public abstract static class Toggle extends KeyPlugin {
		@Override
		public final void updateKey(int key) {
			Keybinding bind = getKey();
			if (bind.match(key) && bind.isDown()) {
				toggle();
			}
		}
	}

	/**
	 * Key-Hold implementation. State changes when plugin bind is held down <i>(Or
	 * released if desired)</i>.
	 * 
	 * @author GenericSkid
	 * @since 12/25/2017
	 */
	public abstract static class Press extends KeyPlugin {
		/**
		 * If {@code true}, mod active on key release.
		 */
		private final boolean inverse;

		public Press() {
			this(false);
		}
		
		public Press(boolean inverse) {
			this.inverse = inverse;
			// Set initial state without firing events
			if (inverse) {
				setEnabled(true, true);
			}
		}

		@Override
		public final void updateKey(int key) {
			Keybinding bind = getKey();
			if (bind.match(key)) {
				setEnabled(bind.isDown() ^ inverse);
			}
		}
	}
}
