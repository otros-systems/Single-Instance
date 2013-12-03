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

package com.negusoft.singleinstance.examples;

import com.negusoft.singleinstance.RequestDelegate;
import com.negusoft.singleinstance.ResponseDelegate;
import com.negusoft.singleinstance.SingleInstance;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;
import java.util.logging.XMLFormatter;

/**
 * @author NEGU Soft
 * @author otros.systems@gmail.com
 *
 */
public class ParameterPassingExample {
	
	private static final String DEFAULT_PARAMETER = "HELLO WORLD!";
	
	public static void main(String[] args) {
    initSocketLogger();
		RequestDelegate request;

		request = new RequestDelegate() {
			@Override public void requestAction(Socket socket, String... params) {
        String parameter;
        if (params.length > 0){
          StringBuilder sb = new StringBuilder();
          for (String string : params) {
            sb.append(string).append(", " );
          }
          parameter = sb.toString();
        }
        else{
          parameter = DEFAULT_PARAMETER;
        }
				try {
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					writer.write(parameter);
					writer.newLine();
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		ResponseDelegate response = new ResponseDelegate() {
			@Override public void responseAction(Socket socket) {
				
				BufferedReader reader;
				try {
					InputStreamReader rAux = new InputStreamReader(socket.getInputStream());
					reader = new BufferedReader(rAux);
					String parameter = reader.readLine();
					System.out.println("Param received: \"" + parameter + "\"");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}};
		
		SingleInstance instance = SingleInstance.request("ParameterDemo",request, response,args);
		if (instance == null)
		{
			System.out.println("There is already an instance running so we close.");
			System.out.println("But we sent it the param we received.");
		}
		else
		{
			System.out.println("There is no instance currently running so we can go ahead:");
			System.out.println("Doing some cool stuff, press ENTER key to stop...");
			try {
        System.in.read();
      } catch (IOException e) {
				e.printStackTrace();
			}
			instance.dropInstance();
			System.out.println("Finished, now another instance can run.");
		}
	}

  private static void initSocketLogger() {
    Logger logger = Logger.getLogger("com.negusoft.singleinstance");
    try {
      SocketHandler socketHandler
           = new SocketHandler("localhost",1900);
      logger.setLevel(Level.ALL);
      XMLFormatter xmlFormatter = new XMLFormatter();
      socketHandler.setFormatter(xmlFormatter);
      socketHandler.setLevel(Level.ALL);
      logger.addHandler(socketHandler);
    } catch (IOException e) {
      //cant open socket
    }

  }

}
