package com.kanji.dialogs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.sun.glass.events.KeyEvent;

public class ConfirmPanel implements PanelCreator {

	private MainPanel main;
	private DialogWindow parentDialog;
	private String message;

	public ConfirmPanel(String message) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		this.message = message;
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	@Override
	public JPanel createPanel() {

		JTextArea prompt = addPromptAtLevel(message);
		main.addRow(RowMaker.createBothSidesFilledRow(prompt));

		JButton yesButton = new JButton("Tak");
		AbstractAction al = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(true);
			}
		};
		yesButton.addActionListener(al);
		yesButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

		yesButton.getActionMap().put("enter", al);

		JButton noButton = new JButton("Nie");
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(false);
			}
		};
		noButton.addActionListener(action);
		noButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "space");
		noButton.getActionMap().put("space", action);

		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, noButton, yesButton));

		return main.getPanel();
	}

	private JTextArea addPromptAtLevel(String message) {
		JTextArea elem = new JTextArea(4, 30);

		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);
		return elem;

	}

}
