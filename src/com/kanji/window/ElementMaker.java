package com.kanji.window;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.kanji.constants.TextValues;
import com.kanji.dialogs.MyDialog;
import com.kanji.fileReading.CustomFileReader;
import com.kanji.myList.MyList;
import com.kanji.myList.RowAsJLabel;
import com.kanji.myList.RowWithDeleteButton;

public class ElementMaker {
	
	private CustomFileReader fileReader;
	private List <JButton> buttons;	
	private ClassWithDialog parent;
	private Map <String, Integer> words;	
	private MyList listOfWords;
	private MyList repeats;
	private JMenuBar menuBar;
	
	
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
		buttons = new ArrayList <JButton> ();		
		for (String name: TextValues.buttonNames)
			buttons.add(new JButton(name));
		initListOfWords();
		initRepeatsList();
		createMenu();
	}
	
	private void createMenu(){
		menuBar = new JMenuBar();
		menuBar.setBackground(Color.orange);
		JMenu menu = new JMenu("Plik");
		menuBar.add(menu);
		JMenuItem item = new JMenuItem("Otwórz");
		
		item.addActionListener (new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				File f = openFile();
				
				try {
					FileInputStream fout = new FileInputStream (f);
					ObjectInputStream oos = new ObjectInputStream(fout);
					listOfWords= (MyList)oos.readObject();
					System.out.println(listOfWords.getRowCreator());
					BaseWindow b = (BaseWindow)parent;
					b.updateLeft();
					fout.close();
					System.out.println(listOfWords);
				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		menu.add(item);
	}
	
	private void initListOfWords(){
		listOfWords = new MyList(parent,TextValues.wordsListTitle, new RowWithDeleteButton());		
		
		Map <String, Integer> initList = new LinkedHashMap <String, Integer>();
		for (int i=1; i<=10; i++){
			String a = "Word no. "+i;
			
			initList.put(a,i);
			
		}
		listOfWords.setWords(initList);		
	}	
	
	private void initRepeatsList(){
		repeats = new MyList (parent,TextValues.repeatedWordsListTitle, new RowAsJLabel());
	}
		
	private void addListeners(List <JButton> buttons){
				
		for (JButton button: buttons){
			switch (button.getText()){
				case TextValues.buttonOpenText: 
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed (ActionEvent e){
							loadWordsListFromFile();
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
	
	
	private void loadWordsListFromFile(){		
		File file = openFile();
		if (file == null)
			return;		
		words = tryToReadWordsFromFile(file);			
		loadWordsListInNewThread();		
	}
	
	private File openFile(){
		JFileChooser fileChooser = new JFileChooser();
		int chosenOption=fileChooser.showOpenDialog(parent);
		if (chosenOption==JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		return file;
	}
	
	private void loadWordsListInNewThread(){
		Runnable r = new Runnable (){
			@Override
			public void run (){
				MyDialog d = new MyDialog(parent);
				d.showErrorDialogInNewWindow("Wait");
				listOfWords.setWords(words);
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
		parent.showDialogToAddWord(listOfWords);		
	}
	
	private void searchWord(){
		parent.showDialogToSearch(listOfWords);
	}
	
	private void start(){
		parent.showLearnStartDialog(repeats, listOfWords.getWordsCount());
		System.out.println(listOfWords.getWordsCount());
	}

	public List <JButton> getButtons() {
		return buttons;
	}
		
	public MyList getWordsList(){
		return listOfWords;
	}
	
	public MyList getRepeatsList(){
		return repeats;
	}
	
	public JMenuBar getMenu(){
		return menuBar;
	}
	
}
