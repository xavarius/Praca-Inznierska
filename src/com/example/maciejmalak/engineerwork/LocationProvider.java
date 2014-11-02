package com.example.maciejmalak.engineerwork;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class LocationProvider {
	
	private static final double DOUBLE_TO_INT = 1e6;
	private LocationManager locationMgr;
	private Criteria criteria;
	private String providerName;
	private Location currentLocation;
	private LatLng currentGeoPlace;
	
	public LocationProvider(LocationManager locMgr) {
		this.locationMgr = locMgr;
		
	}
	
	protected void setUpLocation() {
		
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		providerName = this.locationMgr.getBestProvider(criteria, true);
		currentLocation = this.locationMgr.getLastKnownLocation(providerName);
		
		if (currentLocation != null) {
			double latitude = currentLocation.getLatitude();
			double longitude =  currentLocation.getLongitude();

			this.setCurrentGeoPlace(new LatLng(latitude, longitude));
		}
	}

	public LatLng getCurrentGeoPlace() {
		return currentGeoPlace;
	}

	public void setCurrentGeoPlace(LatLng currentGeoPlace) {
		this.currentGeoPlace = currentGeoPlace;
	}
	


}
