package org.daoke.mlcp.callback;

import org.daoke.mlcp.interfaces.MLCPInterface;

public class MLCPCallBack {
	public static final int MLCP_SOCKET_EVENT_NOTIFY_IND = 1;

	private static int eventType = 0;
	private MLCPInterface mlcpInterface = null;

	public static int getEventType() {
		return eventType;
	}

	private static void setEventType(int eventType) {
		MLCPCallBack.eventType = eventType;
	}

	public void setProtocolEventHandler(MLCPInterface callBack, int eventType) {

		setEventType(eventType);
		if (null == mlcpInterface) {
			mlcpInterface = callBack;
		}

	}

	public void doProtocolEventHandler(int eventType, String mlcpName,
			int socketStatus, byte[] socketData) {
		if (null != mlcpInterface) {
			switch (eventType) {
			case MLCP_SOCKET_EVENT_NOTIFY_IND:
				mlcpInterface.socketNotify(mlcpName, socketStatus, socketData);
				break;

			}
		}
	}
}
