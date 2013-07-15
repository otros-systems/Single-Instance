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
