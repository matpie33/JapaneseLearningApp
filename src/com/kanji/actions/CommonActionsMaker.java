package com.kanji.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.KeyStroke;

import com.kanji.dialogs.DialogWindow;

public class CommonActionsMaker {

	public static JButton createButtonDispose(String text, int keyEventName,
			final DialogWindow dialog) {
		JButton button = new JButton(text);
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};
		button.addActionListener(action);
		button.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(keyEventName, 0), "space");

		button.getActionMap().put("space", action);
		return button;
	}

}
