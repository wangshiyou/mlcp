package org.daoke.mlcp.net;

import org.daoke.mlcp.callback.MLCPCallBack;
import org.daoke.mlcp.interfaces.MLCPInterface;
import org.daoke.mlcp.util.MLCPLogUtil;
import org.daoke.mlcp.util.MLCPNetUtil;

import android.content.Context;

public class MLCPSocketControl extends MLCPSocket {
	protected static final int MLCP_SOCKET_NOT_CONNECT = 0;
	protected static final int MLCP_SOCKET_DISCONNECT = 1;
	protected static final int MLCP_SOCKET_CONNECTING = 2;
	protected static final int MLCP_SOCKET_CONNECT_FAILED = 3;
	protected static final int MLCP_SOCKET_CLOSE = 4;
	protected static final int MLCP_SOCKET_WRITE_ERR = 5;
	protected static final int MLCP_SOCKET_READ_ERR = 6;
	protected static final int MLCP_SOCKET_HEART_ERR = 7;
	protected static final int MLCP_SOCKET_CLIENT_CLOSE_NET_ERR = 8;

	protected static final int MLCP_SOCKET_CONNECTED = 9;
	protected static final int MLCP_SOCKET_WRITE = 10;
	protected static final int MLCP_SOCKET_READ = 11;

	// 心跳回调
	protected static final int MLCP_SOCKET_HEART = 12;

	private static final int MLCP_TIME_ONE_SECOND = 1000;

	private static final int MLCP_NET_CONNECT_DELAY = 10 * MLCP_TIME_ONE_SECOND;

	private static final int MLCP_NET_KEEP_ALIVE_DELAY = 30 * MLCP_TIME_ONE_SECOND;

	private static final int MLCP_NET_RECONNECT_DELAY = 60 * MLCP_TIME_ONE_SECOND;

	private static final int MLCP_NET_SEND_DELAY = 100;
	private static final int MLCP_NET_CONNECTING_DELAY = 10;
	private static final int MLCP_NET_RECEIVE_DELAY = 10;

	private int mlcpHeartGap = MLCP_NET_KEEP_ALIVE_DELAY;
	private int mlcpReConnectGap = MLCP_NET_RECONNECT_DELAY;

	private int mlcpSocketStatus = 0;

	private String mlcpSocketName = null;

	private String mlcpUrl = null;
	private int mlcpPort = 0;

	private Context mlcpContext = null;
	// private MLCPSocket mlcpSocket = null;

	private Thread mlcpReceiveThread = null;
	private Runnable mlcpReceiveRunnable = null;

	private Thread mlcpKeepAliveThread = null;
	private Runnable mlcpKeepAliveRunnable = null;

	private static MLCPCallBack mlcpCallBack = new MLCPCallBack();

	private Context getMLCPContext() {
		return mlcpContext;
	}

	private void setMLCPContext(Context mlcpContext) {
		this.mlcpContext = mlcpContext;
	}

	private int getMLCPHeartGap() {
		return mlcpHeartGap;
	}

	private void setMLCPHeartGap(int mlcpHeartGap) {
		if (0 < mlcpHeartGap) {
			this.mlcpHeartGap = mlcpHeartGap * MLCP_TIME_ONE_SECOND;
		}
	}

	private int getMLCPReConnectGap() {
		return mlcpReConnectGap;
	}

	private void setMLCPReConnectGap(int mlcpReConnectGap) {
		if (0 < mlcpReConnectGap) {
			this.mlcpReConnectGap = mlcpReConnectGap * MLCP_TIME_ONE_SECOND;
		}
	}

	private synchronized void setMLCPSocketStatus(int socketStatus) {
		MLCPLogUtil.i("2016.06.16 wsy setMLCPSocketStatus socketStatus = "+ socketStatus);
		if(false ==isMLCPClosed()){
			mlcpSocketStatus = socketStatus;
		}
	}

	private boolean isMLCPClosed(){
		boolean closed = false;
		if(MLCP_SOCKET_CLOSE == mlcpSocketStatus){
			closed = true;
		}
		return closed;
	}
	
	
	protected int getMLCPSocketStatus() {
		return mlcpSocketStatus;
	}

	private String getMLCPSocketName() {
		return mlcpSocketName;
	}

	private void setMLCPSocketName(String mlcpSocketName) {
		this.mlcpSocketName = mlcpSocketName;
	}

