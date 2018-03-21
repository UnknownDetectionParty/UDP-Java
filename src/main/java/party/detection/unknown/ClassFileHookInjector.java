package party.detection.unknown;

import org.pmw.tinylog.Logger;
import party.detection.unknown.hook.HookController;
import party.detection.unknown.hook.InvalidHookException;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author bloo
 * @since 7/14/2017
 */
final class ClassFileHookInjector implements ClassFileTransformer {

    private final HookController hookController;

	ClassFileHookInjector(final HookController hookController) {
	    this.hookController = hookController;
    }

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] hooked = classfileBuffer;
		try {
			hooked = hookController.hook(classfileBuffer);
		} catch (final InvalidHookException e) {
			Logger.error(e);
		}
		return hooked;
	}
}
