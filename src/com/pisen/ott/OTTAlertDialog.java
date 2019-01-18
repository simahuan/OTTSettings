package com.pisen.ott;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pisen.ott.settings.R;

public class OTTAlertDialog extends OTTDialog {
	Button btnOk;
	Button btnCancel;
	public OTTAlertDialog(Context context) {
		super(context, R.style.AppDialog);
		initView(context);
		try
		 {
		    Field field = this.getClass().getSuperclass().getDeclaredField("mShowing");
		    field.setAccessible(true);
		      //设置mShowing值，欺骗android系统
		    field.set(this, true);
		 } catch(Exception e) {
		    e.printStackTrace();
		 }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	public void initView(Context context) {
		setContentView(R.layout.ui_dialog_alert);
		btnOk =  (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
	}
	public void setOkListener(View.OnClickListener listener){
		btnOk.setOnClickListener(listener);
	}
	
    public void setCancelListener(View.OnClickListener listener){
    	btnCancel.setOnClickListener(listener);
	}
}
