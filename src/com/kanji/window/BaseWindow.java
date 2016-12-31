package com.kanji.window;

import java.awt.CardLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.kanji.constants.Titles;
import com.kanji.fileReading.KanjiLoader;
import com.kanji.graphicInterface.ActionMaker;
import com.kanji.graphicInterface.SimpleWindow;
import com.kanji.myList.MyList;
import com.kanji.panels.InsertWordPanel;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panels.SearchWordPanel;
import com.kanji.panels.StartingPanel;
import com.kanji.range.SetOfRanges;

@SuppressWarnings("serial")
public class BaseWindow extends SimpleWindow {

	private Insets insets = new Insets(20, 20, 20, 20);
	private ElementMaker maker;

	private JPanel mainPanel;
	private RepeatingWordsPanel repeatingWordsPanel;
	private boolean areKanjiLoaded;
	private Set<Integer> problematicKanjis;
	private StartingPanel startingPanel;
//	private MyDialog dialog;
	public KanjiLoader kanjiLoader;
	public static final String LIST_PANEL = "Panel with lists and buttons";
	public static final String LEARNING_PANEL = "Panel for repeating words";
	private JFrame window;

	public BaseWindow() {
		super();
		window = new JFrame();
		problematicKanjis = new HashSet<Integer>();
		areKanjiLoaded = false;
		maker = new ElementMaker(this);
		mainPanel = new JPanel(new CardLayout());

		startingPanel = new StartingPanel(maker);
		mainPanel.add(startingPanel.getPanel(), LIST_PANEL);

		repeatingWordsPanel = new RepeatingWordsPanel(this);
		mainPanel.add(repeatingWordsPanel, LEARNING_PANEL);
		
		setProperties(mainPanel);
		window.setJMenuBar(maker.getMenu());

	}
	
	@Override
	public void setProperties(JPanel panel){
		window.setContentPane(panel);
		window.pack();
		window.setMinimumSize(window.getSize());
		window.setTitle(Titles.appTitle);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);	
		window.addWindowListener(ActionMaker.createClosingListener(this));
		isOpened = true;
	}
	
	private boolean indexIsHigherThanHalfOfSize(int i, int size) {
		return i > (size - 1) / 2;
	}

	public void showCardPanel(String cardName) {
		((CardLayout) mainPanel.getLayout()).show(mainPanel, cardName);
		mainPanel.repaint();
		mainPanel.revalidate();
	}

	public void setWordsRangeToRepeat(SetOfRanges ranges, boolean withProblematic) {

		repeatingWordsPanel.setRepeatingWords(maker.getWordsList());
		repeatingWordsPanel.setRangesToRepeat(ranges);
		repeatingWordsPanel.reset();
		System.out.println("setting: " + problematicKanjis);
		if (withProblematic)
			repeatingWordsPanel.setProblematicKanjis(problematicKanjis);

		repeatingWordsPanel.startRepeating();
	}

	public void startLoadingKanji() {

		kanjiLoader = new KanjiLoader();
		kanjiLoader.load();
		areKanjiLoaded = true;
		repeatingWordsPanel.setExcelReader(kanjiLoader);

	}

	public boolean isExcelLoaded() {
		return areKanjiLoaded;
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjiList) {
		this.problematicKanjis.addAll(problematicKanjiList);
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public void setProblematicKanjis(Set<Integer> problematicKanjis) {
		this.problematicKanjis.addAll(problematicKanjis);
	}
	
	public StartingPanel getStartingPanel (){
		return startingPanel;
	}

	public void save() {
		this.maker.save();
	}
	
	public void updateTitle(String update) {
		getWindow().setTitle(Titles.appTitle + "   " + update);
		
	}
		
	public void showDialogToAddWord(MyList list) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			InsertWordPanel dialog = new InsertWordPanel(this);
			newDialog.setProperties(dialog.createPanel(list));
			newDialog.setEscapeOnClose();
		}
	}
	
	public void showDialogToSearch(MyList list) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			SearchWordPanel dialog = new SearchWordPanel(newDialog);
			newDialog.setProperties(dialog.createPanel(list));
			newDialog.setEscapeOnClose();
		}

	}

	public void showLearnStartDialog(MyList list, int maximumNumber) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			LearningStartPanel dialog = new LearningStartPanel(mainPanel, 
					this, maximumNumber, list);		
			newDialog.setProperties(dialog.createElements());
			newDialog.setEscapeOnClose();
			
		}
	}
	
	

}
