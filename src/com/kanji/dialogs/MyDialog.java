package com.kanji.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.kanji.constants.TextValues;
import com.kanji.myList.MyList;
import com.kanji.window.BaseWindow;


public class MyDialog extends JDialog  {

	private static final long serialVersionUID = 7484743485658276014L; 
	private Insets insets = new Insets(10,10,0,10);
	private Color backgroundColor = Color.GREEN;
	private GridBagConstraints layoutConstraints;
	private boolean isOpened;	
	private MyDialog upper;		
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
		initialize();
		initializeLayout();
		addEscapeKeyToCloseTheWindow();		
	}
	
	
	private void initialize(){
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
		isOpened=true;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);	
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
	
	public void showLearningStartDialog (MyList list){		
		LearningStartDialog dialog = new LearningStartDialog(mainPanel,this);
		mainPanel = dialog.createDialog(list);
		showYourself(TextValues.learnStartDialogTitle);		
	}		
		
	private void showYourself(String title){ 	
		pack();
		setMinimumSize(getSize());
		setVisible(true);
		setTitle(title);	
	}
	
	public void showMsgDialog(String message){			
		MessageDialog dialog = new MessageDialog(mainPanel,this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createDialog(message);
		showYourself(TextValues.messageDialogTitle);	
		
	}	
		
	public void showSearchWordDialog (MyList list){							
		SearchWordDialog dialog = new SearchWordDialog(mainPanel,this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createDialog(list);
		showYourself(TextValues.wordSearchDialogTitle);		
	}				
	
	public void showInsertDialog(MyList list){
		InsertWordDialog dialog = new InsertWordDialog(mainPanel,this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createDialog(list);
		showYourself(TextValues.insertWordDialogTitle);		
	}	
	
	public void showErrorDialogInNewWindow(String message){ // TODO jak tego uniknac bo to kopia
		if (upper==null || !upper.isOpened){
			upper = new MyDialog(this);				
		}
		else return;
		
		upper.showMsgDialog(message);	
		upper.setLocationAtCenterOfParent(this);
		upper.pack();
		upper.setMinimumSize(upper.getSize());
			
	}	
	
	public boolean isOpened(){
		return isOpened;
	}
	
	public JButton createButtonDispose(String text){
		JButton button = new JButton (text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				dispose();
			}
		});
		return button;
	}
	
	public void setLocationAtCenterOfParent (Window parent){
		setLocationRelativeTo(parent);
	}
	
	public void setLocationAtLeftUpperCornerOfParent (Window parent){
		setLocation(parent.getLocation());
	}
	
}
