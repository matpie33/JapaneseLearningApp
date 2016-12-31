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

	private SimpleWindow window;
	private MainPanel panel;

	public MessagePanel(SimpleWindow window) {
		panel = new MainPanel(MyColors.LIGHT_VIOLET);
		this.window = window;
	}

	public JPanel createPanel(String message) {
		JButton button = GuiMaker.createButton(ButtonsNames.buttonApproveText, 
				ActionMaker.createDisposingAction(window));
		JTextArea area = GuiMaker.createTextArea(false);
		area.setText(message);
		panel.addRow(panel.createBothSidesFilledRow(area));
		panel.addRow(panel.createUnfilledRow(GridBagConstraints.CENTER, button));
		return panel.getPanel();
	}

	

}
