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

import com.kanji.textValues.TextValues;


public class MyDialog extends JDialog  {

	private static final long serialVersionUID = 7484743485658276014L; 
	private Insets insets = new Insets(10,10,0,10);
	private Map<JRadioButton, Integer> options;
	
	private JRadioButton searchMany;
	private JRadioButton caseSensitive;
	private JTextField textField;
	private GridBagConstraints c;
	private boolean isOpened;
	
	private MyList list;
	private int invalidNumber=-1;
	
	MyDialog upper;
	private JTextField insertWord;
	private JTextField insertNumber;
	
	private JPanel p;
	
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
				dispose();
			}
				
			  		
            return false;
        }
    }
	
	public MyDialog (Window b){
		super(b);
		options = new HashMap <JRadioButton, Integer> ();
		isOpened=true;
		if (b instanceof BaseWindow){
			setLocation(b.getLocation());
		}
		else {
			setLocationRelativeTo(b);		
			System.out.println(b);
		}
		initialize();
		
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				isOpened=false;
			}
		});
	}
	
	public MyDialog (Window b,MyList myList){		
		this(b);
		list=myList;		
	}
		
	private void initialize(){		
				
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
						
		
		p = new JPanel();
		p.setBackground(Color.green);		
		GridBagLayout g = new GridBagLayout();
		p.setLayout(g);			
						
		setContentPane(p);
		c = new GridBagConstraints();
		c.insets=insets;		
				
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);		
		setTitle(TextValues.wordSearchDialogTitle);		
			
	}
	
	public void showMsgDialog(String message){
		
		JLabel label1 = new JLabel (message);
		p.add(label1,c);
		c.gridy++;
		
		JButton button = new JButton (TextValues.buttonApproveText);
		p.add(button,c);
		
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				dispose();
			}
		});
		
		showYourself();
	}
	
	
	public void showSearchWordDialog (){
							
		JLabel label1 = new JLabel (TextValues.wordAddDialogPrompt);
		p.add(label1,c);
		
		textField = new JTextField(20);
		c.gridx=1;
		c.gridy=0;
		p.add(textField,c);
		
		
		JRadioButton defaultb = new JRadioButton (TextValues.wordSearchDefaultOption);
		c.gridy++;
		c.gridx=0;
		c.gridwidth=2;
		
		c.anchor=GridBagConstraints.WEST;
		p.add(defaultb,c);
		
		defaultb.setSelected(true);
		
		searchMany = new JRadioButton (TextValues.wordSearchOnlyFullWordsOption);	
		options.put(searchMany, 1);		
				
		c.gridy++;
		p.add(searchMany,c);
		
		caseSensitive = new JRadioButton (TextValues.wordSearchPerfectMatchOption);
		options.put(caseSensitive, 2);
		c.gridy++;
		p.add(caseSensitive,c);
		
		
		
		ButtonGroup group = new ButtonGroup();
		group.add(searchMany);
		group.add(caseSensitive);
		group.add(defaultb);
		

		
		
		JButton previous = new JButton (TextValues.buttonPreviousText);
		c.gridy++;
		c.anchor=GridBagConstraints.EAST;
		c.gridwidth=1;
		p.add(previous,c);
		
		JButton next = new JButton (TextValues.buttonNextText);
		c.anchor=GridBagConstraints.WEST;
		c.gridx++;
		p.add(next,c);
		
		textField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed (KeyEvent e){
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					search(1);
			}
		});
		
		next.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				search(1);			
			}
		});
		
		previous.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				search(-1);			
			}
		});
		
		showYourself();
		
	}
	
	public void showInsertDialog(){
		JLabel prompt = new JLabel (TextValues.wordAddDialogPrompt);
		c.gridx=0;
		c.gridy=0;
		p.add(prompt,c);
		
		insertWord = new JTextField(20);
		c.gridx++;
		p.add(insertWord,c);
		
		JLabel numberPrompt = new JLabel (TextValues.wordAddNumberPrompt);
		c.gridy++;
		c.gridx=0;
		
		p.add(numberPrompt,c);
		
		insertNumber = new JTextField(20);
		c.gridx++;
		
		p.add(insertNumber,c);
		
		
		
		JButton cancel = new JButton (TextValues.buttonCancelText);
		c.gridx=0;
		c.gridy++;
		
		cancel.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				dispose();
			}
		});
		
		p.add(cancel,c);
		
		JButton approve = new JButton (TextValues.buttonApproveText);
		c.gridx++;
		
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
		
		p.add(approve,c);
		showYourself();
		
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
		
	}
	
	private void showYourself(){
		setVisible(true);
		pack();
	}				
	
	private void search (int direction){
		Set <Integer> set = new HashSet <Integer> ();
	
		for (JRadioButton checkbox: options.keySet()){
			if (checkbox.isSelected())
				set.add(options.get(checkbox));
		}
	
		try {
			list.search(textField.getText(),direction, set);
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
