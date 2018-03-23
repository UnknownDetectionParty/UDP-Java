package party.detection.unknown.hook;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import party.detection.unknown.event.Event;
import party.detection.unknown.event.EventCancellable;
import party.detection.unknown.util.ASM;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.pmw.tinylog.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * Management of hooking injections.
 * 
 * @author bloo
 * @since 7/13/2017
 */
public enum HookController {
	INSTANCE;

	/**
	 * ASM flags to use for ClassWriters.
	 */
	private final static int FLAGS = 0;
	/**
	 * Map of internal names to their facade classes.
	 */
	private final BiMap<String, Class<?>> classProxies = HashBiMap.create();
	/**
	 * Set of members with mappings.
	 */
	private final Set<MemberData> memberData = new HashSet<>();
	/**
	 * Map of static mapping classes to instances of their handlers.
	 */
	private final Map<Class<?>, Object> handlerCache = new HashMap<>();
	/**
	 * Map of internal names to their list of injected event calls.
	 */
	private final Map<String, List<EventCodeInjection>> eventInjections = new HashMap<>();

	/**
	 * Register proxy mapping from an internal class to a facade class.
	 * 
	 * @param internalName
	 *            Internal class name.
	 * @param proxyClass
	 *            Facade depicting the internal class.
	 */
	public void mapClass(String internalName, Class<?> proxyClass) {
		classProxies.put(internalName, proxyClass);
	}

	/**
	 * Register proxy mapping from an internally defined method to a facade from one
	 * of the {@link #classProxies facades}.
	 * 
	 * @param owner
	 *            Internal class name.
	 * @param name
	 *            Internal class's method name
	 * @param desc
	 *            Internal class's method descriptor
	 * @param id
	 *            Facade method name
	 */
	public void mapMember(String owner, String name, String desc, String id) {
		memberData.add(new MemberData(owner, name, desc, id));
	}

	/**
	 * Insert event call to the defined method.
	 * 
	 * @param internalClassName
	 *            Internal method owner name.
	 * @param name
	 *            Method name.
	 * @param desc
	 *            Method descriptor.
	 * @param pos
	 *            Method opcode offset.
	 * @param eventClass
	 *            Event class to call.
	 * @param locals
	 *            Local variables to push to event.
	 */
	public void addEventInjection(String internalClassName, String name, String desc, int pos,
			Class<? extends Event> eventClass, int[] locals) {
		if (eventClass == null) {
			throw new IllegalStateException(
					"Event for injection of " + internalClassName + "." + name + desc + " @" + pos + " was null.");
		}
		eventInjections.computeIfAbsent(internalClassName, k -> new ArrayList<>())
				.add(new EventCodeInjection(name, desc, pos, eventClass, locals));
	}

