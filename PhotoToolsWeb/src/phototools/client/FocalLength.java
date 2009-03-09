/**
 * 
 */
package phototools.client;

/**
 * @author tstavenger
 *
 */
public class FocalLength {
	private static final int[] FOCAL_LENGTHS = { 17, 18, 19, 20, 21, 22, 23,
		24, 25, 26, 27, 28, 29, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80,
		85, 90, 95, 100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150,
		155, 160, 165, 170, 175, 180, 185, 190, 195, 200 };
	
	public static int[] getFocalLengths() {
		return FOCAL_LENGTHS;
	}
	
	public static int selectFocalLength(int index) {
		return FOCAL_LENGTHS[index];
	}
}
