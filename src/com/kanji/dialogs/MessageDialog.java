package com.kanji.dialogs;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.kanji.constants.TextValues;

public class MessageDialog {

	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private MyDialog parentDialog;
	
	public MessageDialog (JPanel panel, MyDialog parent){
		mainPanel = panel;
		parentDialog=parent;
		layoutConstraints = new GridBagConstraints();	
	}
	
	public void setLayoutConstraints (GridBagConstraints c){
		layoutConstraints=c;
	}
	
	public JPanel createDialog (String message){
		
		int level = 0;
		addPromptAtLevel(level,message);
		
		JButton button = parentDialog.createButtonDispose(TextValues.buttonApproveText);
		mainPanel.add(button,layoutConstraints);	
		
		return mainPanel;
	}

	private void addPromptAtLevel(int level, String message){
		layoutConstraints.gridy=level;
		JLabel label = new JLabel (message);
		mainPanel.add(label,layoutConstraints);
	}
	
}
