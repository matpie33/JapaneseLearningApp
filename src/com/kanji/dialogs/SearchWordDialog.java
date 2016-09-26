package com.kanji.dialogs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.kanji.constants.NumberValues;
import com.kanji.constants.TextValues;
import com.kanji.window.MyList;

public class SearchWordDialog {

	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private JTextField textField;
	private MyDialog parentDialog;
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private Map<JRadioButton, Integer> options;	
	private MyList list;
	
	public SearchWordDialog (JPanel panel, MyDialog parent){
		mainPanel = panel;
		parentDialog=parent;
		layoutConstraints = new GridBagConstraints();			
		options = new HashMap <JRadioButton, Integer> ();
	}
	
	public void setLayoutConstraints (GridBagConstraints c){
		layoutConstraints=c;
	}
	
	public JPanel createDialog(MyList list){
		this.list=list;
		int level = 0;
		textField = addPromptAndTextFieldAndReturnTextField(level,TextValues.wordSearchDialogPrompt);
		
		level++;
		JRadioButton defaultSearchOption = createRadioButton (level,TextValues.wordSearchDefaultOption);		
		level++;
		fullWordsSearchOption = createRadioButton (level, TextValues.wordSearchOnlyFullWordsOption);	
		level++;
		perfectMatchSearchOption = createRadioButton (level, TextValues.wordSearchPerfectMatchOption);
		
		addRadioButtonsToGroup (new JRadioButton [] {defaultSearchOption, fullWordsSearchOption,
					perfectMatchSearchOption});
		
		defaultSearchOption.setSelected(true);
		options.put(fullWordsSearchOption, 1);	//TODO avoid pure numbers here
		options.put(perfectMatchSearchOption, 2);
		
		level++;
		JButton previous = createButtonPrevious(TextValues.buttonPreviousText);
		JButton next = createButtonNext(TextValues.buttonNextText);
		addButtonsAtLevel(level,new JButton [] {previous,next});
		return mainPanel;
	}
	
	private JTextField addPromptAndTextFieldAndReturnTextField (int level, String promptMessage){
		
		JLabel prompt = new JLabel (promptMessage);		
		JTextField insertWord = new JTextField(20);		
				
		JPanel panel = new JPanel ();
		panel.add(prompt);
		panel.add(insertWord);
		layoutConstraints.gridy=level;
		mainPanel.add(panel,layoutConstraints);
		
		return insertWord;
	}
	
	private JRadioButton createRadioButton (int level, String text){
		JRadioButton radioButton = new JRadioButton (text);
		layoutConstraints.gridy=level;
		mainPanel.add(radioButton,layoutConstraints);
		return radioButton;
	}
	
	private void addRadioButtonsToGroup (JRadioButton [] buttons){
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button: buttons)
			group.add(button);
	}		
	

	private JButton createButtonPrevious (String text){
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				search(NumberValues.BACKWARD_DIRECTION);			
			}
		});
		
		return button;
	}
	
	private JButton createButtonNext (String text){
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				search(NumberValues.FORWARD_DIRECTION);			
			}
		});
		return button;		
	}	
	
	private void search (int direction){
		Set <Integer> chosenOptions = new HashSet <Integer> ();
	
		for (JRadioButton checkbox: options.keySet()){
			if (checkbox.isSelected())
				chosenOptions.add(options.get(checkbox));
		}
	
		tryToFindNextOccurence(direction,chosenOptions);
			
	}
	
	private void tryToFindNextOccurence(int direction, Set <Integer> chosenOptions){
		try {
			list.findAndHighlightNextOccurence(textField.getText(), direction, chosenOptions);
		} 
		catch (Exception e) {
			e.printStackTrace();
			parentDialog.showErrorDialogInNewWindow(e.getMessage()); //TODO to nie zawsze dobry pomys³
		}
	}
	
	private void addButtonsAtLevel(int level, JComponent [] buttons){
		JPanel panel = new JPanel ();
		for (JComponent button: buttons)
			panel.add(button);
		
		layoutConstraints.gridy=level;
		mainPanel.add(panel,layoutConstraints);
	}
	
}
