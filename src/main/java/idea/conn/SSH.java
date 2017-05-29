/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.conn;

import java.io.*;
import java.util.Properties;

import com.jcraft.jsch.*;

/**
 * 處理 SSH 的各種動作.
 *
 * @author Miles Chen
 */
public class SSH {
	protected JSch jsch = null;
	protected Session session = null;

	/** SSH id. */
	protected String user;
	/** SSH server. */
	protected String host;
	/** SSH port. */
	protected int port;
	/** SSH password. */
	protected String pw;

	/** localhost port. */
	protected int lport;
	/** remote host (DB). */
	protected String rhost;
	/** remote port (DB). */
	protected int rport;

	/**
	 * 從 properties 檔載入連線資訊.
	 * 帳號變數名稱: user.
	 * Host 變數名稱: host.
	 * Port 變數名稱: port.
	 * 密碼變數名稱: pw.
	 * Localhost port 變數名稱: lport.
	 * Remote host 變數名稱: rhost.
	 * Remote port 變數名稱: rport.
	 *
	 * @param filename properties 檔名稱
	 * @throws IOException properties 檔讀取失敗
	 */
	public void loadProperties(String filename) throws IOException {
		Properties props = new Properties();
		props.load(new FileInputStream(filename));

		user = props.getProperty("user");
		host = props.getProperty("host");
		port = Integer.valueOf(props.getProperty("port"));
		pw = props.getProperty("pw");

		lport = Integer.valueOf(props.getProperty("lport"));
		rhost = props.getProperty("rhost");
		rport = Integer.valueOf(props.getProperty("rport"));
	}

	/**
	 * 設定 session 連線資訊.
	 *
	 * @param user SSH id
	 * @param host SSH server
	 * @param port SSH port
	 * @param pw   SSH password
	 */
	public void setSession(String user, String host, int port, String pw) {
		this.user = user;
		this.host = host;
		this.port = port;
		this.pw = pw;
	}

	/**
	 * 設定 tunnel ports.
	 *
	 * @param lport localhost port
	 * @param rhost remote host
	 * @param rport remote port
	 */
	public void setPorts(int lport, String rhost, int rport) {
		this.lport = lport;
		this.rhost = rhost;
		this.rport = rport;
	}

	/**
	 * 開啟 tunnel.
	 *
	 * @return assinged_port
	 * @throws JSchException 連線錯誤
	 */
	public int tunnel() throws JSchException {
		jsch = new JSch();

		session = jsch.getSession(user, host, port);
		session.setPassword(pw);
		session.setConfig("StrictHostKeyChecking", "no");	// 設置第一次登入的時候提示，可選值：(ask | yes | no)
		session.connect();

		int assinged_port = session.setPortForwardingL(lport, rhost, rport);
//		System.out.println("localhost:" + assinged_port + " -> " + rhost + ":" + rport);

		return assinged_port;
	}

	/** 關閉連線. */
	public void close() {
		if (session != null)
			session.disconnect();
	}
}
