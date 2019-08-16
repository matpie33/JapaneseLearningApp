package com.kanji.utilities;

import java.io.*;

public class LogWriter {

	public static final String LOG_FILENAME = "/log.txt";

	public String logFile(String path, Exception e) {
		try {
			return log(path, e);
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

		return "";
	}

	private String log(String path, Exception e) throws IOException {
		String pathname = path + LOG_FILENAME;
		File file = new File(pathname);
		file.createNewFile();
		PrintStream ps = new PrintStream(file);
		e.printStackTrace(ps);
		return file.getPath();
	}

}
