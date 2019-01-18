package com.pisen.ott.settings.common.location;

import java.util.List;

import com.pisen.ott.service.WeatherService;
import com.pisen.ott.settings.util.DateUtils;
import com.pisen.ott.settings.util.HttpUtils;
import com.pisen.ott.settings.util.KeyUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.izy.preference.PreferencesUtils;
import android.izy.util.LogUtils;

/**
 * 城市信息管理类
 * 
 * @author Liuhc
 * @version 1.0 2014年12月3日 下午2:44:12
 */
public class CityManager {

	private CityDbHelper mCityDbHelper = null;
	private static Context mContext;
	public static CityManager mInstance = null;

	private CityManager(Context ctx) {
		mContext = ctx;
	}

	public static CityManager getInstance(Context ctx) {
		if (mInstance == null) {
			mInstance = new CityManager(ctx);
		}
		return mInstance;
	}

	/**
	 * 释放资源
	 */
	public static void release() {
		if (mContext != null) {
			mContext = null;
		}
		if (mInstance != null) {
			mInstance = null;
		}
	}

	/**
	 * 手动更新天气
	 */
	public void updateWeatherInfo(){
		mContext.startService(new Intent(mContext,WeatherService.class));
	}
	
	/**
	 * 设置闹钟及循环时间间隔
	 */
	public void setAlarmTime() {
		long[] times = DateUtils.getAlarmTimes();
		for (int i = 0; i < times.length; i++) {
			Intent intent = new Intent(mContext,WeatherService.class);
			PendingIntent sender = PendingIntent.getService(mContext, 8637+i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			// 设为每隔24小时更新
			long interval = 24*60*60*1000;
			AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
			am.setRepeating(AlarmManager.RTC_WAKEUP, times[i], interval, sender);
			LogUtils.i(KeyUtils.TAG_LHC, "设定天气更新时间："+DateUtils.long2Time(times[i]));
		}
	}
	
	/**
	 * 设置城市天气id
	 * @param info
	 */
	public void setDefaultCityAreaid(String id){
		PreferencesUtils.setString(CityInfo.Table.AREA_ID, id);
	}
	
	public String getDefaultCityAreaid(){
		return PreferencesUtils.getString(CityInfo.Table.AREA_ID, "101270101");
	}
	
	/**
	 * 设置城市天气 名称
	 * @param info
	 */
	public void setDefaultCityName(String name){
		PreferencesUtils.setString(CityInfo.Table.CITY, name);
	}
	
	public String getDefaultCityName(){
		return PreferencesUtils.getString(CityInfo.Table.CITY, "成都");
	}
	
	private SQLiteDatabase getCityDatabase() {
		if (mCityDbHelper == null) {
			mCityDbHelper = new CityDbHelper(mContext);
		}
		return mCityDbHelper.getReadableDatabase();
	}
	
	
	/**
	 * 获取完整天气数据请求url加密地址
	 * @return
	 */
	public String getChinaWeatherReqUrl() {
		String weather_id = getDefaultCityAreaid();
		String data = HttpUtils.CHINA_WEATHER;
		String time = DateUtils.getCurrentTime(DateUtils.TIME_FORMAT_WEATHER);
		String public_key = String.format(data, weather_id, "forecast_v", time, KeyUtils.CHINA_WEATHER_APPID_COMPLETE);
		String key = URLEncodeUtils.standardURLEncoder(public_key, KeyUtils.CHINA_WEATHER_PRIVATE_KEY);
		return String.format(data, weather_id, "forecast_v", time, KeyUtils.CHINA_WEATHER_APPID) + "&key=" + key;
	}

	/**
	 * 获得省列表
	 * 
	 * @return
	 */
	public List<CityInfo> getProvince() {
		SQLiteDatabase db = getCityDatabase();
		Cursor c = db.rawQuery("select * from " + CityInfo.Table.TABLE_PROVINCE, null);
		return CityInfo.cursor2bean(c, CityInfo.Table.TABLE_PROVINCE);
	}

	/**
	 * 根据省id获得市级列表
	 * 
	 * @param province_id
	 * @return
	 */
	public List<CityInfo> getCity(String province_id) {
		SQLiteDatabase db = getCityDatabase();
		Cursor c = db.rawQuery("select * from " + CityInfo.Table.TABLE_CITY + " where " + CityInfo.Table.PROVINCE_ID + "='" + province_id + "'", null);
		return CityInfo.cursor2bean(c, CityInfo.Table.TABLE_CITY);
	}

	/**
	 * 根据县id获得区域列表
	 * 
	 * @param city_id
	 * @return
	 */
	public List<CityInfo> getArea(String city_id) {
		SQLiteDatabase db = getCityDatabase();
		Cursor c = db.rawQuery("select * from " + CityInfo.Table.TABLE_AREA + " where " + CityInfo.Table.CITY_ID + "='" + city_id + "'", null);
		return CityInfo.cursor2bean(c, CityInfo.Table.TABLE_AREA);
	}

	/**
	 * 根据天气编码查询天气现象名称
	 * 
	 * @param weatherNum
	 *            天气编码
	 * @return 天气现象名称
	 */
	public String getWeatherName(String weatherNum) {
		String name = "";
		SQLiteDatabase db = getCityDatabase();
		Cursor c = db.rawQuery("select * from " + CityInfo.Table.TABLE_WEATHER + " where " + CityInfo.Table.CODE_ID + "='" + weatherNum + "'", null);
		if (c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				if (c.getColumnIndex(CityInfo.Table.ZN_NAME) != -1) {
					name = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.ZN_NAME));
				}
			}
			c.close();
		}
		return name;
	}
	
	/**
	 * 保存天气信息到Preferences（始终存在最新天气信息）
	 * @param info
	 */
	public void saveWeatherInfo(String info){
		PreferencesUtils.setString(KeyUtils.ACTION_WEATHER_DATA, info);
		LogUtils.i(KeyUtils.TAG_LHC, "insert weather info to Preferences success..");
	}
	
	/**
	 * 没有联网情况下显示本地天气信息
	 * @return
	 */
	public String getLocalWeatherInfo(){
		return PreferencesUtils.getString(KeyUtils.ACTION_WEATHER_DATA, "");
	}
	
	/**
	 * 天气数据库扩展使用
	 */
