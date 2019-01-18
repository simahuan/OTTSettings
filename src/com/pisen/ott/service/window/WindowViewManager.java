package com.pisen.ott.service.window;

import java.util.Stack;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * View窗口管理类
 * 
 * @author yangyp
 * @version 1.0, 2014-6-26 上午10:57:59
 */
public class WindowViewManager implements WindowController {

	private final static boolean DEBUG = true;
	private static String TAG = "KeyguardViewManager";

	private final Context mContext;
	private final ViewManager mViewManager;

	private WindowManager.LayoutParams mWindowLayoutParams;

	private FrameLayout mWindowHost;
	private Stack<WindowViewBase> mBackStack;
	private WindowViewBase mKeyguardView;

	/***
	 * @param context
	 *            Used to create views.
	 * @param viewManager
	 *            Keyguard will be attached to this.
	 * @param callback
	 *            Used to notify of changes.
	 */
	public WindowViewManager(Context context) {
		mContext = context;
		mViewManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		mBackStack = new Stack<WindowViewBase>();
		initRootView();
	}

	public void initRootView() {
		if (DEBUG)
			Log.d(TAG, "show(); mKeyguardView==" + mKeyguardView);

		if (mWindowHost == null) {
			if (DEBUG)
				Log.d(TAG, "keyguard host is null, creating it...");

			mWindowHost = new WindowViewHost(mContext);
			mWindowHost.setVisibility(View.GONE);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
			lp.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //
					| WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR //
					| WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN //
					| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //
					| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON //
					| WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER;

			lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
			lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
			lp.alpha = 1f;
			// lp.windowAnimations =
			// com.android.internal.R.style.Animation_LockScreen;
			lp.setTitle("Keyguard");
			mWindowLayoutParams = lp;
			mViewManager.addView(mWindowHost, lp);
		}
	}

	@Override
	public void hide() {
		if (DEBUG)
			Log.d(TAG, "hide()");

		if (mWindowHost != null) {
			mWindowHost.setVisibility(View.GONE);
			if (mKeyguardView != null) {
				mBackStack.clear();
				mKeyguardView.cleanUp();
				mWindowHost.removeView(mKeyguardView);
				mKeyguardView = null;
			}
		}
	}

	/***
	 * @return Whether the keyguard is showing
	 */
	public synchronized boolean isShowing() {
		return (mWindowHost != null && mWindowHost.getVisibility() == View.VISIBLE);
	}

	@Override
	public synchronized void addToBackStack(WindowViewBase view) {
		if (mKeyguardView != null) {
			mKeyguardView.onPause();
//			mWindowHost.removeAllViewsInLayout();
		}
		view.setController(this);
		view.onResume();
		mWindowHost.addView(view, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//		mViewManager.updateViewLayout(mWindowHost, mWindowLayoutParams);
		 mWindowHost.removeView(mKeyguardView);
		mWindowHost.setVisibility(View.VISIBLE);
		mBackStack.push(view);
		view.requestFocus();
		mKeyguardView = view;
	}

	@Override
	public synchronized void popBackStack() {
		if (!mBackStack.isEmpty()) {
			mBackStack.pop();
			if (!mBackStack.isEmpty()) {
				WindowViewBase peekView = mBackStack.peek();
				addToBackStack(peekView);
			}
		}
	}

	/***
	 * Helper class to host the keyguard view.
	 */
	private static class WindowViewHost extends FrameLayout {

		private WindowViewHost(Context context) {
			super(context);
		}

	}
}
