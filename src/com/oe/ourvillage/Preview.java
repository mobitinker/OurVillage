package com.oe.ourvillage;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class Preview extends SurfaceView implements SurfaceHolder.Callback{

	public Camera camera;
	SurfaceHolder mHolder;
	private final String TAG = "Preview";

	public Preview(Context context) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		Camera.Parameters p = camera.getParameters();
		//mkm Added setting of format and picture size
		Size s = p.getPictureSize();	//debug shows 1280x960
		p.setPreviewSize(w, h);
		//TODO mkm Unfortunately l shows 1280x960 as the smallest size. 
		//List<Size> l = p.getSupportedPictureSizes();
		//List<Integer> spf = p.getSupportedPictureFormats();	//Droid only supports JPEG
		p.setPictureSize(640, 480);	//Will set to smallest available
		//int i = p.getPictureFormat();
		
		//p.setPictureFormat(PixelFormat.RGB_565);	//Not supported. Causes setParameters() to crash
		p.setPictureFormat(PixelFormat.JPEG);
		
		//int i = p.getJpegQuality();
		//p.setJpegQuality(30);	//Want small file. This is only way at moment. Less than 30 looks bad
		
		camera.setParameters(p);
		camera.startPreview();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();

		try 
		{
			camera.setPreviewDisplay(holder);
		} 
		catch (IOException e) 
		{
			Log.d(TAG, "IOException thrown" + e);
			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

}
