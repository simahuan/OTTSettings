package com.pisen.ott.settings.about;

import android.os.Bundle;
import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

/**
 * TODO
 * @author  mahuan
 * @date    2014年12月10日 下午3:18:22
 */
public class ContackServiceActivity extends OTTBaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_contact_service);
		initView();
	}
    
	public void initView(){
		setTitle(R.string.about_contact_service, R.string.about_contact_service_describle);
	}
}
