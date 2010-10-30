package com.oe.ourvillage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.oe.ourvillage.ImageLocationDatabase.ImageLocations;

public class ImageLocationSQLHelper extends SQLiteOpenHelper{

  Context context;
  SQLiteDatabase db;
  
  //Database name
  private static final String DATABASE_NAME = "images.db";
  private static final int DATABASE_VERSION = 1;
  
  //Log info
  private static final String TAG = "NewsSQLHelper";

  public ImageLocationSQLHelper(Context context, String name, CursorFactory factory,
                                int version) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.context = context;
    
  }

  public static ContentValues ImageToContentValues(Image image){
    ContentValues values = new ContentValues();
    values.put(ImageLocations.IMAGE_NAME, image.name);
    values.put(ImageLocations.IMAGE_LATITUDE, image.latitude);
    values.put(ImageLocations.IMAGE_LONGITUDE, image.longitude);
    values.put(ImageLocations.IMAGE_CAPTION, image.caption); //
    Log.d(TAG, "IMAGE contentValues inserted");
    return values;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Create the images links table
    db.execSQL("CREATE TABLE "+ ImageLocations.IMAGE_TABLE_NAME +"("
                    + ImageLocations._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ImageLocations.IMAGE_NAME + " TEXT,"
                    + ImageLocations.IMAGE_LATITUDE + " TEXT,"
                    + ImageLocations.IMAGE_LONGITUDE + " TEXT,"
                    + ImageLocations.IMAGE_CAPTION + " TEXT"
                    +");"); 
    Log.d(TAG, "Created SQLiteDatabase");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // TODO Auto-generated method stub
    
  }
}
