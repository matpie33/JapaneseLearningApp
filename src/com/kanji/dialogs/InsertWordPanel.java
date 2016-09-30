package com.kanji.dialogs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import com.kanji.constants.NumberValues;
import com.kanji.constants.TextValues;
import com.kanji.myList.MyList;
import com.kanji.window.LimitDocumentFilter;

public class InsertWordPanel {

	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private MyDialog parentDialog;
	private MyList list;
	private JTextField insertWord;
	private JTextField insertNumber;
	
	public InsertWordPanel (JPanel panel, MyDialog parent){
		mainPanel = panel;
		parentDialog=parent;
		layoutConstraints = new GridBagConstraints();	
	}
	
	public void setLayoutConstraints (GridBagConstraints c){
		layoutConstraints=c;
	}
	
	public JPanel createPanel (MyList list){
		this.list=list;
		int level = 0;
		insertWord = addPromptAndTextField(level,TextValues.wordAddDialogPrompt);
		
		level++;
		insertNumber = addPromptAndTextField(level, TextValues.wordAddNumberPrompt);
		limitCharactersAccordingToInteger(insertNumber);
		
		level++;
		JButton cancel = parentDialog.createButtonDispose(TextValues.buttonCancelText);
		JButton approve = createButtonValidate(TextValues.buttonApproveText);
		addButtonsAtLevel(level, new JButton [] {cancel, approve});	
		return mainPanel;
	}
	
	private JTextField addPromptAndTextField (int level, String promptMessage){
		
		JLabel prompt = new JLabel (promptMessage);		
		JTextField insertWord = new JTextField(20);		
				
		JPanel panel = new JPanel ();
		panel.add(prompt);
		panel.add(insertWord);
		layoutConstraints.gridy=level;
		mainPanel.add(panel,layoutConstraints);
		
		return insertWord;
	}
	
	private void limitCharactersAccordingToInteger (JTextField textField){
		((AbstractDocument)textField.getDocument()).setDocumentFilter(
				new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
	}
	
	private JButton createButtonValidate(String text){
		JButton button = new JButton (text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				String numberInput = insertNumber.getText();
				String wordInput = insertWord.getText(); 
				if (isNumberValid(numberInput)){
					int number = Integer.parseInt(numberInput);
					if (checkIfInputIsValid(wordInput, number))			
						addWordToList(wordInput, number);
				}				
								
			}
		});
		return button;
	}
			
			
	private boolean isNumberValid(String number){
		boolean valid = number.matches("\\d+");
	
		if (!valid)
			parentDialog.showErrorDialogInNewWindow(TextValues.numberFormatException);
		return valid;
	}	
	
	private boolean checkIfInputIsValid(String word, int number){					
		return (isWordIdUndefinedYet(number) && isWordUndefinedYet(word));								
	}	
	
	private boolean isWordIdUndefinedYet(int number){
		boolean undefined=list.isWordIdUndefinedYet(number);		
		if (!undefined)
			parentDialog.showErrorDialogInNewWindow(TextValues.idAlreadyDefinedException);
		return undefined;
	}
	
	private boolean isWordUndefinedYet(String word){
		boolean undefined = list.isWordUndefinedYet(word);
		if (!undefined)
			parentDialog.showErrorDialogInNewWindow(TextValues.wordAlreadyDefinedException);
		return undefined;
	}
	
	private void addWordToList(String word, int number){
		list.addWord(word,number);	
		list.scrollToBottom();
	}
	
	private void addButtonsAtLevel(int level, JComponent [] buttons){
		JPanel panel = new JPanel ();
		for (JComponent button: buttons)
			panel.add(button);
		
		layoutConstraints.gridy=level;
		mainPanel.add(panel,layoutConstraints);
	}
	
}
