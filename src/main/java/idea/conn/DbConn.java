/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.conn;

import java.io.*;
import java.util.*;
import java.sql.*;

/**
 * 處理與資料庫連線與 SQL 的各種動作.
 *
 * @author Miles Chen
 */
public class DbConn {
	/** 批次處理的大小. */
	protected int batch_size = 100;
	/** 目前批次處理的個數. */
	protected int count = 0;

	/** 資料庫連線. */
	protected Connection con;
	/** 執行 SQL 指令. */
	protected Statement stat;
	/** 預先編譯 SQL 指令. */
	protected PreparedStatement pst;

	/** 資料庫 driver. */
	protected String driver;
	/** 資料庫連結. */
	protected String url;
	/** 帳號. */
	protected String usr;
	/** 密碼. */
	protected String pw;

	/**
	 * 從 properties 檔載入資料庫連線資訊.
	 * 資料庫 driver 變數名稱: driver.
	 * URL 變數名稱: url. 例: jdbc:xxsql://xxx.x.x.xx:xxxx/xxxxx.
	 * 帳號變數名稱: usr.
	 * 密碼變數名稱: pw.
	 *
	 * @param filename 檔案名稱
	 * @throws FileNotFoundException 找不到檔案
	 * @throws IOException           檔案讀取失敗
	 */
	public void loadProperties(String filename) throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(filename));

		driver = props.getProperty("driver");
		url = props.getProperty("url");
		usr = props.getProperty("usr");
		pw = props.getProperty("pw");
	}

	/**
	 * 設定所使用的資料庫 driver.
	 *
	 * @param driver 資料庫 driver
	 */
	public void setDriver(String driver) {
		this.driver = driver;
	}

	/**
	 * 設定連線資訊.
	 *
	 * @param url 資料庫 URL
	 * @param usr 使用者帳號
	 * @param pw  使用者密碼
	 */
	public void setConn(String url, String usr, String pw) {
		this.url = url;
		this.usr = usr;
		this.pw = pw;
	}

	/**
	 * Connect to database.
	 *
	 * @throws ClassNotFoundException Driver class not found!
	 * @throws SQLException           Unable to connect to the database!
	 */
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName(driver);
		con = DriverManager.getConnection(url, usr, pw);
		stat = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	/**
	 * 從資料庫找資料.
	 *
	 * @param sql 欲執行的 SELECT SQL 語法
	 * @return SELECT 出來的資料
	 * @throws SQLException SQL 執行錯誤
	 */
	public ResultSet select(String sql) throws SQLException {
		if (isInjection(sql))
			return null;
		return stat.executeQuery(sql);
	}

	/**
	 * 找出該資料的筆數.
	 *
	 * @param sql COUNT 資料筆數的 SQL 語法
	 * @return 資料筆數
	 * @throws SQLException SQL 執行錯誤
	 */
	public int getSize(String sql) throws SQLException {
		int total = 0;
		ResultSet rs = select(sql);
		if (rs.next())
			total = rs.getInt(1);
		rs.close();
		return total;
	}

	/**
	 * 計算 ResultSet 的資料筆數.
	 *
	 * @param rs 欲計算的 ResultSet
	 * @return 資料筆數
	 * @throws SQLException ResultSet 為空
	 */
	public static int getSize(ResultSet rs) throws SQLException {
		int total = 0;
		if (rs.last()) {
			total = rs.getRow();
			rs.beforeFirst();
		}
		return total;
	}

	/**
	 * 更新資料庫的資料.
	 *
	 * @param sql 欲執行的 INSERT, UPDATE SQL語法
	 * @return 受指令影響列數
	 * @throws SQLException SQL 執行錯誤
	 */
	public int update(String sql) throws SQLException {
		if (isInjection(sql))
			return -1;
		return stat.executeUpdate(sql);
	}

	/**
	 * 刪除資料庫的資料.
	 *
	 * @param sql 欲執行的 DELETE 等 SQL 語法
	 * @return 受指令影響列數
	 * @throws SQLException SQL 執行錯誤
	 */
	public int delete(String sql) throws SQLException {
		return stat.executeUpdate(sql);
	}

	/**
	 * 設定批次處理的大小.
	 *
	 * @param size 批次處理的大小
	 */
	public void setBatchSize(int size) {
		batch_size = size;
	}

	/**
	 * 將 SQL 語法加入批次處理. 若達到處理上限則進行處理.
	 *
	 * @param sql 欲加入批次處理的 SQL 語法
	 * @return null or an array of update counts
	 * @throws SQLException SQL 錯誤
	 */
	public int[] addBatch(String sql) throws SQLException {
		stat.addBatch(sql);

		count = (++count) % batch_size;
		if (count == 0)
			return stat.executeBatch();
		return null;
	}

	/**
	 * 執行批次處理.
	 *
	 * @return an array of update counts
	 * @throws SQLException SQL 執行錯誤
	 */
	public int[] executeStBatch() throws SQLException {
		return stat.executeBatch();
	}

	/** 關閉連線. Connection closed. */
	public void close() {
		try {
			if (pst != null)
				pst.close();
			if (stat != null)
				stat.close();
			if (con != null)
				con.close();
		} catch (SQLException e) {
		}
	}

	/**
	 * 設定 prepare statement SQL 語句.
	 *
	 * @param sql SQL語法
	 * @throws SQLException SQL語法錯誤
	 */
	public void setPrepareStatement(String sql) throws SQLException {
		if (pst != null)
			pst.close();
		pst = con.prepareStatement(sql);
	}

	/**
	 * 設定 prepare statement SQL 語句、回傳自動生成的主鍵.
	 *
	 * @param sql SQL語法
	 * @throws SQLException SQL語法錯誤
	 */
	public void setPreStmtRtID(String sql) throws SQLException {
		if (pst != null)
			pst.close();
		pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	}

	/**
	 * 取得 prepare statement 自動生成的 ID.
	 *
	 * @return 自動生成的 ID
	 * @throws SQLException 取得 ID 失敗
	 */
	public long getPstID() throws SQLException {
		long id = -1;
		ResultSet rs = pst.getGeneratedKeys();
		if (rs.next())
			id = rs.getLong(1);
		rs.close();
		return id;
	}

	/**
	 * 設定 prepare statement String 參數.
	 *
	 * @param idx 參數 index
	 * @param x   參數
	 * @throws SQLException 資料格式設定錯誤
	 */
	public void setString(int idx, String x) throws SQLException {
		pst.setString(idx, x);
	}

	/**
	 * 設定 prepare statement int 參數.
	 *
	 * @param idx 參數 index
	 * @param x   參數
	 * @throws SQLException 資料格式設定錯誤
	 */
	public void setInt(int idx, int x) throws SQLException {
		pst.setInt(idx, x);
	}

	/**
	 * 設定 prepare statement long 參數.
	 *
	 * @param idx 參數 index
	 * @param x   參數
	 * @throws SQLException 資料格式設定錯誤
	 */
	public void setLong(int idx, long x) throws SQLException {
		pst.setLong(idx, x);
	}

	/**
	 * 設定 prepare statement double 參數.
	 *
	 * @param idx 參數 index
	 * @param x   參數
	 * @throws SQLException 資料格式設定錯誤
	 */
	public void setDouble(int idx, double x) throws SQLException {
		pst.setDouble(idx, x);
	}

	/**
	 * 設定 prepare statement Object 參數.
	 *
	 * @param idx 參數 index
	 * @param x   參數
	 * @throws SQLException 資料格式設定錯誤
	 */
	public void setObject(int idx, Object x) throws SQLException {
		pst.setObject(idx, x);
	}

	/**
	 * 設定 prepare statement 參數為 NULL.
	 *
	 * @param idx     參數 index
	 * @param sqlType 參數
	 * @throws SQLException 資料格式設定錯誤
	 */
	public void setNull(int idx, int sqlType) throws SQLException {
		pst.setNull(idx, sqlType);
	}

	/**
	 * 執行 prepare statement 的 select 語法.
	 *
	 * @return select 出來的資料
	 * @throws SQLException SQL執行錯誤
	 */
	public ResultSet select() throws SQLException {
		return pst.executeQuery();
	}

	/**
	 * 執行 prepare statement 的 insert, update 語法.
	 *
	 * @return 受指令影響列數
	 * @throws SQLException SQL執行錯誤
	 */
	public int update() throws SQLException {
		return pst.executeUpdate();
	}

	/**
	 * 將 SQL 語法加入批次處理. 若達到處理上限則進行處理.
	 *
	 * @return null or an array of update counts
	 * @throws SQLException SQL 錯誤
	 */
	public int[] addBatch() throws SQLException {
		pst.addBatch();

		count = (++count) % batch_size;
		if (count == 0)
			return pst.executeBatch();
		return null;
	}

	/**
	 * 執行批次處理.
	 *
	 * @return an array of update counts
	 * @throws SQLException SQL 執行錯誤
	 */
	public int[] executePstBatch() throws SQLException {
		return pst.executeBatch();
	}

	/**
	 * 清除 table 裡的資料.
	 *
	 * @param table_name 欲清除的 table 名稱
	 * @throws SQLException SQL 執行錯誤
	 */
	public void clearTable(String table_name) throws SQLException {
		stat.executeUpdate("DELETE FROM " + table_name);
		stat.executeUpdate("ALTER TABLE "+ table_name + " AUTO_INCREMENT = 1");
	}

	/**
	 * 清除 table 裡某個日期以前的資料.
	 *
	 * @param table_name 欲清除的 table 名稱
	 * @param column     日期欄位名稱
	 * @param date       日期
	 * @throws SQLException SQL 執行錯誤
	 */
	public void clearTable(String table_name, String column, String date) throws SQLException {
		stat.executeUpdate("DELETE FROM " + table_name + " WHERE " + column + " < '" + date + "'");
		stat.executeUpdate("ALTER TABLE "+ table_name + " AUTO_INCREMENT = 1");
	}

	/**
	 * 取得最後時間.
	 *
	 * @param table_name 表格名稱
	 * @param column     時間欄位
	 * @return 最後時間
	 * @throws SQLException SQL 執行錯誤
	 */
	public long getLastTime(String table_name, String column) throws SQLException {
		long millis = -1;
		String sql = "SELECT MAX(" + column + ") FROM " + table_name;
		ResultSet rs = stat.executeQuery(sql);
		if (rs.next())
			millis = rs.getTimestamp(1).getTime();
		rs.close();
		return millis;
	}

	/**
	 * 取得字串格式最後時間.
	 *
	 * @param table_name 表格名稱
	 * @param column     時間欄位
	 * @return 字串格式最後時間
	 * @throws SQLException SQL 執行錯誤
	 */
	public String getLastTimeToString(String table_name, String column) throws SQLException {
		String time = null;
		String sql = "SELECT MAX(" + column + ") FROM " + table_name;
		ResultSet rs = stat.executeQuery(sql);
		if (rs.next())
			time = rs.getTimestamp(1).toString();
		rs.close();
		return time;
	}

	/**
	 * 將一個單引號取代成兩個單引號.
	 *
	 * @param sql SQL語法
	 * @return 取代過後的 SQL 語法
	 */
	public static String escapeSql(String sql) {
		return sql.replaceAll("'", "''");
	}

	/**
	 * 將 Set 中的資料轉換成以小括號包住、以逗號分隔的字串.
	 *
	 * @param s 欲轉換的 Set
	 * @return 以小括號包住、以逗號分隔的字串
	 */
	public static String toSqlInClause(Set<String> s) {
		if (s == null || s.isEmpty())
			return null;
		return toSqlInClause(s.toArray(new String[s.size()]));
	}

	/**
	 * 將 String array 中的資料轉換成以小括號包住、以逗號分隔的字串.
	 *
	 * @param s 欲轉換的 String array
	 * @return 以小括號包住、以逗號分隔的字串
	 */
	public static String toSqlInClause(String[] s) {
		if (s == null)
			return null;

		StringBuilder sb = new StringBuilder("(");

		for (int i = 0; i < s.length; i++)
			sb.append("'" + s[i] + "',");
		sb.replace(sb.length()-1, sb.length(), ")");

		return sb.toString();
	}

	/**
	 * 判斷是否為攻擊指令.
	 *
	 * @param sql SQL語法
	 * @return 是否為攻擊指令
	 */
	public static boolean isInjection(String sql) {
		if (sql.matches(".*[;].*"))
			return true;
		if (sql.matches(".*[Dd][Rr][Oo][Pp].*"))
			return true;
		if (sql.matches(".*[Tt][Rr][Uu][Nn][Cc][Aa][Tt][Ee].*"))
			return true;
		if (sql.matches(".*[Dd][Ee][Ll][Ee][Tt][Ee].*"))
			return true;
		return false;
	}
}
