package com.pisen.ott.settings.about;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.pisen.ott.OTTAlertDialog;
import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
/**
 * 关于我们
 * @author yangyp
 * @version 1.0, 2014年11月21日 上午11:49:55
 */
public class AboutActivity extends OTTBaseActivity implements OnItemClickFocusListener {
	private final String TAG = "AboutActivity";
	private FocusLinearLayout aboutSettingsLayout;
	private TextView txtDeviceValue;
	private TextView txtProductValue;
	private TextView txtWifiValue;
	private TextView txtBluetoothAddr;
	private TextView txtIpAddr;
	private TextView txtTelevisionSequence;
	private TextView txtSoftwareVersion;
	OTTAlertDialog dialogAlert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitle(R.string.about_title, R.string.about_title_describle);
		initView();
	}

	/**
	 * 设置标题,描述,以及返回事件
	 */
	private void setTitleInfo(){
		TextView title = (TextView) findViewById(R.id.txtTitle);
		((TextView)findViewById(R.id.txtSubtitle)).setText(getString(R.string.about_title_describle));
		title.setText(getString(R.string.about_title));
		title.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
	public void initView(){
		   this.setTitleInfo();
		   aboutSettingsLayout = (FocusLinearLayout) findViewById(R.id.aboutSettingsLayout);
		   aboutSettingsLayout.setOnItemClickListener(this);
		   
		   findViewById(R.id.contactLayout);
		   findViewById(R.id.legalLayout);
		   
		   txtDeviceValue = (TextView) findViewById(R.id.txt_device_value);
		   txtDeviceValue.setText(SettingConfig.getDeviceName());
		   
		   txtProductValue = (TextView) findViewById(R.id.txt_product_value);
		   txtProductValue.setText( android.os.Build.MODEL);
		   
		   txtWifiValue = (TextView) findViewById(R.id.txt_wifi_value);
		   txtWifiValue.setText(getWifiMac());
		   
		   txtBluetoothAddr = (TextView) findViewById(R.id.txt_bluetooth_addr);
		   txtIpAddr = (TextView) findViewById(R.id.txt_ip_addr);
		   txtIpAddr.setText(getIpAddr());
		   
		   txtTelevisionSequence = (TextView) findViewById(R.id.txt_television_sequence);
		   txtSoftwareVersion = (TextView) findViewById(R.id.txt_software_version);
		   txtSoftwareVersion.setText(getSoftwareVersion());
	}
	
	/*
	 * 当前app版本号
	 */
	public String getSoftwareVersion(){
		try {
			return this.getPackageManager().getPackageInfo("com.pisen.ott.settings", 0).versionCode+"";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0+"";
	}
	
	/*
	 * 获取物理wifiMac
	 */
	public String getWifiMac(){
		 String macWifi = null;
         String str = "";
         try {
                 Process pp = Runtime.getRuntime().exec(
                                 "cat /sys/class/net/wlan0/address ");
                 InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                 LineNumberReader input = new LineNumberReader(ir);
                 for (; null != str;) {
                         str = input.readLine();
                         if (str != null) {
                        	 macWifi = str.trim();// 去空格
                              break;
                         }
                 }
         } catch (IOException ex) {
           ex.printStackTrace();
         }
         return macWifi;
	}
	
	public String getBluetoothMac(){
		return null;
	}
	
	/*
	 * 当前ip地址
	 */
	public String getIpAddr(){
		WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMan.getConnectionInfo();
		int ipAddress = info.getIpAddress();
		String ipString = "";// 本机在WIFI状态下路由分配给的IP地址
		if (ipAddress != 0) {
		       ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." 
				+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
		   }
		return ipString;
	}
	
	
	@Override
	public void onItemClick(View agrg0) {
		switch (agrg0.getId()) {
		case R.id.contactLayout:
			startActivity(new Intent(this,ContackServiceActivity.class));
			break;
		case R.id.softwareVersionLayout:
			break;
		case R.id.legalLayout:
			startActivity(new Intent(this,LegalInfosActivity.class));
			break;
		//恢复出厂设置	
		case R.id.resetLayout:
			showRecoveryDialog();
			break;
		default:
			break;
		}
	}
	
	private void showRecoveryDialog(){
		dialogAlert = new OTTAlertDialog(this);
		dialogAlert.setOkListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialogAlert.dismiss();
	        	Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
				intent.putExtra("wipe_media", true);
				sendBroadcast(intent);
	        }
	    });
		dialogAlert.setCancelListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Log.d("testMsg", "in cancel");
	        	dialogAlert.dismiss();
	        }
	    });
		dialogAlert.show();
	}
	
}
