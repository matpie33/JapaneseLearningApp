package com.kanji.window;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JScrollPane;
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
	private JScrollPane scrollPane;
	private JTextField textField;
	private GridBagConstraints layoutConstraints;
	private boolean isOpened;	
	private MyList list;	
	private MyDialog upper;
	private JTextField insertWord;
	private JTextField insertNumber;	
	private JPanel mainPanel;	
	private int rowsNumber;
	
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
        rowsNumber=0;
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
		initializeLayoutConstraints();
			
	}
	
	private void initializeLayoutConstraints(){
		layoutConstraints = new GridBagConstraints();
		layoutConstraints.insets=insets;
		layoutConstraints.anchor=GridBagConstraints.WEST;
	}
	
	private void addEscapeKeyToCloseTheWindow (){
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				isOpened=false;
			}
		});
	}
	
	public void showLearningStartDialog (){
		
		
		int level = 0;
		addPromptAtLevel(level, TextValues.learnStartPrompt);
				
		level++;
		JPanel panel = addTextFieldsForRange(level);
		scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setPreferredSize(new Dimension(500,100));
		mainPanel.add(scrollPane,layoutConstraints);
		
		level++;
		JButton newRow = createButtonAddRow(TextValues.buttonAddRowText, panel);
		addButtonsAtLevel(level, new JButton []{newRow});
		
		level++;
		JButton cancel = createButtonDispose(TextValues.buttonCancelText);
		JButton approve = new JButton (TextValues.buttonApproveText); // TODO change
		
		addButtonsAtLevel(level, new JButton []{cancel,approve});
		
		showYourself();
		
	}
		
	
	private void addPromptAtLevel(int level, String message){
		layoutConstraints.gridy=level;
		JLabel label = new JLabel (message);
		mainPanel.add(label,layoutConstraints);
	}
	
	private JPanel addTextFieldsForRange(int level){
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		addRowToPanel(panel);
		
		
		layoutConstraints.gridy=level;
		layoutConstraints.anchor=GridBagConstraints.WEST;				
//		mainPanel.add(panel,layoutConstraints);
		return panel;
		
	}
	
	private void addRowToPanel (final JPanel panel){
		
		final JPanel rowPanel = new JPanel();
		
		JLabel from = new JLabel ("od");
		JTextField fieldFrom = new JTextField(10);
		JLabel labelTo = new JLabel ("do");
		JTextField fieldTo = new JTextField(10);
		int a=5;
		Insets insets = new Insets(a,a,a,a);
		
		rowPanel.add(from);
		rowPanel.add(fieldFrom);
		rowPanel.add(labelTo);
		rowPanel.add(fieldTo);	
				
		if (rowsNumber>0){			
			JButton delete = new JButton (TextValues.buttonRemoveRowText);
			delete.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					deleteRow(rowPanel,panel);
				}
			});
			rowPanel.add(delete);
		}
		if (rowsNumber==1){
			final JPanel firstRow = (JPanel)panel.getComponent(0);
			JButton delete = new JButton (TextValues.buttonRemoveRowText);
			delete.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					deleteRow(firstRow,panel);
				}
			});
			firstRow.add(delete);
		}
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets=insets;
		c.gridx=0;
		c.gridy=rowsNumber;
		
		panel.add(rowPanel,c);
		rowsNumber++;
	}
	
	private void deleteRow(JPanel rowToDelete, JPanel panel){
		
		removeRowAndUpdateOtherRows(rowToDelete, panel);
		if (panel.getComponentCount()==1){
			JPanel firstRow = (JPanel) panel.getComponent(0);
			for (Component c: firstRow.getComponents()){
				if (c instanceof JButton)
					firstRow.remove(c);
					
			}
			
		}
		panel.repaint();
		panel.revalidate();
		rowsNumber--;
		
	}
	
	private void removeRowAndUpdateOtherRows(JPanel rowToDelete, JPanel panel){
		boolean found=false;
		for (int i=0; i< panel.getComponentCount(); i++){ 
			if (panel.getComponent(i)==rowToDelete){
				panel.remove(i);
				found=true;
				i--;
				continue;
			}
			if (!found) continue;
			
			JPanel row = (JPanel)panel.getComponent(i);
			GridBagLayout g = (GridBagLayout)panel.getLayout();
			GridBagConstraints c = g.getConstraints(row);
			c.gridy--;
			g.setConstraints(row, c);
		}
	}
	
	private JButton createButtonAddRow (String text, final JPanel panel){
		JButton button = new JButton (text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){				
				addRowToPanel(panel);
				scrollPane.repaint();
				scrollPane.revalidate();
				revalidate();			
				scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
			}
		});
		return button;
	}
	
	private JButton createButtonDispose(String text){
		JButton button = new JButton (text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				dispose();
			}
		});
		return button;
	}
	
	private void addButtonsAtLevel(int level, JButton [] buttons){
		JPanel panel = new JPanel ();
		for (JButton button: buttons)
			panel.add(button);
		
		layoutConstraints.gridy=level;
		mainPanel.add(panel,layoutConstraints);
	}
	
	private void showYourself(){
		setVisible(true);
		pack();
	}
	
	public void showMsgDialog(String message){
		
		int level = 0;
		addPromptAtLevel(level,message);
		
		JButton button = createButtonDispose(TextValues.buttonApproveText);
		mainPanel.add(button,layoutConstraints);		
				
		showYourself();
	}	
		
	public void showSearchWordDialog (){
							
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
		showYourself();
		
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
	
	public void showInsertDialog(){
		int level = 0;
		insertWord = addPromptAndTextFieldAndReturnTextField(level,TextValues.wordAddDialogPrompt);
		
		level++;
		insertNumber = addPromptAndTextFieldAndReturnTextField(level, TextValues.wordAddNumberPrompt);
		
		level++;
		JButton cancel = createButtonDispose(TextValues.buttonCancelText);
		JButton approve = createButtonValidate(TextValues.buttonApproveText);
		addButtonsAtLevel(level, new JButton [] {cancel, approve});	
		showYourself();
		
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
			e.printStackTrace();
			showErrorDialog(e.getMessage()); //TODO to nie zawsze dobry pomys³
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
