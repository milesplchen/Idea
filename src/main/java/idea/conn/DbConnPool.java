/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.conn;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * 處理 Connection Pool.
 *
 * @author Miles Chen
 */
public class DbConnPool {
	/** Connection Pool. */
	protected DataSource datasource = new DataSource();

	/** 資料庫 driver. */
	protected String driver;
	/** 資料庫連結. */
	protected String url;
	/** 帳號. */
	protected String usr;
	/** 密碼. */
	protected String pw;

	protected boolean jmxEnabled = true;
	protected boolean testWhileIdle = false;
	protected boolean testOnBorrow = true;					// false
	protected String validationQuery = "SELECT 1";
	protected boolean testOnReturn = false;
	protected long validationInterval = 30000;				// 3000
	protected int timeBetweenEvictionRunsMillis = 30000;	// 5000
	protected int maxActive = 100;
	protected int initialSize = 10;
	protected int maxWait = 30000;
	protected int removeAbandonedTimeout = 60;
	protected int minEvictableIdleTimeMillis = 60000;
	protected int minIdle = 10;
	protected boolean logAbandoned = false;
	protected boolean removeAbandoned = false;
	protected String jdbcInterceptors = "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer";

	protected boolean fairQueue = true;

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

	/** 設定參數. */
	public void setPoolProperties() {
		PoolProperties p = new PoolProperties();

		p.setDriverClassName(driver);
		p.setUrl(url);
		p.setUsername(usr);
		p.setPassword(pw);

		p.setJmxEnabled(jmxEnabled);
		p.setTestWhileIdle(testWhileIdle);
		p.setTestOnBorrow(testOnBorrow);
		p.setValidationQuery(validationQuery);
		p.setTestOnReturn(testOnReturn);
		p.setValidationInterval(validationInterval);
		p.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		p.setMaxActive(maxActive);
		p.setInitialSize(initialSize);
		p.setMaxWait(maxWait);
		p.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		p.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		p.setMinIdle(minIdle);
		p.setLogAbandoned(logAbandoned);
		p.setRemoveAbandoned(removeAbandoned);
		p.setJdbcInterceptors(jdbcInterceptors);

		datasource.setPoolProperties(p);
	}

	/**
	 * 從 Connection Pool 取得 Connection.
	 *
	 * @return Connection
	 * @throws SQLException 連線錯誤
	 */
	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	/** 關閉連線. */
	public void close() {
		datasource.close();
	}
}
