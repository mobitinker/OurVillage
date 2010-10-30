package com.oe.ourvillage;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class OurVillage extends Activity implements LocationListener{

  private class MsgDialogCallback implements MsgDialog.ReadyListener {
		@Override
		public void ready(String chalk) {
			FileOutputStream out = null;

			image.caption = chalk;
			
			//Let user know this will take a little time
			Toast.makeText(OurVillage.this, "Posting to server...", Toast.LENGTH_LONG).show();

			ChalkPoster cp = new ChalkPoster();
			
			//TODO mkm Last param is category. If unspecified, BlockChalk puts chalk into 'chatter' category
			String chalkID = cp.post(chalk, lat, lon, chalkUser, "");
			
			filename = Environment.getExternalStorageDirectory().toString() + "/"+ chalkID + ".jpg";
			out = null;
			try {
				File file = new File(filename);
				out = new FileOutputStream(file);
				Bitmap bm = BitmapFactory.decodeByteArray(pictureBuffer, 0, pictureBuffer.length);
				Bitmap sbm = Bitmap.createScaledBitmap(bm, 640, 480, false);
				sbm.compress(Bitmap.CompressFormat.JPEG, 40, out);
				out.close();
				bm.recycle();
				sbm.recycle();
				Log.d(TAG, "dialog done");
				String url = "http://eitc.comze.com/chalk/upload_file.php";				
				HttpFileUploader uploader = new HttpFileUploader(url, filename, chalkID);
				uploader.upload();

				file.delete();
			} catch(Exception ex) {
				Log.d(TAG, "dialog problem");
				
			}
			
			//Display the entered message
			Toast.makeText(OurVillage.this, chalk, Toast.LENGTH_LONG).show();
		}
	}

	private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
      lat = Double.toString(location.getLatitude());
      lon = Double.toString(location.getLongitude());

      //String Text = "My current location is: " +  "Latitude = " + location.getLatitude() + "Longitude =" + location.getLongitude();
      //Toast.makeText( getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
      
    }

    @Override
    public void onProviderDisabled(String provider) {
      // TODO Auto-generated method stub
      Toast.makeText( getApplicationContext(),"GPS Disabled", Toast.LENGTH_SHORT ).show();
      
    }

    @Override
    public void onProviderEnabled(String provider) {
      // TODO Auto-generated method stub
      Toast.makeText( getApplicationContext(), "GPS Enabled",Toast.LENGTH_SHORT).show();
      
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
      // TODO Auto-generated method stub
      
    }
  
  }
	
	private static final String TAG = "OurVillage";

	//Use the same chalk user
	private static final String chalkUser = "0add24dec4864bc68bc2211b6cea0810";
	byte[]pictureBuffer;
	
	int lenPictureBuffer;

	Preview preview;
	Button buttonClick;

	//Button buttonExit;

	FrameLayout frame;

	TextView locate;

	LocationManager locationManager;

	Image image = new Image();

	private String filename;

	
	private SQLiteDatabase db;
	
	private String bestProvider;
	private String lat = "";
	private String lon = "";

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "Shutter with onShutter method");
		}
	};

	//Handles data for raw picture - ignored, though could be used for image reduction
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};


	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

			preview.camera.startPreview();

			//TODO mkm Some of this could be put in asynch threads
			//Dialog to get chalk message
			//TODO mkm would be nice to freeze image taken
			MsgDialog dialog = new MsgDialog(OurVillage.this, "", new MsgDialogCallback());
			dialog.show();

			pictureBuffer = data.clone();
			lenPictureBuffer = data.length;
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	public void myLocation(){

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		bestProvider = locationManager.getBestProvider(criteria, false);
		Log.d(TAG, "Best provider : " + bestProvider);

		LocationListener mlocListener = new MyLocationListener();
		locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		
		
		//Location location = locationManager.getLastKnownLocation(bestProvider);
		//filename = "IMAG" + System.currentTimeMillis() + ".jpg" ;
		//Log.d(TAG,"Filename: " + filename );

		/* TODO mkm
		image.name = filename;
		image.latitude = Double.toString(location.getLatitude());
		image.longitude = Double.toString(location.getLongitude());
		image.caption = "TEST CAPTION";
		*/
		
/*		lat = Double.toString(location.getLatitude());
		lon = Double.toString(location.getLongitude());*/
		
		//Toast.makeText(this, "Latitude: "+ Double.toString( location.getLatitude())+ " Longitude: " + Double.toString(location.getLongitude()), Toast.LENGTH_SHORT).show();
		//return "Latitude: "+ Double.toString( location.getLatitude())+ "\nLongitude: " + Double.toString(location.getLongitude());

	}

	// Called when the activity is first created 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		preview = new Preview(this);
		frame = (FrameLayout) findViewById(R.id.preview);
		frame.addView(preview);

		locate = (TextView) findViewById(R.id.locate);
		//buttonExit = (Button) findViewById(R.id.buttonExit);
		buttonClick = (Button) findViewById(R.id.buttonClick);

		buttonClick.setOnClickListener(new OnClickListener() {
			//Handle camera shutter click
			public void onClick(View v) {

				//mkm Might call this at start up too to get GPS woken up
				myLocation();

				//Dialog and chalk post moved to jpegCallback
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);


				//locate.setText("Long: "+ image.longitude + "\nLat: " + image.latitude );


				/* TODO figure this out
            //Insert into db
            boolean ispushed = pushintoDB(image);
            Log.d(TAG, " After PushintoDB: Inserted into database: "+ ispushed);

            //Post the image on to website
            //TODO crashing

				 */
				//new PostImageTask().execute("http://flicker.com/...", null, null);

				//mkm Need to restart preview mode after taking picture
				//preview.camera.startPreview();

			}

		});

/*		buttonExit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Exiting the Mobile App");
				onDestroy();
			}  
		});*/
		//TODO why is this commented?
		//preview.camera.release();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		OurVillage.this.finish();
	}


	@Override
	public void onLocationChanged(Location location) {
		if(location !=null){
			//mkm Todo: verify these will get hit
			lat = Double.toString(location.getLatitude());
			lon = Double.toString(location.getLongitude());
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private void printProvider(String provider) {
		// TODO Auto-generated method stub

	}
	//Storing the image data into database
	//NOT WORKING
	private boolean pushintoDB(Image imag) {
		try
		{  
			ContentValues values = ImageLocationSQLHelper.ImageToContentValues(imag);
			//db.insertOrThrow(ImageLocations.IMAGE_TABLE_NAME, null, values);
			Toast.makeText(this, "Inserted into database:\n File: "+ image.name + "\n Long: "+ image.longitude + "\n Lat: " + image.latitude, Toast.LENGTH_SHORT).show();
			//Log.d(TAG, " Inserted into DB Sucessfully");
		}
		catch(Exception e)
		{
			Log.e(TAG, "Problem Inserting an image data into database" + e);
			return false;
		}
		return true;
	} 
}