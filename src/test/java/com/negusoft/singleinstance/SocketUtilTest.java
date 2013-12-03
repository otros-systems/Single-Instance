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

import org.testng.Assert;

import java.net.ServerSocket;

public class SocketUtilTest {

  @org.testng.annotations.Test
  public void testIsSocketBusy() throws Exception {
    SocketUtil socketUtil = new SocketUtil(SocketUtilTest.class.getName());
    int portUsedForTesting = 50101;
    Assert.assertFalse(socketUtil.isSocketBusy(portUsedForTesting));

    ServerSocket serverSocket = null;
    try {
      serverSocket = socketUtil.openLocalServerSocket(portUsedForTesting);
      Assert.assertTrue(socketUtil.isSocketBusy(portUsedForTesting));
    } finally {
      if (serverSocket != null) {
        serverSocket.close();
      }
    }
  }
}