package com.pisen.ott.settings;

import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.izy.preference.PreferencesUtils;
import android.provider.Settings;
import android.util.Log;

/**
 * 配置文件
 * 
 * @author mahuan
 * @created 2014年12月11日 上午10:05:56
 */
public class SettingConfig {

	static final String TAG = "SettingConfig";

	public static final int REQUEST_DEVNAME = 0x1;
	public static final int REQUEST_SCREENPRO = 0x2;
	public static final int REQUEST_AUTOSLEEP = 0x3;
	public static final int HANDLE_DREAMSERVICE = 0x4;

	public static final int FifteenMinutes = 15 * 60 * 1000;
	public static final int HalfHours = 30 * 60 * 1000;
	public static final int OneHours = 60 * 60 * 1000;
	public static final int HalfAnHours = 90 * 60 * 1000;

	public static final int ScreenProTimeDefMill = -1;
	public static final int OneMinutesMill = 1 * 5 * 1000;
	public static final int FourMinutesMill = 4 * 60 * 1000;
	public static final int EightMinutesMill = 8 * 60 * 1000;

	public static final String ScreenProTime = "ScreenProTime";
	public static final String ScreenProTimeDefault = "关闭";
	public static final String OneMinutes = "1分钟";
	public static final String FourMinutes = "4分钟";
	public static final String EightMinutes = "8分钟";

	public static final String ScreenProItemIndex = "ScreenProItemIndex";
	public static final String AutoSleepTime = "AutoSleepTime";
	public static final String AutoSleepTimeDefault = "15分钟";
	public static final String ThirteenMinutes = "30分钟";
	public static final String SixTeenMinutes = "60分钟";
	public static final String NineTeenMinutes = "90分钟";
	public static final String AutoSleepItemIndex = "AutoSleepItemIndex";

	// 儿童锁
	public static final String ChildLock = "ChildLock";
	public static final String ChildrenLockState = "ChildLockKeyState";
	public static final String ChildrenPassWordContent = "ChildrenPassWord";

	static final String ResolutionDef = "ResolutionDef";
	static final String ScreenZoom = "ScreenZoom";
	static final String ScreenDisplacement = "ScreenDisplacement";
	static final String AudioOutput = "AudioOutput";
	static final String Compression = "DynamicRange";

	static final String DeviceName = "DeviceName";
	static final String DeviceLocation = "DeviceLocation";
	static final String CecControl = "CecControl";

	static final String ScreensaverTime = "ScreensaverTime";
	static final String ScreenSleepTime = "ScreenSleepTime";
	static final String UnknownApk = "UnknownApk";
	static final String ADBDebug = "ADBDebug";

	// private static Context mContext;
	private static ContentResolver mContentResolver;

	// private static Resources mResources;

	public static void init(Context context) {
		// mContext = context;
		mContentResolver = context.getContentResolver();
		// mResources = context.getResources();
	}

	/**
	 * 获取屏保时间
	 * 
	 * @return
	 */
	public static String getScreenPro() {
		return PreferencesUtils.getString(ScreenProTime, ScreenProTimeDefault);
	}

	/**
	 * 设置屏保时间
	 * 
	 * @param proTime
	 */
	public static void setScreenPro(String proTime) {
		PreferencesUtils.setString(ScreenProTime, proTime);
	}

	/**
	 * 获取屏保下标项
	 * 
	 * @return
	 */
	public static int getScreenProIndex() {
		return PreferencesUtils.getInt(ScreenProItemIndex, 0);
	}

	/**
	 * 设置屏保下标项
	 * 
	 * @param proIndex
	 */
	public static void setScreenProIndex(int proIndex) {
		PreferencesUtils.setInt(ScreenProItemIndex, proIndex);
	}

	/**
	 * 获取休眠时间
	 * 
	 * @return
	 */
	public static String getAutoSleep() {
		return PreferencesUtils.getString(AutoSleepTime, AutoSleepTimeDefault);
	}

	/**
	 * 设置休眠时间
	 * 
	 * @param sleepTime
	 */
	public static void setAutoSleep(String sleepTime) {
		PreferencesUtils.setString(AutoSleepTime, sleepTime);
	}

