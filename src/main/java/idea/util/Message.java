/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.util;

import org.json.*;

/**
 * 將欲回傳訊息打包成 JSON 格式.
 *
 * @author Miles Chen
 */
public class Message {
	/** 錯誤訊息 status. */
	public final static String FALSE = "false";
	/** 正確訊息 status. */
	public final static String TRUE = "true";

	/**
	 * 將錯誤訊息打包成 JSON 格式.
	 *
	 * @param msg 錯誤訊息
	 * @return JSON 格式錯誤訊息
	 */
	public static String errMsg(String msg) {
		return message(msg, FALSE, 0, null);
	}

	/**
	 * 將正確訊息打包成 JSON 格式.
	 *
	 * @param msg    訊息
	 * @param total  總共資料筆數
	 * @param json   JSON 格式物件
	 * @return JSON 格式的訊息
	 */
	public static String trueMsg(String msg, int total, Object json) {
		return message(msg, TRUE, total, json);
	}

	/**
	 * 將訊息打包成 JSON 格式.
	 *
	 * @param msg    訊息
	 * @param status 是否正確
	 * @param total  總共資料筆數
	 * @param json   JSON 格式物件
	 * @return JSON 格式的訊息
	 */
	public static String message(String msg, String status, int total, Object json) {
		JSONObject jo = new JSONObject();
		try {
			jo.put("message", msg);
			jo.put("status", status);
			jo.put("total", total);
			jo.put("data", json);
		} catch (JSONException e) {
			return e.toString();
		}
		return jo.toString();
	}
}
