package party.detection.unknown.event;

/**
 * @author bloo
 * @since 7/25/2017
 */
public class EventCancellable extends Event {
	private boolean cancelled;

	public final void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean fireAndCheckCancelled() {
		super.fire();
		return isCancelled();
	}

	public final boolean isCancelled() {
		return this.cancelled;
	}
}
