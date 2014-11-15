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
		this.coder = new Geocoder(appContext);
	}

	public Location getLocationFromAddress(String enteredAddress){
		
		List<Address> address;  
		Location currentlyAdding = new Location(enteredAddress);
		
		try {
		    address = coder.getFromLocationName(enteredAddress,1);
		    
		    if (address != null) { 	
			    Address currentLocation = address.get(0);
			    currentlyAdding.setLatitude(currentLocation.getLatitude());
			    currentlyAdding.setLongitude(currentLocation.getLongitude());	
			    			    
			    Toast.makeText(appContext,
			    		currentlyAdding.toString() , Toast.LENGTH_SHORT)
		                .show();
			    
			    return currentlyAdding;
		    }   
		} catch (Exception e) { e.printStackTrace(); 
		} finally {}
		
		return null;
	}
	
	
	public String getAdressFromLocation(Location loc){
		
		List<Address> address;  
		try {
		    address = coder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
		    
		    if (address != null && address.size() > 0) { 	
			    Address searchedAddress = address.get(0);
			    String addressToBeReturned = searchedAddress.getAdminArea() + " " +
			    		searchedAddress.getCountryCode() + " " +
			    		searchedAddress.getFeatureName() + " " +
			    		searchedAddress.getLocality() + " " +
			    		searchedAddress.getSubAdminArea() + " " +
			    		searchedAddress.getThoroughfare();
			    		
			    		
			    return addressToBeReturned;
		    }   
		} catch (Exception e) { e.printStackTrace(); 
		} finally {}
		
		return "Cannot decode to address";
	}
}
