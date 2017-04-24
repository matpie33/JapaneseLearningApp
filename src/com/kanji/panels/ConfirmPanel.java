package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.KeyBindingsMaker;
import com.kanji.graphicInterface.MyColors;
import com.kanji.graphicInterface.SimpleWindow;
import com.kanji.listenersAndAdapters.ActionMaker;

public class ConfirmPanel {
	
	private SimpleWindow parentDialog;
	private MainPanel panel;
	private boolean isAccepted;

	public ConfirmPanel(SimpleWindow parent) {
		panel = new MainPanel(MyColors.DARK_GREEN);
		parentDialog = parent;
	}

	public JPanel createPanel(String message) {
		JTextArea area = GuiMaker.createTextArea(false);
		area.setText(message);
		panel.addRow(RowMaker.createBothSidesFilledRow(area));

		AbstractAction confirmingAction = ActionMaker.createConfirmingAction(this, true);
		AbstractAction refuseAction = ActionMaker.createConfirmingAction(this, false);
		JButton yesButton = GuiMaker.createButton("Tak", confirmingAction);
						
		KeyBindingsMaker.makeBindings(yesButton, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), 
				confirmingAction);
		
		JButton noButton = GuiMaker.createButton("Nie", refuseAction);		
		noButton.addActionListener(refuseAction);

		panel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.EAST, yesButton,noButton));
		return panel.getPanel();
	}
	
	public boolean accepted(){
		return isAccepted;
	}
	
	public void setAccepted (boolean chosen){
		isAccepted=chosen;
		parentDialog.close();
	}

}

    