	/**
	 * Get or create a handler for managing the injection of static methods to
	 * mapped classes.
	 * 
	 * @param handlerClass
	 *            Static handler class <i>(Denoted by
	 *            {@code @StaticHandler(NonStaticMapping.class)})</i>
	 * @return
	 * @throws InvalidHookException
	 *             Thrown if generation of hooks for the targeted class fails.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getStaticHandler(Class<T> handlerClass) throws InvalidHookException {
		Object x = handlerCache.get(handlerClass);
		if (x != null)
			return (T) x;

		StaticHandler staticHandler = handlerClass.getAnnotation(StaticHandler.class);
		if (staticHandler == null)
			throw new IllegalArgumentException();
		Class<?> proxyClass = staticHandler.value();
		String internClsName = classProxies.inverse().get(proxyClass);
		ClassWriter cw = new ClassWriter(FLAGS);
		String name = "sun/reflect/" + UUID.randomUUID().toString();
		cw.visit(V1_5, ACC_PUBLIC, name, null, "sun/reflect/MagicAccessorImpl",
				new String[] { handlerClass.getName().replace('.', '/') });
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		// Iterate methods in the static mapping class.
		for (Method method : handlerClass.getMethods()) {
			Type returnType = Type.getReturnType(method);
			Type[] argTypes = Type.getArgumentTypes(method);
			// Edge-case for constructors?
			Constructor constructor = method.getAnnotation(Constructor.class);
			if (constructor != null) {
				try {
					Class<?> actualClass = ClassLoader.getSystemClassLoader()
							.loadClass(internClsName.replace('/', '.'));
					search: for (java.lang.reflect.Constructor<?> c : actualClass.getConstructors()) {
						Class<?>[] actualArgTypes = c.getParameterTypes();
						if (actualArgTypes.length == argTypes.length) {
							for (int i = 0; i < argTypes.length; i++) {
								Class<?> actualArgType = actualArgTypes[i];
								if (!isTypeApplicable(actualArgType, argTypes[i])) {
									continue search;
								}
							}
							// we've found a valid constructor
							mv = cw.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null,
									null);
							mv.visitCode();
							mv.visitTypeInsn(NEW, internClsName);
							mv.visitInsn(DUP);
							for (int a = 0, v = 1; a < argTypes.length; a++) {
								Type argType = argTypes[a];
								mv.visitVarInsn(argType.getOpcode(ILOAD), v);
								if (argType.getSort() == Type.OBJECT) {
									mv.visitTypeInsn(CHECKCAST, Type.getType(actualArgTypes[a]).getInternalName());
								} else if (argType.getSort() == Type.ARRAY) {
									// TODO: do array casts too
								}
								v += argType.getSize();
							}
							mv.visitMethodInsn(INVOKESPECIAL, internClsName, "<init>", Type.getConstructorDescriptor(c),
									false);
							mv.visitTypeInsn(CHECKCAST, Type.getReturnType(method).getInternalName());
							mv.visitInsn(ARETURN);
							mv.visitMaxs(2 + argTypes.length, 2 + argTypes.length);
							mv.visitEnd();
							break;
						}
					}
				} catch (ClassNotFoundException e) {
					Logger.error(new InvalidHookException("Class '" + internClsName
							+ "' could not be hooked because it could not be loaded by the system Classloader"));
				}
				continue;
			}

			// Create static getters for static fields belonging to the target class.
			Getter getter = method.getAnnotation(Getter.class);
			if (getter != null) {
				MemberData md = getFieldByName(internClsName, getter.value());
				if (md != null) {
					final String originalName = md.originalName;
					mv = cw.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
					mv.visitCode();
					final String desc = md.desc;
					final String retDesc = returnType.getDescriptor();
					mv.visitFieldInsn(GETSTATIC, internClsName, originalName, desc);
					if ((returnType.getSort() == Type.ARRAY || returnType.getSort() == Type.OBJECT)
							&& !desc.equals(retDesc)) {
						mv.visitTypeInsn(CHECKCAST, returnType.getInternalName());
					}
					mv.visitInsn(returnType.getOpcode(IRETURN));
					mv.visitMaxs(3, 3);
					mv.visitEnd();
				} else {
					Logger.debug("Missing getter for: " + internClsName + "#" + method.getName());
				}
				continue;
			}
			// Create static setters for static fields belonging to the target class.
			Setter setter = method.getAnnotation(Setter.class);
			if (setter != null) {
				MemberData md = getFieldByName(internClsName, setter.value());
				if (md != null) {
					final String originalName = md.originalName;
					mv = cw.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
					mv.visitCode();
					final Type argType = argTypes[0];
					mv.visitVarInsn(argType.getOpcode(ILOAD), 0);
					final String desc = md.desc;
					final String argDesc = argType.getDescriptor();
					if ((returnType.getSort() == Type.ARRAY || returnType.getSort() == Type.OBJECT)
							&& !desc.equals(argDesc)) {
						mv.visitTypeInsn(CHECKCAST, Type.getType(md.desc).getInternalName());
					}
					mv.visitFieldInsn(PUTSTATIC, internClsName, originalName, desc);
					mv.visitInsn(RETURN);
					mv.visitMaxs(3, 3);
					mv.visitEnd();
				} else {
					Logger.debug("Missing setter for: " + internClsName + "#" + method.getName());
				}
				continue;
			}
			// Create static proxy callers for static methods belonging to the target class.
			MethodProxy methodProxy = method.getAnnotation(MethodProxy.class);
			if (methodProxy != null) {
				MemberData md = getMethodByName(internClsName, methodProxy.value());
				if (md != null) {
					final String originalName = md.originalName;
					final String originalDesc = md.desc;
					final Type originalType = Type.getType(md.desc);
					final Type[] originalArgTypes = originalType.getArgumentTypes();
					mv = cw.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null, null);
					mv.visitCode();
					for (int a = 0, v = 1; a < argTypes.length; a++) {
						Type argType = argTypes[a];
						mv.visitVarInsn(argType.getOpcode(ILOAD), v);
						v += argType.getSize();
						if (argType.getSort() == Type.OBJECT) {
							// TODO: do array casts too
							Type originalArgType = originalArgTypes[a];
							mv.visitTypeInsn(CHECKCAST, originalArgType.getInternalName());
						}
					}
					mv.visitMethodInsn(INVOKESTATIC, internClsName, originalName, originalDesc, false);
					if (returnType.getSort() == Type.OBJECT) {
						// TODO: do array casts too
						mv.visitTypeInsn(CHECKCAST, returnType.getInternalName());
					}
					mv.visitInsn(returnType.getOpcode(IRETURN));
					mv.visitMaxs((3 + argTypes.length) << 1, (3 + argTypes.length) << 1);
					mv.visitEnd();
				} else {
					Logger.debug("Missing static proxy caller for: " + internClsName + "#" + method.getName());
				}
			}
		}
		cw.visitEnd();
		Object instance = null;
		try {
			instance = DefinitionLoader.INSTANCE.define(cw.toByteArray()).newInstance();
		} catch (Exception e) {
			throw new InvalidHookException("Failed to define proxy class for: " + internClsName);
		}
		handlerCache.put(handlerClass, instance);
		return (T) instance;
	}

	/**
	 * Insert hooks into the target class given its original bytecode.
	 * 
	 * @param clz
	 *            Original class bytecode.
	 * @return Hooked class containing proxt method getters, setters, callers.
	 * @throws InvalidHookException
	 *             Thrown if hooking could not be completed.
	 */
	public byte[] hook(byte[] clz) throws InvalidHookException {
		clz = doInjections(clz);
		ClassReader cr = new ClassReader(clz);
		ClassWriter cw = new ClassWriter(FLAGS);
		final String internClsName = cr.getClassName();
		final Class<?> proxy = classProxies.get(internClsName);
		if (proxy == null)
			return clz;

		final Method[] proxyMethods = getProxyMethods(proxy, internClsName);
		Logger.info("Injecting facade '" + proxy.getSimpleName() + "' over '" + internClsName + "'");
		cr.accept(new ClassVisitor(ASM5, cw) {
			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				// inject the proxy interface
				String proxyName = proxy.getName().replace('.', '/');
				if (interfaces == null) {
					interfaces = new String[] { proxyName };
				} else {
					int len = interfaces.length;
					String[] replace = new String[len + 1];
					System.arraycopy(interfaces, 0, replace, 0, len);
					replace[len] = proxyName;
					interfaces = replace;
				}
				// upgrade the interfaces to Java at least 8 so that we can use default methods
				super.visit(Modifier.isInterface(access) ? Math.max(V1_8, version) : version, access, name, signature,
						superName, interfaces);
			}

			@Override
			public void visitEnd() {
				MethodVisitor mv;
				for (Method method : proxyMethods) {
					final Type returnType = Type.getReturnType(method);
					final Type[] argTypes = Type.getArgumentTypes(method);
					Getter getter = method.getAnnotation(Getter.class);
					if (getter != null) {
						final String replacementName = getter.value();
						MemberData md = getFieldByName(internClsName, replacementName);
						if (md != null) {
							final String originalName = md.originalName;
							mv = super.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null,
									null);
							mv.visitCode();
							mv.visitVarInsn(ALOAD, 0);
							final String desc = md.desc;
							final String retDesc = returnType.getDescriptor();
							mv.visitFieldInsn(GETFIELD, internClsName, originalName, desc);
							if ((returnType.getSort() == Type.ARRAY || returnType.getSort() == Type.OBJECT)
									&& !desc.equals(retDesc)) {
								mv.visitTypeInsn(CHECKCAST, returnType.getInternalName());
							}
							mv.visitInsn(returnType.getOpcode(IRETURN));
							mv.visitMaxs(3, 3);
							mv.visitEnd();
						} else {
							Logger.debug("Missing getter for: " + internClsName + "#" + method.getName());
						}
						continue;
					}

					Setter setter = method.getAnnotation(Setter.class);
					if (setter != null) {
						final String replacementName = setter.value();
						MemberData md = getFieldByName(internClsName, replacementName);
						if (md != null) {
							final String originalName = md.originalName;
							mv = super.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null,
									null);
							mv.visitCode();
							mv.visitVarInsn(ALOAD, 0);
							final Type argType = argTypes[0];
							mv.visitVarInsn(argType.getOpcode(ILOAD), 1);
							final String desc = md.desc;
							final String argDesc = argType.getDescriptor();
							if ((returnType.getSort() == Type.ARRAY || returnType.getSort() == Type.OBJECT)
									&& !desc.equals(argDesc)) {
								mv.visitTypeInsn(CHECKCAST, Type.getType(md.desc).getInternalName());
							}
							mv.visitFieldInsn(PUTFIELD, internClsName, originalName, desc);
							mv.visitInsn(RETURN);
							mv.visitMaxs(3, 3);
							mv.visitEnd();
						} else {
							Logger.debug("Missing setter for: " + internClsName + "#" + method.getName());
						}
						continue;
					}

					MethodProxy methodProxy = method.getAnnotation(MethodProxy.class);
					if (methodProxy != null) {
						final String replacementName = methodProxy.value();
						MemberData md = getMethodByName(internClsName, replacementName);
						if (md != null) {
							final String originalName = md.originalName;
							final String originalDesc = md.desc;
							final Type originalType = Type.getType(md.desc);
							final Type[] originalArgTypes = originalType.getArgumentTypes();
							mv = super.visitMethod(ACC_PUBLIC, method.getName(), Type.getMethodDescriptor(method), null,
									null);
							mv.visitCode();
							mv.visitVarInsn(ALOAD, 0);

							for (int a = 0, v = 1; a < argTypes.length; a++) {
								Type argType = argTypes[a];
								mv.visitVarInsn(argType.getOpcode(ILOAD), v);
								v += argType.getSize();
								if (argType.getSort() == Type.OBJECT) {
									// TODO: do array casts too
									Type originalArgType = originalArgTypes[a];
									mv.visitTypeInsn(CHECKCAST, originalArgType.getInternalName());
								}
							}

							mv.visitMethodInsn(INVOKEVIRTUAL, internClsName, originalName, originalDesc, false);
							if (returnType.getSort() == Type.OBJECT) {
								// TODO: do array casts too
								mv.visitTypeInsn(CHECKCAST, returnType.getInternalName());
							}
							mv.visitInsn(returnType.getOpcode(IRETURN));
							mv.visitMaxs((3 + argTypes.length) << 1, (3 + argTypes.length) << 1);
							mv.visitEnd();
						} else {
							Logger.debug("Missing method proxy for: " + internClsName + "#" + method.getName());
						}
					}
				}

				super.visitEnd();
			}
		}, 0);

		return cw.toByteArray();
	}

	/**
	 * Get member wrapper for given field.
	 * 
	 * @param internalName
	 *            Field owner name.
	 * @param name
	 *            Field name.
	 * @return Wrapper for field.
	 */
	private MemberData getFieldByName(String internalName, String name) {
		for (MemberData d : memberData) {
			if (d == null)
				continue;
			if (d.owner.equals(internalName) && d.identifier.equals(name) && !d.desc.contains("(")) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Get member wrapper for given method.
	 * 
	 * @param internalName
	 *            Method owner name.
	 * @param name
	 *            Method name.
	 * @return Wrapper for method.
	 */
	private MemberData getMethodByName(String internalName, String name) {
		for (MemberData d : memberData) {
			if (d == null)
				continue;
			if (d.owner.equals(internalName) && d.identifier.equals(name) && d.desc.contains("(")) {
				return d;
			}
		}
		return null;
	}

	/**
	 * Inject event calls into class bytecode.
	 * 
	 * @param clz
	 *            Class bytecode.
	 * @return Hooked bytecode.
	 */
	private byte[] doInjections(byte[] clz) {
		ClassReader cr = new ClassReader(clz);
		String internalClsName = cr.getClassName();
		List<EventCodeInjection> eventInjections = this.eventInjections.get(internalClsName);
		if (eventInjections == null)
			return clz;

		ClassWriter cw = new ClassWriter(FLAGS);
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);

		MethodNode[] methodNodes = new MethodNode[eventInjections.size()];
		AbstractInsnNode[] insertionPoints = new AbstractInsnNode[methodNodes.length];
		for (int i = 0; i < methodNodes.length; i++) {
			EventCodeInjection injection = eventInjections.get(i);
			methodNodes[i] = ASM.getMethod(cn, injection.methodName, injection.desc);
			if (injection.injectionPoint < methodNodes[i].instructions.size()) {
				insertionPoints[i] = methodNodes[i].instructions.get(i);
			}
		}

		for (int i = 0; i < methodNodes.length; i++) {
			MethodNode mn = methodNodes[i];
			EventCodeInjection injection = eventInjections.get(i);
			InsnList insns = new InsnList();
			Class<? extends Event> eventClass = injection.eventClass;
			String eventInternal = eventClass.getName().replace('.', '/');
			insns.add(new TypeInsnNode(NEW, eventInternal));
			insns.add(new InsnNode(DUP));
			int[] loaders = ASM.getLocalLoadInsns(mn);
			for (int j : injection.locals) {
				insns.add(new VarInsnNode(loaders[j], j));
			}
			insns.add(new MethodInsnNode(INVOKESPECIAL, eventInternal, "<init>",
					Type.getConstructorDescriptor(eventClass.getConstructors()[0]), false));
			if (EventCancellable.class.isAssignableFrom(eventClass)) {
				insns.add(new MethodInsnNode(INVOKEVIRTUAL, eventInternal, "fireAndCheckCancelled", "()Z", false));
				LabelNode label = new LabelNode();
				insns.add(new JumpInsnNode(IFEQ, label));
				ASM.appendReturn(insns, mn.desc);
				insns.add(label);
			} else {
				insns.add(new MethodInsnNode(INVOKEVIRTUAL, eventInternal, "fire", "()V", false));
			}
			if (insertionPoints[i] == null) {
				mn.instructions.add(insns);
			} else {
				mn.instructions.insertBefore(insertionPoints[i], insns);
			}
		}
		cn.accept(cw);
		return cw.toByteArray();
	}

	/**
	 * Checks if the given class matches the given type.
	 * 
	 * @param actual
	 * @param target
	 * @return Type can be applied to class.
	 */
	private static boolean isTypeApplicable(Class<?> actual, Type target) {
		if (actual == null)
			return false;
		if (actual.isPrimitive())
			return Type.getType(actual).getSort() == target.getSort();
		if (actual.isArray())
			return target.getSort() == Type.ARRAY
					&& isTypeApplicable(actual.getComponentType(), target.getElementType());
		// if we reach here, we can only be dealing with objects
		if (Type.getType(actual).getInternalName().equals(target.getInternalName()))
			return true;
		if (isTypeApplicable(actual.getSuperclass(), target))
			return true;
		for (Class<?> inter : actual.getInterfaces()) {
			if (isTypeApplicable(inter, target)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Fetch array of method that need to have hooks generated in target class
	 * <i>(Target being the class that the proxy class is a mapping for)</i>.
	 * 
	 * @param proxyClass
	 *            Mapping of target class.
	 * @param internName
	 *            Internal name of target class.
	 * @return Array of methods to make hooks for in the target class.
	 */
	private static Method[] getProxyMethods(Class<?> proxyClass, String internName) {
		List<Method> proxyMethodList = new ArrayList<>(Arrays.asList(proxyClass.getMethods()));
		if (proxyClass.getSuperclass() != null) {
			proxyMethodList.removeAll(Arrays.asList(proxyClass.getSuperclass().getMethods()));
		}
		for (Class<?> ext : proxyClass.getInterfaces()) {
			if (ext != null) {
				proxyMethodList.removeAll(Arrays.asList(ext.getMethods()));
			}
		}
		for (int i = proxyMethodList.size() - 1; i >= 0; i--) {
			Method m = proxyMethodList.get(i);
			if (Modifier.isStatic(m.getModifiers())) {
				proxyMethodList.remove(i);
			}
			// TODO: Ignore methods that are not supported by the loaded version json.
			/*
			 * // Seems like it should work but doesn't. String id = getID(m); MemberData md
			 * = getMethodByName(internName, id); if (md == null) { Logger.info("rem: " +
			 * m); proxyMethodList.remove(i); }
			 */
		}
		return proxyMethodList.toArray(new Method[0]);
	}

	/**
	 * Extract annotation id from given method.
	 * 
	 * @param method
	 * @return Annotation id if it exists.
	 */
	@SuppressWarnings("unused")
	private static String getID(Method method) {
		Getter g = method.getDeclaredAnnotation(Getter.class);
		if (g != null) {
			return g.value();
		}
		Setter s = method.getDeclaredAnnotation(Setter.class);
		if (s != null) {
			return s.value();
		}
		MethodProxy mp = method.getDeclaredAnnotation(MethodProxy.class);
		if (mp != null) {
			return mp.value();
		}
		return null;
	}

	/**
	 * Wrapper of obfuscated member. Contains original and replacement names,
	 * descriptor, and owner data.
	 * 
	 * @author bloo
	 * @since 7/13/2017
	 */
	public static final class MemberData {
		/**
		 * Internal name of class that contains the member.
		 */
		private final String owner;
		/**
		 * Original member definition.
		 */
		private final String originalName, desc;
		/**
		 * Identifier in Json.
		 */
		private final String identifier;

		public MemberData(String owner, String originalName, String desc, String id) {
			this.owner = owner;
			this.originalName = originalName;
			this.desc = desc;
			this.identifier = id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			MemberData that = (MemberData) o;

			if (!owner.equals(that.owner))
				return false;
			if (!originalName.equals(that.originalName))
				return false;
			if (!desc.equals(that.desc))
				return false;
			return identifier.equals(that.identifier);
		}

		@Override
		public int hashCode() {
			int result = owner.hashCode();
			result = 31 * result + originalName.hashCode();
			result = 31 * result + desc.hashCode();
			result = 31 * result + identifier.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return owner + "#[" + originalName + "/" + identifier + "]" + desc;
		}
	}

	/**
	 * ClassLoader used to define classes from byte arrays.
	 * 
	 * @author bloo
	 * @since 7/13/2017
	 */
	private static final class DefinitionLoader extends ClassLoader {
		public static final DefinitionLoader INSTANCE = new DefinitionLoader();

		private DefinitionLoader() {
			super(getSystemClassLoader());
		}

		public Class<?> define(byte[] bytes) {
			return defineClass(new ClassReader(bytes).getClassName().replace('/', '.'), bytes, 0, bytes.length);
		}
	}

	/**
	 * Event injection data wrappers. Contains all neccesary information needed to
	 * insert an event call into the target method.
	 * 
	 * @author bloo
	 * @since 7/13/2017
	 */
	private static final class EventCodeInjection {
		/**
		 * Targeted method definition.
		 */
		private final String methodName, desc;
		/**
		 * Bytecode offset to inject event into.
		 */
		private final int injectionPoint;
		/**
		 * Event to call.
		 */
		private final Class<? extends Event> eventClass;
		/**
		 * Local variables to push to the event.
		 */
		private final int[] locals;

		public EventCodeInjection(String methodName, String desc, int injectionPoint, Class<? extends Event> eventClass,
				int[] locals) {
			this.methodName = methodName;
			this.desc = desc;
			this.injectionPoint = injectionPoint;
			this.eventClass = eventClass;
			this.locals = locals;
		}
	}
}
