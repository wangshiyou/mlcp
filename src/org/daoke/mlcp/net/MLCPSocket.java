package org.daoke.mlcp.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import org.daoke.mlcp.util.MLCPLogUtil;
import org.daoke.mlcp.util.MLCPSysUtil;

public class MLCPSocket {

	private static final int SOCKET_READ_MAX_INPUT_BUFFER = 1024 * 4;// 4K
	private static final int SOCKET_BUFLIST_MAX_LEN = 1000;// 4K*1000= 4000K
	// private static final int SOCKET_CONNECT_DELAY = 10 * 1000;

	private static final int SOCKET_WRITE_MAX_OUTPUT_BUFFER = 2048;
	private static final int MLCP_SOCKET_RECEIVE_DELAY = 5;
	private String mlcpSocketUrl = null;
	private int mlcpSocketPort = 0;
	private boolean mlcpSocketRuning = false;

	private Thread mlcpSocketThread = null;
	private Runnable mlcpSocketRunnable = null;

	protected Socket mlcpSocket = null;

	private Vector<byte[]> mlcpSocketBufList = null;

	protected MLCPSocket() {
		// TODO Auto-generated constructor stub

	}

	protected MLCPSocket(String url, int port) {
		// TODO Auto-generated constructor stub
		createSocket(url, port);
	}

	protected String getFptpSocketUrl() {
		return mlcpSocketUrl;
	}

	private void setFptpSocketUrl(String mlcpSocketUrl) {
		this.mlcpSocketUrl = mlcpSocketUrl;
	}

	protected int getFptpSocketPort() {
		return mlcpSocketPort;
	}

	private void setFptpSocketPort(int mlcpSocketPort) {
		this.mlcpSocketPort = mlcpSocketPort;
	}

	protected boolean isMLCPSocketRuning() {

		return mlcpSocketRuning;
	}

	private void setMLCPSocketRuning(boolean mlcpSocketRuning) {
		this.mlcpSocketRuning = mlcpSocketRuning;
	}

	private synchronized void setupSocketBufList(byte[] socketBuf) {
		// MLCPLogUtil.i("setupSocketBufList socketBuf.length = " +
		// socketBuf.length);
		if ((null != socketBuf) && (0 < socketBuf.length)) {
			if (SOCKET_BUFLIST_MAX_LEN < getSocketBufCount()) {

				mlcpSocketBufList.remove(0);
			}
			mlcpSocketBufList.add(socketBuf);

		}
	}

	protected synchronized void clearSocketBufList() {
		// mlcpSocketBufList.removeAllElements();
		if (null != mlcpSocketBufList) {
			mlcpSocketBufList.clear();
		}
	}

	protected synchronized byte[] readSocketBuf() {

		byte[] socketBuf = null;

		if (0 < getSocketBufCount()) {

			socketBuf = (byte[]) mlcpSocketBufList.get(0);

			mlcpSocketBufList.remove(0);
		}

		return socketBuf;
	}

	protected synchronized int getSocketBufCount() {
		int socketBufCount = 0;
		// MLCPLogUtil.i("getSocketBufCount mlcpSocketBufList = " + " size = " +
		// mlcpSocketBufList.size());
		if (null != mlcpSocketBufList) {
			socketBufCount = mlcpSocketBufList.size();
		}
		return socketBufCount;
	}

	private void createThread() {
		if (null == mlcpSocketThread) {
			mlcpSocketRunnable = new Runnable() {
				public void run() {
					InputStream inputStream = null;

					while (isMLCPSocketRuning()) {

						try {

							if (null != mlcpSocket) {
								inputStream = mlcpSocket.getInputStream();
								setupSocketBufList(MLCPSysUtil.input2Byte(
										inputStream,
										SOCKET_READ_MAX_INPUT_BUFFER));

							}
							Thread.sleep(MLCP_SOCKET_RECEIVE_DELAY);
						} catch (Exception e) {
							e.printStackTrace();
							setMLCPSocketRuning(false);
							MLCPLogUtil
									.i("2015.01.26 wsy mlcpSocketRunnable read err");
						}

					}
				}
			};

			setMLCPSocketRuning(true);
			mlcpSocketThread = new Thread(mlcpSocketRunnable,
					"mlcpSocketRunnable");
			mlcpSocketThread.setDaemon(true);
			mlcpSocketThread.start();

		}
	}

	private void destroyThread() {

		if (null != mlcpSocketThread) {
			setMLCPSocketRuning(false);
			// try {
			// mlcpSocketThread.join();
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			mlcpSocketThread.interrupt();
			mlcpSocketThread = null;
			mlcpSocketRunnable = null;
		}
	}

	private void initSocketBufList() {
		mlcpSocketBufList = new Vector<byte[]>();
		// mlcpSocketBufList.setSize(SOCKET_BUFLIST_MAX_LEN);
	}

	protected synchronized boolean writeSocket(byte[] sendBuffer) {
		boolean writeSuccess = false;
		int sendLength = 0;
		int sendCount = 0;
		OutputStream outputStream = null;

		try {
			if (null != mlcpSocket) {
				outputStream = mlcpSocket.getOutputStream();

				while (sendLength != sendBuffer.length) {
					sendCount = ((SOCKET_WRITE_MAX_OUTPUT_BUFFER) < (sendBuffer.length - sendLength)) ? SOCKET_WRITE_MAX_OUTPUT_BUFFER
							: (sendBuffer.length - sendLength);

					outputStream.write(sendBuffer, sendLength, sendCount);

					sendLength += sendCount;

				}

				outputStream.flush();
				writeSuccess = true;
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
			// sendto failed: EPIPE (Broken pipe)
			setMLCPSocketRuning(false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMLCPSocketRuning(false);
			MLCPLogUtil.i("2015.01.26 wsy sendSocket err");
		}

		return writeSuccess;
	}

	protected void closeSocket() {
		try {

			destroyThread();

			if (null != mlcpSocket) {
				mlcpSocket.close();
				mlcpSocket = null;
			}
			clearSocketBufList();
			mlcpSocketBufList = null;
			// mlcpSocketThread.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MLCPLogUtil.i("2015.01.26 wsy closeSocket err");
		}

	}

	protected boolean createSocket(String url, final int port) {
		boolean createSuccess = false;
		try {
			//
			if (null == mlcpSocket) {
				mlcpSocket = new Socket(url, port);
			}
			// mlcpSocket.setSoTimeout(timeout);
			if (mlcpSocket != null) {
				setFptpSocketUrl(url);
				setFptpSocketPort(port);
				initSocketBufList();

				// //Thread.sleep(SOCKET_CONNECT_DELAY);
				createThread();

				createSuccess = true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			MLCPLogUtil.i("2015.01.26 wsy createSocket err");
		}

		return createSuccess;
	}

}
