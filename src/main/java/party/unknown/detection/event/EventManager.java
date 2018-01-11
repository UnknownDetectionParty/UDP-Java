package party.unknown.detection.event;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.pmw.tinylog.Logger;

/**
 * Event manager with efficient caching and priority-sorted event invocation.
 * 
 * @author GenericSkid
 * @since 6/18/17
 */
public enum EventManager {
	INSTANCE;
	/**
	 * Comparator used for sorting methods <i>(Wrapped)</i> by event-listening
	 * priority. Identity hashcode is appended to prevent same-priority events
	 * conflicting.
	 */
	private static final Comparator<MethodWrapper> METHOD_WRAPPER_COMPARE = Comparator
			.<MethodWrapper, Integer>comparing(EventManager::getListenPriority).thenComparing(System::identityHashCode);
	/**
	 * Map of event types to their handlers. Priority of events is per-method only.
	 * The declaring class of a method plays no role.
	 */
	private final Map<Class<? extends Event>, EventTypeHandler> typeToHandlers = new ConcurrentHashMap<>();
	/**
	 * Map of classes to their declared methods that are viable for event-listening.
	 * Used as a cache so re-registering a class for events doesn't have to do all
	 * the same checks over again.
	 */
	private final Map<Class<?>, MethodCache> classToCache = new ConcurrentHashMap<>();

	/**
	 * Register an object for event-listening. To register a method to an event,
	 * apply a '@EventListener' annotation with the type of event to receive <i>(And
	 * optionally its priority relative to other methods receiving the same type.
	 * Higher values are invoked first)</i>. An example declaration would look like:
	 * 
	 * <pre>
	&#64;EventListener( priority = 32)
	void myMethod(SomeEvent event) {
	    ...
	}
	 * </pre>
	 * 
	 * See {@link #unregister(Object)} for unregistering an object from events.
	 * 
	 * @param instance
	 *            Object to register for events.
	 */
	public void register(Object instance) {
		MethodCache cache = isCached(instance) ? getCache(instance) : createCache(instance);
		for (Method method : cache.getMethods()) {
			Class<? extends Event> eventType = cache.getEventType(method);
			getHandler(eventType).register(cache.getWrapper(method));
		}
	}

	/**
	 * Unregister an object for event-listening. See {@link #register(Object)} for
	 * registering an object for events.
	 * 
	 * @param instance
	 *            Object to unregister events from.
	 */
	public void unregister(Object instance) {
		MethodCache cache = isCached(instance) ? getCache(instance) : createCache(instance);
		for (Method method : cache.getMethods()) {
			Class<? extends Event> eventType = cache.getEventType(method);
			getHandler(eventType).unregister(cache.getWrapper(method));
		}
	}

	/**
	 * Sends an event to all objects registered to its type.
	 * 
	 * @param event
	 *            Event to send to listeners.
	 */
	public void invoke(Event event) {
		try {
			getHandler(event.getClass()).invoke(event);
		} catch (Exception e) {
		}
	}

	/**
	 * Create a handler associated with the given event type.
	 * 
	 * @param eventType
	 *            Event Type.
	 * @return EventTypeHandler for event type.
	 */
	private EventTypeHandler createHandler(Class<? extends Event> eventType) {
		EventTypeHandler handler = null;
		this.typeToHandlers.put(eventType, handler = new EventTypeHandler());
		return handler;
	}

	/**
	 * Retrieve the handler associated with the given event type.
	 * 
	 * @param eventType
	 *            Event Type.
	 * @return EventTypeHandler for event type.
	 */
	private EventTypeHandler getHandler(Class<? extends Event> eventType) {
		return this.typeToHandlers.get(eventType);
	}

	/**
	 * Check if the given event type has a handler.
	 * 
	 * @param eventType
	 * @return EventTypeHandler for type exists.
	 */
	private boolean isHandled(Class<? extends Event> eventType) {
		return this.typeToHandlers.containsKey(eventType);
	}

	/**
	 * Create a cache for methods in the given object instance.
	 * 
	 * @param instance
	 *            Object with methods to register for events.
	 * @return MethodCache for the given object.
	 */
	private MethodCache createCache(Object instance) {
		Class<?> classKey = instance.getClass();
		// Create new cache and insert it into the map
		MethodCache cache = null;
		this.classToCache.put(classKey, cache = new MethodCache());
		// Populate cache with methods receiving events
		for (Method method : classKey.getDeclaredMethods()) {
			// Check if method has event-listener
			Class<? extends Event> eventType = getEventType(method);
			if (eventType != null) {
				// Create method wrapper
				cache.cacheMethod(method, instance, eventType);
				// Create or get handler for the event type
				if (!isHandled(eventType)) {
					createHandler(eventType);
				}
			}
		}
		return cache;
	}

