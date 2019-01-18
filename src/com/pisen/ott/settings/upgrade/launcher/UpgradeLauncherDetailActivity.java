package com.pisen.ott.settings.upgrade.launcher;

import android.os.Bundle;
import android.widget.EditText;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

/**
 * 系统版本详细信息 
 * @author Liuhc
 * @version 1.0 2014年12月17日14:22:38
 */
public class UpgradeLauncherDetailActivity extends OTTBaseActivity{

	private EditText edtUpdateInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgrade_launcher_detail);
		setTitle(R.string.setting_upgrade, R.string.setting_upgrade_describle);
		
		edtUpdateInfo = (EditText) findViewById(R.id.edtUpdateInfo);
	}

}
