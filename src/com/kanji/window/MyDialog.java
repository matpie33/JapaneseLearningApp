package com.kanji.window;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.kanji.constants.NumberValues;
import com.kanji.constants.TextValues;


public class MyDialog extends JDialog  {

	private static final long serialVersionUID = 7484743485658276014L; 
	private Insets insets = new Insets(10,10,0,10);
	private Color backgroundColor = Color.GREEN;
	private Map<JRadioButton, Integer> options;	
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private JTextField textField;
	private GridBagConstraints layoutConstraints;
	private boolean isOpened;	
	private MyList list;	
	private MyDialog upper;
	private JTextField insertWord;
	private JTextField insertNumber;	
	private JPanel mainPanel;	
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {        	
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();	
            return false;
        }
    }
	
	public MyDialog (Window b){
		super(b);
		setLocationBasedOnParent(b);
		initialize();
		initializeLayout();
		addEscapeKeyToCloseTheWindow();
		
	}
	
	public MyDialog (Window b,MyList myList){		
		this(b);
		list=myList;		
	}
	
	private void setLocationBasedOnParent (Window parent){
		if (parent instanceof BaseWindow)
			setLocation(parent.getLocation());
		
		else setLocationRelativeTo(parent);	
		
	}
	
	private void initialize(){
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
		options = new HashMap <JRadioButton, Integer> ();
		isOpened=true;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		setTitle(TextValues.wordSearchDialogTitle);		
	}
		
	private void initializeLayout(){													
		
		mainPanel = new JPanel();
		mainPanel.setBackground(backgroundColor);	
		mainPanel.setLayout(new GridBagLayout());			
						
		setContentPane(mainPanel);
		layoutConstraints = new GridBagConstraints();
		layoutConstraints.insets=insets;
			
	}
	
	private void addEscapeKeyToCloseTheWindow (){
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				isOpened=false;
			}
		});
	}
	
	public void showMsgDialog(String message){
		
		JLabel label1 = new JLabel (message);
		mainPanel.add(label1,layoutConstraints);
		layoutConstraints.gridy++;
		
		JButton button = new JButton (TextValues.buttonApproveText);
		mainPanel.add(button,layoutConstraints);
		
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				dispose();
			}
		});
		
		showYourself();
	}
	
	public void showSearchWordDialog (){
							
		addPromptAndTextField(TextValues.wordSearchDialogPrompt);
		addRadioButtons();
		addButtonPreviousAndNext();
		showYourself();
		
	}
	

	private void showYourself(){
		setVisible(true);
		pack();
	}
	
	private void addPromptAndTextField(String promptText){
		JLabel label1 = new JLabel (promptText);
		mainPanel.add(label1,layoutConstraints);
		
		textField = new JTextField(20);
		layoutConstraints.gridx=1;
		layoutConstraints.gridy=0;
		mainPanel.add(textField,layoutConstraints);		
		
		textField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed (KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					search(NumberValues.FORWARD_DIRECTION);
			}
		});
	}
	
	private void addRadioButtons(){
		JRadioButton defaultSearchOption = new JRadioButton (TextValues.wordSearchDefaultOption);
		layoutConstraints.gridy++;
		layoutConstraints.gridx=0;
		layoutConstraints.gridwidth=2;
		
		layoutConstraints.anchor=GridBagConstraints.WEST;
		mainPanel.add(defaultSearchOption,layoutConstraints);
		
		defaultSearchOption.setSelected(true);
		
		fullWordsSearchOption = new JRadioButton (TextValues.wordSearchOnlyFullWordsOption);	
		options.put(fullWordsSearchOption, 1);	//TODO avoid pure numbers here
				
		layoutConstraints.gridy++;
		mainPanel.add(fullWordsSearchOption,layoutConstraints);
		
		perfectMatchSearchOption = new JRadioButton (TextValues.wordSearchPerfectMatchOption);
		options.put(perfectMatchSearchOption, 2);
		layoutConstraints.gridy++;
		mainPanel.add(perfectMatchSearchOption,layoutConstraints);	
				
		ButtonGroup group = new ButtonGroup();
		group.add(fullWordsSearchOption);
		group.add(perfectMatchSearchOption);
		group.add(defaultSearchOption);	
	}
	
	private void addButtonPreviousAndNext(){
		JButton previous = new JButton (TextValues.buttonPreviousText);
		layoutConstraints.gridy++;
		layoutConstraints.anchor=GridBagConstraints.EAST;
		layoutConstraints.gridwidth=1;
		mainPanel.add(previous,layoutConstraints);
		
		JButton next = new JButton (TextValues.buttonNextText);
		layoutConstraints.anchor=GridBagConstraints.WEST;
		layoutConstraints.gridx++;
		mainPanel.add(next,layoutConstraints);
		
		next.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				search(NumberValues.FORWARD_DIRECTION);			
			}
		});
		
		previous.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				search(NumberValues.BACKWARD_DIRECTION);			
			}
		});
	}
	
	public void showInsertDialog(){
		addPromptsAndTextFields();
		addButtonsCancelAndApprove();	
		showYourself();
		
	}	
	
	private void addPromptsAndTextFields(){
		JLabel prompt = new JLabel (TextValues.wordAddDialogPrompt);
		layoutConstraints.gridx=0;
		layoutConstraints.gridy=0;
		mainPanel.add(prompt,layoutConstraints);
		
		insertWord = new JTextField(20);
		layoutConstraints.gridx++;
		mainPanel.add(insertWord,layoutConstraints);
		
		JLabel numberPrompt = new JLabel (TextValues.wordAddNumberPrompt);
		layoutConstraints.gridy++;
		layoutConstraints.gridx=0;
		
		mainPanel.add(numberPrompt,layoutConstraints);
		
		insertNumber = new JTextField(20);
		layoutConstraints.gridx++;
		
		mainPanel.add(insertNumber,layoutConstraints);		
	}
	
	private void addButtonsCancelAndApprove(){
		JButton cancel = new JButton (TextValues.buttonCancelText);
		layoutConstraints.gridx=0;
		layoutConstraints.gridy++;
		
		cancel.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				dispose();
			}
		});
		
		mainPanel.add(cancel,layoutConstraints);
		
		JButton approve = new JButton (TextValues.buttonApproveText);
		layoutConstraints.gridx++;
		
		approve.addActionListener(new ActionListener (){
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
		
		mainPanel.add(approve,layoutConstraints);
	}
			
	private boolean isNumberValid(String number){
		boolean valid = number.matches("\\d+");
		if (!valid)
			showErrorDialog(TextValues.numberFormatException);
		return valid;
	}	
	
	private boolean checkIfInputIsValid(String word, int number){					
		return (isWordIdUndefinedYet(number) && isWordUndefinedYet(word));								
	}	
	
	private boolean isWordIdUndefinedYet(int number){
		boolean undefined=list.isWordIdUndefinedYet(number);		
		if (!undefined)
			showErrorDialog(TextValues.idAlreadyDefinedException);
		return undefined;
	}
	
	private boolean isWordUndefinedYet(String word){
		boolean undefined = list.isWordUndefinedYet(word);
		if (!undefined)
			showErrorDialog(TextValues.wordAlreadyDefinedException);
		return undefined;
	}
	
	private void addWordToList(String word, int number){
		list.addWord(word,number);	
		list.scrollToBottom();
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
			showErrorDialog(e.getMessage());
		}
	}
	
	private void showErrorDialog(String message){ // TODO jak tego uniknac bo to kopia
		if (upper==null || !upper.isOpened)
			upper = new MyDialog(this);
		else return;
		
		upper.showMsgDialog(message);
		
	}
	
	
	public boolean isOpened(){
		return isOpened;
	}
}
