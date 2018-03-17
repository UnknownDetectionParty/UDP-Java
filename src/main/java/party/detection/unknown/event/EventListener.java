package party.detection.unknown.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author bloo & GenericSkid
 * @since 7/30/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
	/**
	 * Priority of the event subscription. Higher values are called before lower
	 * values.
	 * 
	 * @return
	 */
	public int priority() default 0;
}
