package party.unknown.detection.util;

public class OS {
	public static Platform getPlatform() {
		final String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			return Platform.WINDOWS;
		}
		if (osName.contains("mac")) {
			return Platform.MACOS;
		}
		if (osName.contains("solaris")) {
			return Platform.SOLARIS;
		}
		if (osName.contains("sunos")) {
			return Platform.SOLARIS;
		}
		if (osName.contains("linux")) {
			return Platform.LINUX;
		}
		if (osName.contains("unix")) {
			return Platform.LINUX;
		}
		return Platform.UNKNOWN;
	}

	public enum Platform {
		LINUX, SOLARIS, WINDOWS, MACOS, UNKNOWN;
	}
}