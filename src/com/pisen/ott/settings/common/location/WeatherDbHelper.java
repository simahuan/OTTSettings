package com.pisen.ott.settings.common.location;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.izy.database.sqlite.SqliteHelper;

/**
 * 天气数据库帮助类
 * @author lhc
 * @version 1.0 2014年12月18日14:36:59
 */
public class WeatherDbHelper extends SqliteHelper {

	private final static String DATABASE_NAME = "ottbox_weather.db";
	private static final int VERSION = 20141218;

	public WeatherDbHelper(Context context) {
		super(context, DATABASE_NAME, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTransferInfo(db);
		onUpgrade(db, 0, VERSION);
	}

	// 创建天气信息管理表
	private void createTransferInfo(SQLiteDatabase db) {
		db.execSQL("create table " + WeatherInfo.Table.TABLE_NAME + "(" 
				+ WeatherInfo.Table._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ WeatherInfo.Table.AREA_ID + " TEXT,"
				+ WeatherInfo.Table.CITY_NAME + " TEXT,"
				+ WeatherInfo.Table.RELEASE_TIME + " TEXT ,"
				+ WeatherInfo.Table.DAYWEATHER_NUM + " TEXT,"
				+ WeatherInfo.Table.DAYWEATHER + " TEXT,"
				+ WeatherInfo.Table.DAY_TEMP + " TEXT,"
				+ WeatherInfo.Table.NIGHTWEATHER_NUM + " TEXT,"
				+ WeatherInfo.Table.NIGHTWEATHER + " TEXT,"
				+ WeatherInfo.Table.NIGHTTEMP + " TEXT)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (newVersion) {
//		case 20141030:
//			addColumn(db, MessageInfo.Table.TABLE_NAME, MessageInfo.Table.CUSTOM_TAG, "TEXT");
//			break;
		default:
			break;
		}
	}
	
}
