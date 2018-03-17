package party.detection.unknown.util;

import java.lang.management.ManagementFactory;

/**
 * Utility class for comparing Minecraft version strings. 
 *
 * @author bloo
 * @since Jun 24, 2015
 */
public final class MCVersion {

	public static boolean isBeta(String ver) {
		return ver.charAt(0) == 'b';
	}

	public static boolean isAlpha(String ver) {
		return ver.charAt(0) == 'a';
	}

	public static boolean isOfficialRelease(String ver) {
		char c = ver.charAt(0);
		return '0' <= c && c <= '9' && ver.indexOf('w') == -1;
	}

	public static boolean isSnapshot(String ver) {
		return ver.length() > 3 && ver.charAt(2) == 'w';
	}

	/**
	 * @return <0 if ver1 < ver2, >0 if ver1 > ver2, 0 if ver1 == ver2
	 */
	public static int compare(String ver1, String alternativeToVer1, String ver2) {
		if (ver2.equals(ver1)) {
			return 0;
		}

		int s1 = s(ver1), s2 = s(ver2);

		boolean o1 = isOfficialRelease(ver1) || isSnapshot(ver1);
		boolean o2 = isOfficialRelease(ver2) || isSnapshot(ver2);

		if (s1 > s2) {
			return 1;
		} else if (s1 < s2) {
			return -1;
		}

		if (o2 && o1) {
			if (isOfficialRelease(ver1) && isOfficialRelease(ver2)) {
				return compareVersionThorough(ver1, ver2);
			} else if (isSnapshot(ver1) && isSnapshot(ver2)) {
				return ver1.compareTo(ver2);
			} else if (isSnapshot(ver2)) {
				return alternativeToVer1.compareTo(ver2);
			} else {
				return compareVersionThorough(alternativeToVer1, ver2);
			}
		} else { // comparison is much easier down here.
			//untested for In(f)dev

			return ver1.replace('_', '-').compareTo(ver2.replace('_', '-'));// fixes some orderings
		}
	}

	private static int s(String s) {
		if (isAlpha(s)) {
			return 1;
		} else if (isBeta(s)) {
			return 2;
		} else if (isOfficialRelease(s) || isSnapshot(s)) {
			return 3;
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static int compare(String ver1, String ver2) {
		return compare(ver1, "", ver2);
	}

	public static int compareVersionThorough(String ver1,
											 String ver2) { // for comparing things like 1.7.10
		int layer = 0;
		String[] p1 = ver1.split("\\.");
		String[] p2 = ver2.split("\\.");
		int c;
		while ((c = Integer.parseInt(p1[layer]) - Integer.parseInt(p2[layer])) == 0) {
			layer++;
			if (layer == p1.length) {
				return -1;
			}
			if (layer == p2.length) {
				return 1;
			}
		}
		return c;
	}

	public static String getGameVersion() {
		return ManagementFactory.getRuntimeMXBean().getClassPath().replace('\\', '/')
				.split("/\\.minecraft/versions/")[1].split("/")[0];
	}
}
