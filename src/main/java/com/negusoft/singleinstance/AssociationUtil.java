package com.negusoft.singleinstance;

import java.io.*;

/**
 * Class used to create file association
 * TODO Not finished
 */
public class AssociationUtil {

  public static void associateProgramWithExtension(String pathToProgram, String name, String extension,
                                                   String... args) throws IOException {
    String osName = System.getProperty("os.name");
    if (osName.indexOf("win") > -1) {
      associateOnWindows(pathToProgram, name, extension, args);
    } else if (osName.indexOf("linux") > -1) {
      //TODO
    }
  }


  private static void associateOnWindows(String pathToProgram, String name, String extension, String[] args) throws IOException {
    File tempFile = File.createTempFile("Otros", ".bat");
    PrintWriter printWriter = new PrintWriter(tempFile);
    printWriter.printf("assoc .%s=%s%n",extension,name);
    StringBuilder sb = new StringBuilder();
    for (String arg:args){
      sb.append(arg).append(" ");
    }
    printWriter.printf("ftype %s=%s %s",name,pathToProgram, sb.toString());
    printWriter.close();
    runCommand(tempFile.getAbsolutePath());
  }

  private static int runCommand(String command, String... args) throws IOException {
    Process exec = Runtime.getRuntime().exec(command, args);
    consumeStream(exec.getInputStream());
    consumeStream(exec.getErrorStream());
    return exec.exitValue();
  }

  private static void consumeStream(InputStream in) throws IOException {
    byte[] buff = new byte[32];
    while (in.read(buff) > 0) {
      //ignore input
    }
  }

}
