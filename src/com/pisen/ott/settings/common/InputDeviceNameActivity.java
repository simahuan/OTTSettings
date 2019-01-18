package com.pisen.ott.settings.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;
import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;

/**
 * TODO
 * @author  mahuan
 * @date    2014年12月10日 下午5:20:39
 */
public class InputDeviceNameActivity extends OTTBaseActivity {
		private EditText edtDeviceName;
		private String devName;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.common_device_name);
			initView();
		}
		
		private void initView(){
			edtDeviceName = (EditText) findViewById(R.id.edtDeviceName);
			edtDeviceName.setText(SettingConfig.getDeviceName());
			edtDeviceName.setSelection(edtDeviceName.length());
		}
		
		public String getDeviceName(){
			devName = edtDeviceName.getText().toString().trim();
			if(null==devName||""==devName){
			   return "";
			  }else {
			   SettingConfig.setDeviceName(devName);
			}
			return devName;
		}
		
		
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode==KeyEvent.KEYCODE_BACK){
				if("".equals(getDeviceName())){
					Toast.makeText(this, "设备名称不能为空", Toast.LENGTH_LONG).show();
				}else {
					Intent intent = new Intent();
					intent.putExtra(SettingConfig.getDeviceName(), getDeviceName());
					setResult(RESULT_OK,intent);
					finish();
				}
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}
}
