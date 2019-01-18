package com.pisen.ott.settings.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络请求URL地址
 * @author Liuhc
 * @version 1.0 2014年12月3日 下午1:56:43
 */
public class HttpUtils {

	
	/**
	 * 更新服务和菜单
	 */
	
	//中国天气网
	public static final String CHINA_WEATHER = "http://open.weather.com.cn/data/?areaid=%s&type=%s&date=%s&appid=%s";

	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public static boolean isNetworkConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
}
