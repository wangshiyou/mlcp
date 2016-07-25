package org.daoke.mlcp.interfaces;


public interface MLCPInterface {
	public void socketNotify(final String mfptpName, final int socketStatus,
			final byte[] socketData);
}
