package com.kanji.windows;

import java.awt.CardLayout;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.constants.Titles;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.panels.InsertWordPanel;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panels.LoadingPanel;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.panels.RepeatingWordsPanel;
import com.kanji.panels.SearchWordPanel;
import com.kanji.panels.StartingPanel;
import com.kanji.range.SetOfRanges;
import com.kanji.utilities.ElementMaker;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private ElementMaker maker;
	private JPanel mainApplicationPanel;
	private RepeatingWordsPanel repeatingWordsPanel;
	private Set<Integer> problematicKanjis;
	private JSplitPane listsSplitPane;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private StartingPanel startingPanel;
	private JFrame container;

	// TODO handle the situation in gui maker when the panel has just 1 row so
	// we
	// don't have to use row (0) but somehow easier

	public ExcelReader excel;

	public static final String LIST_PANEL = "Panel with lists and buttons";
	public static final String LEARNING_PANEL = "Panel for repeating words";

	public ApplicationWindow() {
		super(null);
		container = new JFrame();
		// TODO searching is case sensitive, should not be
		problematicKanjis = new HashSet<Integer>();
		maker = new ElementMaker(this);
		mainApplicationPanel = new JPanel(new CardLayout());

		startingPanel = new StartingPanel(this, maker);
		mainApplicationPanel.add(startingPanel.createPanel(), LIST_PANEL);

		repeatingWordsPanel = new RepeatingWordsPanel(this);
		mainApplicationPanel.add(repeatingWordsPanel.createPanel(), LEARNING_PANEL);

		setWindowProperties();
		startingPanel.addHotkeys();
	}

	private void setWindowProperties() {
		container = new JFrame();
		container.setJMenuBar(maker.getMenu());
		container.setContentPane(mainApplicationPanel);
		container.pack();
		container.setMinimumSize(container.getSize());
		container.setTitle(Titles.app);
		container.setLocationRelativeTo(null);
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		container.setVisible(true);
	}

	public void showCardPanel(String cardName) {
		((CardLayout) mainApplicationPanel.getLayout()).show(mainApplicationPanel, cardName);
	}

	public void setWordsRangeToRepeat(SetOfRanges ranges, boolean withProblematic) {

		repeatingWordsPanel.setRepeatingWords(maker.getWordsList());
		// TODO if set of ranges is empty, we should not call set ranges to
		// repeat all, so probably
		// split this method
		repeatingWordsPanel.setRangesToRepeat(ranges);
		repeatingWordsPanel.reset();
		System.out.println("setting: " + problematicKanjis);
		if (withProblematic)
			repeatingWordsPanel.setProblematicKanjis(problematicKanjis);

		repeatingWordsPanel.startRepeating();
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsPanel.setRepeatingInformation(info);
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

	public void save() {
		this.maker.save();
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.changeSaveStatus(savingStatus);
		container.repaint();
	}

	public void updateTitle(String update) {
		container.setTitle(Titles.app + "   " + update);
	}

	public void addToRepeatsList(RepeatingInformation info) {
		maker.getRepeatsList().getWords().add(info);
	}

	public void scrollToBottom() {
		maker.getRepeatsList().scrollToBottom();
	}

	public void addButtonIcon() {
		if (problematicKanjiPanel.allProblematicKanjisRepeated()) {
			return;
		}
		startingPanel.addButtonIcon();
	}

	public void removeButtonProblematicsKanji() {
		startingPanel.removeButtonProblematicsKanji();
	}

	public void showLearningStartDialog(MyList list, int maximumNumber) {
		showPanel(new LearningStartPanel(this, maximumNumber, list), Titles.learnStartDialog, false,
				Position.CENTER);

	}

	// TODO dialogs should either be jframe or modal in order for alt tab to
	// switch focus to the right window
	public void showInsertDialog(MyList list) {
		showPanel(new InsertWordPanel(list), Titles.insertWordDialog, false, Position.LEFT_CORNER);
	}

	public void showSearchWordDialog(MyList list) {
		showPanel(new SearchWordPanel(list), Titles.insertWordDialog, false, Position.LEFT_CORNER);
	}

	public void showProblematicKanjiDialog(KanjiWords kanjiWords, Set<Integer> problematicKanjis) {
		problematicKanjiPanel = new ProblematicKanjiPanel(kanjiWords, problematicKanjis);
		showPanel(problematicKanjiPanel, Titles.insertWordDialog, true, Position.CENTER);
	}

	public void showProblematicKanjiDialog() {
		showReadyPanel(problematicKanjiPanel.getDialog());
	}

	public LoadingPanel showProgressDialog() { // TODO progress dialog doesn't
												// scroll anymore
		LoadingPanel dialog = new LoadingPanel(Prompts.kanjiLoadingPrompt);
		showPanel(dialog, Titles.messageDialog, false, Position.CENTER);
		return dialog;
	}

	public void closeDialog() {
		childWindow.getContainer().dispose();
	}

	public JFrame getContainer() {
		return container;
	}

}
