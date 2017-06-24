package com.kanji.actions;

import javax.swing.AbstractAction;
import javax.swing.JButton;

public class GuiElementsMaker {

	public static JButton createButton(String message, AbstractAction actionOnClick, int hotkey) {
		JButton button = createButton(message, actionOnClick);
		CommonActionsMaker.addHotkey(hotkey, actionOnClick, button);
		return button;
	}

	public static JButton createButton(String message, AbstractAction actionOnClick) {
		JButton button = new JButton(message);
		button.addActionListener(actionOnClick);
		return button;
	}

	// TODO remember that we have element maker, gotta refactor it to here.

}
