package com.pisen.ott.service.window;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 窗口服务
 * 
 * @author yangyp
 * @version 1.0, 2014年12月26日 上午9:13:06
 */
public class DialogService extends Service implements OnTouchListener, OnKeyListener {

	private final String TAG = DialogService.class.getSimpleName() + "[" + getClass().getSimpleName() + "]";

	/**
	 * The {@link Intent} that must be declared as handled by the service.
	 */
	public static final String SERVICE_INTERFACE = "android.service.dreams.DreamService";

	private final Handler mHandler = new Handler();
	// private IBinder mWindowToken;
	private WindowManager mWindow;
	private WindowManager.LayoutParams mWindowLayoutParams;
	private View mContentView;
	private boolean mInteractive;
	private boolean mFullscreen;
	private boolean mScreenBright = true;
	private boolean mStarted;
	private boolean mWaking;
	private boolean mFinished;
	// private boolean mCanDoze;
	private boolean mDozing;
	private boolean mWindowless;

	private boolean mDebug = false;

	public DialogService() {
	}

	// begin Window.Callback methods

	// end Window.Callback methods

	// begin public api
	/**
	 * Retrieves the current {@link android.view.WindowManager} for the dream.
	 * Behaves similarly to {@link android.app.Activity#getWindowManager()}.
	 *
	 * @return The current window manager, or null if the dream is not started.
	 */
	public WindowManager getWindowManager() {
		return mWindow;
	}

	/**
	 * Inflates a layout resource and set it to be the content view for this
	 * Dream. Behaves similarly to
	 * {@link android.app.Activity#setContentView(int)}.
	 *
	 * <p>
	 * Note: Requires a window, do not call before {@link #onAttachedToWindow()}
	 * </p>
	 *
	 * @param layoutResID
	 *            Resource ID to be inflated.
	 *
	 * @see #setContentView(android.view.View)
	 * @see #setContentView(android.view.View,
	 *      android.view.ViewGroup.LayoutParams)
	 */
	public void setContentView(int layoutResID) {
		setContentView(View.inflate(this, layoutResID, null));
	}

	/**
	 * Sets a view to be the content view for this Dream. Behaves similarly to
	 * {@link android.app.Activity#setContentView(android.view.View)} in an
	 * activity, including using {@link ViewGroup.LayoutParams#MATCH_PARENT} as
	 * the layout height and width of the view.
	 *
	 * <p>
	 * Note: This requires a window, so you should usually call it during
	 * {@link #onAttachedToWindow()} and never earlier (you
	 * <strong>cannot</strong> call it during {@link #onCreate}).
	 * </p>
	 *
	 * @see #setContentView(int)
	 * @see #setContentView(android.view.View,
	 *      android.view.ViewGroup.LayoutParams)
	 */
	public void setContentView(View view) {
		setContentView(view, null);
	}

