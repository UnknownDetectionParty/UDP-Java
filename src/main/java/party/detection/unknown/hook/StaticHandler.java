package party.detection.unknown.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation applied to mapping classes that contain hooks for static methods.
 * 
 * @author bloo
 * @since 7/13/2017
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticHandler {
	/**
	 * @return proxy class
	 */
	Class<?> value();
}
