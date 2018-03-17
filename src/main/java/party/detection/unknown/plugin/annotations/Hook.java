package party.detection.unknown.plugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import party.detection.unknown.plugin.HookType;

/**
 * @author bloo
 * @since 8/14/2017
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hook {
	int id() default -1;

	Method[] method() default {};

	HookType type();

	int offset() default -1;

	String[] versions() default {};

	int[] locals() default {};
}
