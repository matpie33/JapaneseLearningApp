package com.kanji.window;

import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class AppStart {

	public static void main(String[] args) {
		BaseWindow b = new BaseWindow();
		b.setVisible(true);

		SetOfRanges set = new SetOfRanges();
		set.addRange(new Range(1, 5));
		// set.getRanges();
		set.addRange(new Range(6, 10));
		// set.getRanges();
		// set.addRange(new Range(30,70));
		System.out.println(set.getRangesAsString());
		System.out.println((int) 62.7 % 60);

	}

}
