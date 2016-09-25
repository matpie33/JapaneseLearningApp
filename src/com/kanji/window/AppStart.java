package com.kanji.window;

import java.awt.Component;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class AppStart {
	
	public static void main (String [] args){
		BaseWindow b = new BaseWindow();
		b.setVisible(true);
		
		SetOfRanges set = new SetOfRanges();
		set.addRange(new Range(50,100));
//		set.getRanges();
		set.addRange(new Range(55,60));
//		set.getRanges();
//		set.addRange(new Range(30,70));
		System.out.println(set.getRanges());
		
	}

}
