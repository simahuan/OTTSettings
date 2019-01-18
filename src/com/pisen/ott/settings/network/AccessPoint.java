package com.pisen.ott.settings.network;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
/**
 * 接入点类，管理wifi的接入点信息
 */
public class AccessPoint  {
	// Wifi加密类型常量
	public static final String WPA2 = "WPA2";
	public static final String WPA = "WPA";
	public static final String WEP = "WEP";
	public static final String OPEN = "Open";// 无密码

    public String SSID;

    public String BSSID;

    public String capabilities;

    public int level;

    public int frequency;
    
    public String msg="";//UI的状态信息
    
    public int signal; //信号强度
    
    public String encryptMode;//加密方式
    
    public WifiConfiguration Config; //配置信息
    
    public int failConnectedCount=0;//失败连接的次数
    
    public enum ResourceSort {
		None, Name, Signal;
	}
    
	public AccessPoint(ScanResult sr) {
		this.SSID=sr.SSID;
		this.BSSID=sr.BSSID;
		this.capabilities= sr.capabilities;
		this.level= sr.level;
		this.frequency = sr.frequency;
		this.signal  = WifiManager.calculateSignalLevel(sr.level, 5);		
		this.encryptMode = getScanResultSecurity(sr);
	}
	
	/**
	 * 判断并返回Ap的加密类型
	 */
	public static String getScanResultSecurity(ScanResult scanResult) {
		final String cap = scanResult.capabilities;
		final String[] securityModes = { AccessPoint.WEP, AccessPoint.WPA, AccessPoint.WPA2 };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				// Log.d(TAG, "Security mode:" + cap);
				return securityModes[i];
			}
		}
		// Log.d(TAG, "Open network found: " + scanResult);
		return AccessPoint.OPEN;
	}
	
	/** Map信号强度 */
	public static String mapStrength(int s) {
		if (s >= 5) {
			return "强";// 5
		} else if (s < 3) {
			return "弱";// 1,2
		} else {
			return "较强";// 3,4
		}
	}
}
