package com.kanji.main;

import com.kanji.utilities.OldToNewestVersionConverter;
import com.kanji.windows.ApplicationWindow;

import java.io.File;
import java.io.IOException;

public class AppStart {

	public static void main(String[] args) {

		ApplicationWindow b = new ApplicationWindow();
		b.initiate();

		File file = new File("C:/files/NowePowtórki");
		try {
			OldToNewestVersionConverter.convertPreviousToNewestFile(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}
