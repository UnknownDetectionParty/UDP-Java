package party.detection.unknown.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Used for online integration with JSON responses.
 * 
 * @author GenericSkid
 * @since 12/31/2017
 */
public class URLReader {
	private final String url;
	private final ResponseType responseType;
	private JsonObject json;

	public URLReader(String url) {
		this.url = url;
		this.responseType = setup();
	}

	/**
	 * Reads the content from the URL, returning the response type and parsing the
	 * {@link #getJson() JSON} in the process.
	 * 
	 * @return {@link #getResponseType() Response type}.
	 */
	private ResponseType setup() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(getURL()).openStream()))) {
			StringBuilder text = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				text.append(line);
			}
			json = new Gson().fromJson(text.toString(), JsonObject.class);
			return ResponseType.SUCCESS;
		} catch (MalformedURLException e) {
			return ResponseType.FAILED_MALFORMED_URL;
		} catch (IOException e) {
			return ResponseType.FAILED_IO;
		}
	}

	/**
	 * Response type. Possible values are SUCCESS and misc. fail reasons.
	 * 
	 * @return Response type.
	 */
	public ResponseType getResponseType() {
		return responseType;
	}

	/**
	 * @return URL connected to.
	 */
	public String getURL() {
		return url;
	}

	/**
	 * @return JSON representation of data fetched from {@link #getURL() the url}.
	 */
	public JsonObject getJson() {
		return json;
	}

	static enum ResponseType {
		SUCCESS, FAILED_IO, FAILED_MALFORMED_URL;
	}
}
