package com.oe.ourvillage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpFileUploader {

	String pathToOurFile = "/data/file_to_send.mp3";
	String pathOnServer = "junk.jpg";
	String urlServer = "eitc.comze.com/chalk/upload_file.php";

	//TODO mkm clean up
	HttpFileUploader(String uploadURL, String localFileName, String serverFileName ){
		try
		{
			urlServer = uploadURL;
			pathToOurFile = localFileName;
			pathOnServer = serverFileName;
		}
		catch(Exception ex)
		{
			Log.i("URL FORMATION","MALFORMATED URL");
		}
	}

	void upload(){
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		//DataInputStream inputStream = null;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try
		{
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");
			
			//TODO mkm Need to designate type of file as jpeg so we can check for it on server side as 
			//security

			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

			outputStream = new DataOutputStream( connection.getOutputStream() );
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + pathToOurFile +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0)
			{
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

			// Responses from the server (code and message) for debugging
			
			Object response = connection.getContent();
			int serverResponseCode = connection.getResponseCode();
			BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)response));
			String line, result = "";
	          while ((line = br.readLine()) != null)
	               result += line;
			String serverResponseMessage = connection.getResponseMessage();
			
		}
		catch (Exception ex)
		{
			//Exception handling
			Log.d("JUNK", ex.getMessage());
		}
	}

}
