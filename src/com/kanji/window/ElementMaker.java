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
import javax.swing.SwingUtilities;

import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Titles;
import com.kanji.constants.MenuTexts;
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
	private SavingStatus savingStatus;
	
	
	private class MyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	
			if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown())
				searchWord();    		
            return false;
        }
    }
	
	public ElementMaker (ClassWithDialog parent){
		
		savingStatus = SavingStatus.BrakZmian;
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
		this.parent=parent;
		
		initElements();
		addListeners(buttons);
		
		
	}
	
	private void initElements(){
		fileReader = new CustomFileReader();			
		buttons = new ArrayList <JButton> ();		
		for (String name: ButtonsNames.buttonNames)
			buttons.add(new JButton(name));
		initListOfWords();
		initRepeatsList();
		createMenu();
	}
	
	private void createMenu(){
		menuBar = new JMenuBar();
		menuBar.setBackground(Color.orange);
		JMenu menu = new JMenu(MenuTexts.menuBarFile);
		menuBar.add(menu);
		JMenuItem item = new JMenuItem(MenuTexts.menuOpen);
		
		item.addActionListener (new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				fileToSave = openFile();
				if (!fileToSave.exists())
					return;
				
				try {
					
					FileInputStream fout = new FileInputStream(fileToSave);
			        ObjectInputStream oos = new ObjectInputStream(fout);
			        final Map<Integer, String> wordss = (Map) oos.readObject();
			        Runnable r = new Runnable (){
			        	@Override
			        	public void run (){
			        		listOfWords.setWords(wordss);
			        	}
			        };
			         Thread t = new Thread(r);
			         t.start();
			        final Map<Integer, String> mapOfRepeats = (Map)oos.readObject();
			        
			        Runnable r2 = new Runnable (){
			        	@Override
			        	public void run (){
			        		repeats.setWords(mapOfRepeats);
			        	}
			        };
			        Thread t2 = new Thread (r2);
			        t2.start();
			        
			       		if (parent instanceof BaseWindow) {
			       			try	{
			       				Set<Integer> problematics = (Set<Integer>)oos.readObject();
			       				BaseWindow p = (BaseWindow)parent;
			       				p.setProblematicKanjis(problematics);
			       			}
			       			catch (EOFException localEOFException) {}
			       		}
			        BaseWindow b = (BaseWindow)parent;
					b.showMessageDialog("loading");
			        b.updateTitle(fileToSave.toString());
			        b.updateLeft();
			          
			        fout.close();
				}
				catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		menu.add(item);
	}
	
	private void initListOfWords(){
		listOfWords = new MyList(parent,Titles.wordsListTitle, new RowWithDeleteButton(), this);		
		
		Map <Integer, String> initList = new LinkedHashMap <Integer, String>();
		for (int i=1; i<=10; i++){
			String a = "Word no. "+i;
			
			initList.put(i,a);
			
		}
		listOfWords.setWords(initList);		
	}	
	
	private void initRepeatsList(){
		repeats = new MyList (parent,Titles.repeatedWordsListTitle, new RowAsJLabel(), this);
	}
		
	private void addListeners(List <JButton> buttons){
				
		for (JButton button: buttons){
			switch (button.getText()){
				case ButtonsNames.buttonOpenText: 
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed (ActionEvent e){
							loadWordsListFromFile();
						}
					});
					break;
				case ButtonsNames.buttonAddText:
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed(ActionEvent e){
							addWord();
						}
					});
					break;
				case ButtonsNames.buttonSearchText:
					button.addActionListener (new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							searchWord();
						}
					});	
					break;
				case ButtonsNames.buttonStartText:
					button.addActionListener(new ActionListener (){
						@Override
						public void actionPerformed(ActionEvent e){
							start();
						}
					});
					break;
				case ButtonsNames.buttonSaveText:
					button.addActionListener (new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e){
							showSaveDialog();
						}
					});	
					break;
				case ButtonsNames.buttonSaveListText:
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
		if (!file.exists())
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
		BaseWindow p = null;
		if (parent instanceof BaseWindow){
			p = (BaseWindow) parent;
			p.changeSaveStatus(SavingStatus.Zapisywanie);
		}
		else return;
	    try
	    {
	      if (this.fileToSave == null) {
	    	  System.out.println("no save");
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
	    p.changeSaveStatus(SavingStatus.Zapisano);
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