	/**
	 * Sets a view to be the content view for this Dream. Behaves similarly to
	 * {@link android.app.Activity#setContentView(android.view.View, android.view.ViewGroup.LayoutParams)}
	 * in an activity.
	 *
	 * <p>
	 * Note: This requires a window, so you should usually call it during
	 * {@link #onAttachedToWindow()} and never earlier (you
	 * <strong>cannot</strong> call it during {@link #onCreate}).
	 * </p>
	 *
	 * @param view
	 *            The desired content to display.
	 * @param params
	 *            Layout parameters for the view.
	 *
	 * @see #setContentView(android.view.View)
	 * @see #setContentView(int)
	 */
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		this.mContentView = view;
		if (params != null) {
			this.mWindowLayoutParams.width = params.width;
			this.mWindowLayoutParams.height = params.height;
		}
	}

	/**
	 * Adds a view to the Dream's window, leaving other content views in place.
	 *
	 * <p>
	 * Note: Requires a window, do not call before {@link #onAttachedToWindow()}
	 * </p>
	 *
	 * @param view
	 *            The desired content to display.
	 * @param params
	 *            Layout parameters for the view.
	 */
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		setContentView(view, params);
		getWindowManager().addView(view, mWindowLayoutParams);
	}

	/**
	 * Finds a view that was identified by the id attribute from the XML that
	 * was processed in {@link #onCreate}.
	 *
	 * <p>
	 * Note: Requires a window, do not call before {@link #onAttachedToWindow()}
	 * </p>
	 *
	 * @return The view if found or null otherwise.
	 */
	public View findViewById(int id) {
		return mContentView.findViewById(id);
	}

	/**
	 * Marks this dream as interactive to receive input events.
	 *
	 * <p>
	 * Non-interactive dreams (default) will dismiss on the first input event.
	 * </p>
	 *
	 * <p>
	 * Interactive dreams should call {@link #finish()} to dismiss themselves.
	 * </p>
	 *
	 * @param interactive
	 *            True if this dream will handle input events.
	 */
	public void setInteractive(boolean interactive) {
		mInteractive = interactive;
	}

	/**
	 * Returns whether or not this dream is interactive. Defaults to false.
	 *
	 * @see #setInteractive(boolean)
	 */
	public boolean isInteractive() {
		return mInteractive;
	}

	/**
	 * Controls {@link android.view.WindowManager.LayoutParams#FLAG_FULLSCREEN}
	 * on the dream's window.
	 *
	 * @param fullscreen
	 *            If true, the fullscreen flag will be set; else it will be
	 *            cleared.
	 */
	public void setFullscreen(boolean fullscreen) {
		if (mFullscreen != fullscreen) {
			mFullscreen = fullscreen;
			int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
			applyWindowFlags(mFullscreen ? flag : 0, flag);
		}
	}

	/**
	 * Returns whether or not this dream is in fullscreen mode. Defaults to
	 * false.
	 *
	 * @see #setFullscreen(boolean)
	 */
	public boolean isFullscreen() {
		return mFullscreen;
	}

	/**
	 * Marks this dream as keeping the screen bright while dreaming.
	 *
	 * @param screenBright
	 *            True to keep the screen bright while dreaming.
	 */
	public void setScreenBright(boolean screenBright) {
		if (mScreenBright != screenBright) {
			mScreenBright = screenBright;
			int flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			applyWindowFlags(mScreenBright ? flag : 0, flag);
		}
	}

	/**
	 * Starts dozing, entering a deep dreamy sleep.
	 * <p>
	 * Dozing enables the system to conserve power while the user is not
	 * actively interacting with the device. While dozing, the display will
	 * remain on in a low-power state and will continue to show its previous
	 * contents but the application processor and other system components will
	 * be allowed to suspend when possible.
	 * </p>
	 * <p>
	 * While the application processor is suspended, the dream may stop
	 * executing code for long periods of time. Prior to being suspended, the
	 * dream may schedule periodic wake-ups to render new content by scheduling
	 * an alarm with the {@link AlarmManager}. The dream may also keep the CPU
	 * awake by acquiring a {@link android.os.PowerManager#PARTIAL_WAKE_LOCK
	 * partial wake lock} when necessary. Note that since the purpose of doze
	 * mode is to conserve power (especially when running on battery), the dream
	 * should not wake the CPU very often or keep it awake for very long.
	 * </p>
	 * <p>
	 * It is a good idea to call this method some time after the dream's entry
	 * animation has completed and the dream is ready to doze. It is important
	 * to completely finish all of the work needed before dozing since the
	 * application processor may be suspended at any moment once this method is
	 * called unless other wake locks are being held.
	 * </p>
	 * <p>
	 * Call {@link #stopDozing} or {@link #finish} to stop dozing.
	 * </p>
	 *
	 * @see #stopDozing
	 * @hide For use by system UI components only.
	 */
	public void startDozing() {
		if (!mDozing) {
			mDozing = true;
			updateDoze();
		}
	}

	private void updateDoze() {
		if (mDozing) {
			/*
			 * try { mSandman.startDozing(mWindowToken, mDozeScreenState,
			 * mDozeScreenBrightness); } catch (RemoteException ex) { // system
			 * server died }
			 */
		}
	}

	/**
	 * Stops dozing, returns to active dreaming.
	 * <p>
	 * This method reverses the effect of {@link #startDozing}. From this moment
	 * onward, the application processor will be kept awake as long as the dream
	 * is running or until the dream starts dozing again.
	 * </p>
	 *
	 * @see #startDozing
	 * @hide For use by system UI components only.
	 */
	public void stopDozing() {
		if (mDozing) {
			mDozing = false;
			/*
			 * try { mSandman.stopDozing(mWindowToken); } catch (RemoteException
			 * ex) { // system server died }
			 */
		}
	}

	/**
	 * Returns true if the dream will allow the system to enter a low-power
	 * state while it is running without actually turning off the screen.
	 * Defaults to false, keeping the application processor awake while the
	 * dream is running.
	 *
	 * @return True if the dream is dozing.
	 *
	 * @see #setDozing(boolean)
	 * @hide For use by system UI components only.
	 */
	public boolean isDozing() {
		return mDozing;
	}

	/**
	 * Called when this Dream is constructed.
	 */
	@Override
	public void onCreate() {
		if (mDebug)
			Log.v(TAG, "onCreate()");
		super.onCreate();
		attach();
	}

	/**
	 * Called when the dream's window has been created and is visible and
	 * animation may now begin.
	 */
	public void onDreamingStarted() {
		if (mDebug)
			Log.v(TAG, "onDreamingStarted()");
		// hook for subclasses
	}

	/**
	 * Called when this Dream is stopped, either by external request or by
	 * calling finish(), before the window has been removed.
	 */
	public void onDreamingStopped() {
		if (mDebug)
			Log.v(TAG, "onDreamingStopped()");
		// hook for subclasses
	}

	/**
	 * Called when the dream is being asked to stop itself and wake.
	 * <p>
	 * The default implementation simply calls {@link #finish} which ends the
	 * dream immediately. Subclasses may override this function to perform a
	 * smooth exit transition then call {@link #finish} afterwards.
	 * </p>
	 * <p>
	 * Note that the dream will only be given a short period of time (currently
	 * about five seconds) to wake up. If the dream does not finish itself in a
	 * timely manner then the system will forcibly finish it once the time
	 * allowance is up.
	 * </p>
	 */
	public void onWakeUp() {
		finish();
	}

	@Override
	public final IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Stops the dream and detaches from the window.
	 * <p>
	 * When the dream ends, the system will be allowed to go to sleep fully
	 * unless there is a reason for it to be awake such as recent user activity
	 * or wake locks being held.
	 * </p>
	 */
	public final void finish() {
		if (mDebug)
			Log.v(TAG, "finish(): mFinished=" + mFinished);

		if (!mFinished) {
			mFinished = true;

			/*
			 * try { mSandman.finishSelf(mWindowToken, true immediate ); } catch
			 * (RemoteException ex) { // system server died }
			 */

			stopSelf();
		}
	}

	/**
	 * Wakes the dream up gently.
	 * <p>
	 * Calls {@link #onWakeUp} to give the dream a chance to perform an exit
	 * transition. When the transition is over, the dream should call
	 * {@link #finish}.
	 * </p>
	 */
	public final void wakeUp() {
		wakeUp(false);
	}

	private void wakeUp(boolean fromSystem) {
		if (mDebug)
			Log.v(TAG, "wakeUp(): fromSystem=" + fromSystem + ", mWaking=" + mWaking + ", mFinished=" + mFinished);

		if (!mWaking && !mFinished) {
			mWaking = true;

			// As a minor optimization, invoke the callback first in case it
			// simply
			// calls finish() immediately so there wouldn't be much point in
			// telling
			// the system that we are finishing the dream gently.
			onWakeUp();

			// Now tell the system we are waking gently, unless we already told
			// it we were finishing immediately.
			if (!fromSystem && !mFinished) {

				/*
				 * try { mSandman.finishSelf(mWindowToken, false immediate ); }
				 * catch (RemoteException ex) { // system server died }
				 */

			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void onDestroy() {
		if (mDebug)
			Log.v(TAG, "onDestroy()");
		// hook for subclasses

		// Just in case destroy came in before detach, let's take care of that
		// now
		detach();

		super.onDestroy();
	}

	// end public api

	/**
	 * Called by DreamController.stopDream() when the Dream is about to be
	 * unbound and destroyed.
	 *
	 * Must run on mHandler.
	 */
	private final void detach() {
		if (mStarted) {
			if (mDebug)
				Log.v(TAG, "detach(): Calling onDreamingStopped()");
			mStarted = false;
			onDreamingStopped();
		}

		if (mWindow != null) {
			// force our window to be removed synchronously
			if (mDebug)
				Log.v(TAG, "detach(): Removing window from window manager");
			mWindow.removeViewImmediate(mContentView);
			mWindow = null;
		}
	}

	/**
	 * Called when the Dream is ready to be shown.
	 *
	 * Must run on mHandler.
	 *
	 * @param windowToken
	 *            A window token that will allow a window to be created in the
	 *            correct layer.
	 */
	private final void attach() {
		if (mFinished || mWaking) {
			Log.w(TAG, "attach() called after dream already finished");
			/*
			 * try { mSandman.finishSelf(windowToken, true immediate ); } catch
			 * (RemoteException ex) { // system server died }
			 */
			return;
		}

		// mWindowToken = windowToken;
		// mCanDoze = canDoze;

		if (!mWindowless) {
			mWindowless = true;
			mWindow = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			// lp.windowAnimations =
			// com.android.internal.R.style.Animation_Dream;
			lp.flags |= (WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //
					| WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR //
					| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //
					| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD //
					| WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON //
					| (mFullscreen ? WindowManager.LayoutParams.FLAG_FULLSCREEN : 0) //
			| (mScreenBright ? WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON : 0));

			lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
			lp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR;
			lp.alpha = 1f;
			lp.setTitle("Keyguard");

			mWindowLayoutParams = lp;
			getWindowManager().addView(mContentView, lp);
			mContentView.setOnTouchListener(this);
			mContentView.setOnKeyListener(this);
		}
		// We need to defer calling onDreamingStarted until after
		// onWindowAttached,
		// which is posted to the handler by addView, so we post
		// onDreamingStarted
		// to the handler also. Need to watch out here in case detach occurs
		// before
		// this callback is invoked.
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mWindow != null || mWindowless) {
					if (mDebug)
						Log.v(TAG, "Calling onDreamingStarted()");
					mStarted = true;
					onDreamingStarted();
				}
			}
		});
	}

	private void applyWindowFlags(int flags, int mask) {
		if (mWindow != null) {
			mWindowLayoutParams.flags = applyFlags(mWindowLayoutParams.flags, flags, mask);
			mWindow.updateViewLayout(mContentView, mWindowLayoutParams);
		}
	}

	private int applyFlags(int oldFlags, int flags, int mask) {
		return (oldFlags & ~mask) | (flags & mask);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (!mInteractive) {
			if (mDebug)
				Log.v(TAG, "Waking up on onKey");
			wakeUp();
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!mInteractive) {
			if (mDebug)
				Log.v(TAG, "Waking up on onTouch");
			wakeUp();
			return true;
		}
		return false;
	}

}
