package com.kanji.actions;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.guimaker.colors.BasicColors;

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

	public static JRadioButton createRadioButton(String buttonLabel) {
		JRadioButton radioButton = new JRadioButton(buttonLabel);
		radioButton.setOpaque(false);
		return radioButton;
	}

	public static JLabel createErrorLabel(String message) {
		JLabel l = new JLabel(message);
		l.setForeground(Color.red);
		return l;
	}

	public static JScrollPane createTextPaneWrappedInScrollPane(String text,
			TextAlignment alignment) {
		JScrollPane pane = new JScrollPane(createTextPane(text, alignment));
		pane.setPreferredSize(new Dimension(250, 70));
		return pane;
	}

	public static JTextPane createTextPane(String text, TextAlignment alignment) {
		JTextPane textPane = new JTextPane();
		textPane.setBackground(BasicColors.VERY_LIGHT_BLUE);
		textPane.setText(text);
		textPane.setEditable(false);
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, alignment.getStyleConstant());
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		return textPane;
	}

	// TODO remove this, move it to gui maker

	// TODO remember that we have element maker, gotta refactor it to here.

}
