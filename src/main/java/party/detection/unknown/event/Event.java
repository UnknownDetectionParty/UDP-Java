package party.detection.unknown.event;

/**
 * The basic event.
 * 
 * @author bloo
 * @since 8/3/2017
 */
public abstract class Event {
	/**
	 * Convenience method for {@code EventManager.INSTANCE.fire(event);}
	 */
	public void fire() {
		EventManager.INSTANCE.invoke(this);
	}
}