	/**
	 * Retrieve the cache for methods in the given object instance.
	 * 
	 * @param instance
	 *            Object with methods to register for events.
	 * @return MethodCache for the given object.
	 */
	private MethodCache getCache(Object instance) {
		return this.classToCache.get(instance.getClass());
	}

	/**
	 * Check if a given object has had its methods cached.
	 * 
	 * @param instance
	 *            Object with methods to register for events.
	 * @return True if object has cached methods. False otherwise.
	 */
	private boolean isCached(Object instance) {
		return this.classToCache.containsKey(instance.getClass());
	}

	/**
	 * Ensures that any cached objects for the given instance are deleted.
	 * 
	 * @param instance
	 */
	public void destroyCache(Object instance) {
		Class<?> classKey = instance.getClass();
		if (this.classToCache.containsKey(classKey)) {
			this.classToCache.remove(classKey);
		}
	}

	/**
	 * Assuming a method has a {@link me.lpk.event.Subscribe} annotation, find the
	 * type of the {@link me.lpk.event.Subscribe#event() event type}.
	 * 
	 * @param method
	 *            Method with the annotation.
	 * @return Value of priority.
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends Event> getEventType(Method method) {
		if (method.getParameterTypes().length != 1) {
			return null;
		}
		Class<?> paramType = method.getParameterTypes()[0];
		if (Event.class.isAssignableFrom(paramType)) {
			return (Class<? extends Event>) paramType;
		}
		return null;
	}

	/**
	 * Assuming a method <i>(which is contained by the wrapper)</i> has a
	 * {@link me.lpk.event.Subscribe} annotation, find the value of the
	 * {@link me.lpk.event.Subscribe#priority() priority}.
	 * 
	 * @param method
	 *            Warapped method with the annotation.
	 * @return Value of priority.
	 */
	private static int getListenPriority(MethodWrapper wrapper) {
		return wrapper.priority;
	}

	// -----------------------------------------------------------------------------

	/**
	 * Handler for which events should be invoked per event-type and in which order
	 * the methods should be invoked.
	 * 
	 * @author GenericSkid
	 * @since 6/18/17
	 */
	private class EventTypeHandler {
		/**
		 * List that auto-sorts method wrappers on insertion.
		 */
		private final ConcurrentSkipListSet<MethodWrapper> listeners = new ConcurrentSkipListSet<>(
				METHOD_WRAPPER_COMPARE);

		/**
		 * Register method for events.
		 * 
		 * @param method
		 */
		public void register(MethodWrapper method) {
			listeners.add(method);
		}

		/**
		 * Unregister method for events
		 * 
		 * @param method
		 */
		public void unregister(MethodWrapper method) {
			listeners.remove(method);
		}

		/**
		 * Passing events to registered methods.
		 * 
		 * @param event
		 */
		public void invoke(Event event) {
			for (MethodWrapper wrapper : listeners) {
				wrapper.invoke(event);
			}
		}
	}

	// -----------------------------------------------------------------------------

	/**
	 * Cache of methods for a class.
	 * 
	 * @author GenericSkid
	 * @since 6/18/17
	 */
	private class MethodCache {
		private final Map<Method, MethodWrapper> methodToWrappers = new ConcurrentHashMap<>();
		private final Map<Method, Class<? extends Event>> methodToType = new ConcurrentHashMap<>();

		public MethodWrapper cacheMethod(Method method, Object instance, Class<? extends Event> eventType) {
			MethodWrapper wrapper;
			this.methodToType.put(method, eventType);
			this.methodToWrappers.putIfAbsent(method, wrapper = new MethodWrapper(method, instance));
			return wrapper;
		}

		public MethodWrapper getWrapper(Method method) {
			return methodToWrappers.get(method);
		}

		public Class<? extends Event> getEventType(Method method) {
			return this.methodToType.get(method);
		}

		public Set<Method> getMethods() {
			return this.methodToType.keySet();
		}
	}

	// -----------------------------------------------------------------------------

	/**
	 * Wrapper for quick reflection invocation of methods.
	 * 
	 * @author GenericSkid
	 * @since 6/18/17
	 */
	private class MethodWrapper implements Comparable<MethodWrapper> {
		private final Method method;
		private final Object instance;
		private final int priority;

		public MethodWrapper(Method method, Object instance) {
			this.method = method;
			this.instance = instance;
			this.priority = method.getDeclaredAnnotation(EventListener.class).priority();
		}

		public void invoke(Event event) {
			// TODO: There should be a better way to handle this.
			//
			// But lambdas literally won't compile when wrapped in try-catched
			// catching non-runtime exceptions. Should I just throw a
			// RuntimeException or a wrapper for the exception thrown that
			// extends RuntimeException?
			try {
				method.invoke(instance, event);
			} catch (Exception e) {
				Logger.error(e, "Could not invoke method: " + instance.getClass().getName() + "#" + method.getName());
			}
		}

		@Override
		public int compareTo(MethodWrapper other) {
			return Integer.compare(priority, other.priority);
		}
	}
}