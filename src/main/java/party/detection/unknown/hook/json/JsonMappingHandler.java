package party.detection.unknown.hook.json;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;

import party.detection.unknown.event.Event;
import party.detection.unknown.event.impl.external.*;
import party.detection.unknown.hook.HookController;
import party.detection.unknown.hook.impl.*;

import java.io.InputStreamReader;

import org.pmw.tinylog.Logger;

/**
 * @author bloo
 * @since 7/14/2017
 */
public enum JsonMappingHandler {
	INSTANCE;

	private final BiMap<String, Class<?>> idToProxyMap = HashBiMap.create();
	private final BiMap<String, Class<? extends Event>> idToEventMap = HashBiMap.create();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	JsonMappingHandler() {
		// Paste new classes mapping classes in here.
		Class[] mappings = new Class[] { Block.class, BlockPos.class, Entity.class, EntityLivingBase.class,
				EntityPlayer.class, EntityPlayerSP.class, FontRenderer.class, GameSettings.class, Gui.class,
				GuiIngame.class, GuiScreen.class, IBlockState.class, KeyBinding.class, Minecraft.class,
				NetHandlerPlayClient.class, NetworkManager.class, PlayerControllerMP.class, TileEntity.class,
				Timer.class, Vec3d.class, Vec3i.class, World.class, WorldClient.class };
		// Paste new events in here.
		Class[] events = new Class[] { PreMotionUpdateEvent.class, GuiRenderEvent.class, AttackEntityEvent.class,
				KeyDispatchEvent.class };
		for (Class clazz : mappings) {
			String key = String.valueOf(clazz.getSimpleName().hashCode());
			idToProxyMap.put(key, clazz);
		}
		for (Class clazz : events) {
			String key = String.valueOf(clazz.getSimpleName().hashCode());
			idToEventMap.put(key, clazz);
		}
	}

	public void loadMappings(String configResource) {
		Gson gson = new Gson();
		HookController hookController = HookController.INSTANCE;

		InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream(configResource));
		try {
			for (JsonClassMapping classMapping : gson.fromJson(reader, JsonClassMapping[].class)) {
				Class<?> proxyClass = idToProxyMap.get(classMapping.getId());
				hookController.mapClass(classMapping.getObfName(), proxyClass);

				for (JsonFieldMapping fieldMapping : classMapping.getFieldMappings()) {
					if (fieldMapping == null)
						continue;
					hookController.mapMember(classMapping.getObfName(), fieldMapping.getObfName(),
							fieldMapping.getDesc(), fieldMapping.getId());
				}

				for (JsonMethodMapping methodMapping : classMapping.getMethodMappings()) {
					hookController.mapMember(classMapping.getObfName(), methodMapping.getObfName(),
							methodMapping.getDesc(), methodMapping.getId());
					if (methodMapping.getObfName() == null)
						continue;
					JsonEventInjection[] injections = methodMapping.getInjections();
					if (injections != null) {
						for (JsonEventInjection injection : injections) {
							hookController.addEventInjection(classMapping.getObfName(), methodMapping.getObfName(),
									methodMapping.getDesc(), injection.getInjectionPos(),
									idToEventMap.get(injection.getEventId()), injection.getLocals());
						}
					}
				}
			}

			reader.close();
		} catch (Exception e) {
			Logger.error(e, "Failed reading JSON contents of mappings file.");
		}
	}

	public String getID(Class<?> clazz) {
		return idToProxyMap.inverse().get(clazz);
	}

	public Class<?> getClass(String id) {
		return idToProxyMap.get(id);
	}

	public String getEventID(Class<?> clazz) {
		return idToEventMap.inverse().get(clazz);
	}

	public Class<?> getEventClass(String id) {
		return idToEventMap.get(id);
	}
}
