package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.HookController;

/**
 * Gay but cleaner to write than the hook-controller blob-o-text every time.
 * 
 * @author GenericSkid
 * @since 12/25/2017
 */
public class Wrapper {
	public static SHMinecraft getStaticMinecraft() {
		return HookController.INSTANCE.getStaticHandler(SHMinecraft.class);
	}

	public static SHGui getStaticGui() {
		return HookController.INSTANCE.getStaticHandler(SHGui.class);
	}

	public static SHBlock getStaticBlock() {
		return HookController.INSTANCE.getStaticHandler(SHBlock.class);
	}

	public static Minecraft getMinecraft() {
		return getStaticMinecraft().getMinecraft();
	}

	public static EntityPlayerSP getPlayer() {
		return getMinecraft().getPlayer();
	}

	public static PlayerControllerMP getController() {
		return getMinecraft().getController();
	}

	public static WorldClient getWorld() {
		return getMinecraft().getWorld();
	}

	public static GameSettings getSettings() {
		return getMinecraft().getGameSettings();
	}
}
