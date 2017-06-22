package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.windows.DialogWindow;
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
		JButton yesButton = createButtonConfirm();
		JButton noButton = createButtonReject();

		main.addRow(RowMaker.createBothSidesFilledRow(prompt));
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

	private JButton createButtonConfirm() {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(true);
			}
		};
		return GuiElementsMaker.createButton(ButtonsNames.buttonConfirmText, action, KeyEvent.VK_ENTER);
	}

	private JButton createButtonReject() {
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(false);
			}
		};
		return GuiElementsMaker.createButton(ButtonsNames.buttonRejectText, action, KeyEvent.VK_ESCAPE);
	}

}
