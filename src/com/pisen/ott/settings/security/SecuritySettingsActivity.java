package com.pisen.ott.settings.security;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pisen.ott.OTTBaseActivity;
import com.pisen.ott.OTTDialog;
import com.pisen.ott.settings.R;
import com.pisen.ott.settings.SingleChoiceDialog;
import com.pisen.ott.settings.SingleChoiceDialog.OnItemCheckedChangeListener;
import com.pisen.ott.widget.FocusLinearLayout;
import com.pisen.ott.widget.FocusLinearLayout.OnItemClickFocusListener;
import com.pisen.ott.widget.FocusLinearLayout.OnItemKeyFocusListener;

/**
 * 帐户与安全
 * 
 * @author yangyp
 * @version 1.0, 2014年12月17日 下午2:04:57
 */
public class SecuritySettingsActivity extends OTTBaseActivity implements OnItemClickFocusListener, OnItemKeyFocusListener {

	private FocusLinearLayout securitySettingsLayout;
	private RelativeLayout unknownSourceSettings;
	private RelativeLayout adbDebutSettings;
	private TextView txtUnknownSource;
	private TextView txtUsbDebug;
	String[] names; // wifi开关显示内容数组
	int unKnownIndex;// 未知来源索引
	int usbDebugIndex;// usb调试索引

	SingleChoiceDialog<Object> usbDebugDialog;// usb调试对话框
	SingleChoiceDialog<Object> unKnownResourceDialog;// 未知来源对话框

	@Override
	protected void onStart() {
		reloadData();
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.security);
		setTitle("帐户与安全", "盒子系统的帐户与安全设置");

		unknownSourceSettings = (RelativeLayout) findViewById(R.id.unknownSourceSettings);
		adbDebutSettings = (RelativeLayout) findViewById(R.id.adbDebugSettings);

		txtUnknownSource = (TextView) findViewById(R.id.txtUnknownSource);
		txtUsbDebug = (TextView) findViewById(R.id.txtUsbDebug);
		names = getResources().getStringArray(R.array.array_on_off);
		reloadData();
		securitySettingsLayout = (FocusLinearLayout) findViewById(R.id.securitySettingsLayout);
		securitySettingsLayout.setOnItemClickListener(this);
		securitySettingsLayout.setOnItemKeyListener(this);
	}

	private void reloadData() {
		// 获取系统usb调试状态
		try {
			usbDebugIndex = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		txtUsbDebug.setText(getResources().getStringArray(R.array.array_on_off)[usbDebugIndex]);

		// 获取系统未知来源状态
		try {
			unKnownIndex = Settings.Global.getInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		txtUnknownSource.setText(getResources().getStringArray(R.array.array_on_off)[unKnownIndex]);
	}

	@Override
	public void onItemClick(View v) {
		switch (v.getId()) {
		// 设置未知来源
		case R.id.unknownSourceSettings:
			showUnKnownChoiceDialog();
			break;
		// 设置ADB调试
		case R.id.adbDebugSettings:
			showUsbDebugChoiceDialog();
			break;
		}
	}

	private void showUnKnownChoiceDialog() {
		unKnownResourceDialog = new SingleChoiceDialog<Object>(this);
		unKnownResourceDialog.setTitle("安装未知来源的应用");
		unKnownResourceDialog.setItems(getResources().getStringArray(R.array.array_on_off));

		int iUnknown = 0;
		// 获取系统状态
		try {
			iUnknown = Settings.Global.getInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		// 设置选中的Item
		unKnownResourceDialog.setCheckedItem(iUnknown);
		unKnownResourceDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				// 设置未知来源
				setNonMarketAppsAllowed(itemIndex == 0 ? false : true);
				// 更新UI
				txtUnknownSource.setText(getResources().getStringArray(R.array.array_on_off)[itemIndex]);
				// 关闭对话框
				unKnownResourceDialog.dismiss();
			}
		});
		unKnownResourceDialog.show();
	}

	private void showUsbDebugChoiceDialog() {
		usbDebugDialog = new SingleChoiceDialog<Object>(this);
		usbDebugDialog.setTitle("ADB调试");
		usbDebugDialog.setItems(getResources().getStringArray(R.array.array_on_off));
		int iAdb = 0;
		// 获取系统状态
		try {
			iAdb = Settings.Global.getInt(getContentResolver(), Settings.Global.ADB_ENABLED);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		usbDebugDialog.setCheckedItem(iAdb);
		usbDebugDialog.setOnItemCheckedChangeListener(new OnItemCheckedChangeListener() {
			@Override
			public void onItemCheckedChanged(OTTDialog dialog, int itemIndex) {
				// 设置Usb调试
				setAdbDebugAllowed(itemIndex);
				// 更新UI
				txtUsbDebug.setText(getResources().getStringArray(R.array.array_on_off)[itemIndex]);
				// 关闭对话框
				usbDebugDialog.dismiss();
			}
		});
		usbDebugDialog.show();

	}

	private void setAdbDebugAllowed(int idx) {
		Settings.Global.putInt(this.getContentResolver(), Settings.Global.ADB_ENABLED, idx);
	}

	@SuppressLint("NewApi")
	private void setNonMarketAppsAllowed(boolean enabled) {
		final UserManager um = (UserManager) this.getSystemService(Context.USER_SERVICE);
		// if
		// (um.hasUserRestriction(UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES))
		// {
		// return;
		// }
		Settings.Global.putInt(getContentResolver(), Settings.Global.INSTALL_NON_MARKET_APPS, enabled ? 1 : 0);
	}

	@Override
	public void onItemKeyLeft(View v, KeyEvent event) {
		switch (v.getId()) {
		// 未知来源
		case R.id.unknownSourceSettings:
			if (unKnownIndex <= 0) {
				unKnownIndex = names.length - 1;
			} else {
				unKnownIndex--;
			}
			// 设置未知来源
			setNonMarketAppsAllowed(unKnownIndex == 0 ? false : true);
			// 更新UI
			txtUnknownSource.setText(names[unKnownIndex]);
			break;
		// usb调试
		case R.id.adbDebugSettings:
			if (usbDebugIndex <= 0) {
				usbDebugIndex = names.length - 1;
			} else {
				usbDebugIndex--;
			}
			// 设置Usb调试
			setAdbDebugAllowed(usbDebugIndex);
			// 更新UI
			txtUsbDebug.setText(names[usbDebugIndex]);
			break;
		}
	}

	@Override
	public void onItemKeyRight(View v, KeyEvent event) {
		switch (v.getId()) {
		// wifi开关
		case R.id.unknownSourceSettings:
			if (unKnownIndex >= names.length - 1) {
				unKnownIndex = 0;
			} else {
				unKnownIndex++;
			}
			// 设置未知来源
			setNonMarketAppsAllowed(unKnownIndex == 0 ? false : true);
			// 更新UI
			txtUnknownSource.setText(names[unKnownIndex]);
			break;
		// usb调试
		case R.id.adbDebugSettings:
			if (usbDebugIndex >= names.length - 1) {
				usbDebugIndex = 0;
			} else {
				usbDebugIndex++;
			}
			// 设置Usb调试
			setAdbDebugAllowed(usbDebugIndex);
			// 更新UI
			txtUsbDebug.setText(names[usbDebugIndex]);
			break;
		}
	}
}
