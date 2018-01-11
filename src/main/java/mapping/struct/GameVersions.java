package mapping.struct;

/**
 * Version data wrapper.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public class GameVersions {
	public GameVersions.Version[] versions;

	public static class Version {
		public String id, url;
	}
}