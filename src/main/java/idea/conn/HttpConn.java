/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.conn;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.security.cert.X509Certificate;
import java.security.GeneralSecurityException;

import javax.net.ssl.*;

/**
 * 處理 HTTP 連線的各種動作.
 *
 * @author Miles Chen
 */
public class HttpConn {
	protected HttpURLConnection con = null;
	/** HTTP response code. */
	protected int code = -1;
	/** Connect timeout in milliseconds. */
	protected int connect_timeout = 0;
	/** Read timeout in milliseconds. */
	protected int read_timeout = 0;
	/** 讀入資料編碼. */
	protected String read_encoding = "UTF-8";

	/** A String constant representing "GET" type.*/
	public final static String GET = "GET";
	/** A String constant representing "POST" type.*/
	public final static String POST = "POST";
	/** A String constant representing "PUT" type.*/
	public final static String PUT = "PUT";
	/** A String constant representing "DELETE" type.*/
	public final static String DELETE = "DELETE";

	/** A String constant representing "Content-Type" type.*/
	public final static String CONTENT_TYPE = "Content-Type";
	/** A String constant representing "Accept" type.*/
	public final static String ACCEPT = "Accept";
	/** A String constant representing "Authorization" type.*/
	public final static String AUTHORIZATION = "Authorization";

	/** A String constant representing "Bearer" type.*/
	public final static String BEARER = "Bearer";

	/** A String constant representing "charset=UTF-8" type.*/
	public final static String UTF8 = "charset=UTF-8";

	/** A String constant representing "application/raw; charset=UTF-8" media type. */
	public final static String APPLICATION_RAW_UTF8 = "application/raw; " + UTF8;
	/** A String constant representing "application/json" media type. */
	public final static String APPLICATION_JSON = "application/json";
	/** A String constant representing "application/json; charset=UTF-8" media type. */
	public final static String APPLICATION_JSON_UTF8 = "application/json; " + UTF8;
	/** A String constant representing "application/x-www-form-urlencoded" media type. */
	public final static String APPLICATION_FORM_URLENCODED_TYPE = "application/x-www-form-urlencoded";
	/** A String constant representing "multipart/form-data" media type. */
	public final static String MULTIPART_FORM_DATA = "multipart/form-data";
	/** A String constant representing "text/xml" media type. */
	public final static String TEXT_XML = "text/xml";
	/** A String constant representing "text/plain" media type. */
	public final static String TEXT_PLAIN = "text/plain";

	/** 忽略驗證 Hostname. */
	protected static void ignoreVerifyHostname() {
		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String urlHostName, SSLSession session) {
//				System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
				return true;
			}
		};
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}

	/**
	 * 忽略 HTTPS 請求的 SSL 驗證，必須在 openConnection 前調用.
	 *
	 * @throws GeneralSecurityException 安全性例外
	 */
	protected static void ignoreVerifyTrustManager() throws GeneralSecurityException {
		TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers()
				{	return null;	}

				public void checkServerTrusted(X509Certificate[] certs, String authType)
				{}

				public void checkClientTrusted(X509Certificate[] certs, String authType)
				{}
			}
		};
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	/**
	 * 忽略驗證 HTTPS，必須在 openConnection 前調用.
	 *
	 * @throws GeneralSecurityException 安全性例外
	 */
	public static void ignoreVerifyCert() throws GeneralSecurityException {
		ignoreVerifyHostname();
		ignoreVerifyTrustManager();
	}

	/**
	 * 開啟網址連結.
	 *
	 * @param url 網址
	 * @throws MalformedURLException URL 格式錯誤
	 * @throws IOException 開啟連結錯誤
	 */
	public void openConnection(String url) throws MalformedURLException, IOException {
		if (url.startsWith("https"))
			con = (HttpsURLConnection) new URL(url).openConnection();
		else
			con = (HttpURLConnection) new URL(url).openConnection();
	}

	/**
	 * 設定 request headers.
	 *
	 * @param method       使用的 request 方法，包含 GET, POST, PUT, DELETE
	 * @param content_type 送出的 request 資訊格式，包含 form, json, xml 等
	 * @param accept       回應的資訊格式，包含 json, xml 等
	 * @throws ProtocolException 設定 header 錯誤
	 */
	public void setRequest(String method, String content_type, String accept) throws ProtocolException {
		con.setDoOutput(true);
		con.setRequestMethod(method);
		if (content_type != null)
			con.setRequestProperty(CONTENT_TYPE, content_type);
		if (accept != null)
			con.setRequestProperty(ACCEPT, accept);
	}

	/**
	 * 設定 header 欄位.
	 *
	 * @param field 欄位
	 * @param value 值
	 */
	public void setHeader(String field, String value) {
		con.setRequestProperty(field, value);
	}

	/**
	 * 設定 ConnectTimeout 與 ReadTimeout.
	 *
	 * @param conn_timeout connect timeout
	 * @param read_timeout read timeout
	 */
	public void setTimeout(int conn_timeout, int read_timeout) {
		this.connect_timeout = conn_timeout;
		this.read_timeout = read_timeout;
		con.setConnectTimeout(this.connect_timeout);
		con.setReadTimeout(this.read_timeout);
	}

	/**
	 * 設定讀入資料編碼.
	 *
	 * @param encode 讀入資料編碼
	 */
	public void setReadEncoding(String encode) {
		read_encoding = encode;
	}

	/**
	 * 送出 request 參數並取得回應.
	 *
	 * @param param 送出的參數
	 * @return 回應的資訊
	 * @throws IOException 請求錯誤
	 */
	public String request(String param) throws IOException {
		OutputStream out = con.getOutputStream();	// Send request
		out.write(param.getBytes());
		out.close();

		return request();
	}

	/**
	 * 送出 request 參數並取得回應.
	 *
	 * @return 回應的資訊
	 * @throws IOException 請求錯誤
	 */
	public String request() throws IOException {
		code = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), read_encoding));		// 讀取 request
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = in.readLine()) != null)
			sb.append(line);
		in.close();

		con.disconnect();

		return sb.toString();
	}

	/**
	 * 取得 HTTP response code.
	 *
	 * @return HTTP response code
	 */
	public int getResponseCode() {
		return code;
	}

	/** 關閉連結. */
	public void disconnect() {
		if (con != null)
			con.disconnect();
	}

	/**
	 * 將 (key, value) 的參數轉換成 Form data 格式.
	 *
	 * @param param 參數 (key, value)
	 * @return 轉換成 Form data 格式的參數
	 */
	public static String paramForm(HashMap<String, String> param) {
		StringBuilder sb = new StringBuilder();

		for (String key : param.keySet())
			sb.append(key + "=" + param.get(key) + "&");
		sb.replace(sb.length()-1, sb.length(), "");

		return sb.toString();
	}
}
