package com.oe.ourvillage;

import android.provider.BaseColumns;

public final class ImageLocationDatabase {
    public static final class ImageLocations implements BaseColumns{
          public static final String IMAGE_TABLE_NAME = "images";
          public static final String IMAGE_NAME = "imagename";
          public static final String IMAGE_LONGITUDE = "longitude";
          public static final String IMAGE_LATITUDE = "latitude";
          public static final String IMAGE_CAPTION= "caption";
          public static final String DEFAULT_SORT_ORDER ="";
          private ImageLocations(){}
    }
    private ImageLocationDatabase(){}
}
