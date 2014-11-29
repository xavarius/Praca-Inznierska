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

public class JSONParser {
	
	private static final int RESULT_OK = 200;

    public static String getJSONFromUrl(String url) {
    	
    	StringBuilder placesBuilder = new StringBuilder();
		HttpClient placesClient = new DefaultHttpClient();
		HttpGet placesGet = new HttpGet(url);

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
				placesContent.close();
				placesReader.close();
				return placesBuilder.toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }
}