	/**
	 * 获取休眠下标项
	 * 
	 * @return
	 */
	public static int getAutoSleepIndex() {
		return PreferencesUtils.getInt(AutoSleepItemIndex, 0);
	}

	/**
	 * 设置休眠下标项
	 * 
	 * @param sleepIndex
	 */
	public static void setAutoSleepIndex(int sleepIndex) {
		PreferencesUtils.setInt(AutoSleepItemIndex, sleepIndex);
	}

	/**
	 * 获取可支持的分辨率列表
	 * 
	 * @return
	 */
	public static String[] getResolutionList(Context ctx) {
		return ctx.getResources().getStringArray(R.array.array_resolution);
	}

	/**
	 * 获取当前显示的分辨率
	 * 
	 * @return
	 */
	public static String getResolution() {
		return PreferencesUtils.getString(ResolutionDef, "自动识别");
	}

	/**
	 * 设置当前显示的分辨率
	 * 
	 * @param value
	 */
	public static void setResolution(String value) {
		PreferencesUtils.setString(ResolutionDef, value);
	}

	/**
	 * 获取当前缩放
	 * 
	 * @return
	 */
	public static int getScreenZoom() {
		return PreferencesUtils.getInt(ScreenZoom, 100);
	}

	/**
	 * 设置当前缩放
	 * 
	 * @param value
	 */
	public static void setScreenZoom(int value) {
		PreferencesUtils.setInt(ScreenZoom, value);
	}

	/**
	 * 获取当前位移
	 * 
	 * @return
	 */
	public static int getScreenDisplacement() {
		return PreferencesUtils.getInt(ScreenDisplacement, 100);
	}

	/**
	 * 设置当前位移
	 * 
	 * @param value
	 */
	public static void setScreenDisplacement(int value) {
		PreferencesUtils.setInt(ScreenDisplacement, value);
	}

	/**
	 * 获取当前支持的声音输出格式
	 * 
	 * @return
	 */
	public static String[] getAudioOutputList(Context ctx) {
		return ctx.getResources().getStringArray(R.array.array_voice_output);
	}

	/**
	 * 获取当前声音输出格式
	 * 
	 * @return
	 */
	public static String getAudioOutput() {
		return PreferencesUtils.getString(AudioOutput, "模拟音频输出");
	}

	/**
	 * 设置当前声音输出格式
	 * 
	 * @param value
	 */
	public static void setAudioOutput(String value) {
		PreferencesUtils.setString(AudioOutput, value);
	}

	/**
	 * 获取当前支持的压缩方式
	 * 
	 * @return
	 */
	public static String[] getCompressionList(Context ctx) {
		return ctx.getResources().getStringArray(R.array.array_on_off);
	}

	/**
	 * 获取压缩方式
	 * 
	 * @return
	 */
	public static String getCompression() {
		return PreferencesUtils.getString(Compression, "关闭");
	}

	/**
	 * 设置压缩方式
	 * 
	 */
	public static void setCompression(String enabled) {
		PreferencesUtils.setString(Compression, enabled);
	}

	/**
	 * 启用儿童锁
	 * 
	 * @param enabled
	 *            true启用 | false禁用
	 */
	public static void setChildLockEnabled(boolean enabled) {
		PreferencesUtils.setBoolean(ChildrenLockState, enabled);
	}

	/**
	 * 获取儿童锁状态
	 * 
	 * @return
	 */
	public static boolean getChildLockState() {
		return PreferencesUtils.getBoolean(ChildrenLockState, false);
	}

	/**
	 * 获取儿童锁密码 初始密码为左左左左
	 * 
	 * @return
	 */
	public static String getChildLockContent() {
		return PreferencesUtils.getString(ChildrenPassWordContent, "LLLL");
	}

	/**
	 * 获取儿童解锁问题题
	 */
	public static List<Entry> getChildUnlockQuestion() {
		return null;
	}

	/**
	 * 获取儿童解锁随机题
	 * 
	 * @return
	 */
	public static Entry getChildUnlockQuestionRandom() {
		return null;
	}

	public static String getInputMethod(String value) {
		return null;
	}

	/**
	 * 设置输入法
	 */
	public static void setInputMethod(String value) {
		Settings.Secure.putString(mContentResolver, Settings.Secure.DEFAULT_INPUT_METHOD, value);
	}

