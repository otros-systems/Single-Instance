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
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author otros.systems@gmail.com
 */
class SocketUtil {

  private static final Logger LOGGER = Logger.getLogger(SocketUtil.class.getName());
  public static final String SYS_PROPERTY = "singleInstance.startPort";
  private final InetAddress loopbackAddress;
  private FileUtil fileUtil;

  SocketUtil(String appName) {
    super();
    this.fileUtil = new FileUtil(appName);
    try {
      loopbackAddress = InetAddress.getByName("127.0.0.1");
    } catch (UnknownHostException e) {
      //this is not going to happened
      throw new RuntimeException("Can't find local \"127.0.0.1\" address ", e);
    }
  }

  int getPortToUse() {
    LOGGER.entering(SocketUtil.class.getName(), "getPortToUse");

    String readFromFile;
    int portFromFile = -1;
    boolean socketAlive = false;
    if (fileUtil.fileExist()) {
      LOGGER.log(Level.INFO, "Definition file exist");
      try {
        readFromFile = fileUtil.readFromFile();
        LOGGER.log(Level.INFO, "Port read from file is {0}", readFromFile);
        portFromFile = Integer.parseInt(readFromFile);
        socketAlive = isSocketBusy(portFromFile);
        LOGGER.log(Level.INFO, "Socket {0} is alive: {1}", new Object[]{portFromFile, socketAlive});
      } catch (IOException e) {
        //ignore it
      }
    }
    if (socketAlive) {
      LOGGER.exiting(SocketUtil.class.getName(), "getPortToUse", true);
      return portFromFile;
    } else {
      int randomSocket = getRandomSocket();
      LOGGER.exiting(SocketUtil.class.getName(), "getPortToUse", randomSocket);
      return randomSocket;
    }
  }

  void markSocketAsBusy(int port) {
    LOGGER.entering(SocketUtil.class.getName(), "markSocketAsBusy", port);
    try {
      fileUtil.writeToFile(Integer.toString(port));
      fileUtil.getFile().deleteOnExit();
    } catch (IOException e) {
      //ignore it
      LOGGER.log(Level.INFO, "Can't write busy socket into file", e);
    }
    LOGGER.exiting(SocketUtil.class.getName(), "markSocketAsBusy");
  }

  void closeSocketAndRemoveMarkerFile(ServerSocket serverSocket) {
    LOGGER.entering(SocketUtil.class.getName(), "closeSocketAndRemoveMarkerFile");
    try {
      serverSocket.close();
    } catch (IOException e) {
      //ignore it
    }
    final boolean delete = fileUtil.getFile().delete();
    LOGGER.fine("File " + fileUtil.getFile().getAbsolutePath() + " was deleted: " + delete);
    LOGGER.exiting(SocketUtil.class.getName(), "closeSocketAndRemoveMarkerFile");

  }

  int getRandomSocket() {
    int port = 10001;
    final String property = System.getProperty(SYS_PROPERTY, Integer.toString(port));
    try {
      port = Integer.parseInt(property);
    } catch (Exception e) {
      LOGGER.severe(String.format("Can't parse %s value \"%s\", error: %s", SYS_PROPERTY, property, e.getMessage()));
    }
    LOGGER.entering(SocketUtil.class.getName(), "getRandomSocket");
    final int portMaxValue = Math.min(port + 10000, 65535);
    for (int i = port; i < portMaxValue; i++) {
      try {
        ServerSocket serverSocket = new ServerSocket(i, 1,
            loopbackAddress);
        serverSocket.close();
        LOGGER.exiting(SocketUtil.class.getName(), "getRandomSocket", i);
        return i;
      } catch (Exception e) {
        // socket busy
      }
    }
    LOGGER.exiting(SocketUtil.class.getName(), "getRandomSocket", -1);
    return -1;
  }

  boolean isSocketBusy(int port) {
    LOGGER.entering(SocketUtil.class.getName(), "isSocketBusy", port);
    boolean result = true;
    try {
      ServerSocket socket = new ServerSocket(port, 1, loopbackAddress);
      result = false;
      socket.close();
    } catch (Exception e) {
      // socket is dead
      LOGGER.log(Level.INFO, "Socket is busy");

    }
    LOGGER.exiting(SocketUtil.class.getName(), "isSocketBusy", result);
    return result;
  }

  public ServerSocket openLocalServerSocket(int port) throws IOException {
    return new ServerSocket(port, 3, loopbackAddress);
  }

  public Socket openLocalClientSocket(int portToUse) throws IOException {
    return new Socket(loopbackAddress, portToUse);

  }
}
