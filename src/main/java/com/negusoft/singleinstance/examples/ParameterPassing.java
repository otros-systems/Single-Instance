package com.negusoft.singleinstance.examples;

import com.negusoft.singleinstance.RequestDelegate;
import com.negusoft.singleinstance.ResponseDelegate;
import com.negusoft.singleinstance.SingleInstance;

import java.io.*;
import java.net.Socket;

/**
 * @author NEGU Soft
 * @author otros.systems@gmail.com
 *
 */
public class ParameterPassing {
	
	private static final String DEFAULT_PARAMETER = "HELLO WORLD!";
	
	private static String parameter;

	public static void main(String[] args) {
		RequestDelegate request;
		if (args.length > 0){
			StringBuilder sb = new StringBuilder();
			for (String string : args) {
				sb.append(string).append(", " );
			}
			parameter = sb.toString();
		}
		else{
			parameter = DEFAULT_PARAMETER;
		}
		request = new RequestDelegate() {
			@Override public void requestAction(Socket socket) {
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
		
		SingleInstance instance = SingleInstance.request("ParameterDemo",request, response);
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
        int read = System.in.read();
      } catch (IOException e) {
				e.printStackTrace();
			}
			instance.dropInstance();
			System.out.println("Finished, now another instance can run.");
		}
	}

}