package com.example.maciejmalak.engineerwork;

import com.google.android.gms.maps.model.LatLng;

public class PlacesAPIMaintenance {
	
	private static final int RESULT_OK = 200;
	private static String placesURI;
	private static String APIKey = "AIzaSyAnWTUXFLuv2dUrPxXehSB9GR72cItHSdE";
	
	public void settingURI(LatLng position) {
		double lat = position.latitude;
		double lng = position.longitude;
		
		placesURI = "https://maps.googleapis.com/maps/api/place/nearbysearch/" 
					+ "json?location="+lat+","+lng
					+ "&radius=300&sensor=true"
					+ "&types=food|bar|store|museum|art_gallery"
					+ "&key="+APIKey;
	}
	
	

}
