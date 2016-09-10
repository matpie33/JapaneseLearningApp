package com.kanji.window;

import java.awt.Component;
import java.text.Normalizer;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JPanel;

public class AppStart {
	
	public static void main (String [] args){
		BaseWindow b = new BaseWindow();
		b.setVisible(true);
		StringBuffer sb = new StringBuffer ("a");
		StringBuffer cs = sb;
		System.out.println(cs.toString().equals("a"));
		
		
	}

}
