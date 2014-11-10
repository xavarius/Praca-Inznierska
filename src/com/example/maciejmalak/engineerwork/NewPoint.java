package com.example.maciejmalak.engineerwork;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class NewPoint extends ActionBarActivity {

	private Geocoder coder;
	private GetLocationFormAddress getLoc = new GetLocationFormAddress();
	private HashMap<String, Location> allPointsProvidedInEditText 
								= new HashMap<String, Location>();
	private static int Iterator = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_point);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		coder = new Geocoder(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_point, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_point,
					container, false);
			return rootView;
		}
	}
	
	public void savePlace (View view) {
		EditText et = (EditText)findViewById(R.id.input_place);
		getLoc.getLocationFromAddress(et.getText().toString());
	}
	
	public void doneAddingPlaces(View view) {
		if (allPointsProvidedInEditText == null) {
			setResult(RESULT_CANCELED);
			finish();
		}
		Bundle extras = new Bundle();
		extras.putSerializable("collectionOfPlaces", allPointsProvidedInEditText);
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtras(extras);
		setResult(RESULT_OK,intent);
		finish();
	}
	
	public class GetLocationFormAddress {
			
		public GetLocationFormAddress() {
	
		}
		
		public void getLocationFromAddress(String enteredAddress){
			
			List<Address> address;  
			Location currentlyAdding = new Location(enteredAddress);
			
			try {
				/* Searching for only one Address */
			    address = coder.getFromLocationName(enteredAddress,1);
			    
			    if (address != null) { 	
			    	Iterator++;
				    String nameOfPoint = "Point nr " + Iterator;
				    Address currentLocation = address.get(0);
				    currentlyAdding.setLatitude(currentLocation.getLatitude());
				    currentlyAdding.setLongitude(currentLocation.getLongitude());	    
				    			    
				    Toast.makeText(getApplicationContext(),
				    		currentlyAdding.toString() , Toast.LENGTH_SHORT)
			                .show();
				    
				    registerLocationsThatWillBeDisplayedOnMap(nameOfPoint,currentlyAdding);
			    }   
			} catch (Exception e) { e.printStackTrace(); 
			} finally {}
			
		}
		
		public void registerLocationsThatWillBeDisplayedOnMap(String key, Location loc) {
			allPointsProvidedInEditText.put(key, loc);
		}
	}
	
}
