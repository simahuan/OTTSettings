package com.pisen.ott.service.window;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Window窗口服务
 * 
 * @author yangyp
 * @version 1.0, 2014-6-4 上午10:35:28
 */
public class WindowService extends Service implements WindowController {

	static final String TAG = WindowService.class.getSimpleName();

	private WindowViewManager mKeyguardViewManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mKeyguardViewManager = new WindowViewManager(this);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		mKeyguardViewManager.hide();
		super.onDestroy();
	}

	@Override
	public void addToBackStack(WindowViewBase view) {
		mKeyguardViewManager.addToBackStack(view);
	}

	@Override
	public void popBackStack() {
		mKeyguardViewManager.popBackStack();
	}

	/*
	 * @Override public void popBackStack(WindowViewBase view) {
	 * mKeyguardViewManager.popBackStack(view); }
	 */

	@Override
	public void hide() {
		mKeyguardViewManager.hide();
	}

}
