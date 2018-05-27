package com.kanji.main;

import com.kanji.windows.ApplicationWindow;
import com.sun.javafx.application.PlatformImpl;

import javax.swing.*;

public class AppStart {

	public static void main(String[] args) {

		PlatformImpl.startup(()->{});
		ApplicationWindow b = new ApplicationWindow();
		b.initiate();

	}

}
