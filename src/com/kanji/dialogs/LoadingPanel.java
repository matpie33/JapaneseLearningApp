package com.kanji.dialogs;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.constants.ButtonsNames;

public class LoadingPanel {

	private MainPanel main;
	private GridBagConstraints layoutConstraints;
	private MyDialog parentDialog;
	private JProgressBar bar;
	private JButton okButton;

	public LoadingPanel(JPanel panel, MyDialog parent) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		parentDialog = parent;
	}

	public JPanel createPanel(String message) {

		int level = 0;
		JTextArea prompt = addPromptAtLevel(level, message);

		okButton = parentDialog.createButtonDispose(ButtonsNames.buttonApproveText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));

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
		this.bar = bar;
		main.removeRow(1);
		main.addRow(RowMaker.createHorizontallyFilledRow(bar));
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));
	}

}
