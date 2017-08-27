package com.kanji.main;

import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.windows.ApplicationWindow;

public class AppStart {

	public static void main(String[] args) {

		int v1 = 1280;
		int v2 = 768;
		int v3 = v1 * v2;

		ApplicationWindow b = new ApplicationWindow();

		SetOfRanges set = new SetOfRanges();
		set.addRange(new Range(1, 5));
		// set.getRanges();
		set.addRange(new Range(6, 10));
		// set.getRanges();
		// set.addRange(new Range(30,70));

	}

}
