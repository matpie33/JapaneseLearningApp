package com.kanji.dialogs;

import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

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
		layoutConstraints.gridy++;
		layoutConstraints.fill=GridBagConstraints.NONE;
		mainPanel.add(button,layoutConstraints);	
		
		return mainPanel;
	}

	private void addPromptAtLevel(int level, String message){
		layoutConstraints.gridy=level;
		layoutConstraints.anchor=GridBagConstraints.CENTER;
		layoutConstraints.weightx=1;
		layoutConstraints.fill=GridBagConstraints.HORIZONTAL;
		JTextArea elem = new JTextArea (message);	
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);
		
		mainPanel.add(elem,layoutConstraints);
	}
	
}
