/*
 * Copyright 2013 Krzysztof Otrebski (otros.systems@gmail.com)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.negusoft.singleinstance;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * <p>This is a utility to control the creation of instances across the local system.
 * This means we can check if there is already an instance of certain program running 
 * at the moment. It is also possible to implement actions to be performed when attempting 
 * to create a new instance if there was already one active.</p>
 * <p>A socket based mechanism is used to establish the instances. You can select the port number 
 * on which you want to work and a listening socket will take it. Like this, the new instance
 * will fail to listen on that port number, meaning that there is another one running. Since 
 * it is a socket based system, it allows passing data between the actual instance and the one 
 * attempting to establish. This can be useful, for example, to pass a file url to a program
 * that will be opened in the currently running instance, if there is one.
 * </p>
 * 
 * @author NEGU Soft
 * @author otros.systems@gmail.com
 *
 */
public class SingleInstance {
	private static final Logger LOGGER = Logger.getLogger(SingleInstance.class.getName());
	private static final long DROP_TIMEOUT = 500;
	
	private int port;
	private ServerSocket serverSocket;
	private ResponseDelegate response;
	private Thread thread;
	private SocketUtil socketUtil;
	

	private SingleInstance(String appName, int port, ResponseDelegate response, SocketUtil socketUtil) {
		this.port = port;
		this.response = response;
		this.socketUtil = socketUtil;
		LOGGER.finest("Creating single instance for app " + appName);
	}
	
	private void establishInstance() throws IOException {
		this.serverSocket = new ServerSocket(this.port);
		socketUtil.markSocketAsBusy(port);

		this.thread = new Thread(new Runnable() {
			@Override public void run() {
				while (true) {
					try {
						//If another instance tries to establish -> respond
						Socket socket = serverSocket.accept();
						if (response != null)
							response.responseAction(socket);
						socket.close();
					} catch (IOException e) {
						break;
					}
				}
				//Set variables to null before exiting
				serverSocket = null;
			}});
		this.thread.start();
	}
	
	/**
	 * Free the instance son that a new instance can be established
	 */
	public void dropInstance() {
		LOGGER.fine("Droping instance");
		if (this.serverSocket == null){
			LOGGER.fine("No server socket was open, returning");
			return;			
		}
		
		//close the ServerSocket
		LOGGER.fine("Closing socket");
		socketUtil.closeSocketAndRemoveMarkerFile(serverSocket);
		
		
		//wait for the thread to finish
		try {
			LOGGER.fine("Waitin for socket thread to close");
			this.thread.join(DROP_TIMEOUT);
			LOGGER.fine("Socket thread finished");
		} catch (InterruptedException e) {
			LOGGER.warning("Waitin for socket thread intterruped");
		}
	}
	

	
	/**
	 * Request an instance representation
	 * @param appName name of application. Will be used to create file with port information
	 * @param request action to be performed if there is already an instance running
	 * @param response action to perform when new instances are requested while this one is running
	 * @return an instance representation or NULL if there is one already one running
	 */
	public static SingleInstance request(String appName, RequestDelegate request, ResponseDelegate response, String... params) {
		//Try to establish the instance
		SocketUtil socketUtil = new SocketUtil(appName);
		SingleInstance result = new SingleInstance(appName,socketUtil.getPortToUse(), response, socketUtil);
		try {
			result.establishInstance();
			return result;
		} catch (IOException e) {
			LOGGER.fine("Can't estabilish new instance, one is already running");
		}
		
		//If failed connect to the current instance and notify
		try {
			LOGGER.fine("Connecting to existing instance");
			Socket socket = new Socket(InetAddress.getLocalHost(), socketUtil.getPortToUse());
			if (request != null){
				request.requestAction(socket,params);
			}
		} catch (Exception e) {
			LOGGER.warning("Can't connect to existing instance");
		}
		
		return null;
	}
}
