package party.detection.unknown.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.pmw.tinylog.Logger;

import party.detection.unknown.util.OS;

/**
 * Misc IO utility functions.
 * 
 * @author GenericSkid
 * @since 1/16/2018
 */
public class IOUtil {
	/**
	 * Reads the contents of the given file into a string.
	 * 
	 * @param path
	 *            Path to file.
	 * @return Contents of file at path.
	 */
	public static String readAllLines(String path) {
		return readAllLines(new File(path));
	}

	/**
	 * Reads the contents of the given file into a string.
	 * 
	 * @param file
	 *            File to read.
	 * @return Contents of file.
	 */
	public static String readAllLines(File file) {
		String content = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null)
				content = content + "\n" + line;
			br.close();
		} catch (IOException e) {
			Logger.error(e, "Could not read contents from: " + file.getName());
		}
		return content;
	}

	/**
	 * Fetch a file that matches the given filter.
	 * 
	 * @param root
	 * @param filter
	 * @return
	 */
	public static File getFirstFile(File root, Predicate<File> filter) {
		return getFiles(root, filter).get(0);
	}

	/**
	 * Fetch a file that matches the given filter.
	 * 
	 * @param root
	 * @param filter
	 * @return
	 */
	public static File getLatestFile(File root, Predicate<File> filter) {
		List<File> l = getFiles(root, filter);
		if (l.size() == 0)
			return null;
		l.sort(new Comparator<File>() {
			@Override
			public int compare(File f1, File f2) {
				long l1 = f1.lastModified();
				long l2 = f2.lastModified();
				return Long.compare(l1, l2);
			}
		});
		return l.get(0);

	}

	/**
	 * Fetch all files in the given root directory that match the given filter.
	 * 
	 * @param root
	 * @param filter
	 * @return
	 */
	public static List<File> getFiles(File root, Predicate<File> filter) {
		List<File> l = new ArrayList<>();
		for (File f : root.listFiles()) {
			if (filter.test(f))
				l.add(f);
			if (f.isDirectory())
				l.addAll(getFiles(f, filter));
		}
		return l;
	}

	/**
	 * Check if the given directory <i>(Non-recursive)</i> contains the given file
	 * names.
	 * 
	 * @param root
	 *            Directory to check.
	 * @param files
	 *            Required file names.
	 * @return {@code true} if the directory contains all of the given files.
	 */
	public static boolean contains(File root, String... files) {
		Set<String> names = new HashSet<>();
		for (File f : root.listFiles()) {
			names.add(f.getName());
		}
		for (String file : files) {
			if (!names.contains(file)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return Cross-platform Minecraft install directory.
	 */
	public static File getMCDir() {
		final String userHome = System.getProperty("user.home", ".");
		File workingDirectory = null;
		switch (OS.getPlatform()) {
		case LINUX:
		case SOLARIS:
			workingDirectory = new File(userHome, String.valueOf('.') + "minecraft" + File.separator);
			break;
		case WINDOWS:
			final String applicationData = System.getenv("APPDATA");
			if (applicationData != null) {
				workingDirectory = new File(applicationData, "." + "minecraft" + File.separator);
				break;
			}
			workingDirectory = new File(userHome, String.valueOf('.') + "minecraft" + File.separator);
			break;
		case MACOS:
			workingDirectory = new File(userHome, "Library/Application Support" + "minecraft");
			break;
		default:
			workingDirectory = new File(userHome, String.valueOf("minecraft") + File.separator);
			break;
		}
		return workingDirectory;
	}

	public static byte[] readBytes(InputStream is) throws IOException {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			return baos.toByteArray();
		}
	}
}
