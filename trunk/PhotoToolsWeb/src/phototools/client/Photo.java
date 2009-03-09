/**
 * Copyright 2008, Timothy J. Stavenger
 */
package phototools.client;

import phototools.client.utility.DoubleFormatter;

/**
 * Store metadata concerning a photo including the focal length, aperture, and
 * focus distance. Calculate the depth of field and hyperfocal distance.
 */
public class Photo {
	private double aperture;
	private Camera camera;
	private double focalLength;
	private double focusDistance;
	private boolean metric;

	/**
	 * Empty constructor - set default values.
	 */
	public Photo() {
		setAperture(0);
		setCamera(new Camera());
		setFocalLength(0);
		setFocusDistance(0);
		setMetric(false);
	}

	/**
	 * Set the focal length, aperture, and focus distance. Default to English
	 * units.
	 * 
	 * @param camera
	 *            Camera which took this photo
	 * @param focalLength
	 *            int focal length
	 * @param aperture
	 *            double aperture
	 * @param focusDistance
	 *            double focus distance
	 */
	public Photo(Camera camera, int focalLength, double aperture,
			double focusDistance) {
		setCamera(camera);
		setFocalLength(focalLength);
		setAperture(aperture);
		setFocusDistance(focusDistance);
		setMetric(false);
	}

	/**
	 * Set the focal length, aperture, focus distance, and metric. A false value
	 * for metric indicates the use of the English system.
	 * 
	 * @param camera
	 *            Camera which took this photo
	 * @param focalLength
	 *            int focal length
	 * @param aperture
	 *            double aperture
	 * @param focusDistance
	 *            double focus distance
	 * @param metric
	 *            boolean true for metric false for English
	 */
	public Photo(Camera camera, int focalLength, double aperture,
			double focusDistance, boolean metric) {
		setCamera(camera);
		setFocalLength(focalLength);
		setAperture(aperture);
		setFocusDistance(focusDistance);
		setMetric(metric);
	}

	/**
	 * Calculate the angle of view for the given frame size measurement and
	 * {@link #getFocalLength()}.
	 * 
	 * @param frameSize
	 *            double frame size in millimeters
	 * @return double angle of view
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/AoV.htm
	 */
	private double calculateAngleOfView(double frameSize) {
		double x = frameSize / (2 * getFocalLength());
		double radians = 2 * Math.atan(x);

		return Math.toDegrees(radians);
	}

	public String formatAngleOfView() {
		String width = DoubleFormatter.round(calculateAngleOfViewHorizontal(),
				2);
		String height = DoubleFormatter
				.round(calculateAngleOfViewVertical(), 2);

		return width + "\u00B0 x " + height + "\u00B0";
	}

	/**
	 * Calculate the diagonal angle of view.
	 * 
	 * @return double diagonal angle of view in degrees
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/AoV.htm
	 */
	public double calculateAngleOfViewDiagonal() {
		return calculateAngleOfView(getCamera().calculateFrameDiagonal());
	}

	/**
	 * Calculate the horizontal angle of view.
	 * 
	 * @return double horizontal angle of view in degrees
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/AoV.htm
	 */
	public double calculateAngleOfViewHorizontal() {
		return calculateAngleOfView(getCamera().getFrameWidth());
	}

	/**
	 * Calculate the vertical angle of view.
	 * 
	 * @return double vertical angle of view in degrees
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/AoV.htm
	 */
	public double calculateAngleOfViewVertical() {
		return calculateAngleOfView(getCamera().getFrameHeight());
	}

	/**
	 * Calculate the distance between the focus distance and the depth of field
	 * near limit.
	 * 
	 * @return double distance before focus distance within depth of field
	 */
	public double calculateDepthOfFieldBefore() {
		return getFocusDistance() - calculateDepthOfFieldNearLimit();
	}

	private String feetOrMeters() {
		if (isMetric()) {
			return " m";
		} else {
			return " ft";
		}
	}

	public String formatDepthOfFieldBefore() {
		return DoubleFormatter.roundDepthOfField(calculateDepthOfFieldBefore(),
				2)
				+ feetOrMeters();
	}

	/**
	 * Calculate the distance between the focus distance and the depth of field
	 * far limit.
	 * 
	 * @return double distance behind focus distance within depth of field
	 */
	public double calculateDepthOfFieldBehind() {
		return calculateDepthOfFieldFarLimit() - getFocusDistance();
	}

	public String formatDepthOfFieldBehind() {
		return DoubleFormatter.roundDepthOfField(calculateDepthOfFieldBehind(),
				2)
				+ feetOrMeters();
	}

