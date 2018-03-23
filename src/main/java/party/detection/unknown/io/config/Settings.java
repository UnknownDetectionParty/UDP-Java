package party.detection.unknown.io.config;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.pmw.tinylog.Logger;

import party.detection.unknown.plugin.PluginBase;

/**
 * @author GenericSkid
 * @since 8/17/17
 */
@SuppressWarnings("unchecked")
public class Settings {
	/**
	 * Map of settings.
	 */
	private final Map<String, Object> values = new HashMap<>();
	/**
	 * Map of setting names to fields.
	 */
	private final Map<String, Field> fields = new HashMap<>();
	/**
	 * Owner of the settings.
	 */
	private final PluginBase owner;
	/**
	 * Flag for initialization. If initialization is run while the flag is
	 * {@code true}, an exception is thrown.
	 */
	private boolean init;

	public Settings(PluginBase owner) {
		this.owner = owner;
	}

	/**
	 * Retrieve value.
	 * 
	 * @param key
	 * @return Value held by key. Null if no key exists.
	 */
	public <V> V get(String key) {
		return (V) values.get(key);
	}

	/**
	 * Set and return passed value.
	 * 
	 * @param key
	 * @param value
	 * @return Passed value.
	 */
	public <V> V put(String key, V value) {
		V returnValue = putWithoutUpdate(key, value);
		try {
			updateField(key, value);
		} catch (Exception e) {
			Logger.error(e);
		}
		return returnValue;
	}

	/**
	 * Set and return passed value.
	 * 
	 * @param key
	 * @param value
	 * @return Passed value.
	 */
	private <V> V putWithoutUpdate(String key, V value) {
		values.put(key, value);
		return value;
	}

	/**
	 * @return Entry set of the config map.
	 */
	public Map<String, Object> getValueMap() {
		return values;
	}

	/**
	 * @param key
	 * @return Generic type of setting by the given key.
	 */
	public Type getType(String key) {
		Field field = fields.get(key);
		if (field == null)
			return null;
		return field.getGenericType();
	}

	/**
	 * @return Plugin owner.
	 */
	public PluginBase getOwner() {
		return owner;
	}

	/**
	 * @return Plugin name.
	 */
	public String getName() {
		return owner.getAnnotation().name();
	}

	/**
	 * Load fields from the setting owner into the {@link #values}.
	 */
	private final void populate() {
		if (init)
			throw new RuntimeException("Settings for: " + owner.getClass().getName() + " already populated!");
		init = true;
		for (Field field : owner.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				Setting setting = field.getAnnotation(Setting.class);
				if (setting != null) {
					putWithoutUpdate(setting.name(), field.get(owner));
					fields.put(setting.name(), field);
				}
			} catch (Exception e) {
				Logger.error(e, "Could not set the value of: " + owner.getClass().getName() + "#" + field.getName());
			}
		}
	}

	/**
	 * Updates the value of the setting by the given key in the {@link #owner}.
	 * 
	 * @param key
	 * @throws Exception
	 *             Thrown if the field could not be set.
	 */
	private void updateField(String key, Object value) throws Exception {
		Field field = fields.get(key);
		if (field != null) {
			field.set(owner, value);
		}
	}

	/**
	 * Loads the config.
	 */
	public void load() {
		populate();
		SettingsManager.load(this);
	}

	/**
	 * Saves the config.
	 */
	public void save() {
		SettingsManager.save(this);
	}
}
