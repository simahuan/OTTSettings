package com.pisen.ott.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.izy.util.LogUtils;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.pisen.ott.service.childlock.ChildLockView;
import com.pisen.ott.service.screensaver.ScreenproView;
import com.pisen.ott.service.window.WindowService;
import com.pisen.ott.settings.SettingConfig;

/**
 * 锁屏服务
 * <p>
 * 用于启动屏保与儿童锁
 * </p>
 * 
 * @author yangyp
 * @version 1.0, 2014年12月26日 上午11:08:50
 */
public class LockScreenService extends WindowService {

	static final String TAG = WindowService.class.getSimpleName();

	// 发送命令指令
	public static final String SERVICECMD = "com.pisen.ott.service.lockscreenservice";
	public static final String CMDNAME = "command";
	public static final int CMD_SCREEN_NONE = -1;
	public static final int CMD_SCREEN_LOCK = 1;
	public static final int CMD_SCREEN_UNLOCK = 2;

	public PowerManager mPowerManager;
	private PowerManager.WakeLock mWakeLock;

	private boolean mScreensaver; // 屏保是否已经启动

	private BroadcastReceiver mScreenReceiver;

	class ScreenReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				actionScreenOff();
			}
		}
	}

	/**
	 * 发送命令
	 * 
	 * @param context
	 * @param cmd
	 */
	public static void sendCmd(Context context, int cmd) {
		Intent i = new Intent(LockScreenService.SERVICECMD);
		i.putExtra(LockScreenService.CMDNAME, cmd);
		context.startService(i);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
				"LockScreen");

		mScreenReceiver = new ScreenReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenReceiver, filter);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		unregisterReceiver(mScreenReceiver);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			int cmd = intent.getIntExtra(CMDNAME, -1);
			Log.i(TAG, "onStartCommand " + action + " / " + cmd);
			switch (cmd) {
			case CMD_SCREEN_LOCK:
				startChildLock();
				break;
			case CMD_SCREEN_UNLOCK:
				hideChildLock();
				break;
			default:
				break;
			}
		}

		return START_STICKY;
	}

	/**
	 * 关闭屏幕处理
	 * 
	 * @param context
	 */
	private void actionScreenOff() {
		// 判断屏保是否设置了时间
		if (SettingConfig.getScreensaverTime() > 0) {
			startScreensaver();
		} else {
			startChildLock();
		}
	}

	/**
	 * 显示屏保
	 */
	private void startScreensaver() {
		if (!mScreensaver) {
			mScreensaver = true;
			addToBackStack(new ScreenproView(this));
			mWakeLock.acquire(10 * 1000);
			// 设置下次屏幕休眠时间
			int nextSleepTime = SettingConfig.getScreenSleepTime() - SettingConfig.getScreensaverTime();
			LogUtils.e("startScreensaver","nextSleepTime:"+nextSleepTime);
//			SettingConfig.setSystemSettings(Settings.System.SCREEN_OFF_TIMEOUT, nextSleepTime);
			
			SettingConfig.setSystemSettings(Settings.System.SCREEN_OFF_TIMEOUT, 5*1000);
		}
	}

	/***
	 * 显示儿童锁
	 */
	private void startChildLock() {
		
		
		mScreensaver = false;
		// 判断儿童锁是否开启
		if (SettingConfig.getChildLockState()) {
			Log.d(TAG, "startChildLock");
			addToBackStack(new ChildLockView(this));
		}else{
//				hide();
		 }
			
	}

	/**
	 * 解锁
	 */
	private void hideChildLock() {
		hide();
	}
}
