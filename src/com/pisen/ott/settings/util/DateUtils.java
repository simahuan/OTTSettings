package com.pisen.ott.settings.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * 时间操作工具类
 * 
 * @author Liuhc
 * @version 1.0 2014年12月5日 上午10:39:31
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {

	// 默认格式
	public static final String TIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	// 简单格式
	public static final String TIME_FORMAT_SIMPLE = "yyyy-MM-dd";
	// 缩写格式（天气查询使用）
	public static final String TIME_FORMAT_WEATHER = "yyyyMMddHHmm";

	/**
	 * 获取当前日期时间字符串 (格式为yyyy-MM-dd HH:mm:ss)
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_DEFAULT);
		return sdf.format(new Date());
	}

	/**
	 * 根据格式获取当前时间
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurrentTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	/**
	 * 根据获取当前日期
	 * 
	 * @return
	 */
	public static long getCurrentLongTime() {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_SIMPLE);
		try {
			return sdf.parse(getCurrentTime()).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date().getTime();
	}

	/**
	 * 时间转换
	 * 
	 * @param pTimeMillis
	 *            精确到秒(如果精确到毫秒需要/1000)
	 * @return
	 */
	public static String long2Time(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_DEFAULT);
		return sdf.format(new Date(time));
	}

	/**
	 * 时间转换
	 * 
	 * @param time
	 *            精确到毫秒(如果精确到秒需要*1000)
	 * @return
	 */
	public static Date long2Date(long time) {
		return new Date(time);
	}

	/**
	 * 根据时间戳获取日期
	 * 
	 * @param pTimeMillis
	 * @return
	 */
	public static String getFormatTime(long pTimeMillis, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(pTimeMillis));
	}

	/**
	 * 时间转换成date
	 * 
	 * @param time
	 * @return
	 */
	public static Date time2Date(String time) {
		SimpleDateFormat df = new SimpleDateFormat(TIME_FORMAT_DEFAULT);
		try {
			return df.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return 返回所有闹钟时间
	 */
	public static long[] getAlarmTimes() {
		long[] times = new long[3];
		times[0] = getAlarmTime("08:30");
		times[1] = getAlarmTime("11:30");
		times[1] = getAlarmTime("18:30");
		return times;
	}

	/**
	 * 时间转换成long
	 * @param alarmTime 格式为24小时时间格式：如09:00，21:00
	 * @return
	 */
	public static long getAlarmTime(String alarmTime) {
		long time = 0;
		try {
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
			Date d = fmt.parse(alarmTime);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(getCurrentLongTime());
			calendar.set(Calendar.HOUR_OF_DAY, d.getHours());
			calendar.set(Calendar.MINUTE, d.getMinutes());
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			time = calendar.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
}
