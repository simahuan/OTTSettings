package com.pisen.ott.settings.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

/**
 * Wifi输入连接密码Activity
 * 
 * @author fq
 * @date 2014年12月15日 下午2:42:25
 */
public class WifiPasswordActivity extends OTTBaseActivity {
	TextView txtWifiPasswordTip;
	EditText edtPassword; 
	String ssid;
	int type;
	String flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_wifi_password);
		setTitle("无线网络","输入连接密码");
		//获取通过intent的传过来的参数
		Intent intent = this.getIntent();
		ssid = intent.getStringExtra("SSID");
		type = intent.getIntExtra("type", 0);
		flag = intent.getStringExtra("flag");
		
		txtWifiPasswordTip = (TextView) findViewById(R.id.txtWifiPasswordTip);
		if(flag.equals(WifiActivity.RE_INPUT_PASS)){
			txtWifiPasswordTip.setText("密码验证失败，请重新输入密码");
		}
		
		edtPassword =  (EditText) findViewById(R.id.edt_wifi_password);
		//监听软键盘“完成”键,获取输入的密码，连接wifi
		edtPassword.setOnEditorActionListener(new OnEditorActionListener() {  

	        @Override  

	        public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {  

	        if (arg1 == EditorInfo.IME_ACTION_DONE) {  

	        	if(edtPassword.getText().length() <8 ){//提示密码长度错误
					txtWifiPasswordTip.setText("密码要求最小长度8位");					
				}else{//连接wifi
		        	WifiHelper wh = new WifiHelper(getApplication());
					int wcgID = wh.wifiManager.addNetwork(wh.createWifiInfo(ssid, edtPassword.getText().toString(), type));				
					boolean b = wh.wifiManager.enableNetwork(wcgID, true);
					wh.wifiManager.saveConfiguration();
					wh.wifiConfigList = wh.wifiManager.getConfiguredNetworks();
					finish();
				}
	        }  
	        return false;  
	        }  

	    });
		
		edtPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
			int count) {

			}


			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
			}


			@Override
			public void afterTextChanged(Editable s) {
				
			}

			});
		
			

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			//WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiHelper wh = new WifiHelper(this);
			int wcgID = wh.wifiManager.addNetwork(wh.createWifiInfo(ssid, edtPassword.getText().toString(), type));
			boolean b = wh.wifiManager.enableNetwork(wcgID, true);
			wh.wifiConfigList = wh.wifiManager.getConfiguredNetworks();
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}	
}