	private synchronized void setMLCPSocketStatusAnddoCallBack(
			int socketStatus, byte[] socketData) {

		setMLCPSocketStatus(socketStatus);

		mlcpCallBack.doProtocolEventHandler(
				mlcpCallBack.MLCP_SOCKET_EVENT_NOTIFY_IND, getMLCPSocketName(),
				socketStatus, socketData);

	}

	private String getMLCPUrl() {
		return mlcpUrl;
	}

	private void setMLCPUrl(String url) {
		mlcpUrl = url;
	}

	private int getMLCPPort() {
		return mlcpPort;
	}

	private void setMLCPPort(int port) {
		mlcpPort = port;
	}

	// for receive start
	private byte[] readReceiveData() {
		byte[] mlcpData = null;
		mlcpData = readSocketBuf();
		return mlcpData;
	}

	protected void cleanReceiveData() {
		clearSocketBufList();
	}

	private synchronized boolean writeSocket(byte[] sendBuffer,
			boolean doProtocolCallBack) {
		boolean sendSuccess = false;

		try {
			if (MLCP_SOCKET_CONNECTED <= getMLCPSocketStatus()) {
				if ((null != mlcpSocket) && (null != sendBuffer)) {

					if (true == doProtocolCallBack) {
						setMLCPSocketStatusAnddoCallBack(MLCP_SOCKET_WRITE,
								sendBuffer);
					}

					if (writeSocket(sendBuffer)) {
						MLCPLogUtil.i("V: writeSocket success");
						sendSuccess = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			MLCPLogUtil.i("write socket & throw exception: " + e);
			reconnect(MLCP_SOCKET_WRITE_ERR);
		}

		return sendSuccess;
	}

	private void createThread() {
		if (null == mlcpReceiveThread) {
			/**
			 * receive thread
			 */
			mlcpReceiveRunnable = new Runnable() {
				byte[] mlcpData = null;

				public void run() {

					MLCPLogUtil
							.i("V: recv thread start & read getMLCPNetStatus() = "
									+ getMLCPSocketStatus());
					while (MLCP_SOCKET_CONNECTED <= getMLCPSocketStatus()) {
						try {

							if ((null != mlcpSocket) && (isMLCPSocketRuning())) {
								mlcpData = null;

								mlcpData = readReceiveData();
								if (null != mlcpData) {
									setMLCPSocketStatusAnddoCallBack(
											MLCP_SOCKET_READ, mlcpData);
								}
							} else {
								reconnect(MLCP_SOCKET_READ_ERR);
							}
							Thread.sleep(MLCP_NET_RECEIVE_DELAY);
						} catch (Exception e) {
							e.printStackTrace();
							MLCPLogUtil
									.i("reconnect 4 & recv thread & throw exception: "
											+ e);

							reconnect(MLCP_SOCKET_READ_ERR);

						}
					}

				}
			};

			mlcpReceiveThread = new Thread(mlcpReceiveRunnable,
					"mlcpReceiveRunnable");
			mlcpReceiveThread.setDaemon(true);
			mlcpReceiveThread.start();

		}

		if (null == mlcpKeepAliveThread) {
			mlcpKeepAliveRunnable = new Runnable() {

				public void run() {
					while (MLCP_SOCKET_CONNECTED <= getMLCPSocketStatus()) {
						MLCPLogUtil.i("2016.06.16 wsy mlcpKeepAliveThread getMLCPSocketStatus()= " + getMLCPSocketStatus());
						try {

							if ((null != mlcpSocket) && (isMLCPSocketRuning())) {
								Thread.sleep(getMLCPHeartGap());
								MLCPLogUtil.i("V: " + getMLCPSocketName()
										+ " & heart count begin");

								setMLCPSocketStatusAnddoCallBack(
										MLCP_SOCKET_HEART, null);
								
							} else {
								reconnect(MLCP_SOCKET_HEART_ERR);
							}

						} catch (Exception e) {
							e.printStackTrace();
							MLCPLogUtil.i("heart & throw exeption: " + e);

							reconnect(MLCP_SOCKET_HEART_ERR);
						}
					}
				}
			};
			mlcpKeepAliveThread = new Thread(mlcpKeepAliveRunnable,
					"mlcpKeepAliveThread");
			mlcpKeepAliveThread.setDaemon(true);
			mlcpKeepAliveThread.start();
		}
	}

	private void destroyThread() {
		if (null != mlcpReceiveThread) {
			mlcpReceiveThread.interrupt();
			mlcpReceiveThread = null;
			mlcpReceiveRunnable = null;
		}
		if (null != mlcpKeepAliveThread) {
			mlcpKeepAliveThread.interrupt();
			mlcpKeepAliveThread = null;
			mlcpKeepAliveRunnable = null;
		}
	}

	private synchronized void disconnect() {
		setMLCPSocketStatusAnddoCallBack(MLCP_SOCKET_DISCONNECT, null);
		closeSocket();
		// close();
	}

	private void connect() {

		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				if (false == isMLCPClosed()) {
					setMLCPSocketStatusAnddoCallBack(MLCP_SOCKET_CONNECTING,
							null);
					while (MLCP_SOCKET_CONNECTING == getMLCPSocketStatus()) {

						if (createSocket(getMLCPUrl(), getMLCPPort())) {
							setMLCPSocketStatusAnddoCallBack(
									MLCP_SOCKET_CONNECTED, null);

							createThread();
						} else {

							setMLCPSocketStatusAnddoCallBack(
									MLCP_SOCKET_CONNECT_FAILED, null);

							MLCPLogUtil
									.i("2015.01.26 wsy connect failed ,it can reconnect later");
							try {
								Thread.sleep(getMLCPReConnectGap());
								setMLCPSocketStatusAnddoCallBack(
										MLCP_SOCKET_CONNECTING, null);
								MLCPLogUtil.i("2015.01.26 wsy start reconnect");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();

								MLCPLogUtil.i("connect & throw exeption: " + e);
							}
						}
					}
				}
			}

		}.start();
	}

	private synchronized void reconnect(int socketType) {
		int mfptoSocketStatus = getMLCPSocketStatus();
		MLCPLogUtil.i("locate: reconnect socketType = " + socketType);
		if (false ==isMLCPClosed()) {
			if (MLCP_SOCKET_CONNECTED <= mfptoSocketStatus) {

				setMLCPSocketStatusAnddoCallBack(socketType, null);

				disconnect();

				connect();

			}
		}
	}

	private void sendThread(final byte[] sendData) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				writeSocket(sendData, true);
			}

		}.start();
	}

	private boolean sendErr(int socketType) {
		MLCPLogUtil.i("2016.05.16 wsy sendErr socketType = " + socketType);
		reconnect(socketType);
		return false;
	}

	protected boolean send(final byte[] sendData) {
		boolean sendSuccess = true;
		if (true == MLCPNetUtil.isNetworkAvailable(getMLCPContext())) {
			if (MLCP_SOCKET_CONNECTED <= getMLCPSocketStatus()) {
				sendThread(sendData);
			} else {
				sendSuccess = sendErr(MLCP_SOCKET_WRITE_ERR);
			}
		} else {
			sendSuccess = sendErr(MLCP_SOCKET_CLIENT_CLOSE_NET_ERR);
		}
		return sendSuccess;
	}

	protected synchronized boolean close() {
		boolean closeSuccess = false;
		try {
			cleanReceiveData();
			setMLCPSocketStatusAnddoCallBack(MLCP_SOCKET_CLOSE, null);
			closeSocket();
			destroyThread();

			closeSuccess = true;

			MLCPLogUtil.i("V-locate: close & resetCount");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MLCPLogUtil.i("close & throw exeption: " + e);
		}

		return closeSuccess;
	}

	protected void connect(String url, int port, int heartGap, int reConnectGap) {
		MLCPLogUtil.i("2016.05.16 wsy connect start ");
		setMLCPUrl(url);
		setMLCPPort(port);
		setMLCPHeartGap(heartGap);
		setMLCPReConnectGap(reConnectGap);

		connect();

	}

	protected void initMLCP(Context mlcpContext, String mlcpName,
			boolean openLog) {
		MLCPLogUtil.i("2016.05.16 wsy initMLCP start ");
		setMLCPContext(mlcpContext);
		setMLCPSocketName(mlcpName);
		MLCPLogUtil.setLogFlag(openLog);

	}

	protected void setProtocolEventHandler(MLCPInterface callBack) {
		mlcpCallBack.setProtocolEventHandler(callBack,
				MLCPCallBack.MLCP_SOCKET_EVENT_NOTIFY_IND);
	}
}
