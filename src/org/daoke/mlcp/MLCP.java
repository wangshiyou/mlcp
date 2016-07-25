package org.daoke.mlcp;

import org.daoke.mlcp.interfaces.MLCPInterface;
import org.daoke.mlcp.net.MLCPSocketControl;
import org.daoke.mlcp.util.MLCPNetUtil;
import org.daoke.mlcp.util.MLCPVerUtil;

import android.content.Context;

public class MLCP extends MLCPSocketControl {
	public static final int MLCP_SOCKET_NOT_CONNECT = MLCPSocketControl.MLCP_SOCKET_NOT_CONNECT;
	public static final int MLCP_SOCKET_DISCONNECT = MLCPSocketControl.MLCP_SOCKET_DISCONNECT;
	public static final int MLCP_SOCKET_CONNECTING = MLCPSocketControl.MLCP_SOCKET_CONNECTING;
	public static final int MLCP_SOCKET_CONNECT_FAILED = MLCPSocketControl.MLCP_SOCKET_CONNECT_FAILED;
	public static final int MLCP_SOCKET_CLOSE = MLCPSocketControl.MLCP_SOCKET_CLOSE;
	public static final int MLCP_SOCKET_WRITE_ERR = MLCPSocketControl.MLCP_SOCKET_WRITE_ERR;
	public static final int MLCP_SOCKET_READ_ERR = MLCPSocketControl.MLCP_SOCKET_READ_ERR;
	public static final int MLCP_SOCKET_HEART_ERR = MLCPSocketControl.MLCP_SOCKET_HEART_ERR;
	public static final int MLCP_SOCKET_CLIENT_CLOSE_NET_ERR = MLCPSocketControl.MLCP_SOCKET_CLIENT_CLOSE_NET_ERR;
	

	public static final int MLCP_SOCKET_CONNECTED = MLCPSocketControl.MLCP_SOCKET_CONNECTED;
	public static final int MLCP_SOCKET_WRITE = MLCPSocketControl.MLCP_SOCKET_WRITE;
	public static final int MLCP_SOCKET_READ = MLCPSocketControl.MLCP_SOCKET_READ;

	public static final int MLCP_SOCKET_HEART = MLCPSocketControl.MLCP_SOCKET_HEART;

	/**
	 * new user MLCP
	 * 
	 * @param mlcpName
	 *            the user define name
	 * @param openLog
	 *            MLCP log switch
	 */
	public MLCP(Context mlcpContext, String mlcpName, boolean openLog) {
		super.initMLCP(mlcpContext, mlcpName, openLog);
	}

	/**
	 * get MLCP current version
	 */
	public int getMLCPVerNo() {
		return MLCPVerUtil.getSdkVersion();
	}

	/**
	 * clean MLCP receive data if current data is not legal , then clean the
	 * receive data
	 */
	public void cleanReceiveData() {
		super.cleanReceiveData();
	}

	// 2015.01.28 wsy add end for data IO

	// 2015.01.28 wsy add start for net IO
	/**
	 * get MLCP current socket status : 0 --> 9
	 */
	public int getMLCPSocketStatus() {
		return super.getMLCPSocketStatus();
	}

	/**
	 * MLCP default send port if sendMessage is packed , then call this function
	 * to send no compression : compression = 0 no encryption : encryption = 0
	 * push action : socketType = 8
	 */
	public boolean send(byte[] sendData) {
		boolean sendSuccess = false;
		sendSuccess = super.send(sendData);
		return sendSuccess;
	}

	/**
	 * close MLCP current socket
	 */
	public boolean close() {
		return super.close();
	}

	/**
	 * connect MLCP
	 * 
	 * @param url
	 *            the url of server.
	 * 
	 * @param port
	 *            the port of server port.
	 */
	public void connect(String url, int port, int heartGap, int reConnectGap) {
		super.connect(url, port, heartGap, reConnectGap);
	}

	/**
	 * set MLCP protocol callBack
	 * 
	 * @param callBack
	 *            protocol user call back function.
	 * 
	 * @param eventType
	 *            protocol type. MLCP_SOCKET_EVENT_NOTIFY_IND : 1
	 */
	public void setProtocolEventHandler(MLCPInterface callBack) {
		super.setProtocolEventHandler(callBack);
	}
	// 2015.01.28 wsy add end for net IO
}
