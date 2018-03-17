package party.detection.unknown.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bloo
 * @since 8/15/2017
 */
public class PluginData {
	public static final String LocalTestID = "LocalTest";
	private String uid = null, name, description, author, pluginClassName;
	private List<String> versions = new ArrayList<>();
	private Class<?> clazz;
	private final File jar;
	private PluginBase instance;
	// TODO: Map hooks to PluginData, so we can manipulate them later (e.g. removing
	// when "unloading" or "reloading"). - John

	public PluginData(File jar) {
		this.jar = jar;
	}

	public String getUniqueID() {
		return this.uid;
	}

	public void setUniqueID(String uid) {
		/*
		 * TODO: When called, verify plugin exists on server. Data not set will be
		 * fetched if it exists.
		 * 
		 * If uid is LocalTestID, then... well something should be done to prevent conflicts
		 */
		this.uid = uid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String identifier) {
		this.name = identifier;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void addVersion(String version) {
		versions.add(version);
	}

	public List<String> getVersions() {
		return versions;
	}

	public String getPluginClassName() {
		return pluginClassName;
	}

	public void setPluginClassName(String pluginClassName) {
		this.pluginClassName = pluginClassName;
	}

	public Class<?> getPluginClass() {
		return clazz;
	}

	public void setPluginClass(Class<?> clazz) {
		this.clazz = clazz;
	}

	public PluginBase getInstance() {
		return instance;
	}

	public void setInstance(PluginBase instance) {
		this.instance = instance;
	}

	public File getJar() {
		return jar;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
