package party.unknown.detection;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;

import party.unknown.detection.event.EventManager;
import party.unknown.detection.hook.HookController;
import party.unknown.detection.hook.InvalidHookException;
import party.unknown.detection.hook.json.JsonMappingHandler;
import party.unknown.detection.io.IOManager;
import party.unknown.detection.plugin.internal.Keys;
import party.unknown.detection.plugin.internal.PluginLoader;

/**
 * @author bloo
 * @since 7/14/2017
 */
enum Injector implements ClassFileTransformer {
	INSTANCE;

	Injector() {
		// TODO: Transformer fix optifine incompat
		setupLogging();
		Logger.info("Loading injection config");
		JsonMappingHandler.INSTANCE.loadMappings("cfg.json");
		Logger.info("Loading plugins");
		PluginLoader.init();
		Logger.info("Registering binds");
		EventManager.INSTANCE.register(Keys.INSTANCE);
	}

	/**
	 * Configures logging to write to the log file.
	 */
	private static void setupLogging() {
		Configurator.defaultConfig().writer(new ConsoleWriter(), "UDP[{level}]: {message}")
				.addWriter(new FileWriter(IOManager.getLoggingFile().getAbsolutePath()),
						"[{date:yyyy-MM-dd HH:mm:ss}] UDP[{level}]: {message}")
				.maxStackTraceElements(10).level(Level.TRACE).activate();
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] hooked = classfileBuffer;
		try {
			hooked = HookController.INSTANCE.hook(classfileBuffer);
		} catch (InvalidHookException e) {
			Logger.error(e);
		}
		return hooked;
	}
}
