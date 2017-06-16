package com.kanji.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.windows.DialogWindow;

public class LoadingPanel implements PanelCreator {

	private MainPanel main;
	private DialogWindow parentDialog;
	private JButton okButton;
	private String message;

	public LoadingPanel(String message) {
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

		okButton = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonApproveText,
				java.awt.event.KeyEvent.VK_SPACE, parentDialog);

		main.addRow(RowMaker.createBothSidesFilledRow(prompt));
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));

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

	public void setProgressBar(JProgressBar bar) {
		main.removeRow(1);
		main.addRow(RowMaker.createHorizontallyFilledRow(bar));
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));
	}

}
