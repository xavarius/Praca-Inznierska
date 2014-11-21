package com.example.maciejmalak.engineerwork;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

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
	private Circle circle;
	private Context appContext;
	private String currentKeyToGeocoder;
	private LatLng currentPositionToGeocoder;
	
	/* We need to store those keys value
	 * because Resources (R) are only
	 * accessible from Activity class
	 */
	
	private String startPointKey,
					currentPointKey,
					MEET;
	
	private HashMap<String, Marker> allMarkersVisibleOnMap = new HashMap<String, Marker>();
	
	private float standardMarker = BitmapDescriptorFactory.HUE_ORANGE;
	private float startMarker =   BitmapDescriptorFactory.HUE_BLUE;
	private float currentPosMarker = BitmapDescriptorFactory.HUE_RED;
	private float meetingPlaceMarker =   BitmapDescriptorFactory.HUE_MAGENTA;

	public MarkerMaintenance(GoogleMap map, String resourceStartPosition, 
								String resourceCurrPosition, Context appContext, String meet){
		this.googleMapInstance = map;
		this.currentPointKey = resourceCurrPosition;
		this.startPointKey = resourceStartPosition;
		this.appContext = appContext;
		this.MEET = meet;
	}
	
	public void registerMarkerOnMap(String key, Location position) {	
		
		if(key == MEET) {
			removeMeetingPlaceMarkerFromMap();
			this.currentKeyToGeocoder = MEET;
			this.currentPositionToGeocoder = GeoMidPointAlgorithm.geographicMidpointAlgorithm();
		} else {
			this.currentKeyToGeocoder = key;
			this.currentPositionToGeocoder = LocalizationCalculationHelper.geoPointFromLocalization(position);
			
			if ( key == currentPointKey ) {
				settingCircle(this.currentPositionToGeocoder ,position.getAccuracy());
				//googleMapInstance.animateCamera(CameraUpdateFactory.newLatLngZoom(, 18.0f));
			}
		}
		new ReverseGeocodingTask(appContext).execute(this.currentPositionToGeocoder);
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
		GeoMidPointAlgorithm.removeAllPosition();
	}
	
	public void settingCircle(LatLng center, float rad) {
		if (circle != null) { circle.remove(); }
		
		CircleOptions circleOptions = new CircleOptions()
		    .center(center)
		    .radius(rad); 

		 circle = googleMapInstance.addCircle(circleOptions);
	}
	
	public void removingCircle() { if (circle != null) circle.remove(); }
	
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

	private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String>{
        Context appContext;
 
        public ReverseGeocodingTask(Context context){
            super();
            appContext = context;
        }
 
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(appContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;
            
            List<Address> address;  
    		try {
    		    address = geocoder.getFromLocation(latitude, longitude, 1);
    		    
    		    if (address != null && address.size() > 0) { 	
    			    Address searchedAddress = address.get(0);
    			    
    			    String addressToBeReturned = searchedAddress.getThoroughfare() + " " +
    			    		searchedAddress.getFeatureName() + " " +
    			    		searchedAddress.getSubAdminArea();
    			    
    			    address.clear();	
    			    return addressToBeReturned;
    		    }   
    		} catch (Exception e) { e.printStackTrace(); 
    		} finally {}
    		
    		return "Cannot decode to address";   
        }
        
        @Override
        protected void onPostExecute(String addressText) {

        	if (allMarkersVisibleOnMap.get(currentKeyToGeocoder) != null ) {
        		allMarkersVisibleOnMap.get(currentKeyToGeocoder).setPosition(currentPositionToGeocoder);
        		allMarkersVisibleOnMap.get(currentKeyToGeocoder).setSnippet(addressText);
        	} else {
        		Marker currentRetriveMarker 
        		= googleMapInstance.addMarker(
        				getMarkerOptions(
        						currentKeyToGeocoder,
        						addressText,
        						currentPositionToGeocoder
        						));
        		allMarkersVisibleOnMap.put(currentKeyToGeocoder, currentRetriveMarker);
        	}
        }
    }
	
	
}