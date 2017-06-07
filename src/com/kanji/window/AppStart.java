package com.kanji.window;

import javax.swing.JDialog;

import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class AppStart {

	public static void main(String[] args) {

		int v1 = 1280;
		int v2 = 768;
		int v3 = v1 * v2;
		System.out.println("v3: " + v3);

		ApplicationWindow b = new ApplicationWindow();
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
