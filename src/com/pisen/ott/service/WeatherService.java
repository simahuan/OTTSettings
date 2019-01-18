package com.pisen.ott.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.izy.util.LogUtils;
import android.izy.util.StringUtils;
import android.izy.util.parse.GsonUtils;
import android.volley.RequestManager;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pisen.ott.settings.common.location.CityManager;
import com.pisen.ott.settings.common.location.WeatherInfo;
import com.pisen.ott.settings.util.HttpUtils;
import com.pisen.ott.settings.util.KeyUtils;

/**
 * 天气定时更新服务
 * 
 * @author Liuhc
 * @version 1.0 2014年12月18日 下午5:05:20
 */
public class WeatherService extends IntentService {

	private Context mContext;
	
	public WeatherService() {
		super("WeatherService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.mContext = getApplication();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		LogUtils.i(KeyUtils.TAG_LHC, "start WeatherService...");
		if (HttpUtils.isNetworkConnected(mContext)) {
			getRemoteWeather();
		}else{
			getLocalWeather();
		}
	}
	
	/**
	 * 获取本地天气信息
	 */
	public void getLocalWeather() {
		LogUtils.i(KeyUtils.TAG_LHC, "网络有问题,获取本地天气信息");
		String info = CityManager.getInstance(mContext).getLocalWeatherInfo();
		if (!StringUtils.isEmpty(info)) {
			LogUtils.i(KeyUtils.TAG_LHC, "获取本地天气信息成功....");
			sendUpdatedBroadCast(info);
		}
	}

	/**
	 * 定时更新天气信息
	 * 
	 */
	public void timerWeatherUpdate() {
		// TODO: implement
	}

	/**
	 * 获取服务器天气信息
	 * 
	 */
	public void getRemoteWeather() {
		String url = CityManager.getInstance(mContext).getChinaWeatherReqUrl();
//		LogUtils.i(KeyUtils.TAG_LHC, "天气更新url:"+url);
		RequestManager.addRequest(new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String arg0) {
//				LogUtils.i(KeyUtils.TAG_LHC, "天气返回："+arg0);
				if (!StringUtils.isEmpty(arg0)) {
					WeatherInfo info = WeatherInfo.json2bean(arg0,mContext);
					String json = GsonUtils.jsonSerializer(info);
					LogUtils.i(KeyUtils.TAG_LHC,"json:"+json);
					if (!StringUtils.isEmpty(json)) {
						sendUpdatedBroadCast(json);
						CityManager.getInstance(mContext).saveWeatherInfo(json);
					}
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				LogUtils.i(KeyUtils.TAG_LHC, "天气刷新失败："+arg0.getMessage());
			}
		}), mContext);
	}

	/**
	 * 发送系统广播通知更新天气
	 * @param info
	 */
	private void sendUpdatedBroadCast(String info){
		LogUtils.i(KeyUtils.TAG_LHC, "发出广播。。刷新天气....");
		Intent intent = new Intent(KeyUtils.ACTION_WEATHER_UPDATE);
		intent.putExtra(KeyUtils.ACTION_WEATHER_DATA, info);
		mContext.sendBroadcast(intent);
	}
}
