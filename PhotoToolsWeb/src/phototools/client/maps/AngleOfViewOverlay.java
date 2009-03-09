/**
 * 
 */
package phototools.client.maps;

import java.util.ArrayList;
import java.util.List;

import phototools.client.Photo;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.event.MarkerDragHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;

/**
 * @author tstavenger
 * 
 */
public class AngleOfViewOverlay extends Overlay implements MarkerDragHandler,
		MarkerDragEndHandler {
	private static final int LINE_WEIGHT = 1;
	private static final String LINE_COLOR = "#ffff00";
	private static final double OPAQUE = 1;
	private static final double TRANSLUCENT = 0.25;
	private static final double CIRCLE_QUALITY = 1;
	private static final double MAX_DISTANCE = 10000;

	private Photo photo;
	private LatLng center;

	private MapWidget map;
	private Marker subject;

	public AngleOfViewOverlay(Photo photo, double centerLatitude,
			double centerLongitude) {
		this.photo = photo;
		this.center = new LatLng(centerLatitude, centerLongitude);
	}

	protected Overlay copy() {
		return null;
	}

	protected void initialize(MapWidget map) {
		this.map = map;
	}

	public void update(Photo photo) {
		this.photo = photo;

		redraw(true);
	}

	public void reset(LatLng center) {
		this.center = center;
		this.subject = null;
	}

	protected void redraw(boolean force) {
		// TODO only clear the overlays this class adds!
		map.clearOverlays();

		double angleOfView = photo.calculateAngleOfViewHorizontal();

		// The angle from which start drawing the angle of view
		double startAngle = 0;

		double focusDistance = photo.getFocusDistanceInMeters();

		if (subject == null) {
			MarkerOptions options = new MarkerOptions();
			options.setDraggable(true);
			options.setTitle("Drag marker to move angle of view");
			this.subject = new Marker(new LatLng(calculateLatitude(
					focusDistance, angleOfView / 2), calculateLongitude(
					focusDistance, angleOfView / 2)), options);
			subject.addMarkerDragHandler(this);
			subject.addMarkerDragEndHandler(this);
		} else {
			startAngle = calculateStartAngle(angleOfView);
			subject.setPoint(new LatLng(calculateLatitude(focusDistance,
					startAngle + (angleOfView / 2)), calculateLongitude(
					focusDistance, startAngle + (angleOfView / 2))));
		}

		map.addOverlay(subject);

		LatLng[] leftAngleOfView = new LatLng[2];
		leftAngleOfView[0] = center;
		double lat = calculateLatitude(MAX_DISTANCE, startAngle);
		double lng = calculateLongitude(MAX_DISTANCE, startAngle);
		leftAngleOfView[1] = new LatLng(lat, lng);

		map.addOverlay(new Polyline(leftAngleOfView, LINE_COLOR, LINE_WEIGHT,
				OPAQUE));

		List<LatLng> points = new ArrayList<LatLng>();

		double radius = photo.calculateDepthOfFieldNearLimitInMeters();

		for (double q = startAngle; q <= (startAngle + angleOfView); q += CIRCLE_QUALITY) {
			points.add(new LatLng(calculateLatitude(radius, q),
					calculateLongitude(radius, q)));
		}

		radius = photo.calculateDepthOfFieldFarLimitInMeters();

		if (radius < 0) {
			radius = MAX_DISTANCE;
		}

		for (double q = (startAngle + angleOfView); q >= startAngle; q -= CIRCLE_QUALITY) {
			points.add(new LatLng(calculateLatitude(radius, q),
					calculateLongitude(radius, q)));
		}

		points.add(new LatLng(calculateLatitude(radius, startAngle),
				calculateLongitude(radius, startAngle)));
		points.add(points.get(0));

		LatLng[] pointsArray = new LatLng[points.size()];

		for (int i = 0; i < points.size(); i++) {
			pointsArray[i] = points.get(i);
		}

		map.addOverlay(new Polygon(pointsArray, LINE_COLOR, LINE_WEIGHT,
				OPAQUE, LINE_COLOR, TRANSLUCENT));

		LatLng[] rightAngleOfView = new LatLng[2];
		rightAngleOfView[0] = center;
		lat = calculateLatitude(MAX_DISTANCE, startAngle + angleOfView);
		lng = calculateLongitude(MAX_DISTANCE, startAngle + angleOfView);
		rightAngleOfView[1] = new LatLng(lat, lng);

		map.addOverlay(new Polyline(rightAngleOfView, LINE_COLOR, LINE_WEIGHT,
				OPAQUE));
	}

	private double calculateStartAngle(double angleOfView) {
		LatLng temp = new LatLng(subject.getPoint().getLatitude(), center
				.getLongitude());
		double adjacent = temp.distanceFrom(center);
		double hypotenuse = subject.getPoint().distanceFrom(center);
		double radians = Math.acos(adjacent / hypotenuse);
		double degrees = Math.toDegrees(radians);

		double startAngle;

		// start angle depends on which quadrant the subject is in
		if (subject.getPoint().getLatitude() >= center.getLatitude()
				&& subject.getPoint().getLongitude() <= center.getLongitude()) {
			// top left
			startAngle = (360 - degrees) - (angleOfView / 2);
		} else if (subject.getPoint().getLatitude() > center.getLatitude()
				&& subject.getPoint().getLongitude() > center.getLongitude()) {
			// top right
			startAngle = degrees - (angleOfView / 2);
		} else if (subject.getPoint().getLatitude() <= center.getLatitude()
				&& subject.getPoint().getLongitude() >= center.getLongitude()) {
			// bottom right
			startAngle = (180 - degrees) - (angleOfView / 2);
		} else {
			// bottom left
			startAngle = (180 + degrees) - (angleOfView / 2);
		}

		return startAngle;
	}

	/**
	 * @param radius
	 *            in meters
	 * @param degrees
	 *            of angle
	 * @return
	 */
	private double calculateLatitude(double radius, double degrees) {
		double radians = Math.toRadians(degrees);
		double meters = radius * Math.cos(radians);
		double metersPerLatitude = calculateMetersPerLatitude();

		return center.getLatitude() + (meters / metersPerLatitude);
	}

	/**
	 * @param radius
	 *            in meters
	 * @param degrees
	 *            of angle
	 * @return
	 */
	private double calculateLongitude(double radius, double degrees) {
		double radians = Math.toRadians(degrees);
		double meters = radius * Math.sin(radians);
		double metersPerLongitude = calculateMetersPerLongitude();

		return center.getLongitude() + (meters / metersPerLongitude);
	}

	/**
	 * @return
	 * 
	 * @see http://www.csgnetwork.com/degreelenllavcalc.html
	 */
	private double calculateMetersPerLatitude() {
		// Set up "Constants"
		double m1 = 111132.92; // latitude calculation term 1
		double m2 = -559.82; // latitude calculation term 2
		double m3 = 1.175; // latitude calculation term 3
		double m4 = -0.0023; // latitude calculation term 4

		double latitudeRadians = Math.toRadians(center.getLatitude());

		// Calculate the length of a degree of latitude in meters
		return m1 + (m2 * Math.cos(2 * latitudeRadians))
				+ (m3 * Math.cos(4 * latitudeRadians))
				+ (m4 * Math.cos(6 * latitudeRadians));
	}

	/**
	 * @return
	 * 
	 * @see http://www.csgnetwork.com/degreelenllavcalc.html
	 */
	private double calculateMetersPerLongitude() {
		// Set up "Constants"
		double p1 = 111412.84; // longitude calculation term 1
		double p2 = -93.5; // longitude calculation term 2
		double p3 = 0.118; // longitude calculation term 3

		double latitudeRadians = Math.toRadians(center.getLatitude());

		// Calculate the length of a degree of longitude in meters
		return (p1 * Math.cos(latitudeRadians))
				+ (p2 * Math.cos(3 * latitudeRadians))
				+ (p3 * Math.cos(5 * latitudeRadians));
	}

	protected void remove() {

	}

	public void onDrag(MarkerDragEvent event) {
		if (subject.equals(event.getSender())) {
			dragSubject(event);
		}
	}

	private void dragSubject(MarkerDragEvent event) {
		LatLng newPoint = event.getSender().getPoint();

		subject.setPoint(findNearestPoint(newPoint));
	}

	private LatLng findNearestPoint(LatLng latLng) {
		List<LatLng> subjectDistance = subjectDistance();

		// find the nearest latitude -- this could give me a point on the wrong
		// side of the circle (i.e., wrong longitude)
		int latIndex = 0;

		while (subjectDistance.get(latIndex).getLatitude() > latLng
				.getLatitude()
				&& latIndex <= 180) {
			latIndex++;
		}

		// find the nearest longitude -- this could give me a point on the wrong
		// side of the circle (i.e., wrong latitude)
		int lngIndex = 90;

		while (subjectDistance.get(lngIndex).getLongitude() > latLng
				.getLongitude()
				&& lngIndex <= 270) {
			lngIndex++;
		}

		int index = latIndex;

		if (lngIndex > 180) {
			index = 359 - latIndex;
		}

		return subjectDistance.get(index);
	}

	private List<LatLng> subjectDistance() {
		List<LatLng> subjectDistance = new ArrayList<LatLng>();
		double radius = photo.getFocusDistanceInMeters();

		for (double degrees = 0; degrees < 360; degrees++) {
			subjectDistance.add(new LatLng(calculateLatitude(radius, degrees),
					calculateLongitude(radius, degrees)));
		}

		return subjectDistance;
	}

	public void onDragEnd(MarkerDragEndEvent event) {
		if (subject.equals(event.getSender())) {
			redraw(true);
		}
	}
}