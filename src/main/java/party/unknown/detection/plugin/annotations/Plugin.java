package party.unknown.detection.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import party.unknown.detection.plugin.PluginData;

/**
 * @author bloo
 * @since 8/14/2017
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Plugin {
	/**
	 * Unique plugin ID. This can be found via the store page.
	 * @return
	 */
	String id() default PluginData.LocalTestID;

	String name();
	
	String description();
	
	String author();

	String[] versions();
}
