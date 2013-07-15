package com.negusoft.singleinstance;

import java.net.Socket;

/**
 * Request to be performed to the currently running instance
 * @author NEGU Soft
 */
public interface RequestDelegate {
	public void requestAction(Socket socket);
}