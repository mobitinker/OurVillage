package com.oe.ourvillage;

import com.oe.ourvillage.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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
    private final ReadyListener readyListener;
    
    EditText etName;

    public MsgDialog(Context context, String name, ReadyListener readyListener) {
      super(context);
      this.name = name;
      this.readyListener = readyListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.msg_dialog);
      setTitle("Enter a chalk about this picture");
      Button buttonOK = (Button) findViewById(R.id.Button01);
      buttonOK.setOnClickListener(new OKListener());
      etName = (EditText) findViewById(R.id.EditTextMsg);
    }
}
