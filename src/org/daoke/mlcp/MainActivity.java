package org.daoke.mlcp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.daoke.mlcp.interfaces.MLCPInterface;
import org.daoke.mlcp.net.MLCPSocket;
import org.daoke.mlcp.util.MLCPLogUtil;
import org.daoke.mlcp.util.MLCPSysUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

	private MLCP MLCPSend = new MLCP(this, "send", true);
	private MLCP MLCPRecieve = new MLCP(this, "receive", true);

	private class MLCPCallback1 implements MLCPInterface {

		private void loginMirrTalkMLCP(MLCP MLCP) {
			byte[] sendBuffer = null;
			String sAppKey = "123456789012345abcdefghji";
			sendBuffer = sAppKey.getBytes();

			// MLCP.send();

		}

		private boolean checkMirrTalkMLCP(final byte[] MLCPData) {
			boolean checkSuccess = false;
			// switch (MLCPData.getMLCPSocketType()) {
			// case MLCP.MLCP_SOCKET_TYPE_PAIR:
			// if ((1 == MLCPData.getDataBodyCount())
			// && (1 == MLCPData.getFrameCount(0))) {
			// if (MLCP.MLCP_AUTHORIZE_SUCCESS == MLCPSysUtil
			// .bytesToInt(MLCPData.getFrameBody(0, 0), 0)) {
			// checkSuccess = true;
			// }
			// }
			// break;
			//
			// }

			return checkSuccess;
		}

		int loginIn = 0;

		@Override
		public void socketNotify(final String MLCPName, final int socketStatus,
				final byte[] MLCPData) {
			int i;

			String test = "wsy test ";
			byte[] sendBuffer = null;
			MLCPLogUtil.i("2015.01.26 wsy socketNotify socketStatus "
					+ socketStatus);
			if (null != MLCPData) {
				MLCPLogUtil.i("2015.01.26 wsy socketNotify length = "
						+ MLCPData.length + " MLCPData = " + (MLCPData));
			}
			switch (socketStatus) {

			case MLCP.MLCP_SOCKET_NOT_CONNECT:

				break;

			case MLCP.MLCP_SOCKET_DISCONNECT:

				break;
			case MLCP.MLCP_SOCKET_CONNECTING:

				break;
			case MLCP.MLCP_SOCKET_CONNECT_FAILED:

				break;
			case MLCP.MLCP_SOCKET_CLOSE:

				break;
			case MLCP.MLCP_SOCKET_WRITE_ERR:

				break;
			case MLCP.MLCP_SOCKET_READ_ERR:

				break;
			case MLCP.MLCP_SOCKET_HEART_ERR:

				break;
			case MLCP.MLCP_SOCKET_CLIENT_CLOSE_NET_ERR:

				break;

			// case MLCP.MLCP_SOCKET_LOGINING:
			//
			// break;
			case MLCP.MLCP_SOCKET_CONNECTED:

				if (MLCPName.equals("send")) {
					loginMirrTalkMLCP(MLCPSend);
				}
				if (MLCPName.equals("receive")) {
					loginMirrTalkMLCP(MLCPRecieve);
				}

				break;
			case MLCP.MLCP_SOCKET_WRITE:

				break;
			case MLCP.MLCP_SOCKET_READ:
				MLCPLogUtil.i("2015.01.27 wsy socketNotify MLCP_SOCKET_READ");
				// �����жϣ�����ֵΪ 0 ����ô�ֶ��ر� ���ӣ��� ֹͣMLCPЭ��
				if (checkMirrTalkMLCP(MLCPData)) {
					// TODO
					// if (1 == loginIn)
					{
						MLCPLogUtil
								.i("2015.01.27 wsy socketNotify REP SUCCESS");
						for (i = 0; i < 1; i++) {
						
							byte[] frame1 = new byte[2 * 1024 * 1024];
							byte[] frame2 = new byte[1 * 1024 * 1024];
							byte[] frame3 = new byte[1 * 1024 * 1024 - 1];
							
							MLCPLogUtil.i("send frame 33333333 frameLen="
									+ frame1.length);
							
							// MLCPRecieve.setSendFrame(false, sendBuffer);

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (MLCPName.equals("send")) {
								MLCPSend.send(frame1);
							}
							if (MLCPName.equals("receive")) {
								// MLCPRecieve.send((byte) 0, (byte) 0, (byte)
								// 7);
							}
							MLCPLogUtil
									.i("2015.01.27 wsy createMLCPSend test =  "
											+ test);
						}
						loginIn = 2;
					}
				} else {
					MLCPLogUtil.i("2015.01.27 wsy socketNotify REP FAIL");
					if (MLCPName.equals("send")) {
						// MLCPSend.close();
					}
					if (MLCPName.equals("receive")) {
						// MLCPRecieve.close();
					}
				}

				break;
			case MLCP.MLCP_SOCKET_HEART:
				// send heart need data
				if (MLCPName.equals("send")) {
					// MLCPSend.close();

					byte[] frame1 = new byte[20 * 1024];
					MLCPSend.send(frame1);
				}
				break;
			default:

				break;
			}
		}
	}

	private void createMLCPSend() {

		int i;
		String test = "wsy test ";
		byte[] sendBuffer = null;

		MLCPSend.connect("192.168.1.16", 8888, 0, 0);
		// MLCPRecieve.connect("192.168.1.15", 888,30000,60000);
		MLCPCallback1 callBack = new MLCPCallback1();
		MLCPSend.setProtocolEventHandler(callBack);

		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Thread.sleep(70000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			// getSupportFragmentManager().beginTransaction()
			// .add(R.id.container, new PlaceholderFragment()).commit();
		}

		// testMLCPDATA();
		//
		// createMLCPSocket();
		// {
		// try {
		// Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// writeMLCPSocket();
		//
		// readMLCPSocket();
		//
		//
		//
		// }
		createMLCPSend();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// // Handle action bar item clicks here. The action bar will
	// // automatically handle clicks on the Home/Up button, so long
	// // // as you specify a parent activity in AndroidManifest.xml.
	// // int id = item.getItemId();
	// // if (id == R.id.action_settings) {
	// // return true;
	// // }
	// // return super.onOptionsItemSelected(item);
	// }

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		// public PlaceholderFragment() {
		// }
		//
		// @Override
		// public View onCreateView(LayoutInflater inflater, ViewGroup
		// container,
		// Bundle savedInstanceState) {
		// View rootView = inflater.inflate(R.layout.fragment_main, container,
		// false);
		// return rootView;
		// }
	}

}
