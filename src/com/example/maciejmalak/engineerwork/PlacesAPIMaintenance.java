package com.example.maciejmalak.engineerwork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacesAPIMaintenance {

	private static final String APIKey = "AIzaSyAFRRQIVS42Gj_fM6KjcuviNdnipQ-YL14";
	private static List<Marker> placesOnMap = new ArrayList<Marker>();
	private static String placesURI;
	private GoogleMap map;
	private Context app;
	
	public PlacesAPIMaintenance(GoogleMap map, Context app) {
		this.map = map;
		this.app = app;
	}
	
	public void settingURI(LatLng position) throws UnsupportedEncodingException {
		
		removeAllPlaces();
		
		double lat = position.latitude;
		double lng = position.longitude;
		String types = "subway_station|restaurant|park|night_club|bowling_alley|cafe|food|bar|store|museum|art_gallery|gas_station";
		
		placesURI = "https://maps.googleapis.com/maps/api/place/nearbysearch/" 
					+ "json?location="+lat+","+lng
					+ "&radius=1000&sensor=true"
					+ "&types="+URLEncoder.encode(types,"UTF-8")
					+ "&key="+APIKey;
		
		new placesASyncTask().execute(placesURI);
	}
	
	public MarkerOptions getMarkerOptions(String key, LatLng pos) {
		
		return new MarkerOptions()
	        .position(pos)
	        .title(key)
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
	}
	
	public void removeAllPlaces() {
		if(placesOnMap != null) {
			for(Marker marker : placesOnMap) {
				marker.remove();
			}
			placesOnMap.clear();
		}
	}
	
	private class placesASyncTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			progressDialog = new ProgressDialog(app);
			progressDialog.setMessage("Downloading nearby places");
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... placesURL) {
			return JSONParser.getJSONFromUrl(placesURL[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			progressDialog.hide();
			if(result != null) {
				LatLng position;
				String nameOfReturnedPlace;

				JSONObject resultObject;
				try {
					resultObject = new JSONObject(result);
					JSONArray placesArray = resultObject.getJSONArray("results");

					for (int p=0; p<placesArray.length(); p++) {
						JSONObject placeObject = placesArray.getJSONObject(p);
						JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
						position = new LatLng(
								Double.valueOf(loc.getString("lat")),
								Double.valueOf(loc.getString("lng")));
						
						nameOfReturnedPlace = placeObject.getString("name");

						Marker currMarker = map.addMarker(
								getMarkerOptions(nameOfReturnedPlace,position));
						placesOnMap.add(currMarker);
					}	
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	} /* ASync */
} /* PlacesAPIMaintenance */
