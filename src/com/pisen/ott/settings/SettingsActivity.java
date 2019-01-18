package com.pisen.ott.settings;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.izy.preference.PreferencesUtils;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.settings.about.AboutActivity;
import com.pisen.ott.settings.common.CommonSettingsActivity;
import com.pisen.ott.settings.graphics.GraphicsSettingsActivity;
import com.pisen.ott.settings.network.NetworkSettingsActivity;
import com.pisen.ott.settings.security.SecuritySettingsActivity;
import com.pisen.ott.settings.upgrade.AppVersion;
import com.pisen.ott.settings.upgrade.DownLoadApp;
import com.pisen.ott.settings.upgrade.RefreshAppService;
import com.pisen.ott.settings.upgrade.RefreshApp.RefreshAppCallBack;
import com.pisen.ott.settings.upgrade.RefreshAppService.MyBinder;
import com.pisen.ott.settings.upgrade.launcher.UpgradeLauncherActivity;
import com.pisen.ott.settings.util.KeyUtils;
import com.pisen.ott.widget.FocusGridLayout;

/**
 * 设置界面
 * 
 * @author yangyp
 * @version 1.0, 2014年11月21日 上午11:51:43
 */
public class SettingsActivity extends OTTBaseActivity implements OnClickListener {

	private FocusGridLayout settingsLayout;
	private boolean isBind = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings); 

		settingsLayout = (FocusGridLayout) findViewById(R.id.settingsLayout);
		settingsLayout.setOnItemClickListener(this);
		Intent intents = new Intent(SettingsActivity.this, RefreshAppService.class);
		this.isBind = bindService(intents, connection, BIND_AUTO_CREATE);
	}

	// 获取当前版本
	private String getVersionName() {
		String version = "未知";
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
			version = packInfo.versionName;
		} catch (Exception e) {
		}
		return version;
	}

	private RefreshAppService appService;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			appService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			((MyBinder) service).setRefreshAppCallBack(back);
			((MyBinder) service).setIsShow(false);
			appService = ((MyBinder) service).getRefreshAppService();
			appService.refresh();
		}
	};
	private RefreshAppCallBack back = new RefreshAppCallBack() {

		@Override
		public void downLoad(final AppVersion ver) throws NumberFormatException, Exception {
			final DownLoadApp app = new DownLoadApp();
			app.apkUrl = ver.Link;
			// 取得保存的版本号
			String lastVersion = PreferencesUtils.getString(KeyUtils.APP_VERSION, "");
			boolean isNextRemind = PreferencesUtils.getBoolean(KeyUtils.UPDATE_NEXT, true);
			final String appVersion = getVersionName();

			// 判断当前版本与服务版本是否一致
			if (!appVersion.equals(ver.Version)) {
				// 判断用户是否取消更新当前版本
				if (!lastVersion.equals(ver.Version) || isNextRemind) {
					// 本地的版本号
					AlertDialog.Builder builder = new Builder(SettingsActivity.this);
					View upView = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.upgrade_dialog_item, null);
					final CheckBox nextCB = (CheckBox) upView.findViewById(R.id.upt_checkbox);
					TextView uptContent = (TextView) upView.findViewById(R.id.upt_content);
					uptContent.setText("有新版本为" + ver.Version + ",是否更新？" + "\r\n" + ver.VersionDescription);
					builder.setView(upView);
					builder.setTitle("更新提示");
					builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							app.showDownloadDialog(SettingsActivity.this);
						}
					});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							PreferencesUtils.setString(KeyUtils.APP_VERSION, ver.Version);
							PreferencesUtils.setBoolean(KeyUtils.APP_VERSION, nextCB.isChecked());
						}
					});
					app.noticeDialog = builder.create();
					app.noticeDialog.show();
				}
			}
		}

		@Override
		public void callBack(String result) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnWifi:
			startActivity(new Intent(this, NetworkSettingsActivity.class));
			break;
		case R.id.btnGraphics:
			startActivity(new Intent(this, GraphicsSettingsActivity.class));
			break;
		case R.id.btnAccount:
			startActivity(new Intent(this, SecuritySettingsActivity.class));
			break;
		case R.id.btnComm:
			startActivity(new Intent(this, CommonSettingsActivity.class));
			break;
		case R.id.btnUpdate:
			startActivity(new Intent(this, UpgradeLauncherActivity.class));
			break;
		case R.id.btnAbout:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finishApplication();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (this.isBind) {
			unbindService(connection);
			this.isBind = false;
		}
	}
}
