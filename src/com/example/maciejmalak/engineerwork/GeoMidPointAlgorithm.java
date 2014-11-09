package com.example.maciejmalak.engineerwork;

import java.util.HashMap;
import java.util.Map.Entry;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class GeoMidPointAlgorithm {
	
	private static HashMap<String, LatLng> allPeoplePositions = 
												new HashMap<String, LatLng>();
	private static HashMap<String, CartesiansCoordinates> allPositionsAsCartesianCoordinates = 
												new HashMap<String, CartesiansCoordinates>();
	
	private static double LAT, LNG;
	public static LatLng geographicMidpoint,
						 centerMinDistance,
						 averageLatLng;
	
	public static void registerPositions(String key, Location position) {
		
		LatLng pos = LocalizationCalculationHelper
						.geoPointFromLocalization(position);
		allPeoplePositions.put(key, pos);	
	}
	
	public static LatLng geographicMidpointAlgorithm() {
		int amoutOfPositions = 0;
		double
			avrX = 0, 
			avrY = 0,
			avrZ = 0;
		
		/* Converting stored positions to Cartesian Coordinates */
		settingContexOfCartesianCoordinatesHashMap();
		
		/* Counting center of gravity - geo midpoint */
		for(Entry<String, CartesiansCoordinates> entry : 
			allPositionsAsCartesianCoordinates.entrySet()) {
			CartesiansCoordinates coordinates = entry.getValue();
			
			avrX+=coordinates.getX();
			avrY+=coordinates.getY();
			avrZ+=coordinates.getZ();
			amoutOfPositions++;
		}
		
		avrX = avrX/amoutOfPositions;
		avrY = avrY/amoutOfPositions;
		avrZ = avrZ/amoutOfPositions;
	
		/* Here LNG and LAT are radians.
		 * We ought to convert them first to degrees 
		 * and next to LatLng android object
		 */
		double hyp = Math.sqrt((avrX*avrX)+(avrY*avrY));
		LNG = Math.atan2(avrY, avrX);
		LAT = Math.atan2(avrZ, hyp);
		
		return convertRadiansToDegreesAndThenToLatLng(LAT,LNG);
	}
	
	protected static void settingContexOfCartesianCoordinatesHashMap() {
		
		for(Entry<String, LatLng> entry : allPeoplePositions.entrySet()) {
		    String key = entry.getKey();
		    LatLng pos = entry.getValue();

		    allPositionsAsCartesianCoordinates.put(key, 
		    		LatLngToCartesian(pos));
		}
	}
	
	protected static CartesiansCoordinates LatLngToCartesian(LatLng position) {
		
		double lat = Math.toRadians(position.latitude);
	    double lng = Math.toRadians(position.longitude);
	    
	    double x = Math.cos(lat) * Math.cos(lng);
	    double y = Math.cos(lat) * Math.sin(lng);
	    double z = Math.sin(lat);
	    
		return new CartesiansCoordinates(x,y,z);
	}
	
	protected static LatLng convertRadiansToDegreesAndThenToLatLng(double lat, double lng) {
		return new LatLng(convertToDegrees(lat), convertToDegrees(lng));
	}
	
	protected static double fromDegreesToRadians(double degree) {
		double rad = degree * (Math.PI/180);
		return rad;
	}
	
	protected static double convertToDegrees(double rad) {
		double degree = rad * (180/Math.PI);
		return degree;
	}
}
