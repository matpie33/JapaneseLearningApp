package com.kanji.window;

import java.awt.Color;
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
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;

public class RepeatingWordsPanel extends JPanel{
	private MyList words;
	private List <String> wordsToRepeat; //TODO add to repeating list information about whether specified row
										// contains words that were learned completely or aborted/paused
	private BaseWindow parent;
	private JLabel time;
	private double timeElapsed;
	private double interval = 0.1;
	private String timeLabel = "Czas: ";
	private Thread timerThread;
	private boolean timerRunning;
	private JButton pauseOrResume;
	private final static String PAUSE_TEXT = "Pauza";
	private final static String RESUME_TEXT = "Wznow";
	
	public RepeatingWordsPanel (BaseWindow parent){
		wordsToRepeat = new LinkedList <String> ();
		setLayout(new GridBagLayout());
		this.parent=parent;		
		timerRunning = false;
		createPanel();		
	}
	
	private void createPanel(){
		int level=0;
		addTitle("Repeating words",level);
		if (!wordsToRepeat.isEmpty()){
			level++;
			addRepeatingPanel(level);
		}
		level++;
		addButtons(level);
	}
	
	private void addTitle(String title, int level){
		GridBagConstraints c = createDefaultConstraints();
		c.gridy=level;
		c.anchor=GridBagConstraints.CENTER;		
		add (new JLabel(title),c);
		
		c.gridx++;
		c.anchor = GridBagConstraints.EAST;		
		time = new JLabel (timeLabel);
		add (time,c);						
	}
	
	
	private void addRepeatingPanel(int level){
		
		JPanel panel = new JPanel (new GridBagLayout());
		pauseOrResume = new JButton (PAUSE_TEXT);
		JButton showWord = new JButton ("Poka¿ kanji.");		
		
		JButton [] buttons = new JButton [] {pauseOrResume,  showWord};
		
		GridBagConstraints c = createDefaultConstraints();		
		c.gridx=0;
		c.gridy=0;
		c.gridwidth = buttons.length;
		c.anchor=GridBagConstraints.CENTER;
		
		JTextArea wordArea = new JTextArea(pickRandomWord());
		wordArea.setLineWrap(true);
		wordArea.setWrapStyleWord(true);
		wordArea.setEditable(false);
		wordArea.setOpaque(false);		
		
		createShowWordListener(showWord, wordArea);
		createPauseOrResumeListener();						
		
		panel.add(wordArea,c);
		
		c.gridwidth = 1;
		c.gridy++;
		
		for (JButton button: buttons){
			panel.add(button,c);
			c.gridx++;
		}		
				
		GridBagConstraints d = new GridBagConstraints();
		d.gridy=level;
		d.anchor=GridBagConstraints.CENTER;
		d.weighty=1;
		
		add(panel,d);
		
	}
	
	private GridBagConstraints createDefaultConstraints(){
		GridBagConstraints c = new GridBagConstraints();
		c.weightx=1;
		int a = 5;
		c.insets = new Insets(a,a,a,a);
		return c;
	}
	
	private void createShowWordListener (JButton showWord, final JTextArea wordArea){
		showWord.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){				
				String word = wordArea.getText();
				wordsToRepeat.remove(word);
				
				if (!wordsToRepeat.isEmpty())
					wordArea.setText(pickRandomWord());				
				else {
					parent.showMessageDialog(TextValues.learningFinished);
					wordArea.setText(TextValues.learningFinished);
				}
				
			}
		});
	}
	
	private void createPauseOrResumeListener(){
		pauseOrResume.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				pauseOrResume();
			}
		});
	}
	
	private String pickRandomWord (){
		Random randomizer = new Random();
		int index = randomizer.nextInt(wordsToRepeat.size());
		System.out.println(wordsToRepeat.get(index));
		return wordsToRepeat.get(index);
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
		removeAll();
		createPanel();
		resetTimer();
		startTimer();
		System.out.println(wordsToRepeat);
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
	

}
