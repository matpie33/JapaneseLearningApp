package com.kanji.actions;

import javax.swing.AbstractAction;
import javax.swing.JButton;

public class GuiElementsMaker {

	public static JButton createButton(String message, AbstractAction actionOnClick, int hotkey) {
		JButton noButton = new JButton(message);
		noButton.addActionListener(actionOnClick);
		CommonActionsMaker.addHotkey(hotkey, actionOnClick, noButton);
		return noButton;
	}

	// TODO remember that we have element maker, gotta refactor it to here.

}
