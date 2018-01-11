package party.unknown.detection.api;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import party.unknown.detection.util.URLReader;

/**
 * UDP-Store requests. Non-functional <i>(Site API does not exist)</i> but outlines what could be done.
 * 
 * @author GenericSkid
 * @since 12/31/2017
 */
public class Requests {
	private final static String API = "https://unknown.detection.party/api/v1/encryptedData";

	public static APIResponse.Login login() {
		return new APIResponse.Login(new URLReader(API).getJson());
	}

	public static APIResponse.Subscriptions subscription() {
		return new APIResponse.Subscriptions(new URLReader(API + "/subscriptions").getJson());
	}

	public static APIResponse.Library library() {
		return new APIResponse.Library(new URLReader(API + "/library").getJson());
	}

	// TODO: update, get latest version. Allow user to update+restart.

	/**
	 * Base request response wrapper.
	 * 
	 * @author GenericSkid
	 * @since 12/31/2017
	 */
	public static class APIResponse {
		protected final JsonObject json;

		public APIResponse(JsonObject json) {
			this.json = json;
		}

		/**
		 * Login data.
		 * 
		 * @author GenericSkid
		 * @since 12/31/2017
		 */
		public static class Login extends APIResponse {
			public Login(JsonObject json) {
				super(json);
			}

			public String getUsername() {
				return json.get("user").getAsString();
			}

			public String getAuthToken() {
				return json.get("token").getAsString();
			}

			public Instant getExpiry() {
				long milli = json.get("time").getAsLong();
				return Instant.ofEpochMilli(milli);
			}
		}

		/**
		 * Subscription data. For instance if user is allowed to use java or cpp
		 * versions of the client.
		 * 
		 * @author GenericSkid
		 * @since 12/31/2017
		 */
		public static class Subscriptions extends APIResponse {
			private static final int CLIENT_JAVA = 1;
			private static final int CLIENT_CPP = 2;
			private final boolean java, cpp;

			public Subscriptions(JsonObject json) {
				super(json);
				// Read subs array
				JsonArray array = json.get("subs").getAsJsonArray();
				Set<Integer> subs = new HashSet<>();
				for (int i = 0; i < array.size(); i++) {
					subs.add((array.get(i).getAsInt()));
				}
				// Check for containment
				java = subs.contains(CLIENT_JAVA);
				cpp = subs.contains(CLIENT_CPP);
			}

			public boolean hasJavaSub() {
				return java;
			}

			public boolean hasCPPSub() {
				return cpp;
			}
		}

		/**
		 * Library data. Contains all the items the user as access to from the client
		 * store.
		 * 
		 * @author GenericSkid
		 * @since 12/31/2017
		 */
		public static class Library extends APIResponse {
			private final Set<Integer> plugins;

			public Library(JsonObject json) {
				super(json);
				plugins = unmodifiable("plugins");
			}

			private final Set<Integer> unmodifiable(String string) {
				JsonArray array = json.get(string).getAsJsonArray();
				Set<Integer> subs = new HashSet<>();
				for (int i = 0; i < array.size(); i++) {
					subs.add((array.get(i).getAsInt()));
				}
				return Collections.unmodifiableSet(subs);
			}

			/**
			 * @return Set of plugins <i>(Represented as their numerical ID)</i> the user
			 *         has access to.
			 */
			public Set<Integer> getPlugins() {
				return plugins;
			}
		}
	}
}
