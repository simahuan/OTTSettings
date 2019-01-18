package com.pisen.ott.service.window;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.FrameLayout;

/***
 * Base class for keyguard views. {@link #reset} is where you should reset the
 * state of your view. Use the {@link KeyguardViewCallback} via
 * {@link #getCallback()} to send information back (such as poking the wake
 * lock, or finishing the keyguard).
 * 
 * Handles intercepting of media keys that still work when the keyguard is
 * showing.
 */
public abstract class WindowViewBase extends FrameLayout {

	private WindowController mController;
	private AudioManager mAudioManager;
	private TelephonyManager mTelephonyManager = null;

	public WindowViewBase(Context context) {
		super(context);
	}

	public void setController(WindowController controller) {
		this.mController = controller;
	}

	public WindowController getController() {
		return mController;
	}

	/**
	 * 相对于onPause()方法，当该界面不处于前台界面时调用，处于VISIBLE状态时调用
	 */
	public void onResume() {

	}

	/**
	 * 当该界面不处于前台界面时调用，包括处于GONE或者该界面即将被remove掉
	 */
	public void onPause() {

	}

	/**
	 * 该界面即将被remove掉 ,即不在需要
	 */
	public void cleanUp() {

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (interceptMediaKey(event)) {
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/***
	 * Allows the media keys to work when the keyguard is showing. The media
	 * keys should be of no interest to the actual keyguard view(s), so
	 * intercepting them here should not be of any harm.
	 * 
	 * @param event
	 *            The key event
	 * @return whether the event was consumed as a media key.
	 */
	private boolean interceptMediaKey(KeyEvent event) {
		final int keyCode = event.getKeyCode();
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_MEDIA_PLAY:
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				/**
				 * Suppress PLAY/PAUSE toggle when phone is ringing or in-call
				 * to avoid music playback
				 */
				if (mTelephonyManager == null) {
					mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
				}
				if (mTelephonyManager != null && mTelephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
					return true; // suppress key event
				}
			case KeyEvent.KEYCODE_MUTE:
			case KeyEvent.KEYCODE_HEADSETHOOK:
			case KeyEvent.KEYCODE_MEDIA_STOP:
			case KeyEvent.KEYCODE_MEDIA_NEXT:
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			case KeyEvent.KEYCODE_MEDIA_REWIND:
			case KeyEvent.KEYCODE_MEDIA_RECORD:
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
				Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
				intent.putExtra(Intent.EXTRA_KEY_EVENT, event);
				getContext().sendOrderedBroadcast(intent, null);
				return true;
			}

			case KeyEvent.KEYCODE_VOLUME_UP:
			case KeyEvent.KEYCODE_VOLUME_DOWN:
			case KeyEvent.KEYCODE_VOLUME_MUTE: {
				synchronized (this) {
					if (mAudioManager == null) {
						mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
					}
				}
				// Volume buttons should only function for music.
				if (mAudioManager.isMusicActive()) {
					mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, keyCode == KeyEvent.KEYCODE_VOLUME_UP ? AudioManager.ADJUST_RAISE
							: AudioManager.ADJUST_LOWER, 0);
				}
				// Don't execute default volume behavior
				return true;
			}
			}
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_MUTE:
			case KeyEvent.KEYCODE_HEADSETHOOK:
			case KeyEvent.KEYCODE_MEDIA_PLAY:
			case KeyEvent.KEYCODE_MEDIA_PAUSE:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
			case KeyEvent.KEYCODE_MEDIA_STOP:
			case KeyEvent.KEYCODE_MEDIA_NEXT:
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
			case KeyEvent.KEYCODE_MEDIA_REWIND:
			case KeyEvent.KEYCODE_MEDIA_RECORD:
			case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
				Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
				intent.putExtra(Intent.EXTRA_KEY_EVENT, event);
				getContext().sendOrderedBroadcast(intent, null);
				return true;
			}
			}
		}
		return false;
	}

}
