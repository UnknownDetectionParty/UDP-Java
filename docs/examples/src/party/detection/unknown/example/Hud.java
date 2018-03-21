package party.detection.unknown.example;

import party.detection.unknown.event.EventListener;
import party.detection.unknown.event.impl.external.GuiRenderEvent;
import party.detection.unknown.hook.impl.*;
import party.detection.unknown.plugin.ActivePlugin;
import party.detection.unknown.plugin.KeyPlugin;
import party.detection.unknown.plugin.PluginData;
import party.detection.unknown.plugin.PluginManager;
import party.detection.unknown.plugin.annotations.Plugin;

/**
 * @author GenericSkid
 * @since 12/31/2017
 */
@Plugin(
		id = "SkidHUD",
		name = "Hud", 
		author = "GenericSkid",
		description = "Shitty gui.", versions = { "1.8", "1.9", "1.10", "1.11", "1.12", "1.12.2", "1.12.2-OptiFine_HD_U_C9" })
public class Hud extends ActivePlugin.Startup {

	@EventListener
	public void onRender(GuiRenderEvent e) {
		int y = 2;
		FontRenderer fr = Wrapper.getMinecraft().getFontRenderer();
		for (PluginData p : PluginManager.INSTANCE.getPlugins()) {
			Object inst = p.getInstance();
			if (inst instanceof KeyPlugin) {
				KeyPlugin plugin = (KeyPlugin) inst;
				String text = p.getName();
				int x = 3;
				if (plugin.isEnabled()) {
					fr.drawString(text, x, y, -1, true);
					y+= 12;
				}
			}
		}
	}
}
