package com.pisen.ott.settings.network;

import android.os.Bundle;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

public class BroadbandActivity extends OTTBaseActivity {
	TextView txtHeadTitle;
    TextView txtHeadDescrible;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.network_broadband_password);
		txtHeadTitle = (TextView) findViewById(R.id.txtTitle);
		txtHeadDescrible = (TextView) findViewById(R.id.txtSubtitle);
		txtHeadTitle.setText("宽带连接");
		txtHeadDescrible.setText("PPPOE连接相关设置");
	}
}
