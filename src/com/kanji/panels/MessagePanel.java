package com.kanji.panels;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.kanji.constants.ButtonsNames;
import com.kanji.graphicInterface.ActionMaker;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.MainPanel;
import com.kanji.graphicInterface.MyColors;
import com.kanji.graphicInterface.SimpleWindow;

public class MessagePanel {

	private GridBagConstraints layoutConstraints;
	private SimpleWindow window;
	private MainPanel panel;

	public MessagePanel(SimpleWindow window) {
		panel = new MainPanel(MyColors.LIGHT_VIOLET);
		this.window = window;
		layoutConstraints = new GridBagConstraints();
	}

	public void setLayoutConstraints(GridBagConstraints c) {
		layoutConstraints = c;
	}

	public JPanel createPanel(String message) {

//		int level = 0;
//		addPromptAtLevel(level, message);

		JButton button = GuiMaker.createButton(ButtonsNames.buttonApproveText, 
				ActionMaker.createDisposingAction(window.getWindow()));
//				parentDialog.createButtonDispose(ButtonsNames.buttonApproveText,
//				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SPACE, 0));
//		layoutConstraints.gridy++;
//		layoutConstraints.fill = GridBagConstraints.NONE;
		JTextArea area = GuiMaker.createTextArea(false);
		area.setText(message);
		panel.createRow(area);
		panel.createRow(button);

		return panel.getPanel();
	}

	

}
