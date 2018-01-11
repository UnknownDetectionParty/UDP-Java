package party.unknown.detection.util;

/**
 * Utility for generating and getting data from integer colors.
 * 
 * @author GenericSkid
 * @since 1/3/2018
 */
public class Colors {
	public static int getColor(int red, int green, int blue) {
		return getColor(red, green, blue, 255);
	}

	/**
	 * Encode given colors into a single integer.
	 * 
	 * @param red
	 *            Inclusive value between 0-255.
	 * @param green
	 *            Inclusive value between 0-255.
	 * @param blue
	 *            Inclusive value between 0-255.
	 * @param alpha
	 *            Inclusive value between 0-255.
	 * @return Encoded color integer.
	 */
	public static int getColor(int red, int green, int blue, int alpha) {
		int color = 0;
		color |= alpha << 24;
		color |= red << 16;
		color |= green << 8;
		color |= blue;
		return color;
	}

	/**
	 * Extracts the alpha value from the given color integer.
	 * 
	 * @param color
	 *            Encoded color integer.
	 * @return Alpha <i>(transparency)</i>.
	 */
	public static double getAlpha(int color) {
		return ((double) ((color >> 24 & 0xff) / 255F));
	}

	/**
	 * Extracts the red value from the given color integer.
	 * 
	 * @param color
	 *            Encoded color integer.
	 * @return Red.
	 */
	public static double getRed(int color) {
		return ((double) ((color >> 16 & 0xff) / 255F));
	}

	/**
	 * Extracts the green value from the given color integer.
	 * 
	 * @param color
	 *            Encoded color integer.
	 * @return Green.
	 */
	public static double getGreen(int color) {
		return ((double) ((color >> 8 & 0xff) / 255F));
	}

	/**
	 * Extracts the blue value from the given color integer.
	 * 
	 * @param color
	 *            Encoded color integer.
	 * @return Blue.
	 */
	public static double getBlue(int color) {
		return ((double) ((color & 0xff) / 255F));
	}
}