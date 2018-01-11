package party.unknown.detection.event.impl.external;

import party.unknown.detection.event.Event;
import party.unknown.detection.hook.impl.Gui;

/**
 * Injected into GuiInGame's render call.
 * 
 * @author bloo
 * @since 8/11/2017
 */
public class GuiRenderEvent extends Event {
	private final Gui gui;
	
	public GuiRenderEvent(Gui gui) {
		this.gui = gui;
	}

	public Gui getGui() {
		return gui;
	}
}
