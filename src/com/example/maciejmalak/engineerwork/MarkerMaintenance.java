package com.example.maciejmalak.engineerwork;

import com.google.android.gms.maps.CameraUpdateFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerMaintenance {
	
	private GoogleMap googleMapInstance;
	private String startPointKey,
					currentPointKey;
	private Marker currentPositionAsMarker, 
					startPointAsMarker,
					meetingPointAsMarker;
	
	public MarkerMaintenance(GoogleMap map, String resourceStartPosition, String resourceCurrPosition){
		this.googleMapInstance = map;
		this.currentPointKey = resourceCurrPosition;
		this.startPointKey = resourceStartPosition;
	}
	
	public void registerMarkerOnMap(String key, LatLng position) {
			
		if (key == startPointKey ) {
			if (startPointAsMarker != null) {
				startPointAsMarker.setPosition(position);
			} else {
				startPointAsMarker = googleMapInstance.addMarker(new MarkerOptions()
		        .position(position)
		        .title(key));
			}
		} else if (key == currentPointKey ) {
			if (currentPositionAsMarker != null) {
				currentPositionAsMarker.setPosition(position);
			} else {
				currentPositionAsMarker = googleMapInstance.addMarker(new MarkerOptions()
		        .position(position)
		        .title(key)
		        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
			}
		}
		googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 18.0f));
	}
}