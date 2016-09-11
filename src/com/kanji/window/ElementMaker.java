package com.kanji.window;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.kanji.fileReading.FileReaders;
import com.kanji.textValues.TextValues;

public class ElementMaker {
	private JFileChooser fileChooser;
	private FileReaders fileReader;
	private List <JButton> buttons;
	
	private ClassWithDialog parent;
	private Map <Integer,String> words;
	
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
		fileReader = new FileReaders();
		initElements();
		addListeners(buttons);
		initMyList();
		
	}
	
	private void initMyList(){
		myList = new MyList();
		for (int i=0; i<10; i++){
			String a = "aaaaaa";
			if (i==3)
				a="to jest testowy text, prosze na niego)" +
						"nie zwracac uwagi ma tylko sluzyc sprawdzeniu" +
						"jak to dziala i w ogole nie ma tu zadnego sensu hehe" +
						"i co mi zrobisz jak mnie nie zlapiesz, ano nie zlapeisz mnie" +
						"ale byki strzelam uuu";
			
			
			myList.addElement(a);
		}
		
	}
	
	private void initElements(){
		
		fileChooser = new JFileChooser();				
		buttons = new ArrayList <JButton> ();
		
		for (String name: TextValues.buttonNames)
			buttons.add(new JButton(name));
	}
	
	private void addListeners(List <JButton> buttons){
				
		for (JButton button: buttons){
			switch (button.getText()){
				case "Wczytaj listê": //TODO nie wiem jak to zrobiæ ³adnie
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed (ActionEvent e){
							openFile();
						}
					});
					break;
				case "Dodaj s³ówko":
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed(ActionEvent e){
							addWord();
						}
					});
					break;
				case "ZnajdŸ s³ówko":
					button.addActionListener (new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							searchWord();
						}
					});
					
			}
				
		}
	}
	
	
	private void openFile(){
		int opt=fileChooser.showOpenDialog(parent);
		if (opt==JFileChooser.CANCEL_OPTION)
			return;
		File file = fileChooser.getSelectedFile();

		words = fileReader.readFile(file);
		addWordsToList();
		
	}
	
	private void addWordsToList (){		
		myList.cleanAll();
		for (String word: words.values()){
			addWordToList(word);
		}
		parent.revalidate();
		parent.repaint();
	}
	
	private void addWordToList(String word){
		myList.addElement(word);		
	}
	
	private void addWord(){
		parent.showDialogToAddWord(myList);		
	}
	
	private void searchWord(){
		parent.showDialogToSearch(myList);
	}

	public List <JButton> getButtons() {
		return buttons;
	}
	
	
	public JScrollPane getMyList(){
		return myList.returnMe();
	}
	
}
