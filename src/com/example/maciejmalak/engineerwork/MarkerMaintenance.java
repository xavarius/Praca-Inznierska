package com.example.maciejmalak.engineerwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
	private float accuracy;
	
	private String startPointKey,
					currentPointKey,
					MEET;
	
	private HashMap<String, Marker> allMarkersVisibleOnMap = new HashMap<String, Marker>();
	private HashMap<String, MarkerDetails> allPositionsAfterGeocoding = new HashMap<String, MarkerDetails>();
	
	private static final float standardMarker = BitmapDescriptorFactory.HUE_ORANGE;
	private static final float startMarker =   BitmapDescriptorFactory.HUE_BLUE;
	private static final float currentPosMarker = BitmapDescriptorFactory.HUE_RED;
	private static final float meetingPlaceMarker =   BitmapDescriptorFactory.HUE_MAGENTA;

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
			LatLng geoMidPoint = GeoMidPointAlgorithm.geographicMidpointAlgorithm();
			allPositionsAfterGeocoding.put(MEET, new MarkerDetails("",geoMidPoint));
		} else {
			LatLng pos = LocalizationCalculationHelper.geoPointFromLocalization(position);
			allPositionsAfterGeocoding.put(key, new MarkerDetails("",pos));
			if ( key == currentPointKey ) {
				accuracy = position.getAccuracy();
			}
		}
		new ReverseGeocodingTask(appContext).execute();
	}
	
	public MarkerOptions getMarkerOptions(String key, String snippet, LatLng pos) {
		float colorOfMarker = colorOfMarker(key);
		return new MarkerOptions()
	        .position(pos)
	        .title(key)
	        .snippet(snippet)
	        .icon(BitmapDescriptorFactory.defaultMarker(colorOfMarker))
	        .draggable(true);
	}
	
	public void removeSelectedMarkerFromMap(String key) {
		allMarkersVisibleOnMap.get(key).remove();
		allMarkersVisibleOnMap.remove(key);
		allPositionsAfterGeocoding.remove(key);
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
		allPositionsAfterGeocoding.clear();
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
			allPositionsAfterGeocoding.remove(MEET);
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
	
	public void setMarkersOnMap() {

		for (Entry<String, MarkerDetails> entry : allPositionsAfterGeocoding.entrySet()) {
			String currentKeyToGeocoder = entry.getKey();
			LatLng currentPositionToGeocoder;
			MarkerDetails value = entry.getValue();	
			currentPositionToGeocoder = value.pos;
			String addr = value.addr;

			if (allMarkersVisibleOnMap.get(currentKeyToGeocoder) != null ) {
				allMarkersVisibleOnMap.get(currentKeyToGeocoder).setPosition(currentPositionToGeocoder);
				allMarkersVisibleOnMap.get(currentKeyToGeocoder).setSnippet(addr);
			} else {
				Marker currentRetriveMarker 
				= googleMapInstance.addMarker(
						getMarkerOptions(
								currentKeyToGeocoder,
								addr,
								currentPositionToGeocoder
								));
				allMarkersVisibleOnMap.put(currentKeyToGeocoder, currentRetriveMarker);
			}
			if ( currentKeyToGeocoder == currentPointKey ) {
				settingCircle(currentPositionToGeocoder,this.accuracy);
			}
		}
	}

	private class ReverseGeocodingTask extends AsyncTask<Void, Void, Void>{
        Context appContext;
 
        public ReverseGeocodingTask(Context context){
            super();
            appContext = context;
        }

		@Override
		protected Void doInBackground(Void... params) {
			Geocoder geocoder = new Geocoder(appContext);

        	List<Address> address;  

        	for (Entry<String, MarkerDetails> entry : allPositionsAfterGeocoding.entrySet()) {
        		String key = entry.getKey();
        		MarkerDetails value = entry.getValue();	

        		if (value.addr == "") {
        			try {
        
        				address = geocoder.getFromLocation(value.pos.latitude, value.pos.longitude, 1);

        				if (address != null && address.size() > 0) { 	
        					Address searchedAddress = address.get(0);

        					value.addr = searchedAddress.getThoroughfare() + " " +
        								 searchedAddress.getFeatureName()  + " " +
        								 searchedAddress.getSubAdminArea();

        					address.clear();
        					allPositionsAfterGeocoding.put(key, value);
        				}   
        			} catch (Exception e) { e.printStackTrace(); 
        			} finally {}
        		} }
			return null;
		}
 
		@Override
		   protected void onPostExecute(Void result) {
			setMarkersOnMap();
		}
    
    }
	
	private class MarkerDetails {

		protected String addr;
		protected LatLng pos;

		public MarkerDetails(String addr, LatLng pos ) {
			this.addr = addr;
			this.pos = pos;
		}
	}
	
	
}