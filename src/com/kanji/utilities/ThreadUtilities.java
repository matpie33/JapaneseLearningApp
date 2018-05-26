package com.kanji.utilities;

public class ThreadUtilities {

	public static void callOnOtherThread (Runnable r){
		new Thread (r).start();
	}
}
