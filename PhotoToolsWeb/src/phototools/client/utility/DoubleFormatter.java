/**
 * 
 */
package phototools.client.utility;

/**
 * @author tstavenger
 * 
 */
public class DoubleFormatter {

	/**
	 * Infinity for the photo calculations correlates to a negative number.
	 * 
	 * @param number double to check
	 * @return boolean true if number represents infinity
	 */
	private static boolean isInfinity(double number) {
		return number < 0;
	}
	
	/**
	 * Format the double into a String with the given digits of precision.
	 * 
	 * @param value
	 *            double
	 * @return String
	 */
	public static String round(double value, int precision) {
		double rounded = value + ((1D / Math.pow(10D, precision)) / 2D);
		String string = String.valueOf(rounded);

		String result;

		if (precision == 0) {
			result = string.substring(0, string.indexOf("."));
		} else {
			result = string.substring(0, string.indexOf(".") + (precision + 1));
		}

		return result;
	}

	public static String roundDepthOfField(double value, int precision) {
		String result;

		if (value >= 0) {
			result = round(value, precision);
		} else {
			result = "\u221e";
		}

		return result;
	}

	public static String truncate(double value, int precision) {
		String string = String.valueOf(value);
		String result;

		if (precision <= 0) {
			result = string.substring(0, string.indexOf("."));
		} else {
			result = string.substring(0, string.indexOf(".") + (precision + 1));
		}

		return result;
	}

	/**
	 * Cannot instantiate, use static methods
	 */
	private DoubleFormatter() {
		super();
	}
}
