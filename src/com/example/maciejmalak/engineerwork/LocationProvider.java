package com.example.maciejmalak.engineerwork;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationProvider implements LocationListener{
	
	private LocationManager locationMgr;
	private Criteria criteria;
	private String providerName;
	private GoogleMap googleMapInstance;
	/* Localization */
	private Location currentLocation;
	private LatLng currentPosition;
	private LatLng phoneStartingPoint;
	private boolean GPSenabled;
	private boolean NETenabled;
	private Marker currentPositionAsMarker, startPointAsMarker;
	
	/* Constructors */
	public LocationProvider(LocationManager locMgr, GoogleMap map) {
		this.locationMgr = locMgr;
		this.googleMapInstance = map;
		this.criteria = new Criteria();
		this.criteria.setAccuracy(Criteria.ACCURACY_FINE);
		providerName = this.locationMgr.getBestProvider(criteria, true);
	}
	
	/* SETTERS AND GETTERS */ 
	public LatLng getPhoneStartingPoint() {
		return phoneStartingPoint;
	}

	public void setPhoneStartingPoint(LatLng phoneStartingPoint) {
		this.phoneStartingPoint = phoneStartingPoint;
	}
	
	protected Location getCurrentLocation() {
		return this.locationMgr.getLastKnownLocation(providerName);
	}
	
	public LocationManager getLocationManager() {
		return locationMgr;
	}
	
	public String getProviderName() {
		return providerName;
	}
	
	/* INTERFACE IMPLEMENTATION */
	
	@Override
	public void onLocationChanged(Location location) {

		if (location != null) {
			currentPosition = geoPointFromLocalization(location);
			
			currentPositionAsMarker = googleMapInstance.addMarker(new MarkerOptions()
            .position(currentPosition)
            .title("Current Location"));
		}	
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	/* Helper methods */
	
	protected void setUpStartingLocation() {
		
		currentLocation = getCurrentLocation();
		if (currentLocation != null) {
			setPhoneStartingPoint(geoPointFromLocalization(currentLocation));
			
			startPointAsMarker = googleMapInstance.addMarker(new MarkerOptions()
            .position(phoneStartingPoint)
            .title("Starting Location"));
			
			googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(getPhoneStartingPoint(), 18.0f));
		}
	}
	
	protected LatLng geoPointFromLocalization(Location loc){
		double latitude = loc.getLatitude();
		double longitude =  loc.getLongitude();
		LatLng here = new LatLng(latitude, longitude);
		return here;
	}
}
