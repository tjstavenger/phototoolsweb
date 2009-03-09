/**
 * 
 */
package phototools.client;

/**
 * @author tstavenger
 * 
 */
public class Aperture {
	private static final double FULL_STOP = 1D;
	private static final double HALF_STOP = 0.5D;
	private static final double THIRD_STOP = 1D / 3D;

	private static final double[] FULL_STEPS = { 1.0, 1.4, 2, 2.8, 4, 5.6, 8,
			11, 16, 22, 32 };
	private static final double[] HALF_STEPS = { 1.0, 1.2, 1.4, 1.7, 2, 2.4,
			2.8, 3.3, 4, 4.8, 5.6, 6.7, 8, 9.5, 11, 13, 16, 19, 22, 27, 32 };
	private static final double[] THIRD_STEPS = { 1.0, 1.1, 1.2, 1.4, 1.6, 1.8,
			2, 2.2, 2.5, 2.8, 3.2, 3.5, 4, 4.5, 5.0, 5.6, 6.3, 7.1, 8, 9, 10,
			11, 13, 14, 16, 18, 20, 22, 25, 29, 32 };

	private double stopMultiplier;

	public Aperture() {
		setThirdStop();
	}

	public double calculateAperture(int step) {
		return Math.pow(2, (step * getStopMultiplier() * 0.5));
	}

	public double[] getFormattedApertures() {
		if (isFullStop()) {
			return FULL_STEPS;
		} else if (isHalfStop()) {
			return HALF_STEPS;
		} else {
			return THIRD_STEPS;
		}
	}

	/**
	 * @return the stopMultiplier
	 */
	protected final double getStopMultiplier() {
		return stopMultiplier;
	}

	public boolean isFullStop() {
		return FULL_STOP == getStopMultiplier();
	}

	public boolean isHalfStop() {
		return HALF_STOP == getStopMultiplier();
	}

	public boolean isThirdStop() {
		return THIRD_STOP == getStopMultiplier();
	}

	public void setFullStop() {
		setStopMultiplier(FULL_STOP);
	}

	public void setHalfStop() {
		setStopMultiplier(HALF_STOP);
	}

	/**
	 * @param stopMultiplier
	 *            the stopMultiplier to set
	 */
	protected final void setStopMultiplier(double stopMultiplier) {
		this.stopMultiplier = stopMultiplier;
	}

	public void setThirdStop() {
		setStopMultiplier(THIRD_STOP);
	}
}