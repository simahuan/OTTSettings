package com.pisen.ott.settings.upgrade.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.R;

/**
 * 系统升级 
 * 盒子的版本升级和介绍
 * @author Liuhc
 * @version 1.0 2014年12月16日 下午5:05:43
 */
public class UpgradeLauncherActivity extends OTTBaseActivity implements OnClickListener{

	private TextView txtMessage;
	private LinearLayout progressLayout;
	private ProgressBar pbarUpgrade;
	private TextView txtProgress;
	private Button btnVersion;
	private Button btnUpdate;
	private Button btnCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upgrade_launcher);
		setTitle(R.string.setting_upgrade, R.string.setting_upgrade_describle);
		
		this.initViews();
	}
	
	private void initViews(){
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
		pbarUpgrade = (ProgressBar) findViewById(R.id.pbarUpgrade);
		txtProgress = (TextView) findViewById(R.id.txtProgress);
		btnVersion = (Button) findViewById(R.id.btnVersion);
		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		checkVersion();
	}
	
	private void checkVersion(){
		UpdateManager.getUpdateManager().checkAppUpdate(this, false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnVersion:
			startActivity(new Intent(this,UpgradeLauncherDetailActivity.class));
			break;
		case R.id.btnUpdate:
			break;
		case R.id.btnCancel:
			break;
		default:
			break;
		}
	}
}
