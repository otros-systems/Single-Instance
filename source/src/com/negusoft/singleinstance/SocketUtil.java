package com.negusoft.singleinstance;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 *  @author otros.systems@gmail.com
 *
 */
class SocketUtil {

	private FileUtil fileUtil;

	SocketUtil(String appName) {
		super();
		this.fileUtil = new FileUtil(appName);
	}

	int getPortToUse() {
		String readFromFile;
		int portFromFile = -1;
		boolean socketAlive = false;
		if (fileUtil.fileExist()) {

			try {
				readFromFile = fileUtil.readFromFile();
				portFromFile = Integer.parseInt(readFromFile);
				socketAlive = isSocketAlive(portFromFile);
			} catch (IOException e) {
        //ignore it
			}
		}
		if (socketAlive) {
			return portFromFile;
		} else {
      return getRandomSocket();
		}
	}

	void markSocketAsBusy(int port) {
		try {
			fileUtil.writeToFile(Integer.toString(port));
		} catch (IOException e) {
		  //ignore it
		}
	}

	void closeSocketAndRemoveMarkerFile(ServerSocket serverSocket) {
		try {
			serverSocket.close();
		} catch (IOException e) {
			//ignore it
		}
		fileUtil.getFile().deleteOnExit();

	}

	int getRandomSocket() {

		for (int i = 10000; i < 20000; i++) {
			try {
				ServerSocket serverSocket = new ServerSocket(i, 1,
						InetAddress.getLocalHost());
				serverSocket.close();
				return i;
			} catch (Exception e) {
				// socket busy
			}
		}
		return -1;
	}

	boolean isSocketAlive(int port) {

		try {
			ServerSocket socket = new ServerSocket(port, 1,
					InetAddress.getLocalHost());
			socket.close();
			return true;
		} catch (Exception e) {
			// socket is dead
		}
		return false;
	}

}
