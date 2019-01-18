package com.pisen.ott.service.screensaver;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.pisen.ott.service.LockScreenService;
import com.pisen.ott.service.window.WindowViewBase;
import com.pisen.ott.settings.R;

public class ScreenproView extends WindowViewBase {

	public ScreenproView(Context context) {
		super(context);
		View.inflate(context, R.layout.test_screenpro_carousel, this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		wakeUp();
		return true;
	}

	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event) {
		wakeUp();
		return true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		wakeUp();
		return true;
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		wakeUp();
		return true;
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		wakeUp();
		return true;
	}

	/**
	 * 唤醒屏幕
	 */
	private void wakeUp() {
		getController().hide();
		LockScreenService.sendCmd(getContext(), LockScreenService.CMD_SCREEN_LOCK);
	}

}
