package com.oe.ourvillage;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class PostImageTask extends AsyncTask<String, Void, Void>{

  String TAG = "PostImageTask";

  HashMap<String, String> NameValuePair; // = new HashMap<String, String>(); // = new List<NameValuePair>();
  List<NameValuePair> imageList = null; // = new List<HashMap>();
  //Image images = new Image();
  
  @Override
  protected Void doInBackground(String... urls) {
    post(urls[0], imageList);
    return null;
  }

  @Override
  protected void onPreExecute(){
    
    // instantiate the namevaluepair with image name
    NameValuePair = new HashMap<String, String>();

    //get image name and place the image and insert into hashmap
    NameValuePair.put("image",getImageName() );
    
    //add to list
    //imageList.add(NameValuePair);
    
  }
  
  private String getImageName() {
    // return image name
    return Image.name;
  }

  //Actual construction of the post command for sending the image
  private void post(String url, List<NameValuePair> nameValuePairs) {
    HttpClient httpClient = new DefaultHttpClient();
    HttpContext localContext = new BasicHttpContext();
    HttpPost httpPost = new HttpPost(url);

    try {
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for(int index=0; index < nameValuePairs.size(); index++) {
            if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
                // If the key equals to "image", we use FileBody to transfer the data
                entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File (nameValuePairs.get(index).getValue())));
            } else {
                // Normal string data
                entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
            }
        }

        httpPost.setEntity(entity);

        HttpResponse response = httpClient.execute(httpPost, localContext);
    } catch (IOException e) {
      
        Log.d(TAG, "IOException" + e);
        e.printStackTrace();
    }
  }

}
