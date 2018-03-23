package party.detection.unknown.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import party.detection.unknown.plugin.PluginPack;

/**
 * Group data applied to any class in a plugin jar. Used to identify the plugin
 * collection as a singular entity.
 * 
 * @author GenericSkid
 * @since 3/22/2018
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface PluginGroup {
	/**
	 * Unique plugin-pack ID. This can be found via the store page.
	 * 
	 * @return
	 */
	String value() default PluginPack.LocalTestID;
}
