package party.unknown.detection.util;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import party.unknown.detection.event.Event;
import party.unknown.detection.event.EventListener;
import party.unknown.detection.event.EventManager;
import party.unknown.detection.hook.HookController;
import party.unknown.detection.plugin.annotations.Hook;
import party.unknown.detection.plugin.internal.PluginClassLoader;
import party.unknown.detection.plugin.internal.exceptions.LoadException;

/**
 * The since date is an estimate, this code has been migrated from the
 * <i>"Misc"</i> class which also does not have a proper date in it.
 * 
 * @author bloo
 * @since 8/15/2017
 */
public class HookGen {
	/**
	 * Private ClassLoader.defineClass() method used to load new classes via their
	 * bytecode.
	 */
	private static Method define;
	/**
	 * Set of plugin class names that have had their hooks generated.
	 */
	private static final Set<String> hookedClasses = new HashSet<>();

	/**
	 * Create hooks to all the appropriate methods in the given instance's class.
	 * 
	 * @param loader
	 * @param instance
	 * @throws LoadException Hook type was unknown <i>(Should never happen)</i>.
	 */
	public static void createHooks(PluginClassLoader loader, Object instance) throws LoadException {
		// Iterate methods and check if it is a hook
		Class<?> pluginClass = instance.getClass();
		// Skip if already hooked
		if (hooked(pluginClass)) {
			return;
		}
		for (Method method : pluginClass.getDeclaredMethods()) {
			Hook hook = method.getDeclaredAnnotation(Hook.class);
			if (hook != null) {
				int pos;
				switch (hook.type()) {
				case BEGIN:
					pos = 0;
					break;
				case OFFSET:
					pos = hook.offset();
					break;
				case END:
					pos = 65535;
					break;
				default:
					throw new LoadException("Invalid hook type!");
				}
				String methodSig = null;
				for (party.unknown.detection.plugin.annotations.Method m : hook.method()) {
					if (m.version().equals(MCVersion.getGameVersion())) {
						methodSig = m.signature();
						break;
					}
				}
				// Generate the caller class
				HookGen.generateCaller(loader, pluginClass, instance, method, methodSig, pos, hook.locals());
			}
		}
	}

