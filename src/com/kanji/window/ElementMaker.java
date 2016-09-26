package com.kanji.window;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import com.kanji.constants.TextValues;
import com.kanji.dialogs.MyDialog;
import com.kanji.fileReading.CustomFileReader;

public class ElementMaker {
	
	private JFileChooser fileChooser;
	private CustomFileReader fileReader;
	private List <JButton> buttons;	
	private ClassWithDialog parent;
	private Map <String, Integer> words;	
	private MyList myList;
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	
			if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown())
				searchWord();    		
            return false;
        }
    }
	
	public ElementMaker (ClassWithDialog parent){
		
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
		this.parent=parent;
		
		initElements();
		addListeners(buttons);
		
		
	}
	
	private void initElements(){
		fileReader = new CustomFileReader();
		fileChooser = new JFileChooser();				
		buttons = new ArrayList <JButton> ();		
		for (String name: TextValues.buttonNames)
			buttons.add(new JButton(name));
		initMyList();
	}
	
	private void initMyList(){
		myList = new MyList(parent);
		Map <String, Integer> initList = new LinkedHashMap <String, Integer>();
		for (int i=0; i<10; i++){
			String a = "Word no. "+i;
			if (i==3)
				a="to jest testowy text, prosze na niego " +
						"nie zwracac uwagi ma tylko sluzyc sprawdzeniu " +
						"jak to dziala i w ogole nie ma tu zadnego sensu hehe " +
						"i co mi zrobisz jak mnie nie zlapiesz, ano nie zlapeisz mnie " +
						"ale byki strzelam uuu";
			
			initList.put(a,i);
			
		}
		myList.setWords(initList);
		
	}	
	
	
	private void addListeners(List <JButton> buttons){
				
		for (JButton button: buttons){
			switch (button.getText()){
				case TextValues.buttonOpenText: 
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed (ActionEvent e){
							openFile();
						}
					});
					break;
				case TextValues.buttonAddText:
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed(ActionEvent e){
							addWord();
						}
					});
					break;
				case TextValues.buttonSearchText:
					button.addActionListener (new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							searchWord();
						}
					});	
					break;
				case TextValues.buttonStartText:
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed(ActionEvent e){
							start();
						}
					});
					break;
			}
				
		}
	}
	
	
	private void openFile(){
		int chosenOption=fileChooser.showOpenDialog(parent);
		if (chosenOption==JFileChooser.CANCEL_OPTION)
			return;
		File file = fileChooser.getSelectedFile();
		words = tryToReadWordsFromFile(file);	
		
		loadWordsListInNewThread();
		
		
	}
	
	private void loadWordsListInNewThread(){
		Runnable r = new Runnable (){
			@Override
			public void run (){
				MyDialog d = new MyDialog(parent);
				d.showMsgDialog("Wait");
				myList.setWords(words);
				d.dispose();
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	private Map <String, Integer> tryToReadWordsFromFile(File file){
		try {
			words = fileReader.readFile(file);
		} 
		catch (Exception e) {
			parent.showMessageDialog(e.getMessage());
			words = new HashMap <String, Integer> ();
		}
		return words;
	}
	
	private void addWord(){
		parent.showDialogToAddWord(myList);		
	}
	
	private void searchWord(){
		parent.showDialogToSearch(myList);
	}
	
	private void start(){
		parent.showLearnStartDialog();
	}

	public List <JButton> getButtons() {
		return buttons;
	}
		
	public MyList getMyList(){
		return myList;
	}
	
}
