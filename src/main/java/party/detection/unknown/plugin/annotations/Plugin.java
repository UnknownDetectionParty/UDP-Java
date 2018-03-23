package party.detection.unknown.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Plugin data applied to each plugin class.
 * 
 * @author bloo
 * @since 8/14/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Plugin {
	String name();

	String description();

	String author();

	String[] versions();
}
