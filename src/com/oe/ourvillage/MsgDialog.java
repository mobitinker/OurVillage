package com.oe.ourvillage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MsgDialog extends Dialog {
	
    public interface ReadyListener {
        public void ready(String name);
    }

    private class OKListener implements android.view.View.OnClickListener {
      @Override
      public void onClick(View v) {
    	  MsgDialog.this.dismiss();
    	  readyListener.ready(String.valueOf(etName.getText()));
      }
    }
    private final String name;
    private final String lat, lon;
    private final ReadyListener readyListener;
    
    EditText etName;

    public MsgDialog(Context context, String name, String lat, String lon, ReadyListener readyListener) {
      super(context);
      this.name = name;
      this.lat = lat;
      this.lon = lon;
      this.readyListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.msg_dialog);
      
      Button buttonOK = (Button) findViewById(R.id.Button01);
      TextView latitude = (TextView) findViewById(R.id.lat);
      TextView longitude = (TextView) findViewById(R.id.lon);
      
      latitude.setText(lat);
      longitude.setText(lon);
      
      setTitle("Enter a chalk about this picture");
      
      buttonOK.setOnClickListener(new OKListener());
      etName = (EditText) findViewById(R.id.EditTextMsg);
    }
}
