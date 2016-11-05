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
import com.kanji.myList.MyList;
import com.kanji.myList.SearchOptions;

public class SearchWordPanel {

	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private JTextField textField;
	private MyDialog parentDialog;
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private SearchOptions options;	
	private MyList list;
	
	public SearchWordPanel (JPanel panel, MyDialog parent){
		mainPanel = panel;
		parentDialog=parent;
		layoutConstraints = new GridBagConstraints();			
		options = new SearchOptions();
	}
	
	public void setLayoutConstraints (GridBagConstraints c){
		layoutConstraints=c;
	}
	
	public JPanel createPanel(MyList list){
		this.list=list;
		int level = 0;
		textField = addPromptAndTextFieldAndReturnTextField(level,TextValues.wordSearchDialogPrompt);
		
		level++;
		JRadioButton defaultSearchOption = createRadioButton (level,TextValues.wordSearchDefaultOption);	
		defaultSearchOption.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				options.setDefaultOption();
			}
		});
		
		level++;
		fullWordsSearchOption = createRadioButton (level, TextValues.wordSearchOnlyFullWordsOption);
		fullWordsSearchOption.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed (ActionEvent e){
				options.enableMatchByWordOnly();
			}
		});
		
		level++;
		perfectMatchSearchOption = createRadioButton (level, TextValues.wordSearchPerfectMatchOption);
		perfectMatchSearchOption.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				options.enableMatchByExpressionOnly();
			}
		});
		
		addRadioButtonsToGroup (new JRadioButton [] {defaultSearchOption, fullWordsSearchOption,
					perfectMatchSearchOption});
		
		defaultSearchOption.setSelected(true);
		
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
				tryToFindNextOccurence(NumberValues.BACKWARD_DIRECTION);			
			}
		});
		
		return button;
	}
	
	private JButton createButtonNext (String text){
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				tryToFindNextOccurence(NumberValues.FORWARD_DIRECTION);			
			}
		});
		return button;		
	}	
	
	
	private void tryToFindNextOccurence(int direction){
		System.out.println(options.isMatchByExpressionEnabled());
		System.out.println(options.isMatchByWordEnabled());
		try {
			boolean found = list.findAndHighlightNextOccurence(textField.getText(), direction, options);
			if (!found)
				parentDialog.showErrorDialogInNewWindow(TextValues.wordNotFoundMessage);
		} 
		catch (Exception e) {
			e.printStackTrace();
			parentDialog.showErrorDialogInNewWindow(e.getMessage()); //TODO to nie zawsze dobry pomys�
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
