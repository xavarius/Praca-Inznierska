package com.example.maciejmalak.engineerwork;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MainActivity extends ActionBarActivity
implements NavigationDrawerFragment.NavigationDrawerCallbacks, LocationListener {

	private static final int NEW_POINT_ADDER = 1;
	/**
	 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in {@link #restoreActionBar()}.
	 */
	private CharSequence appTitle;
	private static GoogleMap map;
	private LocationManager locationMgr;
	private MarkerMaintenance MarkerFactory;
	private PlacesAPIMaintenance placesOnMap;
	private String providerName;
	private static String DIRECTION_URI;
	private Criteria criteria;
	private Location userStartingPoint;
	private static List<Polyline> polylineOnMap = new ArrayList<Polyline>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* ----------------- NavigationDrawer Section ----------------- */
		appTitle = getTitle();
		mNavigationDrawerFragment = (NavigationDrawerFragment)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));

		/* ----------------- Localization Section ----------------- */

		locationMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		providerName = locationMgr.getBestProvider(criteria, true);

		/* ----------------- Map Section ----------------- */
		try {
			loadingObjectOfMainMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* ----------------- Providers Section ----------------- */
		checkProvidersNET();
		checkProvidersGPS();
	} /* onCreate */
	
	protected void loadingObjectOfMainMap() {
		if( map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
					.getMap();
			map.setMyLocationEnabled(true);
			
			MarkerFactory = new MarkerMaintenance(
					map, getString(R.string.action_my_start),
					getString(R.string.curr_position), this,
					getString(R.string.meet_place));

			map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

				@Override
				public void onInfoWindowClick(Marker marker) {

					String key = marker.getTitle();
					MarkerFactory.removeSelectedMarkerFromMap(key);
				}
			});

			map.setOnMarkerDragListener(new OnMarkerDragListener() {

				@Override
				public void onMarkerDragEnd(Marker marker) {
					String key = marker.getTitle();
					Location position = 
							LocalizationCalculationHelper.LocalizationFromGeopoint(marker.getPosition());
					MarkerFactory.registerMarkerOnMap(key, position);
					GeoMidPointAlgorithm.registerPositions(key,position);
				}

				@Override
				public void onMarkerDrag(Marker arg0) {}

				@Override
				public void onMarkerDragStart(Marker arg0) {}
			});	
		}
	}
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
		.commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			makeURLForDirectionRequest();
			new connectAsyncTask().execute();
			break;	
		case 2:
			getPlacesNearbyMeetingPlace();
			break;		
		case 3:
			removePolylineFromMap();
			break;		
		case 4:
			removeNearbyPlaces();
			break;
		case 5:
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case 6:
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case 7:
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case 8:
			map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case 9:
			map.setMyLocationEnabled(false);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(appTitle);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		boolean ifDone = false;

		switch (id) {
		case R.id.meet_place:
			setUpMeetingPlace();
			ifDone = true;
			break;
		case R.id.friends_point:
			navigateToNewPointActivity();
			ifDone = true;
			break;
		case R.id.remove_all_places:
			MarkerFactory.clearMarkerMap();
			ifDone = true;
			break;
		case R.id.action_my_place:
			if(isGPSEnabled()) {  setUpStartingLocation(); } 
			else 			   {  checkProvidersGPS();     }
			ifDone = true;
			break;
		}

		if (ifDone) return true;
		return super.onOptionsItemSelected(item);
	}
	
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (locationMgr != null) {
			locationMgr.removeUpdates(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		locationMgr.requestLocationUpdates(providerName, 400, 1, this);
		loadingObjectOfMainMap();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			MarkerFactory.registerMarkerOnMap(getString(R.string.curr_position),location);
		}	
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}

	
	/* ----------- Others methods ----------------------------------------- */
	

	protected Location getCurrentLocation() {
		return locationMgr.getLastKnownLocation(providerName);		
	}

	protected boolean isGPSEnabled() {
		if(locationMgr != null
				&&	locationMgr.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			return true;
		}
		return false;
	}

	public boolean isInternetEnabled() {
		ConnectivityManager cManager =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cManager.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	protected void checkProvidersGPS() {

		if (!isGPSEnabled()) {	
			AlertDialog.Builder alertWindow = new AlertDialog.Builder(this);
			alertWindow.setTitle(R.string.gps_distabled)
			.setMessage(R.string.on_question)
			.setCancelable(true)
			.setPositiveButton(R.string.yes, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(
							new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
							);	
				}
			} 
					)
					.setNegativeButton(R.string.no, 
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}); 	
			AlertDialog alert = alertWindow.create();
			alert.show();	
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.gps_enabled, Toast.LENGTH_SHORT)
					.show();
		}	
	}

	protected void checkProvidersNET() {

		if (!isInternetEnabled()) {

			AlertDialog.Builder alertWindow = new AlertDialog.Builder(this);
			alertWindow.setTitle(R.string.net_distabled)
			.setMessage(R.string.on_question)
			.setCancelable(true)
			.setPositiveButton(R.string.yes, 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					startActivity(
							new Intent(Settings.ACTION_WIFI_SETTINGS)
							);	
				}
			} 
					)
					.setNegativeButton(R.string.no, 
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}); 	
			AlertDialog alert = alertWindow.create();
			alert.show();	
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.net_enabled, Toast.LENGTH_SHORT)
					.show();
		}	
	}

	protected void setUpStartingLocation() {

		this.userStartingPoint = getCurrentLocation();
		if (this.userStartingPoint != null) {

			System.out.println(userStartingPoint.toString());
			MarkerFactory.registerMarkerOnMap(getString(R.string.action_my_start), 
					this.userStartingPoint);
			GeoMidPointAlgorithm.registerPositions(getString(R.string.action_my_start), 
					this.userStartingPoint);
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.start_pos_is_not_set, 
					Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void setUpMeetingPlace() {
		MarkerFactory.registerMarkerOnMap(
				getString(R.string.meet_place),
				getCurrentLocation());
	}

	protected void navigateToNewPointActivity() {
		Intent newPointIntent = new Intent(this, NewPoint.class);
		startActivityForResult(newPointIntent,NEW_POINT_ADDER);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == NEW_POINT_ADDER && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			if(extras !=null) {

				HashMap<String, Location> allPointsProvidedInNewPoint
				= (HashMap<String, Location>) 
				extras.getSerializable("collectionOfPlaces");

				if (allPointsProvidedInNewPoint != null) {
					storeLocalizationsFromNewPointToMap(allPointsProvidedInNewPoint);
				}
			}
		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(getApplicationContext(),
					R.string.not_saved_changes, Toast.LENGTH_SHORT)
					.show();
		}
	}

	protected void storeLocalizationsFromNewPointToMap(HashMap<String, Location> allPointsProvidedInNewPoint) {
		for(Entry<String,Location> entry : allPointsProvidedInNewPoint.entrySet()) {
			String key = entry.getKey();
			Location val = entry.getValue();
			MarkerFactory.registerMarkerOnMap(key,val);
			GeoMidPointAlgorithm.registerPositions(key,val);
		}
	}
	
	protected void getPlacesNearbyMeetingPlace() {
		if(MarkerFactory.getMeetingPlaceLatLng() != null) {
			LatLng meetingPos = MarkerFactory.getMeetingPlaceLatLng();
			placesOnMap = new PlacesAPIMaintenance(this.map);
			try {
				placesOnMap.settingURI(meetingPos);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}	
		}
	}
	
	protected void removeNearbyPlaces() {
		if(placesOnMap != null) placesOnMap.removeAllPlaces();
	}
	
/* -------------------------------- rysowanie drogi g�wno */
	
	public void removePolylineFromMap() {
		if(polylineOnMap!=null) {
			for(Polyline line : polylineOnMap) {
				line.remove();
			}
			polylineOnMap.clear();
		}
		
	}
	
	public void makeURLForDirectionRequest(){
		double sourcelat, sourcelog,  destlat, destlog;
		
		LatLng meetPos = MarkerFactory.getMeetingPlaceLatLng();
		LatLng startPos = MarkerFactory.getStartPlaceLatLng();
	
		System.out.println(meetPos.toString());
		System.out.println(startPos.toString());
		
		if(meetPos != null && startPos != null) {
			destlat = meetPos.latitude;
			destlog = meetPos.longitude;
			sourcelat = startPos.latitude;
			sourcelog = startPos.longitude;
				
			StringBuilder urlString = new StringBuilder();
			urlString.append("http://maps.googleapis.com/maps/api/directions/json");
			urlString.append("?origin=");
			urlString.append(Double.toString(sourcelat));
			urlString.append(",");
			urlString
			.append(Double.toString( sourcelog));
			urlString.append("&destination=");// to
			urlString
			.append(Double.toString( destlat));
			urlString.append(",");
			urlString.append(Double.toString( destlog));
			urlString.append("&sensor=false&mode=driving&alternatives=true");
			DIRECTION_URI =  urlString.toString(); 
		} else {
			Toast.makeText(getApplicationContext(),
					R.string.cannot_dir, Toast.LENGTH_SHORT)
					.show();
		}
	}

	
	public void drawPath(String  result) {

	    try {
	           final JSONObject json = new JSONObject(result);
	           JSONArray routeArray = json.getJSONArray("routes");
	           JSONObject routes = routeArray.getJSONObject(0);
	           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
	           String encodedString = overviewPolylines.getString("points");
	           List<LatLng> list = decodePoly(encodedString);

	           for(int z = 0; z<list.size()-1;z++){
	                LatLng src= list.get(z);
	                LatLng dest= list.get(z+1);
	                Polyline line = map.addPolyline(new PolylineOptions()
	                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
	                .width(5)
	                .color(Color.RED).geodesic(true));
	                polylineOnMap.add(line);
	            }

	    } 
	    catch (JSONException e) {

	    }
	}
	
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}
	
	private class connectAsyncTask extends AsyncTask<Void, Void, String>{
	    private ProgressDialog progressDialog;
	    
	    @Override
	    protected void onPreExecute() {
	       
	        super.onPreExecute();
	        progressDialog = new ProgressDialog(MainActivity.this);
	        progressDialog.setMessage("Fetching route, Please wait...");
	        progressDialog.setIndeterminate(true);
	        progressDialog.show();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	
	    	if(DIRECTION_URI != null) {
	    		JSONParser jParser = new JSONParser();
	    		String json = jParser.getJSONFromUrl(DIRECTION_URI);
	    		return json;
	    	}
	    	return null;
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);   
	        progressDialog.hide();        
	        if(result!=null){
	            drawPath(result);
	        }
	    }
	}
} /* Main Activity */
