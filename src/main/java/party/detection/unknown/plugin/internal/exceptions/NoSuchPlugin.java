package party.detection.unknown.plugin.internal.exceptions;

public class NoSuchPlugin extends RuntimeException {
	private final Class<?> offendingClass;

	public NoSuchPlugin(Class<?> offendingClass) {
		this.offendingClass = offendingClass;
	}

	/**
	 * @return The class that could not be found in the PluginFactory loaded plugins
	 *         set.
	 */
	public Class<?> getOffendingClass() {
		return offendingClass;
	}
}
