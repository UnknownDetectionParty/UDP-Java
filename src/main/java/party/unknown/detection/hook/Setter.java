package party.unknown.detection.hook;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotation used to indicate a setter for some field in a targeted
 * class.
 * 
 * @author bloo
 * @since 7/13/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Setter {
	/**
	 * @return member name
	 */
	String value();
}