	/**
	 * 
	 * @param loader
	 * @param pluginClass
	 * @param pluginInstance
	 * @param hookMethod
	 * @param obfMethod
	 * @param pos
	 * @param locals
	 */
	private static void generateCaller(PluginClassLoader loader, Class<?> pluginClass, Object pluginInstance,
			Method hookMethod, String obfMethod, int pos, int[] locals) {
		/*
		 * parse the obfuscated method signature `obfMethod` into the owner class name,
		 * the method name, and the method descriptor.
		 */
		int splitPos = obfMethod.indexOf('(');
		String desc = obfMethod.substring(splitPos);
		obfMethod = obfMethod.substring(0, splitPos);
		splitPos = obfMethod.lastIndexOf('/');
		String owner = obfMethod.substring(0, splitPos);
		String name = obfMethod.substring(splitPos + 1);
		Type[] argTypes = Type.getArgumentTypes(hookMethod);
		try {
			// Bring out event and caller classes to runtime
			ClassWrapper<? extends Event> event = createEventClass(argTypes, hookMethod);
			ClassWrapper<?> caller = createCallerClass(argTypes, loader, pluginClass, hookMethod, event);
			// set up its instance of the class containing the hook method
			Field field = caller.clazz.getDeclaredField("x");
			field.setAccessible(true);
			field.set(null, pluginInstance);
			// register our caller class to receive our generated event
			EventManager.INSTANCE.register(caller.clazz.newInstance());
			// set up our generated event to get injected by HookController.
			HookController.INSTANCE.addEventInjection(owner, name, desc, pos, event.clazz, locals);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Creates a caller class. This will listen to the given event
	 * <i>(eventClass)</i> and unpack the event's generated fields so they can be
	 * passed to the given hook method.
	 * 
	 * @param argTypes
	 * @param loader
	 *            ClassLoader used to load the plugin.
	 * @param pluginClass
	 *            Plugin that holds the hook.
	 * @param hookMethod
	 *            Method to call where hook defines.
	 * @param eventClass
	 *            Event to call
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> ClassWrapper<?> createCallerClass(Type[] argTypes, PluginClassLoader loader,
			Class<?> pluginClass, Method hookMethod, ClassWrapper<T> eventClass) {
		String callerClassName = "c" + UUID.randomUUID().toString().replace('-', '_');
		ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_5, ACC_PUBLIC, callerClassName, null, "java/lang/Object", null);

		// the instance of the class containing the hook method
		cw.visitField(ACC_STATIC, "x", Type.getDescriptor(pluginClass), null, null);

		/*
		 * Default constructor.
		 */
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, 3);
		mv.visitEnd();

		/*
		 * Generate a static method which calls our method hook.
		 */
		mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "c", Type.getMethodDescriptor(hookMethod), null, null);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, callerClassName, "x", Type.getDescriptor(pluginClass));
		for (int a = 0, v = 0; a < argTypes.length; v += argTypes[a].getSize(), a++) {
			mv.visitVarInsn(argTypes[a].getOpcode(ILOAD), v);
		}
		mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(pluginClass), hookMethod.getName(),
				Type.getMethodDescriptor(hookMethod), false);
		mv.visitInsn(RETURN);
		mv.visitMaxs((3 + argTypes.length) << 1, (3 + argTypes.length) << 1);
		mv.visitEnd();

		/*
		 * Generate our EventListener method to receive the event and pass it to our
		 * static method.
		 */
		mv = cw.visitMethod(ACC_PUBLIC, "q", "(L" + eventClass.name + ";)V", null, null);
		mv.visitAnnotation(Type.getDescriptor(EventListener.class), true).visitEnd();
		mv.visitCode();
		for (int i = 0; i < argTypes.length; i++) {
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(GETFIELD, eventClass.name, Integer.toString(i), argTypes[i].getDescriptor());
		}
		mv.visitMethodInsn(INVOKESTATIC, callerClassName, "c", Type.getMethodDescriptor(hookMethod), false);
		mv.visitInsn(RETURN);
		mv.visitMaxs((3 + argTypes.length) << 1, 3);
		mv.visitEnd();

		Class<?> clazz = HookGen.defineClass(loader, cw.toByteArray());
		return new ClassWrapper(callerClassName, clazz);
	}

	/**
	 * Creates an event class. This will be inserted where the hook defines the call
	 * to be inserted. The event in comparison to a straight up method call prevents
	 * a lot of issues with plugin unloading and potential method-not-found
	 * exceptions and the like.
	 * 
	 * @param argTypes
	 * @param hookMethod
	 *            Method to call where hook defines.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> ClassWrapper<T> createEventClass(Type[] argTypes, Method hookMethod) {
		String eventClassName = "e" + UUID.randomUUID().toString().replace('-', '_');
		ClassWriter cw = new ClassWriter(0);
		cw.visit(V1_5, ACC_PUBLIC, eventClassName, null, Type.getInternalName(Event.class), null);

		// create a field for each parameter of the hook
		for (int i = 0; i < argTypes.length; i++) {
			cw.visitField(ACC_PUBLIC, Integer.toString(i), argTypes[i].getDescriptor(), null, null);
		}

		// create a constructor which takes in the hook parameters and stores them in
		// the fields.
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(hookMethod), null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Event.class), "<init>", "()V", false);
		for (int i = 0, v = 1; i < argTypes.length; v += argTypes[i].getSize(), i++) {
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(argTypes[i].getOpcode(ILOAD), v);
			mv.visitFieldInsn(PUTFIELD, eventClassName, Integer.toString(i), argTypes[i].getDescriptor());
		}
		mv.visitInsn(RETURN);
		mv.visitMaxs(3, (3 + argTypes.length) << 1);
		mv.visitEnd();
		Class<T> clazz = (Class<T>) HookGen.defineClass(cw.toByteArray());
		return new ClassWrapper<T>(eventClassName, clazz);
	}

	/**
	 * Checks if the given class has had it's hooks generated.
	 * 
	 * @param pluginClass
	 *            Plugin class containing hook annotations.
	 * @return {@code true} hooks have been generated.
	 */
	private static boolean hooked(Class<?> pluginClass) {
		String name = pluginClass.getName();
		if (hookedClasses.contains(name)) {
			return true;
		}
		hookedClasses.add(name);
		return false;
	}

	/**
	 * Defines a class via its bytecode using the given ClassLoader
	 * 
	 * @param loader
	 *            ClassLoader to define class with.
	 * @param code
	 *            Class bytecode.
	 * @return Defined class.
	 */
	private static Class<?> defineClass(ClassLoader loader, byte[] code) {
		try {
			return (Class<?>) define.invoke(loader, code, 0, code.length);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Define a class via its bytecode using the system ClassLoader.
	 * 
	 * @param code
	 *            Class bytecode.
	 * @return Defined class.
	 */
	private static Class<?> defineClass(byte[] code) {
		return defineClass(ClassLoader.getSystemClassLoader(), code);
	}

	static class ClassWrapper<T> {
		private final String name;
		private final Class<T> clazz;

		public ClassWrapper(String name, Class<T> clazz) {
			this.name = name;
			this.clazz = clazz;
		}

	}

	static {
		// Setup private classloader define method.
		try {
			define = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
			define.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException("Hook generator failed to create reflection method.");
		}
	}
}
