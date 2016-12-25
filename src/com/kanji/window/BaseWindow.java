package com.kanji.window;

import java.awt.CardLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import com.kanji.constants.Titles;
import com.kanji.fileReading.ExcelReader;
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
	private boolean isExcelReaderLoaded;
	private Set<Integer> problematicKanjis;
	private StartingPanel startingPanel;
//	private MyDialog dialog;
	public ExcelReader excel;
	public static final String LIST_PANEL = "Panel with lists and buttons";
	public static final String LEARNING_PANEL = "Panel for repeating words";

	public BaseWindow() {

		problematicKanjis = new HashSet<Integer>();
		isExcelReaderLoaded = false;
		maker = new ElementMaker(this);
		mainPanel = new JPanel(new CardLayout());
//		setContentPane(mainPanel);

		startingPanel = new StartingPanel(maker);
		mainPanel.add(startingPanel.getPanel(), LIST_PANEL);

		repeatingWordsPanel = new RepeatingWordsPanel(this);
		mainPanel.add(repeatingWordsPanel, LEARNING_PANEL);
		SimpleWindow window = new SimpleWindow();
		window.setProperties(mainPanel);
		window.setMenuBar(maker.getMenu());

//		setWindowProperties();

	}

	

	private boolean indexIsHigherThanHalfOfSize(int i, int size) {
		return i > (size - 1) / 2;
	}


//	private void setWindowProperties() {
//		pack();
//		setMinimumSize(getSize());
//		setTitle(Titles.appTitle);
//		setLocationRelativeTo(null);
//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//	}

	public void showCardPanel(String cardName) {
		((CardLayout) mainPanel.getLayout()).show(mainPanel, cardName);
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

	public void loadExcelReader() {

		excel = new ExcelReader();
		excel.load();
		isExcelReaderLoaded = true;
		repeatingWordsPanel.setExcelReader(excel);

	}

	public boolean isExcelLoaded() {
		return isExcelReaderLoaded;
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjiList) {
		this.problematicKanjis.addAll(problematicKanjiList);

		System.out.println(this.problematicKanjis);
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
	

//	public boolean isDialogOpened() {
//		return dialog.isOpened();
//	}
	
	public void showDialogToAddWord(MyList list) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			InsertWordPanel dialog = new InsertWordPanel( this);
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
					this, maximumNumber);		
			newDialog.setProperties(dialog.createPanel(list));
			
		}
	}

}
