package com.kanji.window;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Map <Integer, String> words;	
	private MyList listOfWords;
	private MyList repeats;
	private JMenuBar menuBar;
	private File fileToSave;
	
	
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
		JMenuItem item = new JMenuItem("Otw�rz");
		
		item.addActionListener (new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				fileToSave = openFile();
				
				try {
					FileInputStream fout = new FileInputStream(fileToSave);
			          ObjectInputStream oos = new ObjectInputStream(fout);
			          Map<Integer, String> wordss = (Map)
			            oos.readObject();
			         listOfWords.setWords(wordss);
			          Map<Integer, String> mapOfRepeats = (Map)oos.readObject();
			         repeats.setWords(mapOfRepeats);
			          if (parent instanceof BaseWindow) {
			            try
			            {
			              Set<Integer> problematics = (Set<Integer>)oos.readObject();
			              BaseWindow p = (BaseWindow)parent;
			              p.setProblematicKanjis(problematics);
			            }
			            catch (EOFException localEOFException) {}
			          }
			          System.out.println(wordss);
			          BaseWindow b = (BaseWindow)ElementMaker.this.parent;
			          b.updateLeft();
			          fout.close();
			          System.out.println(ElementMaker.this.listOfWords);
				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		menu.add(item);
	}
	
	private void initListOfWords(){
		listOfWords = new MyList(parent,TextValues.wordsListTitle, new RowWithDeleteButton(), this);		
		
		Map <Integer, String> initList = new LinkedHashMap <Integer, String>();
		for (int i=1; i<=10; i++){
			String a = "Word no. "+i;
			
			initList.put(i,a);
			
		}
		listOfWords.setWords(initList);		
	}	
	
	private void initRepeatsList(){
		repeats = new MyList (parent,TextValues.repeatedWordsListTitle, new RowAsJLabel(), this);
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
				case TextValues.buttonSaveText:
					button.addActionListener (new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							showSaveDialog();
						}
					});	
					break;
				case TextValues.buttonSaveListText:
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed(ActionEvent e){
							exportList();
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
	
	private Map <Integer, String> tryToReadWordsFromFile(File file){
		try {
			words = fileReader.readFile(file);
		} 
		catch (Exception e) {
			parent.showMessageDialog(e.getMessage());
			words = new HashMap <Integer, String> ();
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
	
	public void save()
	  {
	    try
	    {
	      if (this.fileToSave == null) {
	        return;
	      }
	      FileOutputStream fout = new FileOutputStream(this.fileToSave);
	      ObjectOutputStream oos = new ObjectOutputStream(fout);
	      oos.writeObject(this.listOfWords.getWordsWithIds());
	      oos.writeObject(this.repeats.getWordsWithIds());
	      if ((this.parent instanceof BaseWindow))
	      {
	        BaseWindow b = (BaseWindow)this.parent;
	        oos.writeObject(b.getProblematicKanjis());
	      }
	      System.out.println("saved");
	      fout.close();
	    }
	    catch (IOException e1)
	    {
	      e1.printStackTrace();
	    }
	  }
	  
	  private void exportList()
	  {
	    File f = new File("list.txt");
	    try
	    {
	      Map<Integer, String> words = this.listOfWords.getWordsWithIds();
	      BufferedWriter p = new BufferedWriter(new OutputStreamWriter(
	        new FileOutputStream(f), "UTF8"));
	      for (Iterator localIterator = words.keySet().iterator(); localIterator.hasNext();)
	      {
	        int i = ((Integer)localIterator.next()).intValue();
	        p.write((String)words.get(Integer.valueOf(i)) + " " + i);
	        p.newLine();
	      }
	      p.close();
	    }
	    catch (IOException e)
	    {
	      e.printStackTrace();
	    }
	  }
	  
	  private void showSaveDialog()
	  {
	    JFileChooser j = new JFileChooser();
	    int option = j.showSaveDialog(this.parent);
	    if (option == 0) {
	      this.fileToSave = j.getSelectedFile();
	    }
	    save();
	  }
	  
	  
}