//	private WeatherDbHelper mWeatherDbHelper = null;
//	private SQLiteDatabase getWeatherDatabase() {
//		if (mWeatherDbHelper == null) {
//			mWeatherDbHelper = new WeatherDbHelper(mContext);
//		}
//		return mWeatherDbHelper.getWritableDatabase();
//	}
//	
//	/**
//	 * 插入天气信息
//	 * 
//	 * @param info
//	 */
//	public void addWeather(WeatherInfo info) {
//		SQLiteDatabase db = getWeatherDatabase();
//		ContentValues cv = new ContentValues();
//		cv.put(WeatherInfo.Table.AREA_ID, info.areaid);
//		cv.put(WeatherInfo.Table.CITY_NAME, info.cityName);
//		cv.put(WeatherInfo.Table.RELEASE_TIME, info.releaseTime);
//		cv.put(WeatherInfo.Table.DAYWEATHER_NUM, info.dayWeatherNum);
//		cv.put(WeatherInfo.Table.DAYWEATHER, info.dayWeather);
//		cv.put(WeatherInfo.Table.DAY_TEMP, info.dayTemp);
//		cv.put(WeatherInfo.Table.NIGHTWEATHER_NUM, info.nightWeatherNum);
//		cv.put(WeatherInfo.Table.NIGHTWEATHER, info.nightWeather);
//		cv.put(WeatherInfo.Table.NIGHTTEMP, info.nightTemp);
//		db.insert(WeatherInfo.Table.TABLE_NAME, null, cv);
//		db.close();
//		LogUtils.i(KeyUtils.TAG_LHC, "insert weather info to db success..");
//	}
//
//	/**
//	 * 删除消息
//	 * 
//	 * @param releaseTiem
//	 * @return
//	 */
//	public void deleteWeather(String releaseTiem) {
//		SQLiteDatabase db = getWeatherDatabase();
//		db.delete(WeatherInfo.Table.TABLE_NAME, "where "+WeatherInfo.Table.RELEASE_TIME +" < '"+releaseTiem+"'", null);
//		db.close();
//	}
//
//	/**
//	 * 查询数据库最近一条记录
//	 * 
//	 * @return
//	 */
//	public WeatherInfo getLocalWeather(String areaid) {
//		if (mWeatherDbHelper == null) {
//			mWeatherDbHelper = new WeatherDbHelper(mContext);
//		}
//		WeatherInfo info =  mWeatherDbHelper.rawQueryForObject("select * from " + WeatherInfo.Table.TABLE_NAME 
//				+ " where "+WeatherInfo.Table.AREA_ID+"='"+areaid+"' "
//				+ " order by _id DESC limit 1", null, new RawQuery<WeatherInfo>() {
//			@Override
//			public WeatherInfo rawQuery(Cursor arg0, int arg1) {
//				return WeatherInfo.cursor2bean(arg0);
//			}
//		});
//		mWeatherDbHelper.close();
//		return info;
//	}
//
//	/**
//	 * 更新天气信息
//	 * 
//	 * @param id
//	 * @param values
//	 */
//	public void update(long... ids) {
//		
//	}

	
}
