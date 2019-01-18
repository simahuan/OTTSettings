package com.pisen.ott.settings.network;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;



/**
 * Wifi 工具类
 * @author  fq
 * @date    2014年12月15日 下午5:49:27
 */
public class WifiHelper {
	public WifiManager wifiManager;
	public List<ScanResult> srList; // 扫描结果list
	public AccessPoint currentAP; // 当前连接的AP
	public List<AccessPoint> wifiListAll;//Ap列表
	public List<WifiConfiguration> wifiConfigList;// 网络配置列表
	
	// WifiConfig类型常量
	public static final int TYPE_NO_PASSWD = 0x11; // 无密码
	public static final int TYPE_WEP = 0x12;
	public static final int TYPE_WPA = 0x13;
	
	/**
	 * 
	 */
	public WifiHelper(Context context) {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiConfigList = wifiManager.getConfiguredNetworks();
	}

	/**
	 * 如果SSID为"SSID"去掉引号
	 */
	public static String formatSSID(String ssid){
	
		if(ssid!=null&&ssid.startsWith("\"")){
			return  ssid.substring(1, ssid.length()-1);
		}
		return ssid;		
	}
	
	/**把wifi扫描结果List转化为apList*/
	public List<AccessPoint> toApList(List<ScanResult> srList) {
		List<AccessPoint> apList = new ArrayList<AccessPoint>();
		if (srList != null && srList.size() > 0) {

			for (ScanResult sr : srList) {
				AccessPoint ap = new AccessPoint(sr);
				if (isConnected(ap)) {
					ap.msg = "已连接";
				}
				apList.add(ap);
			}
		}
		return apList;
	}
	
	/**
	 * 判断ap是否为当前ap,并且已连接
	 */
	public boolean isConnected(AccessPoint ap) {
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info != null && formatSSID(info.getSSID()).equals(formatSSID(ap.SSID))&&info.getSupplicantState().equals(SupplicantState.COMPLETED)) {
			return true;
		}
		return false;
	}
	
	/** 创建新的WifiConfiguration */
	public WifiConfiguration createWifiInfo(String SSID, String password, int type) {

		// Log.v(TAG, "SSID = " + SSID + "## Password = " + password +
		// "## Type = " + type);

		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			wifiManager.removeNetwork(tempConfig.networkId);
		}

		// 分为三种情况：1没有密码2用wep加密3用wpa加密
		if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS
			// config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			// config.wepTxKeyIndex = 0;

		} else if (type == TYPE_WEP) { // WIFICIPHER_WEP
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == TYPE_WPA) { // WIFICIPHER_WPA
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}

		return config;
	}
	
    /**判断SSID是否存在配置，如果有返回这个WifiConfiguration*/
	public WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}
	
	/** 获得当前ap */
	public AccessPoint findCurApFromList() {
		WifiInfo info = wifiManager.getConnectionInfo();
		if (wifiListAll != null && wifiListAll.size() > 0 & info != null) {
			for (AccessPoint ap : wifiListAll) {
				if (formatSSID(info.getSSID()).equals(ap.SSID)) {
					return ap;
				}
			}
		}
		return null;
	}
	
	/**
	 * 判断ap是否有配置文件
	 */
	public WifiConfiguration getConfigedInfo(AccessPoint ap) {
		// 比较前加引号
		String temp = "\"" + ap.SSID + "\"";
		for (WifiConfiguration config : wifiConfigList) {
			if (config.SSID.equals(temp)) {
				return  config;				
			}
		}
		return null;
	}
	
	public WifiConfiguration getConfigedInfoFromWifiInfo(WifiInfo info) {
		//wifiConfigList  = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration config : wifiConfigList) {
			if (formatSSID(config.SSID).equals(formatSSID(info.getSSID()))) {
				return  config;				
			}
		}
		return null;
	}
	
	/* ip地址int型转化成String */
	public String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}
	
	/**
	 * 清除数据
	 */
	public void clear(){
		wifiManager=null;
		srList=null; 
		currentAP=null; 
		wifiListAll=null;
		wifiConfigList=null;
	}
	
}
