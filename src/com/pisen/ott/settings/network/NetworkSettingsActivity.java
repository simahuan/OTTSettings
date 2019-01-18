package com.pisen.ott.settings.network;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;

public class NetworkSettingsActivity extends OTTBaseActivity implements OnItemClickFocusListener {

	private FocusLinearLayout netSettingsLayout;
	TextView txtSetNetWork;//
	TextView txtSetWifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network);
		setTitle("网络设置", "网络的相关设置");
		netSettingsLayout = (FocusLinearLayout) findViewById(R.id.netSettingsLayout);
		txtSetNetWork = (TextView) findViewById(R.id.setNetWork);
		txtSetWifi = (TextView) findViewById(R.id.setWifi);

		netSettingsLayout.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(View v) {
		switch (v.getId()) {
		//无线网络
		case R.id.wifiSettings:
			startActivity(new Intent(this, WifiActivity.class));
			break;
		//本地网络
		case R.id.netSettings:
			startActivity(new Intent(this, BroadbandActivity.class));
			break;
		}
	}

}
