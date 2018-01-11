package party.unknown.detection.io.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import party.unknown.detection.io.IOManager;
import party.unknown.detection.io.IOUtil;
import party.unknown.detection.plugin.PluginBase;
import party.unknown.detection.plugin.PluginData;
import party.unknown.detection.plugin.PluginManager;
import party.unknown.detection.plugin.internal.exceptions.NoSuchPlugin;

/**
 * @author GenericSkid
 * @since 8/17/17
 */
public enum SettingsManager {
	INSTANCE;

	/**
	 * Map of plugin containers to their settings.
	 */
	private final Map<PluginBase, Settings> settings = new HashMap<>();

	public Settings create(PluginBase owner) {
		PluginData data = PluginManager.INSTANCE.getPlugin(owner.getClass());
		if (data != null) {
			Settings s = new Settings(owner);
			settings.put(owner, s);
			return s;
		}
		throw new NoSuchPlugin(owner.getClass());
	}

	/**
	 * @param owner
	 *            Plugin container.
	 * @return Settings instance for plugin.1
	 */
	public Settings get(PluginBase owner) {
		return settings.get(owner);
	}

	/**
	 * Load values into the given settings.
	 * 
	 * @param settings
	 */
	public static void load(Settings settings) {
		try {
			File confFile = getConfig(settings);
			if (!exist(confFile))
				return;
			JsonObject pluginJson = new JsonParser().parse(IOUtil.readAllLines(confFile)).getAsJsonObject();
			for (Entry<String, Object> e : settings.getValueMap().entrySet()) {
				String key = e.getKey();
				// Object value = e.getValue();
				JsonElement element = pluginJson.get(key);
				if (element != null) {
					Object newValue = new Gson().fromJson(element, settings.getType(key));
					settings.put(key, newValue);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save the given settings.
	 * 
	 * @param settings
	 */
	public static void save(Settings settings) {
		try {
			File confFile = getConfig(settings);
			// Call to ensure directories exist.
			exist(confFile);
			// Write configuration
			Writer writer = new FileWriter(confFile);
			writer.write(new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
				@Override
				public boolean shouldSkipClass(Class<?> clazz) {
					return false;
				}

				@Override
				public boolean shouldSkipField(FieldAttributes field) {
					return !isFieldAccessible(field);
				}
			}).setPrettyPrinting().create().toJson(settings.getValueMap()));
			writer.close();
		} catch (IOException e) {

		}
	}

	private static File getConfig(Settings settings) {
		return new File(IOManager.getPluginDirectory(classFromSettings(settings)), "settings.json");
	}

	private static PluginData classFromSettings(Settings settings) {
		return PluginManager.INSTANCE.getPlugin(settings.getOwner().getClass());
	}

	private static boolean isFieldAccessible(FieldAttributes field) {
		return field.getAnnotation(Setting.class) != null;
	}

	private static boolean exist(File confFile) {
		if (!confFile.exists()) {
			if (!confFile.getParentFile().exists()) {
				confFile.getParentFile().mkdirs();
			}
			return false;
		}
		return true;
	}
}
