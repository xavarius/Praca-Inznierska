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
	
	private GoogleMap googleMapInstance;
	Circle circle;
	
	/* dlatego, ¿e R mo¿na pobieraæ tylko z Services albo Activity */
	private String startPointKey,
					currentPointKey;
	
	private HashMap<String, Marker> allMarkersVisibleOnMap = new HashMap<String, Marker>();
	
	public MarkerMaintenance(GoogleMap map, String resourceStartPosition, String resourceCurrPosition){
		this.googleMapInstance = map;
		this.currentPointKey = resourceCurrPosition;
		this.startPointKey = resourceStartPosition;
	}
	
	public void registerMarkerOnMap(String key, Location position) {
		LatLng pos = geoPointFromLocalization(position);	
		if (allMarkersVisibleOnMap.get(key) != null ) {
			allMarkersVisibleOnMap.get(key).setPosition(pos);
		} else {
			Marker currentRetriveMarker 
				= googleMapInstance.addMarker(new MarkerOptions()
		        .position(pos)
		        .title(key)
		        .snippet(key)
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			allMarkersVisibleOnMap.put(key, currentRetriveMarker);
		}
		
		if ( key == currentPointKey ) {
			settingCircle(pos,position.getAccuracy());
			googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 18.0f));
		}
	}
	
	public void removeSelectedMarkerFromMap(String key) {
		allMarkersVisibleOnMap.get(key).remove();
		allMarkersVisibleOnMap.remove(key);
	}
	
	public void animateCameraOnMarker(Marker marker) {
		googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 18.0f));
	}
	
	public void clearMarkerMap () {
		for (String key : allMarkersVisibleOnMap.keySet()) {
			allMarkersVisibleOnMap.get(key).remove();
		}
		allMarkersVisibleOnMap.clear();
	}
	
	public void settingCircle(LatLng center, float rad) {
		if (circle != null) { circle.remove(); }
		
		CircleOptions circleOptions = new CircleOptions()
		    .center(center)
		    .radius(rad); // In meters

		 circle = googleMapInstance.addCircle(circleOptions);
	}
	
	protected LatLng geoPointFromLocalization(Location loc){
		double latitude = loc.getLatitude();
		double longitude =  loc.getLongitude();
		LatLng here = new LatLng(latitude, longitude);
		return here;
	}
	
}