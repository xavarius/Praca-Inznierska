package com.example.maciejmalak.engineerwork;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
    private LocationManager minorLocalizationManager;
    private LocationProvider LocationProvider;
    private boolean GPSenabled;
	private boolean NETenabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        try {
            loadingObjectOfMainMap();
         } catch (Exception e) {
            e.printStackTrace();
        }
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
    	
    	if (minorLocalizationManager.isProviderEnabled(
    			android.location.LocationManager.GPS_PROVIDER)) {
		    LocationProvider = new LocationProvider(minorLocalizationManager, map);
		    LocationProvider.setUpStartingLocation();
		    
		    Toast.makeText(getApplicationContext(),
	                "geoplace:" +  LocationProvider.getPhoneStartingPoint().toString(), Toast.LENGTH_SHORT)
	                .show();
    	}
    }
    
    protected boolean isGPSEnabled() {
    	if(minorLocalizationManager != null
    		&&	minorLocalizationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
    		return true;
    	}
    	return false;
    }
    
    protected void checkProviders() {
		
		minorLocalizationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
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
        	checkProviders();
            setLocationOnMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        /*if (LocationProvider != null) {
        	LocationProvider.getLocationManager().removeUpdates(this);
        }*/
      }

    @Override
    protected void onResume() {
        super.onResume();
        /*LocationProvider.getLocationManager().requestLocationUpdates(LocationProvider.getProviderName(), 400, 1, this);
         loadingObjectOfMainMap();*/
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

}
