/**
 * Copyright 2008, Timothy J. Stavenger
 */
package phototools.client;

/**
 * Store data concerning a camera including the model name and circle of
 * confusion.
 */
public class Camera {
	private String model;
	private double circleOfConfusion;
	private double frameWidth;
	private double frameHeight;

	/**
	 * Empty constructor - set default values for the model name and circle of
	 * confusion.
	 */
	public Camera() {
		setModel("Canon APS-C");
		setCircleOfConfusion(0.019);
		setFrameWidth(22.2);
		setFrameHeight(14.8);
	}

	/**
	 * @return all possible cameras
	 * 
	 * @see Sensor sizes taken from http://en.wikipedia.org/wiki/Image_sensor_format#Table_of_sensor_sizes
	 * @see Circle of confusion taken from http://www.dofmaster.com/dofjs.html
	 */
	public static final Camera[] getCameras() {
		Camera[] cameras = new Camera[4];
		cameras[0] = new Camera("Canon APS-C", 0.019, 22.2, 14.8);
		cameras[1] = new Camera("Canon APS-H", 0.023, 28.7, 19.1);
		cameras[2] = new Camera("Nikon DX", 0.02, 23.6, 15.5);
		cameras[3] = new Camera("Full Frame", 0.03, 36, 24);
		
		return cameras; 
	}
	
	public static Camera selectCamera(int index) {
		return getCameras()[index];
	}

	/**
	 * Set the model name and circle of confusion.
	 * 
	 * @param model
	 *            String model name
	 * @param circleOfConfusion
	 *            double circle of confusion in millimeters
	 * @param frameWidth
	 *            double width of film frame or sensor in millimeters
	 * @param frameHeight
	 *            double height of film frame or sense in millimeters
	 */
	public Camera(String model, double circleOfConfusion, double frameWidth,
			double frameHeight) {
		setModel(model);
		setCircleOfConfusion(circleOfConfusion);
		setFrameWidth(frameWidth);
		setFrameHeight(frameHeight);
	}

	/**
	 * Calculate the diagonal size of the sensor frame using the Pythagorean
	 * theorem.
	 * 
	 * @return double diagonal frame size in millimeters
	 */
	public double calculateFrameDiagonal() {
		return Math.sqrt((getFrameWidth() * getFrameWidth())
				+ (getFrameHeight() * getFrameHeight()));
	}

	/**
	 * @return the circleOfConfusion in millimeters
	 */
	public double getCircleOfConfusion() {
		return circleOfConfusion;
	}

	/**
	 * @return the frameHeight in millimeters
	 */
	public double getFrameHeight() {
		return frameHeight;
	}

	/**
	 * @return the frameWidth in millimeters
	 */
	public double getFrameWidth() {
		return frameWidth;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param circleOfConfusion
	 *            the circleOfConfusion to set in millimeters
	 */
	public void setCircleOfConfusion(double circleOfConfusion) {
		this.circleOfConfusion = circleOfConfusion;
	}

	/**
	 * @param frameHeight
	 *            the frameHeight to set in millimeters
	 */
	public void setFrameHeight(double frameHeight) {
		this.frameHeight = frameHeight;
	}

	/**
	 * @param frameWidth
	 *            the frameWidth to set in millimeters
	 */
	public void setFrameWidth(double frameWidth) {
		this.frameWidth = frameWidth;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
}