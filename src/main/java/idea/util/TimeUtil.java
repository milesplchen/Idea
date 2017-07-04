/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.util;

import java.util.*;
import java.text.*;

/**
 * 時間處理相關函式.
 *
 * @author Miles Chen
 */
public class TimeUtil {
	/** 一秒有幾毫秒. */
	public final static long SECOND = 1000;
	/** 一分有幾毫秒. */
	public final static long MINUTE = 60 * 1000;
	/** 一小時有幾毫秒. */
	public final static long HOUR = 60 * MINUTE;
	/** 一天有幾毫秒. */
	public final static long DAY = 24 * 60 * 60 * 1000;
	/** 一周有幾毫秒. */
	public final static long WEEK = DAY * 7;
	/** 一個月有幾毫秒. */
	public final static long MONTH = DAY * 30;
	/** 一季有幾毫秒. */
	public final static long SEASON = DAY * 90;
	/** 一年有幾毫秒. */
	public final static long YEAR = DAY * 365;

	/** 短日期格式. */
	public final static String SHORT_DATE_FORMAT = "yyyyMMdd HH:mm:ss";
	/** MySQL 日期格式. */
	public final static String MYSQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 將日期輸出成字串.
	 *
	 * @param date 日期
	 * @param fmt  欲輸出的日期格式
	 * @return 字串格式日期
	 */
	public static String format(Date date, String fmt) {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(date);
	}

	/**
	 * 將毫秒格式的日期輸出成字串.
	 *
	 * @param millis 毫秒格式的日期
	 * @param fmt    欲輸出的日期格式
	 * @return 字串格式日期
	 */
	public static String format(long millis, String fmt) {
		return format( new Date(millis), fmt );
	}

	/**
	 * 輸入日期字串，輸出 Date 型態日期.
	 *
	 * @param time 日期字串
	 * @param fmt  輸入的日期格式
	 * @return Date 型態日期
	 * @throws ParseException 日期格式錯誤
	 */
	public static Date toDate(String time, String fmt) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat( fmt.substring(0, time.length()) );
		return sdf.parse(time);
	}

	/**
	 * 輸入日期字串，輸出以毫秒表示的日期.
	 *
	 * @param time 日期字串
	 * @param fmt  輸入的日期格式
	 * @return 以毫秒表示的日期
	 * @throws ParseException 日期格式錯誤
	 */
	public static long toDateInMillis(String time, String fmt) throws ParseException {
		return toDate(time, fmt).getTime();
	}

	/**
	 * 輸入日期，輸出星期幾.
	 * 1 ~ 7. 週一到週日.
	 *
	 * @param date 日期
	 * @return 星期幾
	 */
	public static int getDayOfWeek(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("u");
		return Integer.valueOf(sdf.format(date));
	}

	/**
	 * 輸入日期，輸出年份.
	 *
	 * @param date 日期
	 * @return 年份
	 */
	public static int getYear(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("y");
		return Integer.valueOf(sdf.format(date));
	}

	/**
	 * 輸入日期，輸出月份.
	 *
	 * @param date 日期
	 * @return 月份
	 */
	public static int getMonth(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("M");
		return Integer.valueOf(sdf.format(date));
	}

	/**
	 * 輸入日期，輸出日.
	 *
	 * @param date 日期
	 * @return 日
	 */
	public static int getDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("d");
		return Integer.valueOf(sdf.format(date));
	}

	/**
	 * 輸入日期，輸出時.
	 *
	 * @param date 日期
	 * @return 時
	 */
	public static int getHour(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("h");
		return Integer.valueOf(sdf.format(date));
	}

	/**
	 * 輸入日期，輸出分.
	 *
	 * @param date 日期
	 * @return 分
	 */
	public static int getMinute(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MINUTE);
	}

	/**
	 * 輸入日期，輸出秒.
	 *
	 * @param date 日期
	 * @return 秒
	 */
	public static int getSecond(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.SECOND);
	}

	/**
	 * 輸入日期，輸出星期幾.
	 * 1 ~ 7. 週一到週日.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 星期幾
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getDayOfWeek(String date, String fmt) throws ParseException {
		return getDayOfWeek( toDate(date, fmt) );
	}

	/**
	 * 輸入日期，輸出年份.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 年份
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getYear(String date, String fmt) throws ParseException {
		return getYear( toDate(date, fmt) );
	}

	/**
	 * 輸入日期，輸出月份.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 月份
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getMonth(String date, String fmt) throws ParseException {
		return getMonth( toDate(date, fmt) );
	}

	/**
	 * 輸入日期，輸出日.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 日
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getDate(String date, String fmt) throws ParseException {
		return getDate( toDate(date, fmt) );
	}

	/**
	 * 輸入日期，輸出時.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 時
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getHour(String date, String fmt) throws ParseException {
		return getHour( toDate(date, fmt) );
	}

	/**
	 * 輸入日期，輸出分.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 分
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getMinute(String date, String fmt) throws ParseException {
		return getMinute( toDate(date, fmt) );
	}

	/**
	 * 輸入日期，輸出秒.
	 *
	 * @param date 日期
	 * @param fmt  輸入的日期格式
	 * @return 秒
	 * @throws ParseException 日期格式錯誤
	 */
	public static int getSecond(String date, String fmt) throws ParseException {
		return getSecond( toDate(date, fmt) );
	}

	/**
	 * 取得目前時間字串.
	 *
	 * @param fmt 日期格式
	 * @return 目前時間
	 */
	public static String getCurrentTime(String fmt) {
		return format(new Date(), fmt);
	}

	/**
	 * 將字串格式的日期加上指定的毫秒數，並輸出字串格式日期答案.
	 *
	 * @param time   欲進行運算的字串格式日期
	 * @param millis 欲相加的毫秒數
	 * @param fmt    日期格式
	 * @return 相加之後的字串格式日期
	 * @throws ParseException 日期格式錯誤
	 */
	public static String addMillis(String time, long millis, String fmt) throws ParseException {
		long t = toDateInMillis(time, fmt) + millis;
		return format(t, fmt);
	}
}
