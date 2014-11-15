package com.example.maciejmalak.engineerwork;

import java.util.HashMap;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerMaintenance {
	
	private final static String MEET = "Meeting Place";
	
	private GoogleMap googleMapInstance;
	private Circle circle;
	private Context appContext;
	private GeocodingTasks geocoder;
	/* We need to store those keys value
	 * because Resources (R) are only
	 * accessible from Activity class
	 */
	private String startPointKey,
					currentPointKey;
	
	private HashMap<String, Marker> allMarkersVisibleOnMap = new HashMap<String, Marker>();
	
	private float standardMarker = BitmapDescriptorFactory.HUE_ORANGE;
	private float startMarker =   BitmapDescriptorFactory.HUE_BLUE;
	private float currentPosMarker = BitmapDescriptorFactory.HUE_RED;
	private float meetingPlaceMarker =   BitmapDescriptorFactory.HUE_MAGENTA;

	public MarkerMaintenance(GoogleMap map, String resourceStartPosition, String resourceCurrPosition, Context appContext){
		this.googleMapInstance = map;
		this.currentPointKey = resourceCurrPosition;
		this.startPointKey = resourceStartPosition;
		this.appContext = appContext;
		this.geocoder = new GeocodingTasks(appContext);
	}
	
	public void registerMarkerOnMap(String key, Location position) {	
		LatLng pos = LocalizationCalculationHelper.geoPointFromLocalization(position);
		String address = getAdressFromLocation(position);
		
		if (allMarkersVisibleOnMap.get(key) != null ) {
			allMarkersVisibleOnMap.get(key).setPosition(pos);
			allMarkersVisibleOnMap.get(key).setSnippet(address);
		} else {
			Marker currentRetriveMarker 
				= googleMapInstance.addMarker(getMarkerOptions(key, address, pos));
			allMarkersVisibleOnMap.put(key, currentRetriveMarker);
		}
		
		if ( key == currentPointKey ) {
			settingCircle(pos ,position.getAccuracy());
			//googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18.0f));
		}
	}
	
	public MarkerOptions getMarkerOptions(String key, String snippet, LatLng pos) {
		float colorOfMarker = colorOfMarker(key);
		return new MarkerOptions()
	        .position(pos)
	        .title(key)
	        .snippet(snippet)
	        .icon(BitmapDescriptorFactory.defaultMarker(colorOfMarker));
	}
	
	public void removeSelectedMarkerFromMap(String key) {
		allMarkersVisibleOnMap.get(key).remove();
		allMarkersVisibleOnMap.remove(key);
		GeoMidPointAlgorithm.removePosition(key);
	}
	
	public void animateCameraOnMarker(Marker marker) {
		googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
	}
	
	public void clearMarkerMap () {
		for (String key : allMarkersVisibleOnMap.keySet()) {
			allMarkersVisibleOnMap.get(key).remove();
			GeoMidPointAlgorithm.removePosition(key);
		}
		removingCircle();
		allMarkersVisibleOnMap.clear();
	}
	
	public void settingCircle(LatLng center, float rad) {
		if (circle != null) { circle.remove(); }
		
		CircleOptions circleOptions = new CircleOptions()
		    .center(center)
		    .radius(rad); 

		 circle = googleMapInstance.addCircle(circleOptions);
	}
	
	public void removingCircle() { circle.remove(); }
	
	public void setMeetingPlace() {
		removeMeetingPlaceMarkerFromMap();
		LatLng meetPointLatLng = GeoMidPointAlgorithm.geographicMidpointAlgorithm();
		Location meetPoint = 
				LocalizationCalculationHelper.LocalizationFromGeopoint(meetPointLatLng);
		
		Marker currentRetriveMarker 
			= googleMapInstance.addMarker(
									getMarkerOptions(
											MEET,
											getAdressFromLocation(meetPoint),
											meetPointLatLng
								));
		allMarkersVisibleOnMap.put(MEET, currentRetriveMarker);
	}
	
	public void removeMeetingPlaceMarkerFromMap() {
		if (allMarkersVisibleOnMap.get(MEET) != null) {
			allMarkersVisibleOnMap.get(MEET).remove();
			allMarkersVisibleOnMap.remove(MEET);
		}
	}
	
	public float colorOfMarker(String key) {
		if (key == currentPointKey) {
			return currentPosMarker;
		} else if (key == MEET) {
			return meetingPlaceMarker;
		} else if (key == startPointKey) {
			return startMarker;
		}
		return standardMarker;
	}
	
	public String getAdressFromLocation(Location pos) {
		return this.geocoder.getAdressFromLocation(pos);
	}
	
}