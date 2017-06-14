package com.kanji.dialogs;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;

public class MessagePanel {

	private MainPanel main;
	private GridBagConstraints layoutConstraints;
	private DialogWindow parentDialog;

	public MessagePanel(DialogWindow parent) {
		parentDialog = parent;
		main = new MainPanel(BasicColors.OCEAN_BLUE);
	}

	public JPanel createPanel(String message) {
		int level = 0;
		JTextArea prompt = addPromptAtLevel(level, message);
		JButton button = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonApproveText,
				java.awt.event.KeyEvent.VK_ESCAPE, parentDialog);

		main.addRow(RowMaker.createBothSidesFilledRow(prompt));
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, button));
		return main.getPanel();
	}

	private JTextArea addPromptAtLevel(int level, String message) {
		JTextArea elem = new JTextArea(4, 30);

		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);

		return elem;
	}

}
