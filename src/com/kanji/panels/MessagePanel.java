package com.kanji.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.windows.DialogWindow;

public class MessagePanel implements PanelCreator {

	private MainPanel main;
	private GridBagConstraints layoutConstraints;
	private DialogWindow parentDialog;
	private String message;

	public MessagePanel(String message) {
		this.message = message;
		main = new MainPanel(BasicColors.OCEAN_BLUE);
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	@Override
	public JPanel createPanel() {
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