	/**
	 * Calculate the depth of field far limit for the current photo.
	 * 
	 * @return double depth of field far limit
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateDepthOfFieldFarLimit() {
		double millimeters = calculateDepthOfFieldFarLimitInMillimeters();

		return convert(millimeters);
	}

	/**
	 * Calculate the depth of field far limit for the current photo.
	 * 
	 * @return double depth of field far limit
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateDepthOfFieldFarLimitInMillimeters() {
		double hyperfocalDistance = calculateHyperfocalDistanceInMillimeters();
		double focusDistance = getFocusDistanceInMillimeters();

		return (focusDistance * (hyperfocalDistance - getAperture()))
				/ (hyperfocalDistance - focusDistance);
	}

	/**
	 * Calculate the depth of field far limit for the current photo.
	 * 
	 * @return double depth of field far limit
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateDepthOfFieldFarLimitInMeters() {
		return calculateDepthOfFieldFarLimitInMillimeters() / 1000D;
	}

	public String formatDepthOfFieldFarLimit() {
		return DoubleFormatter.roundDepthOfField(
				calculateDepthOfFieldFarLimit(), 2)
				+ feetOrMeters();
	}

	/**
	 * Calculate the depth of field near limit for the current photo.
	 * 
	 * @return double depth of field near limit
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateDepthOfFieldNearLimit() {
		double millimeters = calculateDepthOfFieldNearLimitInMillimeters();
		
		return convert(millimeters);
	}

	/**
	 * Calculate the depth of field near limit for the current photo.
	 * 
	 * @return double depth of field near limit
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateDepthOfFieldNearLimitInMillimeters() {
		double hyperfocalDistance = calculateHyperfocalDistanceInMillimeters();
		double focusDistance = getFocusDistanceInMillimeters();

		return (focusDistance * (hyperfocalDistance - getAperture()))
				/ (hyperfocalDistance + focusDistance - (2 * getAperture()));
	}

	/**
	 * Calculate the depth of field near limit for the current photo.
	 * 
	 * @return double depth of field near limit
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateDepthOfFieldNearLimitInMeters() {
		return calculateDepthOfFieldNearLimitInMillimeters() / 1000D;
	}

	public String formatDepthOfFieldNearLimit() {
		return DoubleFormatter.roundDepthOfField(
				calculateDepthOfFieldNearLimit(), 2)
				+ feetOrMeters();
	}

	/**
	 * Calculate the diagonal of the view of view, represented in meters or feet
	 * depending on {@link #isMetric()}. Calculated by using Pythagorean theorem
	 * with the {@link #calculateFieldOfViewVerticalInMillimeters()} and
	 * {@link #calculateFieldOfViewHorizontalInMillimeters()}.
	 * 
	 * @return double field of view diagonal in meters or feet depending on
	 *         {@link #isMetric()}
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	public double calculateFieldOfViewDiagonal() {
		double millimeters = calculateFieldOfViewDiagonalInMillimeters();

		return convert(millimeters);
	}

	/**
	 * Calculate the diagonal of the view of view, represented in meters or feet
	 * depending on {@link #isMetric()}. Calculated by using Pythagorean theorem
	 * with the {@link #calculateFieldOfViewVerticalInMillimeters()} and
	 * {@link #calculateFieldOfViewHorizontalInMillimeters()}.
	 * 
	 * @return double field of view diagonal in millimeters
	 */
	private double calculateFieldOfViewDiagonalInMillimeters() {
		double height = calculateFieldOfViewVerticalInMillimeters();
		double width = calculateFieldOfViewHorizontalInMillimeters();

		return Math.sqrt((height * height) + (width * width));
	}

	public String formatFieldOfView() {
		String width = DoubleFormatter.round(calculateFieldOfViewHorizontal(),
				2);
		String height = DoubleFormatter
				.round(calculateFieldOfViewVertical(), 2);

		return width + feetOrMeters() + " x " + height + feetOrMeters();
	}

	/**
	 * Calculate the horizontal width of the view of view, represented in meters
	 * or feet depending on {@link #isMetric()}.
	 * 
	 * @return double field of view width
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	public double calculateFieldOfViewHorizontal() {
		double millimeters = calculateFieldOfViewHorizontalInMillimeters();

		return convert(millimeters);
	}

	/**
	 * Calculate the horizontal width of the view of view, represented in meters
	 * or feet depending on {@link #isMetric()}.
	 * 
	 * @return double field of view width
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	private double calculateFieldOfViewHorizontalInMillimeters() {
		return getCamera().getFrameWidth()
				/ calculateMagnificationInMillimeters();
	}

	/**
	 * Calculate the vertical height of the view of view, represented in meters
	 * or feet depending on {@link #isMetric()}.
	 * 
	 * @return double field of view height in meters or feet depending on
	 *         {@link #isMetric()}
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	public double calculateFieldOfViewVertical() {
		double millimeters = calculateFieldOfViewVerticalInMillimeters();

		return convert(millimeters);
	}

	/**
	 * Calculate the vertical height of the view of view, represented in meters
	 * or feet depending on {@link #isMetric()}.
	 * 
	 * @return double field of view height in millimeters
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	private double calculateFieldOfViewVerticalInMillimeters() {
		return getCamera().getFrameHeight()
				/ calculateMagnificationInMillimeters();
	}

	/**
	 * Calculate half the hyperfocal distance. This distance is the beginning
	 * point at which the photo will be in focus when the focus distance is
	 * equal to the hyperfocal distance.
	 * 
	 * If using the Metric system, return the hyperfocal distance in meters. If
	 * using the English system, return the hyperfocal distance in feet.
	 * 
	 * @return double half the hyperfocal distance
	 * 
	 * @see #calculateHyperfocalDistanceInMillimeters()
	 */
	public double calculateHalfHyperfocalDistance() {
		double millimeters = calculateHyperfocalDistanceInMillimeters() / 2;

		return convert(millimeters);
	}