	/**
	 * 获取设备名称
	 * 
	 * @return
	 */
	public static String getDeviceName() {
		return PreferencesUtils.getString(DeviceName, "Pisen视吧盒子");
	}

	/**
	 * 修改设备名称
	 * 
	 * @param deviceName
	 */
	public static void setDeviceName(String deviceName) {
		PreferencesUtils.setString(DeviceName, deviceName);
	}

	/**
	 * 获取设备所在位置代码
	 * 
	 * @return
	 */
	public static String getDeviceLocation() {
		return PreferencesUtils.getString(DeviceLocation, "");
	}

	/**
	 * 设置设备所在位置代码
	 * 
	 * @param locationCode
	 */
	public static void setDeviceLocation(String locationCode) {
		PreferencesUtils.setString(DeviceLocation, locationCode);
	}

	/**
	 * 启用CEC控制
	 * 
	 * @param enabled
	 */
	public static void setCecControlEnabled(boolean enabled) {
		PreferencesUtils.setBoolean(CecControl, enabled);
	}

	/**
	 * 获取屏保时间
	 *
	 * @return
	 */
	public static int getScreensaverTime() {
		return PreferencesUtils.getInt(ScreensaverTime, 0);
	}

	/**
	 * 设置屏保时间
	 *
	 * @param millisecond
	 */
	public static void setScreensaverTime(int millisecond) {
		PreferencesUtils.setInt(ScreensaverTime, millisecond);
		setSystemSettings(Settings.System.SCREEN_OFF_TIMEOUT, millisecond);
	}

	/**
	 * 获取休眠时间
	 *
	 * @return
	 */
	public static int getScreenSleepTime() {
		return PreferencesUtils.getInt(ScreenSleepTime, FifteenMinutes);
	}

	/**
	 * 设置休眠时间
	 *
	 * @param millisecond
	 */
	public static void setScreenSleepTime(int millisecond) {
		PreferencesUtils.setInt(ScreenSleepTime, millisecond);
		// 如果屏保没有设置休眠时间，那么这里进行休眠设置
		if (getScreensaverTime() <= 0) {
			setSystemSettings(Settings.System.SCREEN_OFF_TIMEOUT, millisecond);
		}
	}

	// /**
	// * 获取未知来源的应用是否启用
	// *
	// * @return
	// */
	// public static boolean getUnknownApkEnabled() {
	// return getGlobalSettingsBoolean(Settings.Global.INSTALL_NON_MARKET_APPS);
	// }
	//
	// /**
	// * 启用安装未知来源的应用
	// *
	// * @param enabled
	// */
	// public static void setUnknownApkEnabled(boolean enabled) {
	// setGlobalSettings(Settings.Global.INSTALL_NON_MARKET_APPS, enabled);
	// }
	//
	// /**
	// * 获取当前ADB是否启用
	// *
	// * @return
	// */
	// public static boolean getAdbDebugEnabled() {
	// return getGlobalSettingsBoolean(Settings.Global.ADB_ENABLED) == 1;
	// }
	//
	// /**
	// * 启用ADB调试
	// *
	// * @param enabled
	// */
	// public static void setADBDebugEnabled(boolean enabled) {
	// setGlobalSettings(Settings.Global.ADB_ENABLED, enabled);
	// }
	//

	public static boolean getGlobalSettingsBoolean(String setting) {
		return getGlobalSettingsInt(setting) != 0;
	}

	public static int getGlobalSettingsInt(String setting) {
		try {
			return Settings.Global.getInt(mContentResolver, setting);
		} catch (Exception e) {
			Log.d(TAG, "setting: " + setting + " not found");
			return 0;
		}
	}

	public static void setGlobalSettings(String setting, boolean value) {
		setGlobalSettings(setting, value ? 1 : 0);
	}

	public static void setGlobalSettings(String setting, int value) {
		Settings.Global.putInt(mContentResolver, setting, value);
	}

	public static void setSystemSettings(String setting, int value) {
		Settings.System.putInt(mContentResolver, setting, value);
	}

	static class Entry {
		public String mKey;
		public String mValue;
	}
}
