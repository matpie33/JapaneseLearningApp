package com.kanji.main;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.Colors;
import com.kanji.windows.ApplicationWindow;
import com.sun.javafx.application.PlatformImpl;

import javax.swing.*;
import java.awt.*;

public class AppStart {

	public static void main(String[] args) {

		PlatformImpl.startup(()->{});
		MainPanel.setDefaultColor(Colors.CONTENT_PANEL_COLOR);
		//TODO some configuration is needed: panel default color,
		//buttons color, list default color
		ApplicationWindow b = new ApplicationWindow();
		b.initiate();

	}

}
