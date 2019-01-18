package com.pisen.ott.settings.common.location;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;
import android.os.Bundle;

/**
 * 城市设置
 * @author Liuhc
 * @version 1.0 2014年12月5日 上午11:26:00
 */
public class LocationCityActivity extends OTTBaseActivity {

	private CityPicker cityPicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_location_city);
		setTitle(R.string.common_system_location, R.string.common_system_location_describle);
		cityPicker = (CityPicker) findViewById(R.id.citypicker);
	}

	@Override
	protected void onPause() {
		cityPicker.getSaveCurrentCity();
		super.onPause();
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
