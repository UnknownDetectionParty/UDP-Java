package party.detection.unknown.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.pmw.tinylog.Logger;

import party.detection.unknown.event.EventManager;
import party.detection.unknown.event.impl.internal.PluginLoadEvent;
import party.detection.unknown.plugin.internal.AnnoVisitorData;
import party.detection.unknown.plugin.internal.PluginClassLoader;
import party.detection.unknown.plugin.internal.exceptions.LoadException;
import party.detection.unknown.util.HookGen;
import party.detection.unknown.util.MCVersion;

/**
 * Container for a collection of plugin classes.
 * 
 * @author bloo
 * @since 8/15/2017
 */
public class PluginPack {
	public static final String LocalTestID = "LocalTest";
	private String uid;
	private final File jar;
	private final Set<PluginClass> classes = new HashSet<>();

	public PluginPack(File jar) {
		this.jar = jar;
	}

	public String getUniqueID() {
		return uid;
	}

	public void setUniqueID(String uid) {
		this.uid = uid;
	}

	public File getJar() {
		return jar;
	}

	public Set<PluginClass> getClasses() {
		return classes;
	}

	public void addClass(byte[] code) {
		PluginClass clazz = new PluginClass(this);
		ClassReader cr = new ClassReader(code);
		cr.accept(new AnnoVisitorData(clazz), ClassReader.SKIP_CODE);
		classes.add(clazz);
	}

	public void loadClasses(PluginClassLoader loader)
			throws LoadException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		for (PluginClass clazz : classes) {
			Class<?> pluginClass = loader.loadClass(clazz.getClassName());
			if (!PluginBase.class.isAssignableFrom(pluginClass)) {
				throw new LoadException(
						"Class '" + pluginClass.getName() + "' had @Plugin, but did not implement any plugin classes.");
			}
			clazz.setPluginClass(pluginClass);
			// Register before instantiation. Requires to plugins can do self-lookups.
			PluginManager.INSTANCE.register(this, pluginClass);
			// Instantiate the plugin
			//
			// TODO: Some plugins may require other plugins as dependencies, which get
			// declared in the constructor. There will need to be a way to account for this
			// in the near future.
			PluginBase instance = (PluginBase) pluginClass.newInstance();
			clazz.setInstance(instance);

			HookGen.createHooks(loader, instance);
			instance.onLoad();
			Logger.info("Loaded '" + uid + "'-'" + clazz.name + "'");
		}
		PluginManager.INSTANCE.register(this);
		EventManager.INSTANCE.invoke(new PluginLoadEvent(this));
	}

	public void checkValid() throws LoadException {
		boolean uid = getUniqueID() == null;
		if (uid) {
			throw new LoadException("No declared @PluginGroup annotation found in the jar '" + jar.getName() + "'!");
		}
		// Copy set so we can remove invalid entries
		for (PluginClass clazz : new HashSet<>(classes)) {
			// plugin data check
			boolean name = clazz.getName() == null;
			boolean auth = clazz.getAuthor() == null;
			boolean desc = clazz.getDescription() == null;
			if (uid || name || auth || desc) {
				classes.remove(clazz);
				Logger.error(String.format(
						"Invalid plugin annotation inside '%s'\nExpected [name, author, desc] but got ['%s', '%s', '%s']",
						jar.getName(), clazz.getName(), clazz.getAuthor(), clazz.getDescription()));
			}
			// verision check
			String gameVersion = MCVersion.getGameVersion();
			List<String> supportedVersions = clazz.getVersions();
			if (supportedVersions.isEmpty()) {
				throw new LoadException("Plugin %s does not support ANY game version?");
			}
			boolean supported = false;
			for (String v : supportedVersions) {
				if (gameVersion.equals(v)
						|| (v.contains("*") && gameVersion.startsWith(v.substring(0, v.indexOf("*"))))) {
					supported = true;
				}
			}
			if (!supported) {
				classes.remove(clazz);
				Logger.error("Removed '" + clazz.name + "' - Does not support game version '" + gameVersion + "'");
			}
		}
	}

	public class PluginClass {
		private final PluginPack group;
		private String author, name, description, className;
		private List<String> versions = new ArrayList<>();
		private Class<?> clazz;
		private PluginBase instance;

		public PluginClass(PluginPack group) {
			this.group = group;
		}

		public void addVersion(String version) {
			versions.add(version);
		}

		public List<String> getVersions() {
			return versions;
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

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getName() {
			return name;
		}

		public void setName(String identifier) {
			this.name = identifier;
		}

		public String getDescription() {
			if (description == null) {
				return "n/a";
			}
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setUpstreamGroup(String id) {
			String current = group.getUniqueID();
			if (current == null || current.equals(id)) {
				group.setUniqueID(id);
				return;
			} else {
				Logger.error("Plugin declared its group as: '" + id + "' but was expected to be '" + current + "'");
			}
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}
	}
}
