package com.pisen.ott.settings;

import android.izy.ApplicationSupport;
import com.pisen.ott.service.LockScreenService;

public class SettingsApplication extends ApplicationSupport {

	@Override
	public void onCreate() {
		super.onCreate();
		SettingConfig.init(this);
		LockScreenService.sendCmd(getApplicationContext(), LockScreenService.CMD_SCREEN_NONE);
	}

}
