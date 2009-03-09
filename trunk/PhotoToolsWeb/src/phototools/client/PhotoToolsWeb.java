package phototools.client;

import phototools.client.maps.AngleOfViewOverlay;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.ScaleControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.impl.MapOptionsImpl;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotoToolsWeb implements EntryPoint, ChangeListener,
		ClickListener, KeyboardListener {
	private Aperture aperture = new Aperture();

	HorizontalSplitPanel mainPanel = new HorizontalSplitPanel();
	ListBox cameras = new ListBox();
	TextBox focalLengths = new TextBox();
	RadioButton fullStop = new RadioButton("apertureScale", "Full");
	RadioButton halfStop = new RadioButton("apertureScale", "Half");
	RadioButton thirdStop = new RadioButton("apertureScale", "Third");
	ListBox apertures = new ListBox();
	TextBox focusDistance = new TextBox();
	RadioButton feet = new RadioButton("measurementSystem", "Feet");
	RadioButton meters = new RadioButton("measurementSystem", "Meters");
	Button render = new Button("Redraw at<br />Center of Map", this);
	MapWidget map = new MapWidget();
	AngleOfViewOverlay angleOfViewOverlay;

	Label nearLimit = new Label();
	Label farLimit = new Label();
	Label before = new Label();
	Label behind = new Label();
	Label half = new Label();
	Label distance = new Label();
	Label fov = new Label();
	Label aov = new Label();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		mainPanel.setWidth("100%");
		mainPanel.setHeight((Window.getClientHeight() - 60) + "px");
		Window.addWindowResizeListener(new WindowResizeListener() {

			public void onWindowResized(int width, int height) {
				mainPanel.setHeight((height - 60) + "px");
				map.checkResize();
			}
		});

		mainPanel.setSplitPosition("250px");

		Grid form = new Grid(18, 2);

		Label cameraLabel = new Label("Camera:");
		form.setWidget(0, 0, cameraLabel);

		cameras.addChangeListener(this);
		cameras.setWidth("158px");

		for (Camera camera : Camera.getCameras()) {
			cameras.addItem(camera.getModel());
		}

		form.setWidget(0, 1, cameras);

		Label focalLengthsLabel = new Label("Focal Length:");
		form.setWidget(1, 0, focalLengthsLabel);

		focalLengths.addKeyboardListener(this);
		focalLengths.setWidth("150px");
		focalLengths.setText("50");
		form.setWidget(1, 1, focalLengths);

		Label apertureScaleLabel = new Label("Aperture Scale:");
		form.setWidget(2, 0, apertureScaleLabel);

		fullStop.addClickListener(this);
		halfStop.addClickListener(this);
		thirdStop.addClickListener(this);
		thirdStop.setChecked(true);
		FlowPanel apertureScale = new FlowPanel();
		apertureScale.add(fullStop);
		apertureScale.add(halfStop);
		apertureScale.add(thirdStop);
		form.setWidget(2, 1, apertureScale);

		Label aperturesLabel = new Label("Aperture:");
		form.setWidget(3, 0, aperturesLabel);

		apertures.addChangeListener(this);
		apertures.setWidth("158px");

		for (double format : aperture.getFormattedApertures()) {
			apertures.addItem(String.valueOf(format));
		}

		apertures.setSelectedIndex(15);
		form.setWidget(3, 1, apertures);

		Label focusDistanceLabel = new Label("Focus Distance:");
		form.setWidget(4, 0, focusDistanceLabel);

		focusDistance.addKeyboardListener(this);
		focusDistance.setWidth("150px");
		focusDistance.setText("10");
		form.setWidget(4, 1, focusDistance);

		Label measurementSystemLabel = new Label("Measurment System:");
		form.setWidget(5, 0, measurementSystemLabel);

		feet.addClickListener(this);
		meters.addClickListener(this);
		feet.setChecked(true);
		FlowPanel measurementSystem = new FlowPanel();
		measurementSystem.add(feet);
		measurementSystem.add(meters);
		form.setWidget(5, 1, measurementSystem);

		form.setWidget(6, 0, new Label(""));

		form.setWidget(7, 0, new Label("Depth of Field"));
		form.setWidget(8, 0, new Label("  Near Limit:"));
		form.setWidget(8, 1, nearLimit);
		form.setWidget(9, 0, new Label("  Far Limit:"));
		form.setWidget(9, 1, farLimit);
		form.setWidget(10, 0, new Label("  Before:"));
		form.setWidget(10, 1, before);
		form.setWidget(11, 0, new Label("  Behind:"));
		form.setWidget(11, 1, behind);
		form.setWidget(12, 0, new Label("Hyperfocal"));
		form.setWidget(13, 0, new Label("  Half:"));
		form.setWidget(13, 1, half);
		form.setWidget(14, 0, new Label("  Distance:"));
		form.setWidget(14, 1, distance);
		form.setWidget(15, 0, new Label("Field of View:"));
		form.setWidget(15, 1, fov);
		form.setWidget(16, 0, new Label("Angle of View:"));
		form.setWidget(16, 1, aov);

		form.setWidget(17, 1, render);

		mainPanel.setLeftWidget(form);

		map.setCenter(new LatLng(29.426432, -98.70817));
		map.setZoomLevel(18);
		map.setSize("100%", "100%");
		map.setCurrentMapType(MapType.getHybridMap());
		map.setScrollWheelZoomEnabled(true);
		map.setContinuousZoom(true);
		map.addControl(new LargeMapControl());
		map.addControl(new MapTypeControl());
		map.addControl(new ScaleControl());
		mainPanel.setRightWidget(map);

		RootPanel.get().add(mainPanel);

		recalculate();

		map.checkResize();
	}

	private void recalculate() {
		Photo photo = new Photo();
		photo.setCamera(Camera.selectCamera(cameras.getSelectedIndex()));
		photo.setAperture(aperture.calculateAperture(apertures
				.getSelectedIndex()));
		photo.setMetric(meters.isChecked());

		try {
			photo.setFocalLength(Double.valueOf(focalLengths.getText()));
			photo.setFocusDistance(Double.valueOf(focusDistance.getText()));

			half.setText(photo.formatHalfHyperfocalDistance());
			distance.setText(photo.formatHyperfocalDistance());
			aov.setText(photo.formatAngleOfView());
			
			nearLimit.setText(photo.formatDepthOfFieldNearLimit());
			farLimit.setText(photo.formatDepthOfFieldFarLimit());
			before.setText(photo.formatDepthOfFieldBefore());
			behind.setText(photo.formatDepthOfFieldBehind());
			fov.setText(photo.formatFieldOfView());

			if (angleOfViewOverlay == null) {
				this.angleOfViewOverlay = new AngleOfViewOverlay(photo, map
						.getCenter().getLatitude(), map.getCenter()
						.getLongitude());
				map.addOverlay(angleOfViewOverlay);
			} else {
				angleOfViewOverlay.update(photo);
			}
		} catch (NumberFormatException e) {
			Window.alert("Focal length or focus distance is not a number");
			
			half.setText("");
			distance.setText("");
			aov.setText("");
			
			nearLimit.setText("");
			farLimit.setText("");
			before.setText("");
			behind.setText("");
			fov.setText("");
		}
	}

	public void onClick(Widget sender) {
		if (sender.equals(fullStop) || sender.equals(halfStop)
				|| sender.equals(thirdStop)) {
			if (fullStop.isChecked()) {
				aperture.setFullStop();
			} else if (halfStop.isChecked()) {
				aperture.setHalfStop();
			} else {
				aperture.setThirdStop();
			}

			apertures.clear();

			for (double format : aperture.getFormattedApertures()) {
				apertures.addItem(String.valueOf(format));
			}
		}

		if (sender.equals(render)) {
			angleOfViewOverlay.reset(map.getCenter());
		}

		recalculate();
	}

	public void onChange(Widget sender) {
		recalculate();
	}

	public void onKeyDown(Widget sender, char keyCode, int modifiers) {

	}

	public void onKeyPress(Widget sender, char keyCode, int modifiers) {

	}

	public void onKeyUp(Widget sender, char keyCode, int modifiers) {
		recalculate();
	}
}