package com.example.maciejmalak.engineerwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import com.google.android.gms.maps.model.MarkerOptions;

public class PlacesAPIMaintenance {
	
	private static final int RESULT_OK = 200;
	private static String placesURI;
	private static String APIKey = "AIzaSyAnWTUXFLuv2dUrPxXehSB9GR72cItHSdE";
	private GoogleMap map;
	
	public PlacesAPIMaintenance(GoogleMap map) {
		this.map = map;
	}
	public void settingURI(LatLng position) {
		double lat = position.latitude;
		double lng = position.longitude;
		
		placesURI = "https://maps.googleapis.com/maps/api/place/nearbysearch/" 
					+ "json?location="+lat+","+lng
					+ "&radius=300&sensor=true"
				/*	+ "&types=food|bar|store|museum|art_gallery"*/
					+ "&key=AIzaSyAFRRQIVS42Gj_fM6KjcuviNdnipQ-YL14";
		
		System.out.println("PRZED EXECUTE"); 
		System.out.println("PRZED EXECUTE");
		System.out.println("PRZED EXECUTE");
		System.out.println("PRZED EXECUTE");
		new GetPlaces().execute(placesURI);
	}
	
	
	public MarkerOptions getMarkerOptions(String key, LatLng pos) {
		
		System.out.println("w getMarkerOpt i mamy pos" + pos.toString()); 
		return new MarkerOptions()
	        .position(pos)
	        .title(key)
	        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
	}
	
	
	private class GetPlaces extends AsyncTask<String, Void, String> {
		
		
		@Override
		protected String doInBackground(String... placesURL) {
			
			
			System.out.println("jestesmy w doInBackground   doInBackground  " +
					"doInBackground   doInBackground   doInBackground");
			
			StringBuilder placesBuilder = new StringBuilder();
			
			HttpClient placesClient = new DefaultHttpClient();
			
			HttpGet placesGet = new HttpGet(placesURL[0]);
			
			try {
				HttpResponse placesResponse = placesClient.execute(placesGet);
				StatusLine placeSearchStatus = placesResponse.getStatusLine();
				
				if ( placeSearchStatus.getStatusCode() == RESULT_OK) {
					
					
					HttpEntity placesEntity = placesResponse.getEntity();
					InputStream placesContent = placesEntity.getContent();
					InputStreamReader placesInput = new InputStreamReader(placesContent);
					BufferedReader placesReader = new BufferedReader(placesInput);
					
					String lineIn;
					while ((lineIn = placesReader.readLine()) != null) {
					    placesBuilder.append(lineIn);
					}
					
					return placesBuilder.toString();
					
				} else {
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
					System.out.println("Coœ siê zjeba³o z PLACES API"); 
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		    
		}
		
		
		@Override
		   protected void onPostExecute(String result) {
			
			System.out.println("jestesmy w onPostExecute" + result);
			
			LatLng placeLL=null;
			String placeName="";
			JSONObject resultObject;
			try {
				resultObject = new JSONObject(result);
				JSONArray placesArray = resultObject.getJSONArray("results");
				
				
				for (int p=0; p<placesArray.length(); p++) {
					JSONObject placeObject = placesArray.getJSONObject(p);
					
					
					JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
					placeLL = new LatLng(
						    Double.valueOf(loc.getString("lat")),
						    Double.valueOf(loc.getString("lng")));
					placeName = placeObject.getString("name");
					
					 map.addMarker(getMarkerOptions(placeName,placeLL));
					
				}	
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
		}
		
		
		} /* ASync */
	
	

}
