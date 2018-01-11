package party.unknown.detection.plugin.impl;

import party.unknown.detection.event.EventListener;
import party.unknown.detection.event.impl.external.GuiRenderEvent;
import party.unknown.detection.hook.impl.*;
import party.unknown.detection.plugin.ActivePlugin;
import party.unknown.detection.plugin.KeyPlugin;
import party.unknown.detection.plugin.PluginData;
import party.unknown.detection.plugin.PluginManager;
import party.unknown.detection.plugin.annotations.Plugin;

/**
 * @author GenericSkid
 * @since 12/31/2017
 */
@Plugin(
		id = "SkidHUD",
		name = "Hud", 
		author = "GenericSkid",
		description = "Shows enabled plugins.", 
		versions = { "1.8", "1.9", "1.10", "1.11", "1.12", "1.12.2", "1.12.2-OptiFine_HD_U_C9" })
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
