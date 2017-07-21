package com.kanji.actions;

import java.awt.Color;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;

public class GuiElementsMaker {

	public static JButton createButton(String message, AbstractAction actionOnClick, int hotkey) {
		JButton button = createButton(message, actionOnClick);
		CommonActionsMaker.addHotkey(hotkey, 0, actionOnClick, button);
		return button;
	}

	public static JButton createButton(String message, AbstractAction actionOnClick) {
		JButton button = new JButton(message);
		button.addActionListener(actionOnClick);
		return button;
	}

	public static JLabel createErrorLabel(String message) {
		JLabel l = new JLabel(message);
		l.setForeground(Color.red);
		return l;
	}

	// TODO remember that we have element maker, gotta refactor it to here.

}
