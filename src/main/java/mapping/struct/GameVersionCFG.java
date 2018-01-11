package mapping.struct;

/**
 * Version config data wrapper.
 * 
 * @author bloo
 * @since 8/13/2017
 */
public class GameVersionCFG {
	public GameVersionCFG.Downloads downloads;

	public static class Downloads {
		public Downloads.Client client;

		public static class Client {
			public String url;
		}
	}
}