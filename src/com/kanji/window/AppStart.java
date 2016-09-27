package com.kanji.window;

import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class AppStart {
	
	public static void main (String [] args){
		BaseWindow b = new BaseWindow();
		b.setVisible(true);
		
		SetOfRanges set = new SetOfRanges();
		set.addRange(new Range(2323,123132));
//		set.getRanges();
		set.addRange(new Range(213,123213));
//		set.getRanges();
//		set.addRange(new Range(30,70));
		System.out.println(set.getRanges());
		
	}

}
