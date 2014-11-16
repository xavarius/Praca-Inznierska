package com.example.maciejmalak.engineerwork;

import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

public class GeocodingTasks {
	
	private Context appContext;
	private Geocoder coder;
	
	public GeocodingTasks(Context c) {
		this.appContext = c;
		this.coder = new Geocoder(c);
	}

	public Location getLocationFromAddress(String enteredAddress){
		
		List<Address> address;  
		Location currentlyAdding = new Location(enteredAddress);
		
		try {
		    address = coder.getFromLocationName(enteredAddress,1);
		    
		    if (address != null && address.size() > 0) { 	
			    Address currentLocation = address.get(0);
			    currentlyAdding.setLatitude(currentLocation.getLatitude());
			    currentlyAdding.setLongitude(currentLocation.getLongitude());	
			    			    
			    Toast.makeText(appContext, 
			    		"You have added place: " + enteredAddress,
			    		Toast.LENGTH_SHORT)
		                .show();
			    
			    return currentlyAdding;
		    }   
		} catch (Exception e) { e.printStackTrace(); 
		} finally {}
		
		return null;
	}
}