	public String formatHalfHyperfocalDistance() {
		return DoubleFormatter.round(calculateHalfHyperfocalDistance(), 2)
				+ feetOrMeters();
	}

	/**
	 * Calculate the hyperfocal distance for the current photo.
	 * 
	 * If using the Metric system, return the hyperfocal distance in meters. If
	 * using the English system, return the hyperfocal distance in feet.
	 * 
	 * @return double hyperfocal distance
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	public double calculateHyperfocalDistance() {
		double millimeters = calculateHyperfocalDistanceInMillimeters();

		return convert(millimeters);
	}

	public String formatHyperfocalDistance() {
		return DoubleFormatter.round(calculateHyperfocalDistance(), 2)
				+ feetOrMeters();
	}

	/**
	 * Calculate the hyperfocal distance in millimeters.
	 * 
	 * @return double hyperfocal distance in millimeters
	 * 
	 * @see http://www.dofmaster.com/equations.html
	 */
	private double calculateHyperfocalDistanceInMillimeters() {
		return ((getFocalLength() * getFocalLength()) / (getAperture() * getCamera()
				.getCircleOfConfusion()))
				+ getFocalLength();
	}

	/**
	 * Calculate the magnification of the lens used to take the photo, given the
	 * focal length and the focus distance. This is used to calculate the field
	 * of view.
	 * 
	 * @return double magnification
	 * 
	 * @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	public double calculateMagnification() {
		double millimeters = calculateMagnificationInMillimeters();

		return convert(millimeters);
	}

	/**
	 * Calculate the magnification of the lens used to take the photo, given the
	 * focal length and the focus distance. This is used to calculate the field
	 * of view.
	 * 
	 * @return double magnification in millimeters
	 * 
	 * @see @see http://www.tangentsoft.net/fcalc/help/FoV.htm
	 */
	private double calculateMagnificationInMillimeters() {
		double focusDistance = getFocusDistanceInMillimeters();

		return getFocalLength() / (focusDistance - getFocalLength());
	}

	/**
	 * Convert millimeters into the selected measurement system.
	 * 
	 * If the Metric system is chosen, convert the millimeters into meters. If
	 * the English system is chosen, convert the millimeters into feet.
	 * 
	 * @param millimeters
	 *            double
	 * @return double measurement in meters or feet
	 */
	private double convert(double millimeters) {
		double result;

		if (isMetric()) {
			result = millimeters / 1000;
		} else {
			result = millimeters * 0.0032808399;
		}

		return result;
	}

	/**
	 * @return the aperture
	 */
	public double getAperture() {
		return aperture;
	}

	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * @return the focalLength in millimeters
	 */
	public double getFocalLength() {
		return focalLength;
	}

	/**
	 * @return the focusDistance
	 */
	public double getFocusDistance() {
		return focusDistance;
	}
	
	public double getFocusDistanceInMeters() {
		if (isMetric()) {
			return getFocusDistance();
		}
		else {
			return getFocusDistance() * 0.3048D;
		}
	}

	/**
	 * Get the focus distance in millimeters. If using the Metric system,
	 * convert the meters into millimeters. If using the English system, convert
	 * the feet into millimeters.
	 * 
	 * @return double focus distance in millimeters
	 */
	private double getFocusDistanceInMillimeters() {
		double millimeters;

		if (isMetric()) {
			millimeters = getFocusDistance() * 1000;
		} else {
			millimeters = getFocusDistance() * 304.8;
		}

		return millimeters;
	}

	/**
	 * @return the metric
	 */
	public boolean isMetric() {
		return metric;
	}

	/**
	 * @param aperature
	 *            the aperture to set
	 */
	public void setAperture(double aperture) {
		this.aperture = aperture;
	}

	/**
	 * @param camera
	 *            the camera to set
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * @param focalLength
	 *            the focalLength to set in millimeters
	 */
	public void setFocalLength(double focalLength) {
		this.focalLength = focalLength;
	}

	/**
	 * @param focusDistance
	 *            the focusDistance to set
	 */
	public void setFocusDistance(double focusDistance) {
		this.focusDistance = focusDistance;
	}

	/**
	 * @param metric
	 *            the metric to set
	 */
	public void setMetric(boolean metric) {
		this.metric = metric;
	}
}