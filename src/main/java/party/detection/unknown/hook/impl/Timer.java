package party.detection.unknown.hook.impl;

import party.detection.unknown.hook.Getter;
import party.detection.unknown.hook.Setter;

/**
 * @author GenericSkid
 * @since 8/10/2017
 */
public interface Timer {
	/**
	 * Tick speed modifier, typically {@code 1.0f}.
	 * 
	 * Not in {@code 1.12+}.
	 * 
	 * @return Speed modifier.
	 */
	@Getter("a")
	float getSpeed();

	/**
	 * Set tick speed modifier, typically {@code 1.0f}.
	 * 
	 * Not in {@code 1.12+}.
	 * 
	 * @param value
	 *            Speed to set.
	 */
	@Setter("a")
	void setSpeed(float value);

	/**
	 * Get elapsed ticks.
	 * 
	 * @return How much time has elapsed since the last tick, in ticks.
	 */
	@Getter("b")
	float getElapsedPartialTicks();

	/**
	 * Set elapsed ticks.
	 * 
	 * @param f
	 *            How much time has elapsed since the last tick, in ticks.
	 */
	@Setter("b")
	void setElapsedPartialTicks(float f);

	/**
	 * Number of ticks per second. May be used as an alternative to
	 * {@link #getSpeed()} for {@code 1.12+}.
	 * 
	 * @return
	 */
	@Getter("c")
	float getTPS();

	/**
	 * nSet the Number of ticks per second. May be used as an alternative to
	 * {@link #getSpeed()} for {@code 1.12+}.
	 * 
	 * @param value
	 *            TPS to set.
	 */
	@Setter("c")
	void setTPS(float value);
}
