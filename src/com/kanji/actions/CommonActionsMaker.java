package com.kanji.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import com.kanji.windows.DialogWindow;

public class CommonActionsMaker {

	public static JButton createButtonDispose(String text, int keyEventName,
			final DialogWindow dialog) {
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.getContainer().dispose();
			}
		};
		return createButtonWithAction(text, keyEventName, dialog, action);
	}

	private static JButton createButtonWithAction(String text, int keyEventName,
			final DialogWindow dialog, AbstractAction actionListener) {
		JButton button = new JButton(text);
		addHotkey(keyEventName, actionListener, button);
		button.addActionListener(actionListener);
		return button;
	}

	public static JButton createButtonHide(String text, int keyEventName,
			final DialogWindow dialog) {
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.getContainer().setVisible(false);
			}
		};
		return createButtonWithAction(text, keyEventName, dialog, action);
	}

	public static void addHotkey(int keyEvent, AbstractAction a, JComponent component) {
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(keyEvent, 0), "close");
		component.getActionMap().put("close", a);
	}

}
