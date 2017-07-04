/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.util;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * 處理檔案複製、移動、刪除、取得建立時間等操作.
 *
 * @author Miles Chen
 */
public class FileUtil {
	/**
	 * 建立路徑中所有的資料夾.
	 *
	 * @param path 資料夾路徑
	 * @return 是否建立成功
	 */
	public static boolean mkdirs(String path) {
		File f = new File(path);
		return f.mkdirs();
	}

	/**
	 * 複製檔案.
	 *
	 * @param src_path 來源路徑
	 * @param dst_path 目的路徑
	 * @throws IOException 讀寫檔案錯誤
	 */
	public static void copy(String src_path, String dst_path) throws IOException {
		File dest = new File(dst_path);
		dest.getParentFile().mkdirs();

		InputStream in = new BufferedInputStream(new FileInputStream(src_path));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(dst_path));
		byte[] buf = new byte[1024];	// Transfer bytes from in to out

		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);

		in.close();
		out.close();
	}

	/**
	 * 刪除檔案.
	 *
	 * @param path 檔案路徑
	 * @return 是否刪除成功
	 */
	public static boolean delete(String path) {
		File f = new File(path);
		return f.delete();
	}

	/**
	 * 移動檔案.
	 *
	 * @param src_path 來源路徑
	 * @param dst_path 目的路徑
	 * @throws IOException 讀寫檔案錯誤
	 */
	public static void move(String src_path, String dst_path) throws IOException {
		copy(src_path, dst_path);
		delete(src_path);
	}

	/**
	 * 複製網址的東西至檔案.
	 *
	 * @param url      檔案來源網址
	 * @param dst_path 目的路徑
	 * @throws MalformedURLException URL 格式錯誤
	 * @throws IOException           開啟 URL 錯誤、寫入檔案錯誤
	 */
	public static void copyURLtoFile(String url, String dst_path) throws MalformedURLException, IOException {
		File dest = new File(dst_path);
		dest.getParentFile().mkdirs();

		URL src = new URL(url);
		URLConnection url_conn = src.openConnection();
		InputStream in = new BufferedInputStream(url_conn.getInputStream());

		OutputStream out = new BufferedOutputStream(new FileOutputStream(dst_path));
		byte[] buf = new byte[1024];	// Transfer bytes from in to out

		int len;
		while ((len = in.read(buf)) > 0)
			out.write(buf, 0, len);

		in.close();
		out.close();

//		FileUtils.copyURLToFile(src, dest);
	}

	/**
	 * 取得 URL 的檔案名稱.
	 *
	 * @param url 檔案來源網址
	 * @return 檔案名稱
	 * @throws MalformedURLException URL 格式錯誤
	 * @throws IOException           開啟 URL 錯誤
	 */
	public static String getUrlFileName(String url) throws MalformedURLException, IOException {
		URL src = new URL(url);
		URLConnection url_conn = src.openConnection();
		String raw = url_conn.getHeaderField("Content-Disposition");

		String name = null;
		if(raw != null && raw.indexOf("=") != -1)
		    name = raw.split("=")[1].replaceAll("\"", "");

		return name;
	}

	/**
	 * 清理資料夾裡超過幾天的檔案.
	 *
	 * @param path 資料夾路徑
	 * @param days 要刪除幾天前的檔案
	 */
	public static void clearDirectory(String path, int days) {
		File dir = new File(path);
		File[] file_list = dir.listFiles();		// 列出資料夾下所有檔案

		Calendar cal = Calendar.getInstance();	// 設定目前時間
		cal.add(Calendar.DAY_OF_MONTH, days);	// 設定要刪除幾天前的檔案

		for (int i = 0; i < file_list.length; i++) {
			Date file_time = new Date(file_list[i].lastModified());		// 取得檔案時間

			if (file_list[i].isFile() && file_time.before(cal.getTime()))
				file_list[i].delete();
		}
	}
}
