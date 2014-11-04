package com.example.maciejmalak.engineerwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LocationListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    /* Used within Google Map */
    private static GoogleMap map;
    private LocationManager locationMgr;
	private Criteria criteria;
	private String providerName;
	private Location currentLocation;
	private LatLng currentPosition;
	private LatLng phoneStartingPoint;
	private boolean GPSenabled;
	private boolean NETenabled;
	private Marker currentPositionAsMarker, startPointAsMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ----------------- NavigationDrawer Section ----------------- */
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
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
        
    } /* onCreate */
    
    
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
    
    /* Tworzenie obiektu GoogleMap poprzez pobranie referencji do fragmentu 
     * layoutu. Pozwolenie na ci¹g³¹ lokalizacjê (niebieska kropka).
     */
    protected void loadingObjectOfMainMap() {
    	if( map == null) {
	    	map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	                .getMap();
	    	if (map != null) {
	    		map.setMyLocationEnabled(true);
	    	} else {
	    		Toast.makeText(getApplicationContext(),
	                    "Mapa siê spierdoli³a", Toast.LENGTH_SHORT)
	                    .show();
	    	}
    	}
    }
    
    protected void setLocationOnMap() { 
    	
    	/* if (minorLocalizationManager.isProviderEnabled(
    			android.location.LocationManager.GPS_PROVIDER)) {
		    LocationProvider = new LocationProvider(minorLocalizationManager, map);
		    LocationProvider.setUpStartingLocation();
		    
		    String help = LocationProvider.getPhoneStartingPoint().toString();
		    if (help != null) {
		    Toast.makeText(getApplicationContext(),
	                "geoplace:" + help, Toast.LENGTH_SHORT)
	                .show();
		    } else {
		    	Toast.makeText(getApplicationContext(),
	                "CHUJ STRZELI£ STARTING POINT", Toast.LENGTH_SHORT)
	                .show();
		    }
    	}*/
    }
    
    protected boolean isGPSEnabled() {
    	if(locationMgr != null
    		&&	locationMgr.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
    		return true;
    	}
    	return false;
    }
    
    protected void checkProviders() {
		
		if (!isGPSEnabled()) {
			
        	AlertDialog.Builder alertWindow = new AlertDialog.Builder(this);
        	alertWindow.setTitle("GPS not enabled")
        		.setMessage("Do you want to turn it on?")
        		.setCancelable(true)
        		.setPositiveButton("Yes, I do", 
        			new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								startActivity(
		        					new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
		        				);	
							}
        				} 
        		)
        		.setNegativeButton("No", 
        			new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								finish();
							}
				}); 	
        	AlertDialog alert = alertWindow.create();
        	alert.show();
        	
        	if(isGPSEnabled()) {
        		GPSenabled = true;
        	} else {
        		Toast.makeText(getApplicationContext(),
                        "GPS nie jest dostêpny", Toast.LENGTH_SHORT)
                        .show();
        	}
        	
        } else {
        	Toast.makeText(getApplicationContext(),
                    "GPS jest dostêpny", Toast.LENGTH_SHORT)
                    .show();
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
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {	
        	
        	if(isGPSEnabled()) {
        		setUpStartingLocation();
        	} else {
        		checkProviders();
        	}
        	return true;
        }
        return super.onOptionsItemSelected(item);
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
        locationMgr.requestLocationUpdates(getProviderName(), 400, 1, this);
         loadingObjectOfMainMap();
     }
    

    /**
     * A placeholder fragment containing a simple view.
     */
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

        public PlaceholderFragment() {
        }

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
	public void onLocationChanged(Location location) {
		if (location != null) {
			currentPosition = geoPointFromLocalization(location);
			
			currentPositionAsMarker = map.addMarker(new MarkerOptions()
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
	
protected void setUpStartingLocation() {
		
		currentLocation = getCurrentLocation();
		if (currentLocation != null) {
			setPhoneStartingPoint(geoPointFromLocalization(currentLocation));
			
			String help = getPhoneStartingPoint().toString();
		    if (help != null) {
		    Toast.makeText(getApplicationContext(),
	                "geoplace:" + help, Toast.LENGTH_SHORT)
	                .show();
		    
		    startPointAsMarker = map.addMarker(new MarkerOptions()
            .position(phoneStartingPoint)
            .title("Starting Location"));
			
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(getPhoneStartingPoint(), 18.0f));
		    
		    } else {
		    	Toast.makeText(getApplicationContext(),
	                "CHUJ STRZELI£ STARTING POINT", Toast.LENGTH_SHORT)
	                .show();
		    }
			
			
		}
	}
	
	protected LatLng geoPointFromLocalization(Location loc){
		double latitude = loc.getLatitude();
		double longitude =  loc.getLongitude();
		LatLng here = new LatLng(latitude, longitude);
		return here;
	}

}
