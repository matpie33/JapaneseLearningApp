package com.kanji.main;

import com.guimaker.panels.MainPanel;
import com.kanji.constants.Colors;
import com.kanji.windows.ApplicationWindow;
import com.sun.javafx.application.PlatformImpl;

public class AppStart {

	public static void main(String[] args) {


		PlatformImpl.startup(() -> {
		});
		MainPanel.setDefaultColor(Colors.CONTENT_PANEL_COLOR);
		//TODO some configuration is needed: panel default color,
		//buttons color, list default color
		ApplicationWindow b = new ApplicationWindow();
		b.initiate();
		String preferIpV4Property = System
				.getProperty("java.net.preferIPv4Stack");
		if (preferIpV4Property == null) {
			String message = "Aplikacja moze byc uruchomiona tylko z pliku exe.";
			b.showMessageDialog(message);
			System.exit(1);

		}

	}

}
