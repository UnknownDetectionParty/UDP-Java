package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.Getter;
import party.detection.unknown.hook.MethodProxy;

/**
 * @author bloo
 * @since 7/14/2017
 */
public interface Minecraft {
	@Getter("a")
	EntityPlayerSP getPlayer();

	@Getter("b")
	Timer getTimer();

	@Getter("c")
	PlayerControllerMP getController();

	@Getter("d")
	WorldClient getWorld();

	@Getter("e")
	GameSettings getGameSettings();

	@Getter("f")
	FontRenderer getFontRenderer();
	
	@Getter("g")
	GuiScreen getCurrentScreen();
	
	@MethodProxy("a")
	void displayGuiScreen(GuiScreen screen);
}
