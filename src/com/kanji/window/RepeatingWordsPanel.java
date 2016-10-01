package com.kanji.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.kanji.constants.TextValues;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class RepeatingWordsPanel extends JPanel{
	private MyList words;
	private List <String> wordsToRepeat; //TODO add to repeating list information about whether specified row
										// contains words that were learned completely or aborted/paused
	private ExcelReader excel;
	private BaseWindow parent;
	
	private JLabel time;
	private double timeElapsed;
	private double interval = 0.1;
	private String timeLabel = "Czas: ";
	private Thread timerThread;
	private boolean timerRunning;
	
	private JTextArea kanjiTextArea;
	private JTextArea wordTextArea;
	private JButton pauseOrResume;
	private JButton showWord;
	private JButton recognizedWord;
	private JButton notRecognizedWord;
	private JPanel repeatingPanel;
	
	private final static String PAUSE_TEXT = "Pauza";
	private final static String RESUME_TEXT = "Wznow";	
	private final static String RECOGNIZED_WORD_TEXT = "Znam";
	private final static String NOT_RECOGNIZED_WORD_TEXT = "Nie pamietam";
	
	private final Color repeatingBackgroundColor = Color.white;
	private final Color windowBackgroundColor = Color.GREEN;
	private final int kanjiFontSize = 100;
	
	public RepeatingWordsPanel (BaseWindow parent){
		wordsToRepeat = new LinkedList <String> ();		
		this.parent=parent;		
		timerRunning = false;
		initialize();
		createPanel();		
	}
	
	private void initialize(){
		setLayout(new GridBagLayout());
		setBackground(windowBackgroundColor);
		repeatingPanel = new JPanel (new GridBagLayout());
		repeatingPanel.setBackground(repeatingBackgroundColor);
	}
	
	private void createPanel(){
		
		int level=0;
		addTitleAndTime("Repeating words",level);
		if (!wordsToRepeat.isEmpty()){
			level++;
			initiateRepeatingPanel(level);
		}
		level++;
		addButtons(level);
		
	}
	
	private void addTitleAndTime(String title, int level){
				
		GridBagConstraints c = createDefaultConstraints();		
		c.gridx=0;
		c.gridy = level;		
		c.anchor=GridBagConstraints.CENTER;		
		add (new JLabel(title),c);
		
		c.weightx = 0;
		c.anchor = GridBagConstraints.EAST;		
		time = new JLabel (timeLabel);
		add (time,c);	
	
	}	
	
	private GridBagConstraints createDefaultConstraints(){
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1;
		int a = 5;
		c.insets = new Insets(a,a,a,a);
		return c;
	}	
		
	private void initiateRepeatingPanel (int level){
		
		createElementsForRepeatingPanel();		
		setButtonsToLearningAndAddThem();
		
		GridBagConstraints d = createDefaultConstraints();
		d.gridy=level;
		d.anchor=GridBagConstraints.CENTER;		
		d.weighty=1;
		d.weightx=0;
		
		add(repeatingPanel,d);
		
	}
	
	private void setButtonsToLearningAndAddThem(){
		addElementsToRepeatingPanel(showWordButtons());
	}
	
	private JButton [] showWordButtons(){
		return new JButton [] {pauseOrResume, showWord};
	}	
			
	private void createElementsForRepeatingPanel (){
		createWordLabel();
		createWordArea();
		createShowWordButton();
		createPauseOrResumeButton();	
		createRecognizedWordButton();
		createNotRecognizedWordButton();
	}
	
	private void createWordLabel(){
		wordTextArea = new JTextArea(10,10);
		wordTextArea.setEditable(false);
		wordTextArea.setLineWrap(true);
		wordTextArea.setWrapStyleWord(true);
		wordTextArea.setText(pickRandomWord());
		
	}
	
	private void createWordArea(){
		Font f = new Font(excel.getFontName(), Font.BOLD, kanjiFontSize);
		
		kanjiTextArea = new JTextArea(10,10);
		kanjiTextArea.setFont(f);
		kanjiTextArea.setEditable(false);
		kanjiTextArea.setLineWrap(true);
		kanjiTextArea.setWrapStyleWord(true);		
	}
	
	private String pickRandomWord (){
		Random randomizer = new Random();
		int index = randomizer.nextInt(wordsToRepeat.size());
		System.out.println(wordsToRepeat.get(index));
		return wordsToRepeat.get(index);
	}
	
	private void createShowWordButton (){
		showWord = new JButton ("Poka¿ kanji.");	
		showWord.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){				
				setButtonsToRecognizeWord();
				showKanji();
			}
		});
	}
	
	private void setButtonsToRecognizeWord(){
		addElementsToRepeatingPanel(recognizeWordButtons());
	}
	
	private JButton [] recognizeWordButtons(){
		return new JButton [] {pauseOrResume, recognizedWord, notRecognizedWord};
	}
	
	private void showKanji(){
		kanjiTextArea.setText(excel.getKanjiById(words.getWordsWithIds().get(wordTextArea.getText())));		
	}
	
	private void createPauseOrResumeButton(){
		pauseOrResume = new JButton (PAUSE_TEXT);
		pauseOrResume.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				pauseOrResume();
			}
		});
	}
	
	private void pauseOrResume(){
		if (pauseOrResume.getText().equals(PAUSE_TEXT)){
			stopTimer();
			pauseOrResume.setText(RESUME_TEXT);
		}
		else{
			startTimer();
			pauseOrResume.setText(PAUSE_TEXT);
		}
			
	}
	
	private void createRecognizedWordButton(){
		recognizedWord = new JButton (RECOGNIZED_WORD_TEXT);
		recognizedWord.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				getNextWord();
			}
		});
	}
	
	private void createNotRecognizedWordButton(){
		notRecognizedWord = new JButton (NOT_RECOGNIZED_WORD_TEXT);
		notRecognizedWord.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				getNextWord();
			}
		});
	}
	
	private void getNextWord(){
		
		String word = this.wordTextArea.getText().toString();
		wordsToRepeat.remove(word);
		System.out.println("removed: "+word);
		
		if (!wordsToRepeat.isEmpty()){
			setButtonsToLearningAndAddThem();
			this.wordTextArea.setText(pickRandomWord());	
			this.kanjiTextArea.setText("");
		}
		else {
			parent.showMessageDialog(TextValues.learningFinished);			
			stopTimer();
			parent.showCardPanel(BaseWindow.LIST_PANEL);
		}
		
	}
	
	private void addElementsToRepeatingPanel(JButton [] buttons){				
		
		repeatingPanel.removeAll();
		
		GridBagConstraints c = createDefaultConstraints();		
		c.gridx=0;
		c.gridy=0;
		c.gridwidth = buttons.length;
		c.anchor=GridBagConstraints.CENTER;	
		c.fill = GridBagConstraints.HORIZONTAL;
		repeatingPanel.add (wordTextArea,c);
		
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		repeatingPanel.add(kanjiTextArea, c);
		
		c.gridwidth = 1;
		c.gridy++;
		
		for (JButton button: buttons){
			repeatingPanel.add(button,c);
			c.gridx++;
		}	
		repaint();
		
	}
	
	
	private void addButtons(int level){
		JButton returnButton = new JButton ("Powrot");
		returnButton.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				parent.showCardPanel(BaseWindow.LIST_PANEL);
				stopTimer();
			}
		});		
		
		GridBagConstraints c = createDefaultConstraints();
		c.gridy=level;
		c.anchor= GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		
		add(returnButton,c);
		
	}
	
	public void setRepeatingWords(MyList wordsList){
		wordsToRepeat = new LinkedList <String> ();
		words = wordsList;	
	}
	
	public void setRangesToRepeat(SetOfRanges ranges){
		for (Range range: ranges.getRangesAsList()){
			for (int i=range.getRangeStart(); i<=range.getRangeEnd(); i++){
				wordsToRepeat.add(words.findWordInRow(i-1));
			}
		}
		
		
	}
	
	public void startRepeating(){
		removeAll();
		createPanel();
		revalidate();
		repaint();
		resetTimer();
		startTimer();
	}
	
	private void resetTimer(){
		timeElapsed = 0;
	}
	
	private void startTimer(){
		
		timerRunning = true;
		Runnable runnable = new Runnable (){
			@Override
			public void run (){
				while (timerRunning){
					timeElapsed+=interval;
					time.setText(timeLabel+String.format("%.2f",timeElapsed));
					
					try {
						Thread.sleep((long)(interval*1000));
					} 
					catch (InterruptedException e) {
						parent.showMessageDialog(e.getMessage());						
					}
				}
				
			}
		};
		timerThread = new Thread (runnable);
		timerThread.start();
	}
	
	private void stopTimer (){
		timerRunning = false;
	}
	
	public void setExcelReader (ExcelReader excel){
		this.excel = excel;
	}
	

}
