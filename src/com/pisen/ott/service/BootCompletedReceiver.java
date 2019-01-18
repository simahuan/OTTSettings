package com.pisen.ott.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.izy.util.LogUtils;

import com.pisen.ott.settings.SettingConfig;
import com.pisen.ott.settings.common.location.CityManager;
import com.pisen.ott.settings.util.KeyUtils;

/**
 * 开机监听广播 开启更新,循环等任务
 * 
 * @author Liuhc
 * @version 1.0 2014年12月18日 上午10:16:01
 */
public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.i(KeyUtils.TAG_LHC, "【OT视吧盒子】开机启动.....");
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
			actionBootCompleted(context);
		}
	}

	/**
	 * 开机广播处理
	 * 
	 * @param context
	 */
	private void actionBootCompleted(Context context) {
		// 设置天气定时更新
		CityManager.getInstance(context).setAlarmTime();
		// 开机后主动刷新一次天气信息（一般情况在监听网络初次连接成功后调用）
		CityManager.getInstance(context).updateWeatherInfo();

		// 判断儿童锁是否开启
		if (SettingConfig.getChildLockState()) {
			LockScreenService.sendCmd(context, LockScreenService.CMD_SCREEN_LOCK);
		}
	}

}
