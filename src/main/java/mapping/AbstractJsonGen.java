package mapping;

import com.google.gson.GsonBuilder;

import mapping.struct.Field;
import mapping.struct.Local;
import mapping.struct.Method;
import party.detection.unknown.event.impl.external.*;
import party.detection.unknown.hook.impl.*;
import party.detection.unknown.hook.json.*;

import java.lang.reflect.Modifier;

/**
 * Tool used to auto-generate mappings for different minecraft versions.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public abstract class AbstractJsonGen {
	/**
	 * Construct an array of id to name mappings for core minecraft classes used by
	 * the client.
	 * 
	 * @return JsonClassMapping[] of core classes.
	 */
	//@formatter:off
	protected JsonClassMapping[] configure() {
		return new JsonClassMapping[] { 
		cls(Minecraft.class, "net/minecraft/client/Minecraft", 
			new Field() {{
				add("a", "thePlayer", "player");
				add("b", "timer");
				add("c", "playerController");
				add("d", "theWorld", "world");
				add("e", "gameSettings");
				add("f", "fontRendererObj", "fontRenderer");
				add("g", "currentScreen");
			}}.array(), 
			new Method() {{
				add("a", "displayGuiScreen", "(Lnet/minecraft/client/gui/GuiScreen;)V");
				add("sa", "getMinecraft", "()Lnet/minecraft/client/Minecraft;");
				add("sb", "dispatchKeypresses", "()V", 
					new Local() {{
						add(KeyDispatchEvent.class, 0);
					}}.array());
			}}.array()), 
		cls(EntityPlayerSP.class, "net/minecraft/client/entity/EntityPlayerSP", 
			new Field() {}.array(),
			new Method() {{
				add("a", "onUpdateWalkingPlayer", "()V", 
					new Local() {{
						add(PreMotionUpdateEvent.class, 0, 0);
					}}.array());
			}}.array()), 
		cls(Timer.class, "net/minecraft/util/Timer", 
			new Field() {{
				add("a", "timerSpeed");
				add("b", "elapsedPartialTicks");
				add("c", "ticksPerSecond", "tickLength");
			}}.array(),
			new Method() {}.array()),
		cls(PlayerControllerMP.class, "net/minecraft/client/multiplayer/PlayerControllerMP", 
			new Field() {{
				add("a", "curBlockDamageMP");
				add("b", "blockHitDelay");
				add("c", "connection");
			}}.array(), 
			new Method() {{
				add("a", "attackEntity", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V",
					new Local() {{
						add(AttackEntityEvent.class, 0, 1, 2);
					}}.array());}}.array()),
		cls(NetHandlerPlayClient.class, "net/minecraft/client/network/NetHandlerPlayClient", 
			new Field() {{
				add("a", "netManager");
			}}.array(), 
			new Method() {}.array()),
		cls(NetworkManager.class, "net/minecraft/network/NetworkManager",
			new Field() {{
				add("a", "socketAddress");
			}}.array(), 
			new Method() {}.array()),
		cls(WorldClient.class, "net/minecraft/client/multiplayer/WorldClient", 
			new Field() {}.array(),
			new Method() {}.array()),
		cls(Entity.class, "net/minecraft/entity/Entity", 
			new Field() {{
				add("a", "fallDistance");
				add("b", "motionX");
				add("c", "motionY");
				add("d", "motionZ");
				add("e", "posX");
				add("f", "posY");
				add("g", "posZ");
				add("h", "onGround");
				add("i", "rotationYaw");
				add("j", "rotationPitch");
				add("k", "ticksExisted");
				add("l", "inWater");
				add("m", "stepHeight");
				add("n", "noClip");
				add("o", "isInWeb");
				add("p", "isDead");
				add("q", "prevPosX");
				add("r", "prevPosY");
				add("s", "prevPosZ");
				add("t", "isCollidedHorizontally", "collidedHorizontally");
				add("u", "isCollidedVertically", "collidedVertically");
				add("v", "isCollided", "collided");
				add("w", "hurtResistantTime");
			}}.array(), 
			new Method() {{
				add("a", "getPosition", "()Lnet/minecraft/util/math/BlockPos;");
				add("b", "getPositionVector", "()Lnet/minecraft/util/math/Vec3d;");
				add("c", "setGlowing", "(Z)V");
			}}.array()),
		cls(FontRenderer.class, "net/minecraft/client/gui/FontRenderer", 
			new Field() {}.array(), 
			new Method() { {
				add("a", "drawString", "(Ljava/lang/String;FFIZ)I", new Local() {}.array());
				add("b", "drawStringWithShadow", "(Ljava/lang/String;FFI)I", new Local() {}.array());
			}}.array()), 
		cls(Gui.class, "net/minecraft/client/gui/Gui", 
			new Field() {}.array(), 
			new Method() {{
				add("sa", "drawRect", "(IIIII)V", new Local() {}.array());
				add("a", "drawHorizontalLine", "(IIII)V", new Local() {}.array());
				add("b", "drawVerticalLine", "(IIII)V", new Local() {}.array());
				add("c", "drawGradientRect", "(IIIIII)V", new Local() {}.array());
				add("d", "drawCenteredString", "(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V", new Local() {}.array());
				add("e", "drawString", "(Lnet/minecraft/client/gui/FontRenderer;Ljava/lang/String;III)V", new Local() {}.array());
				add("f", "drawTexturedModalRect", "(IIIIII)V", new Local() {}.array());
			}}.array()), 
		cls(GuiScreen.class, "net/minecraft/client/gui/GuiScreen", 
			new Field() {{
				add("a", "width");
				add("b", "height");
				add("c", "fontRendererObj", "fontRenderer");
			}}.array(), 
			new Method() {{
				add("a", "sendChatMessage", "(Ljava/lang/String;Z)V", new Local() {}.array());
			}}.array()),
		cls(GuiIngame.class, "net/minecraft/client/gui/GuiIngame", 
			new Field() {}.array(), 
			new Method() {{
				add("a", "renderGameOverlay", "(F)V", new Local() {{
					add(GuiRenderEvent.class, 0, 0);
				}}.array());
			}}.array()),
		cls(GameSettings.class, "net/minecraft/client/settings/GameSettings",
			new Field() {{
				add("a", "keyBindForward");
				add("b", "keyBindLeft");
				add("c", "keyBindBack");
				add("d", "keyBindRight");
				add("e", "keyBindJump");
				add("f", "keyBindSneak");
				add("g", "keyBindAttack");
				add("h", "keyBindSprint");
				add("i", "gammaSetting");
				add("j", "viewBobbing");
			}}.array(), 
			new Method() {}.array()),
		cls(KeyBinding.class, "net/minecraft/client/settings/KeyBinding",
			new Field() {{
				add("a", "keyDescription");
				add("b", "keyCategory");
				add("c", "keyCodeDefault");
				add("d", "keyCode");
				add("e", "pressed");
				add("f", "pressTime");
			}}.array(),
			new Method() {}.array()), 
		cls(Block.class, "net/minecraft/block/Block", 
			new Field() {{
				add("a", "unlocalizedName");
			}}.array(),
			new Method() {{
				add("a", "getIdFromBlock", "(Lnet/minecraft/block/Block;)I");
				add("b", "getBlockById", "(I)Lnet/minecraft/block/Block;");
			}}.array()), 
		cls(World.class, "net/minecraft/world/World", 
			new Field() {{
				add("a", "loadedEntityList");
				add("b", "loadedTileEntityList");
				add("c", "playerEntities");
			}}.array(), 
			new Method() {{
				add("a", "getBlockState", "(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;");
			}}.array()), 
		cls(BlockPos.class, "net/minecraft/util/math/BlockPos",
			new Field() {}.array(),
			new Method() {}.array()),
		cls(Vec3i.class, "net/minecraft/util/math/Vec3i", 
			new Field() {{
				add("a", "x", "xCoord");
				add("b", "y", "yCoord");
				add("c", "z", "zCoord");
			}}.array(), 
			new Method() {{
				add("a", "distanceSq", "(DDD)D");
			}}.array()), 
		cls(Vec3d.class, "net/minecraft/util/math/Vec3d", 
			new Field() {{
				add("a", "x", "xCoord");
				add("b", "y", "yCoord");
				add("c", "z", "zCoord");
			}}.array(), 
			new Method() {{
				add("a", "distanceTo", "(Lnet/minecraft/util/math/Vec3d;)D");
				add("b", "subtract", "(DDD)Lnet/minecraft/util/math/Vec3d;");
				add("c", "addVector", "(DDD)Lnet/minecraft/util/math/Vec3d;");
				add("d", "lengthVector", "()D");
				add("e", "add", "(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;");
				add("f", "subtract", "(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;");
			}}.array()),
		cls(IBlockState.class, "net/minecraft/block/state/IBlockState", 
			new Field() {}.array(), 
			new Method() {{
				add("a", "getBlock", "()Lnet/minecraft/block/Block;");
			}}.array()), 
		cls(EntityLivingBase.class, "net/minecraft/entity/EntityLivingBase", 
			new Field() {{
				add("a", "moveStrafing");
				add("b", "moveForward");
			}}.array(), 
			new Method() {}.array()),};
	}
	//@formatter:on

	/**
	 * MC version to create mappings for.
	 */
	protected final String mcVersion;
	/**
	 * Flag to pretty-print output.
	 */
	private final boolean pretty;

	public AbstractJsonGen(String mcVersion, boolean pretty) {
		this.mcVersion = mcVersion;
		this.pretty = pretty;
	}

	/**
	 * @return JSON mappings file.
	 * @throws Exception
	 *             Thrown if the JSON could not be created.
	 */
	public abstract String createJSON() throws Exception;

	/**
	 * Construct JSON given the array of mappings.
	 * 
	 * @param config
	 *            The mappings to create a JSON for.
	 * @return JSON text of mappings.
	 */
	protected String build(JsonClassMapping[] config) {
		// Print out results.
		GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL);
		if (pretty) {
			builder = builder.setPrettyPrinting();
		}
		return builder.create().toJson(config).replace("null,", "").replace(",null", "").replace("null", "");
	}

	/**
	 * Generates a class mapping with the given information.
	 * 
	 * @param id
	 *            Mapping ID.
	 * @param mcpName
	 *            MCP name.
	 * @param fields
	 *            Field mappings.
	 * @param methods
	 *            Method mappings.
	 * @return
	 */
	private static JsonClassMapping cls(Class<?> clazz, String mcpName, JsonFieldMapping[] fields,
			JsonMethodMapping[] methods) {
		String id = JsonMappingHandler.INSTANCE.getID(clazz);
		return new JsonClassMapping(mcpName, id, fields, methods);
	}

	/**
	 * Print string.
	 * 
	 * @param s
	 *            String to print.
	 */
	protected void p(String s) {
		System.out.println(s);
	}
}