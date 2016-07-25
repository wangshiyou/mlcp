package org.daoke.mlcp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.telephony.TelephonyManager;

public class MLCPSysUtil {
		
	public static String getLocalIpAddress() {

		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();

			en.hasMoreElements();) {

				NetworkInterface intf = en.nextElement();

				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses();

				enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();

					if (!inetAddress.isLoopbackAddress()) {

						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
		}

		return null;

	}

	public static int getSimStatus(Context context) {
		int simStatus = -1;

		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		simStatus = manager.getSimState();

		return simStatus;

	}

	
	
	/**
	 * byte数组中取int数值，本方法适用于(高位在前，低位在后)的顺序，和和intToBytes（）配套使用
	 * 
	 * @param src
	 *            byte数组
	 * @param offset
	 *            从数组的第offset位开始
	 * @return int数值
	 */
	public static int bytesToInt(byte[] src, int offset) {
		int value = 0;
		switch (src.length) {
		case 1:
			value = (int) (src[offset] & 0xFF);
			break;
		case 2:
			value = (int) ((src[offset + 1] & 0xFF) | ((src[offset] & 0xFF) << 8));
			break;
		case 3:
			value = (int) ((src[offset + 2] & 0xFF)
					| ((src[offset + 1] & 0xFF) << 8) | ((src[offset] & 0xFF) << 16));
			break;
		case 4:
			value = (int) ((src[offset + 3] & 0xFF)
					| ((src[offset + 2] & 0xFF) << 8)
					| ((src[offset + 1] & 0xFF) << 16) | ((src[offset] & 0xFF) << 24));
			break;

		}
		return value;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt（）配套使用
	 * 
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes(int value) {
		byte[] src = null;
		if (0 < ((value >> 24) & 0xFF)) {
			src = new byte[4];
			src[3] = (byte) (value & 0xFF);
			src[2] = (byte) ((value >> 8) & 0xFF);
			src[1] = (byte) ((value >> 16) & 0xFF);
			src[0] = (byte) ((value >> 24) & 0xFF);
		} else if (0 < ((value >> 16) & 0xFF)) {
			src = new byte[3];
			src[2] = (byte) (value & 0xFF);
			src[1] = (byte) ((value >> 8) & 0xFF);
			src[0] = (byte) ((value >> 16) & 0xFF);
		} else if (0 < ((value >> 8) & 0xFF)) {
			src = new byte[2];
			src[1] = (byte) (value & 0xFF);
			src[0] = (byte) ((value >> 8) & 0xFF);
		} else {
			src = new byte[1];
			src[0] = (byte) (value & 0xFF);
		}

		return src;
	}
	
	 /**
     * 从一个byte[]数组中截取一部分
     * @param src
     * @param begin
     * @param count
     * @return
     */
    public static byte[] subBytes(byte[] src, int begin, int length) {
        byte[] bs = new byte[length];
        System.arraycopy(src, begin, bs, 0, length);
//        for (int i=begin; i<begin+length; i++) bs[i-begin] = src[i];
        return bs;
    }

	public static byte[] spliceByte(byte[] srcBytes, final byte addByte) {
		byte[] tempBytes = new byte[srcBytes.length + 1];

		System.arraycopy(srcBytes, 0, tempBytes, 0, srcBytes.length);
		System.arraycopy(addByte, 0, tempBytes, srcBytes.length, 1);

		return tempBytes;
	}

	public static byte[] spliceBytes(byte[] srcBytes, final byte[] addBytes) {
		byte[] tempBytes = new byte[srcBytes.length + addBytes.length];

		System.arraycopy(srcBytes, 0, tempBytes, 0, srcBytes.length);
		System.arraycopy(addBytes, 0, tempBytes, srcBytes.length,
				addBytes.length);

		return tempBytes;
	}

	public static byte[] spliceBytes(byte[] srcBytes, final int srcOffset,
			final byte[] addBytes) {
		byte[] tempBytes = null;

		int copyLength = srcBytes.length - srcOffset;

		System.arraycopy(srcBytes, srcOffset, tempBytes, 0, copyLength);

		System.arraycopy(addBytes, 0, tempBytes, copyLength, addBytes.length);

		return tempBytes;
	}

	public static synchronized final byte[] input2Byte(InputStream inStream, int ReadBufLen) {
		byte[] stream2Byte = null;
		try {
//			Log.i("wsy","input2Byte inStream.available()=" + inStream.available() + "ReadBufLen = " + ReadBufLen );
			if ((null != inStream) && (0 < inStream.available())) {
				ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
				byte[] buff = new byte[ReadBufLen];
				//MLCPLogUtil.i("input2Byte ReadBufLen = " + ReadBufLen);
				
				//MLCPLogUtil.i("input2Byte buff.length = " + buff.length);
				int rc = 0;
				while (0 < inStream.available()) {
					// while (-1 != (rc = inStream.read(buff))) {
					rc = inStream.read(buff);
					swapStream.write(buff, 0, rc);
					// }
				}

				swapStream.flush();
				// inStream.close();

				stream2Byte = swapStream.toByteArray();

				// return swapStream.toByteArray();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MLCPLogUtil.i("2015.01.26 wsy input2Byte swap err");

		}
		//MLCPLogUtil.i("input2Byte stream2Byte.length = " + stream2Byte.length);
		return stream2Byte;
	}
}
