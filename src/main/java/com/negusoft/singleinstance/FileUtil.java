package com.negusoft.singleinstance;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author otros.systems@gmail.com
 *
 */
public class FileUtil {

	private static final Charset CHARSET = Charset.forName("UTF-8");
	private File file;

	FileUtil(String appName) {
		super();
		// add user name if many instance are running on one machine
		this.file = new File(System.getProperty("java.io.tmpdir"), appName
				+ "-" + System.getProperty("user.name"));
	}

	boolean fileExist() {
		return file.exists();
	}

	void writeToFile(String string) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(string.getBytes(CHARSET));
		fout.close();
	}

	String readFromFile() throws IOException {
		FileInputStream fin = new FileInputStream(file);
		byte[] buff = new byte[128];
		int read ;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((read = fin.read(buff)) > 0) {
			bout.write(buff, 0, read);
		}
		fin.close();
    return new String(bout.toByteArray(), CHARSET);
	}

	File getFile() {
		return file;
	}

}
