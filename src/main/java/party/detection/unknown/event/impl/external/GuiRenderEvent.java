package party.detection.unknown.event.impl.external;

import party.detection.unknown.event.Event;
import party.detection.unknown.hook.impl.Gui;

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
