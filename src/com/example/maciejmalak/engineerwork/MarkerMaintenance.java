package com.example.maciejmalak.engineerwork;

import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerMaintenance {
	
	private GoogleMap googleMapInstance;
	
	/* dlatego, ¿e R mo¿na pobieraæ tylko z Services albo Activity */
	private String startPointKey,
					currentPointKey;
	
	private HashMap<String, Marker> allMarkersVisibleOnMap = new HashMap<String, Marker>();
	
	public MarkerMaintenance(GoogleMap map, String resourceStartPosition, String resourceCurrPosition){
		this.googleMapInstance = map;
		this.currentPointKey = resourceCurrPosition;
		this.startPointKey = resourceStartPosition;
	}
	
	public void registerMarkerOnMap(String key, LatLng position) {
			
		if (allMarkersVisibleOnMap.get(key) != null ) {
			allMarkersVisibleOnMap.get(key).setPosition(position);
		} else {
			Marker currentRetriveMarker 
				= googleMapInstance.addMarker(new MarkerOptions()
		        .position(position)
		        .title(key)
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			allMarkersVisibleOnMap.put(key, currentRetriveMarker);
		}
		googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));
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
	
}