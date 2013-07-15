package com.negusoft.singleinstance;

import java.net.Socket;

/**
 * Response for the new instance attempts
 * @author NEGU Soft
 */
public interface ResponseDelegate {
	public void responseAction(Socket socket);
}