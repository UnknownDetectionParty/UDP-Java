package party.detection.unknown;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import party.detection.unknown.event.EventManager;
import party.detection.unknown.hook.HookController;
import party.detection.unknown.hook.json.JsonMappingHandler;
import party.detection.unknown.io.IOManager;
import party.detection.unknown.plugin.internal.Keys;
import party.detection.unknown.plugin.internal.PluginLoader;

import java.lang.instrument.Instrumentation;

/**
 * @author bloo
 * @since 7/14/2017
 */
public final class Agent {

    private static void setupLogging() {
        Configurator.defaultConfig().writer(new ConsoleWriter(), "UDP[{level}]: {message}")
                .addWriter(new FileWriter(IOManager.getLoggingFile().getAbsolutePath()),
                        "[{date:yyyy-MM-dd HH:mm:ss}] UDP[{level}]: {message}")
                .maxStackTraceElements(10).level(Level.TRACE).activate();
    }

	public static void premain(String args, Instrumentation instrumentation) {
        setupLogging();
        Logger.info("Loading injection config");
        JsonMappingHandler.INSTANCE.loadMappings("cfg.json");
        Logger.info("Loading plugins");
        PluginLoader.init();
        Logger.info("Registering binds");
        EventManager.INSTANCE.register(Keys.INSTANCE);

		instrumentation.addTransformer(new ClassFileHookInjector(HookController.INSTANCE));
	}
}
