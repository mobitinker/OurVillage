package com.oe.ourvillage;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChalkPoster {

	String urlServer = "http://zeum.blockchalk.com/api/v0.6/";//"http://blockchalk.com/api/v0.6/";
	String id = "";
	String TAG = "TAG";

	ChalkPoster(){
		try
		{
		}
		catch(Exception ex)
		{
			Log.i("JUNK", ex.getMessage());
		}
	}

	// Get a user ID to use in subsequent calls
	// Currently using the constant in OurVillage.java. No need to call this function
	String getUser() {
		if (id.length() > 0)
		{
			return id;
		}
		
		HttpURLConnection connection = null;

		String retVal = "";
		String result = "";
		
		try
		{
			//No need to use User Agent. Consumer=OEC is adequate. OEC is not registered. Any string ok
			URL url = new URL(urlServer + "user/new?consumer=oec");
			
			connection = (HttpURLConnection) url.openConnection();
			
			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			//mkm Trying this because getting "Too many redirects" error
//			HttpURLConnection.setFollowRedirects(false);
			connection.setUseCaches(false);
			connection.setAllowUserInteraction(false);

			connection.setRequestMethod("GET");

			connection.connect();

			int resCode = connection.getResponseCode();

			InputStream in = connection.getInputStream(); 

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
        result += line;
      }
			if ((resCode == HttpURLConnection.HTTP_OK) && (result.length() > 0)) 
			{
				retVal = result.trim();
			} 

		}
		catch (Exception ex)
		{
			//Exception handling
			Log.d("JUNK", ex.getMessage());
		}

		id = retVal;
		
		return retVal;
	}

	// Post a chalk
	String post(String msg, String lat, String lon, String id, String category){
		
		String retVal = "junk";	//If problem occurs, file uploaded will be junk.jpg
		
		//URLEncoding
		String modMsg = "";
		
    try {
      
      modMsg = URLEncoder.encode((msg.length() > 0 ? msg : "Picture only"),"UTF-8");
      
    } catch (UnsupportedEncodingException e) {
        //Error
        Log.e(TAG," URLEncoder error: "+ e);
    };
		
		//mkm This method uses the simple httpclient
		HttpClient httpclient = new DefaultHttpClient();

		//Put the params in query string even though it's a post
		HttpPost httppost = new HttpPost(urlServer + "chalk?consumer=oec&msg=" + modMsg + "&lat=" + lat + "&long=" + lon + "&user=" + id + "&format=json" );

		try {

			HttpResponse response;
			response = httpclient.execute(httppost);
			
			System.out.println("Response: " + response);
			Log.d(TAG, ">>>> RESPONSE: "+ response);
			
			//Show the response for troubleshooting. Should be XML or JSON
			String s = EntityUtils.toString(response.getEntity()); 
			//TODO parse response
			
			try{
				JSONObject json = new JSONObject(s);
				JSONArray nameArray = json.names();
				JSONArray valArray = json.toJSONArray(nameArray);
				//TODO probably a smoother more robust way to do this
				if (nameArray.getString(0).equals("id"))
				{
					retVal = valArray.getString(0);
				}
			}

			catch (JSONException e) {
				Log.d("JSON", "There was an error parsing the JSON", e);
			} catch(Exception e) {
				e.printStackTrace();

			}
				
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
}
