package com.kanji.panelsAndControllers.controllers;

import com.guimaker.enums.MoveDirection;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.enums.ApplicationSaveableState;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProblematicWordsController<Word extends ListElement>
		implements ApplicationStateManager {
	private List<WordRow> wordsToReview;
	private int nextWordToReview = 0;
	//TODO 2 variables with similar names -> maybe remove WordRow class and use map ->
	// row number to list element
	private MyList<Word> wordsToReviewList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private ProblematicWordsDisplayer<Word> problematicWordsDisplayer;
	private boolean wordsReviewed = false;

	public ProblematicWordsController(ApplicationWindow applicationWindow) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
		wordsToReview = new ArrayList<>();
	}

	public void setProblematicWordsDisplayer(
			ProblematicWordsDisplayer<Word> problematicWordsDisplayer) {
		this.problematicWordsDisplayer = problematicWordsDisplayer;
		problematicWordsDisplayer.getPanel().setMaximize(true);
		getPanel().getDialog().maximize();
		wordsToReviewList = problematicWordsDisplayer.getWordsToReviewList();

	}

	public void initialize() {
		problematicWordsDisplayer.initialize();
	}

	public void createProblematicWordsList(List<Word> reviewedWords,
			List<Word> notReviewedWords) {
		for (int i = 0; i < reviewedWords.size(); i++) {
			Word reviewedWord = reviewedWords.get(i);
			wordsToReviewList.addWord(reviewedWord);

		}
		int firstUnreviewedWordRowNumber = reviewedWords.size();
		for (int i = 0; i < notReviewedWords.size(); i++) {
			Word notReviewedWord = notReviewedWords.get(i);
			wordsToReviewList.addWord(notReviewedWord);
			wordsToReview.add(problematicWordsDisplayer
					.createWordRow(notReviewedWord,
							firstUnreviewedWordRowNumber + i));
		}
	}

	public void highlightReviewedWords(int numberOfReviewedWords) {
		for (int i = 0; i < numberOfReviewedWords; i++) {
			wordsToReviewList.highlightRow(i);
		}
	}

	public void addProblematicWords(Set<Word> problematicWords) {
		if (wordsToReview.isEmpty()) {
			wordsToReviewList.cleanWords();
		}
		for (Word word : problematicWords) {
			addWord(word);
		}
		wordsToReviewList.scrollToTop();
		goToNextResource();
	}

	private void addWord(Word word) {
		boolean addedToList = wordsToReviewList.addWord(word);
		if (addedToList) {
			wordsToReview.add(problematicWordsDisplayer.createWordRow(word,
					wordsToReviewList.getNumberOfWords() - 1));
		}
	}

	private void goToNextResource() {
		WordRow row = wordsToReview.get(nextWordToReview);
		goToSpecifiedResource(row);
	}

	public void goToSpecifiedResource(WordRow row) {
		//TODO do I really need the kanji context as separate class? theres so many already,
		//I should reconsider some of the modelling objects
		problematicWordsDisplayer.browseWord(row);
		wordsToReviewList.highlightRow(row.getRowNumber());

	}

	public boolean haveAllWordsBeenRepeated() {
		return wordsReviewed;
	}

	public AbstractAction closeDialogAndManageState() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.addButtonIcon();
				if (haveAllWordsBeenRepeated()) {
					applicationController.finishedRepeating();
					applicationController.saveProject();
				}
				applicationWindow.showPanel(ApplicationPanels.STARTING_PANEL);
			}
		};

	}

	public AbstractAction createActionShowNextWordOrCloseDialog(
			MoveDirection direction) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!problematicWordsDisplayer.isListPanelFocused()) {
					return;
				}
				nextWordToReview =
						nextWordToReview + direction.getIncrementValue();
				if (nextWordToReview < 0) {
					nextWordToReview = 0;
					return;
				}
				if (nextWordToReview == wordsToReview.size() - 1) {
					wordsReviewed = true;
				}
				if (nextWordToReview == wordsToReview.size()) {
					applicationWindow
							.showMessageDialog(Prompts.NO_MORE_WORDS_TO_REVIEW);
					nextWordToReview = 0;
					wordsToReviewList.clearHighlightedWords();
				}

				goToNextResource();

			}
		};

	}

	public int getNumberOfRows() {
		return wordsToReviewList.getNumberOfWords();
	}

	public MyList getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public SavingInformation getApplicationState() {
		ProblematicKanjisState<Word> information = new ProblematicKanjisState<>(
				wordsToReviewList.getHighlightedWords(),
				wordsToReviewList.getNotHighlightedWords());

		SavingInformation savingInformation = applicationController
				.getApplicationState();
		savingInformation.setProblematicKanjisState(information,
				wordsToReview.get(0).getListElement().getClass()
						.equals(Kanji.class) ?
						ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS :
						ApplicationSaveableState.REVIEWING_PROBLEMATIC_JAPANESE_WORDS);

		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		if (savingInformation.containsProblematicJapaneseWords()
				|| savingInformation.containsProblematicKanji()) {
			Class wordType = savingInformation.getProblematicKanjisState()
					.getReviewedWords().isEmpty() ?
					savingInformation.getProblematicKanjisState()
							.getNotReviewedWords().get(0).getClass() :
					savingInformation.getProblematicKanjisState()
							.getReviewedWords().get(0).getClass();
			applicationController.switchToList(wordType);
			initialize();
		}
		applicationWindow.showProblematicWordsDialog(
				savingInformation.getProblematicKanjisState());
	}

	public void addProblematicWordsHighlightReviewed(List<Word> reviewedWords,
			List<Word> notReviewedWords) {
		int i = 0;
		for (Word listWord : reviewedWords) {
			wordsToReviewList.addWord(listWord);
			wordsToReviewList.highlightRow(i);
			i++;
		}
		i--;
		for (Word listWord : notReviewedWords) {
			addWord(listWord);
		}
		goToNextResource();

	}

	public Class getWordType() {
		return problematicWordsDisplayer.getWordsToReviewList()
				.getListElementClass();
	}

	public boolean isDialogHidden() {
		return !problematicWordsDisplayer.getPanel().isDisplayable();
	}

	public DialogWindow getDialog() {
		return problematicWordsDisplayer.getPanel().getDialog();
	}

	public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicWordsDisplayer.getPanel();
	}

	public void initializeHotkeyActions() {
		initializeActionBrowseNextWord();
		initializeActionBrowsePreviousWord();
		//TODO this doesn't have to be public, we can do it whenever panel is ready
	}

	private void initializeAction(int hotkey, AbstractAction action,
			String actionDescription) {
		getPanel().addHotkey(hotkey, action, getPanel().getPanel(),
				actionDescription);
	}

	private void initializeAction(KeyModifiers keyModifier, int hotkey,
			AbstractAction action, String actionDescription) {
		getPanel().addHotkey(hotkey, action, getPanel().getPanel(),
				actionDescription);
	}

	private void initializeActionBrowseNextWord() {
		initializeAction(KeyEvent.VK_SPACE,
				createActionShowNextWordOrCloseDialog(MoveDirection.BELOW),
				HotkeysDescriptions.SHOW_NEXT_PROBLEMATIC_WORD);

	}

	private void initializeActionBrowsePreviousWord() {
		initializeAction(KeyEvent.VK_BACK_SPACE,
				createActionShowNextWordOrCloseDialog(MoveDirection.ABOVE),
				HotkeysDescriptions.SHOW_PREVIOUS_PROBLEMATIC_WORD);
	}

	public void initializeWindowListener() {
		getPanel().getDialog().getContainer()
				.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						closeDialogAndManageState();
					}
				});
	}

	public Word getCurrentlySelectedWord() {
		return wordsToReviewList.getWordInRow(nextWordToReview);
	}

}
