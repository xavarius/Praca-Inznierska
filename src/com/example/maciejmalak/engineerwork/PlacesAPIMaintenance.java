package com.example.maciejmalak.engineerwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacesAPIMaintenance {
	
	private static final int RESULT_OK = 200;
	private static final String APIKey = "AIzaSyAFRRQIVS42Gj_fM6KjcuviNdnipQ-YL14";
	private static List<Marker> placesOnMap = new ArrayList<Marker>();
	private static String placesURI;
	private GoogleMap map;
	
	public PlacesAPIMaintenance(GoogleMap map) {
		this.map = map;
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
		
		new GetPlaces().execute(placesURI);
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
	
	private class GetPlaces extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... placesURL) {

			StringBuilder placesBuilder = new StringBuilder();
			HttpClient placesClient = new DefaultHttpClient();
			HttpGet placesGet = new HttpGet(placesURL[0]);

			try {
				HttpResponse placesResponse = placesClient.execute(placesGet);
				StatusLine placeSearchStatus = placesResponse.getStatusLine();

				if (placeSearchStatus.getStatusCode() == RESULT_OK) {

					HttpEntity placesEntity = placesResponse.getEntity();
					InputStream placesContent = placesEntity.getContent();
					InputStreamReader placesInput = new InputStreamReader(placesContent);
					BufferedReader placesReader = new BufferedReader(placesInput);

					String lineIn;
					while ((lineIn = placesReader.readLine()) != null) {
						placesBuilder.append(lineIn);
					}
					return placesBuilder.toString();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

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
