package party.unknown.detection.io.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author GenericSkid
 * @since 8/17/2017
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Setting {
	/**
	 * @return  Public name of the setting.
	 */
	public String name();
}
