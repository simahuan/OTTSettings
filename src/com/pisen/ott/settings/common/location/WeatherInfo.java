package com.pisen.ott.settings.common.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.izy.util.StringUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

/**
 * 天气详细信息对象
 * (初步定义天气信息为一天的简要信息,后期需扩展参考文档SmartWeatherAPI_Lite_WebAPI_3.0.2)
 * @author Liuhc
 * @version 1.0 2014年12月8日 下午2:23:59
 */
public class WeatherInfo implements Parcelable{
	
	/**
	 * 数据库创建消息表
	 */
	public static final class Table implements BaseColumns {
		/** 表名 */
		public static final String TABLE_NAME = "weatherinfo_table";
		/** /天气所属城市区域id */
		public static final String AREA_ID = "areaid";
		/** 城市名 */
		public static final String CITY_NAME = "cityName";
		/** 天气更新时间*/
		public static final String RELEASE_TIME = "releaseTime";
		/** 白天天气编号 */
		public static final String DAYWEATHER_NUM = "dayWeatherNum";
		/** 白天天气 */
		public static final String DAYWEATHER = "dayWeather";
		/** 白天天气温度**/
		public static final String DAY_TEMP = "dayTemp";
		/** 夜间天气编号**/
		public static final String NIGHTWEATHER_NUM = "nightWeatherNum";
		/** 夜间天气 */
		public static final String NIGHTWEATHER = "nightWeather";
		/** 夜间天气温度**/
		public static final String NIGHTTEMP = "nightTemp";
		
	}
	
	//天气所属城市区域id
	public String areaid;
	//城市名
	public String cityName;
	//天气更新时间
	public String releaseTime;
	//白天天气编号
	public String dayWeatherNum;
	//白天天气
	public String dayWeather;
	//白天天气温度
	public String dayTemp;
	//夜间天气编号
	public String nightWeatherNum;
	//夜间天气
	public String nightWeather;
	//夜间天气温度
	public String nightTemp;
	

	/**
	 * 
	 */
	public WeatherInfo() {
	}
	
	/**
	 * 访问中国天气网查询接口http://open.weather.com.cn/data/?areaid=%s&type=%s&date=%s&appid=%s返回数据包
	 * @param json
	 * @param ctx
	 * @return
	 */
	public static WeatherInfo json2bean(String json, Context ctx){
		WeatherInfo info = null;
		if (!StringUtils.isEmpty(json)) {
			try {
				JSONObject obj = new JSONObject(json);
				info = new WeatherInfo();
				info.areaid = obj.getJSONObject("c").getString("c1");
				info.cityName = obj.getJSONObject("c").getString("c3");
				
				info.releaseTime = obj.getJSONObject("f").getString("f0");
				JSONArray array = obj.getJSONObject("f").getJSONArray("f1");
				for (int i = 0; i < array.length(); i++) {
					JSONObject fObj = array.getJSONObject(i);
					//获取第一天的天气数据
					info.dayWeatherNum = fObj.getString("fa");
					info.nightWeatherNum = fObj.getString("fb");
					info.dayTemp = fObj.getString("fc");
					info.nightTemp = fObj.getString("fd");
					
					if (!StringUtils.isEmpty(info.dayWeatherNum)) {
						info.dayWeather = CityManager.getInstance(ctx).getWeatherName(info.dayWeatherNum);
					}
					if (!StringUtils.isEmpty(info.nightWeatherNum)) {
						info.nightWeather = CityManager.getInstance(ctx).getWeatherName(info.nightWeatherNum);
					}
					
					return info;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return info;
	}

	/**
	 * 生成传输对象
	 * @param cursor
	 * @return
	 */
	public static WeatherInfo cursor2bean(Cursor cursor) {
		WeatherInfo info = new WeatherInfo();
//		info.id = cursor.getLong(cursor.getColumnIndexOrThrow(WeatherInfo.Table._ID));
		info.areaid = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.AREA_ID));
		info.cityName = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.CITY_NAME));
		info.releaseTime = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.RELEASE_TIME));
		info.dayWeatherNum = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.DAYWEATHER_NUM));
		info.dayWeather = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.DAYWEATHER));
		info.dayTemp = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.DAY_TEMP));
		info.nightWeatherNum = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.NIGHTWEATHER_NUM));
		info.nightWeather = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.NIGHTWEATHER));
		info.nightTemp = cursor.getString(cursor.getColumnIndexOrThrow(WeatherInfo.Table.NIGHTTEMP));
		return info;
	}
	
	
	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getDayWeatherNum() {
		return dayWeatherNum;
	}

	public void setDayWeatherNum(String dayWeatherNum) {
		this.dayWeatherNum = dayWeatherNum;
	}

	public String getDayWeather() {
		return dayWeather;
	}

	public void setDayWeather(String dayWeather) {
		this.dayWeather = dayWeather;
	}

	public String getDayTemp() {
		return dayTemp;
	}

	public void setDayTemp(String dayTemp) {
		this.dayTemp = dayTemp;
	}

	public String getNightWeatherNum() {
		return nightWeatherNum;
	}

	public void setNightWeatherNum(String nightWeatherNum) {
		this.nightWeatherNum = nightWeatherNum;
	}

	public String getNightWeather() {
		return nightWeather;
	}

	public void setNightWeather(String nightWeather) {
		this.nightWeather = nightWeather;
	}

	public String getNightTemp() {
		return nightTemp;
	}

	public void setNightTemp(String nightTemp) {
		this.nightTemp = nightTemp;
	}

	@Override
	public String toString() {
		return "WeatherInfo [areaid=" + areaid + ", cityName=" + cityName + ", releaseTime=" + releaseTime + ", dayWeatherNum=" + dayWeatherNum
				+ ", dayWeather=" + dayWeather + ", dayTemp=" + dayTemp + ", nightWeatherNum=" + nightWeatherNum + ", nightWeather=" + nightWeather
				+ ", nightTemp=" + nightTemp + "]";
	}

	
	
	public static final Parcelable.Creator<WeatherInfo> CREATOR = new Creator<WeatherInfo>() {
		
		@Override
		public WeatherInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new WeatherInfo[size];
		}
		
		@Override
		public WeatherInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new WeatherInfo(source);
		}
	};
	
	/**
	 * @param source
	 */
	public WeatherInfo(Parcel source) {
		readFromParcel(source);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	//注意写入变量和读取变量的顺序应该一致 不然得不到正确的结果 
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(areaid);
		dest.writeString(cityName);
		dest.writeString(releaseTime);
		dest.writeString(dayWeatherNum);
		dest.writeString(dayWeather);
		dest.writeString(dayTemp);
		dest.writeString(nightWeatherNum);
		dest.writeString(nightWeather);
		dest.writeString(nightTemp);
        
	}
	
	 //注意读取变量和写入变量的顺序应该一致 不然得不到正确的结果  
    public void readFromParcel(Parcel source) {
    	areaid = source.readString();
    	cityName = source.readString();
    	releaseTime = source.readString();
    	dayWeatherNum = source.readString();
    	dayWeather = source.readString();
    	dayTemp = source.readString();
    	nightWeatherNum = source.readString();
    	nightWeather = source.readString();
    	nightTemp = source.readString();
    }
	
}
