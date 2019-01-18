package com.pisen.ott.settings.common.location;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * 城市信息
 * @author Liuhc
 * @version 1.0 2014年12月3日 下午3:26:21
 */
public class CityInfo {

	
	/**
	 * 数据库创建消息表
	 */
	public static final class Table implements BaseColumns {
		/** 表名 */
		public static final String TABLE_PROVINCE = "province_table"; 	//省
		public static final String TABLE_CITY = "city_table";			//市县
		public static final String TABLE_AREA = "area_table";			//市镇
		public static final String TABLE_WEATHER = "weather_phenomenon_table";	//天气现象表
		
		/**
		 * 字段名
		 */
		public static final String PROVINCE_ID = "PROVINCE_ID";
		public static final String PROVINCE = "PROVINCE";
		public static final String CITY_ID = "CITY_ID";
		public static final String CITY = "CITY";
		public static final String AREA_ID = "AREA_ID";
		public static final String AREA = "AREA";
		public static final String WEATHER_ID = "WEATHER_ID";
		
		public static final String CODE_ID = "CODE_ID";
		public static final String ZN_NAME = "ZN_NAME";
		
	}
	
	public String city_id;
	public String cityName;
	public String weather_id; //天气id
	
	/**
	 * 数据库查询结果转换成集合
	 * @param cursor
	 * @return
	 */
	public static List<CityInfo> cursor2bean(Cursor c,String tableName) {
		List<CityInfo> list = new ArrayList<CityInfo>();
		if (c != null && c.getCount() > 0) {
			while (c.moveToNext()) {
				CityInfo info = new CityInfo();
				if (tableName.equals(CityInfo.Table.TABLE_PROVINCE)) {
					info.city_id = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.PROVINCE_ID));
					info.cityName = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.PROVINCE));
				}else if (tableName.equals(CityInfo.Table.TABLE_CITY)) {
					info.city_id = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.CITY_ID));
					info.cityName = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.CITY));
				}else if (tableName.equals(CityInfo.Table.TABLE_AREA)) {
					info.city_id = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.AREA_ID));
					info.cityName = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.AREA));
					if (c.getColumnIndex(CityInfo.Table.WEATHER_ID) != -1) {
						info.weather_id = c.getString(c.getColumnIndexOrThrow(CityInfo.Table.WEATHER_ID));
					}
				}
				list.add(info);
			}
			c.close();
		}
		return list;
	}
}
