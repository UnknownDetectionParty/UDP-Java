package party.unknown.detection;

import java.lang.instrument.Instrumentation;

/**
 * @author bloo
 * @since 7/14/2017
 */
public class Agent {
	public static void premain(String args, Instrumentation instrumentation) {
		instrumentation.addTransformer(Injector.INSTANCE);
	}
}
