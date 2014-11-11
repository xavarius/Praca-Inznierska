package com.example.maciejmalak.engineerwork;

import java.util.HashMap;

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
	Circle circle;
	
	/* We need to store those keys value
	 * because Resources (R) are only
	 * accessible from Activity class
	 */
	private String startPointKey,
					currentPointKey;
	
	private HashMap<String, Marker> allMarkersVisibleOnMap = new HashMap<String, Marker>();

	public MarkerMaintenance(GoogleMap map, String resourceStartPosition, String resourceCurrPosition){
		this.googleMapInstance = map;
		this.currentPointKey = resourceCurrPosition;
		this.startPointKey = resourceStartPosition;
	}
	
	public void registerMarkerOnMap(String key, Location position) {	
		LatLng pos = LocalizationCalculationHelper.geoPointFromLocalization(position);
		
		if (allMarkersVisibleOnMap.get(key) != null ) {
			allMarkersVisibleOnMap.get(key).setPosition(pos);
		} else {
			Marker currentRetriveMarker 
				= googleMapInstance.addMarker(getMarkerOptions(key, pos));
			allMarkersVisibleOnMap.put(key, currentRetriveMarker);
		}
		
		if ( key == currentPointKey ) {
			settingCircle(pos ,position.getAccuracy());
			googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18.0f));
		}
	}
	
	public MarkerOptions getMarkerOptions(String key, LatLng pos) {
		return new MarkerOptions()
	        .position(pos)
	        .title(key)
	        .snippet(pos.toString())
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
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
		Marker currentRetriveMarker 
			= googleMapInstance.addMarker(getMarkerOptions(MEET,
								GeoMidPointAlgorithm.geographicMidpointAlgorithm()));
		allMarkersVisibleOnMap.put(MEET, currentRetriveMarker);
	}
	
	public void removeMeetingPlaceMarkerFromMap() {
		if (allMarkersVisibleOnMap.get(MEET) != null) {
			allMarkersVisibleOnMap.get(MEET).remove();
		}
	}
}