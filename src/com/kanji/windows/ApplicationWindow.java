package com.kanji.windows;

import java.awt.CardLayout;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.kanji.Row.KanjiWords;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.constants.Titles;
import com.kanji.controllers.RepeatingWordsController;
import com.kanji.controllers.StartingPanelController;
import com.kanji.fileReading.KanjiCharactersReader;
import com.kanji.myList.MyList;
import com.kanji.panels.InsertWordPanel;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panels.LoadingPanel;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.panels.SearchWordPanel;
import com.kanji.panels.StartingPanel;
import com.kanji.utilities.ElementMaker;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private ElementMaker maker;
	private JPanel mainApplicationPanel;
	private RepeatingWordsController repeatingWordsPanel;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private StartingPanel startingPanel;
	private JFrame container;
	private StartingPanelController startingPanelController; // TODO we should
																// create it in
																// starting
																// panel instead

	// TODO handle the situation in gui maker when the panel has just 1 row so
	// we
	// don't have to use row (0) but somehow easier

	public KanjiCharactersReader excel;

	public static final String LIST_PANEL = "Panel with lists and buttons";
	public static final String LEARNING_PANEL = "Panel for repeating words";

	public ApplicationWindow() {
		super(null);
		container = new JFrame();
		maker = new ElementMaker(this);
		mainApplicationPanel = new JPanel(new CardLayout());

		repeatingWordsPanel = new RepeatingWordsController(this);
		startingPanelController = new StartingPanelController(maker, repeatingWordsPanel);
		startingPanel = new StartingPanel(this, maker);

		mainApplicationPanel.add(startingPanel.createPanel(), LIST_PANEL);
		mainApplicationPanel.add(repeatingWordsPanel.getPanel().createPanel(), LEARNING_PANEL);

		setWindowProperties();
	}

	public StartingPanelController getStartingController() {
		return startingPanelController; // TODO remove this method later
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

	public void scrollToBottom() {
		maker.getRepeatsList().scrollToBottom();
	}

	public void addButtonIcon() {
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
		showPanel(new SearchWordPanel(list), Titles.wordSearchDialog, false, Position.LEFT_CORNER);
	}

	public void showProblematicKanjiDialog(KanjiWords kanjiWords, Set<Integer> problematicKanjis) {
		problematicKanjiPanel = new ProblematicKanjiPanel(kanjiWords, problematicKanjis);
		showPanel(problematicKanjiPanel, Titles.insertWordDialog, true, Position.CENTER);
	}

	public void showProblematicKanjiDialog() {
		showReadyPanel(problematicKanjiPanel.getDialog());
	}

	public LoadingPanel showProgressDialog() {
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
