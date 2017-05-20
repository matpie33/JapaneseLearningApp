package com.kanji.dialogs;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import com.kanji.constants.ButtonsNames;

public class LoadingPanel {
	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private MyDialog parentDialog;
	private JProgressBar bar;

	public LoadingPanel(JPanel panel, MyDialog parent) {
		mainPanel = panel;
		parentDialog = parent;
		layoutConstraints = new GridBagConstraints();
	}

	public void setLayoutConstraints(GridBagConstraints c) {
		layoutConstraints = c;
	}

	public JPanel createPanel(String message) {

		int level = 0;
		addPromptAtLevel(level, message);

		JButton button = parentDialog.createButtonDispose(ButtonsNames.buttonApproveText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
		layoutConstraints.gridy++;
		layoutConstraints.fill = GridBagConstraints.NONE;
		// bar = new JProgressBar();
		// mainPanel.add(bar, layoutConstraints);
		layoutConstraints.gridy++;
		layoutConstraints.fill = GridBagConstraints.NONE;
		mainPanel.add(button, layoutConstraints);

		return mainPanel;
	}

	private void addPromptAtLevel(int level, String message) {
		layoutConstraints.gridy = level;
		layoutConstraints.anchor = GridBagConstraints.CENTER;
		layoutConstraints.weightx = 1;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		JTextArea elem = new JTextArea(4, 30);

		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);

		mainPanel.add(elem, layoutConstraints);
	}

	public void setProgressBar(JProgressBar bar) {
		this.bar = bar;
		layoutConstraints.gridy = 1;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(bar, layoutConstraints);
	}

}
