package com.kanji.panelsAndControllers.controllers;

import com.guimaker.enums.MoveDirection;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.ListElementModificationType;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listObserver.ListObserver;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.ProblematicWordsState;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProblematicWordsController<Word extends ListElement>
		implements ApplicationStateManager, ListObserver<Word> {
	private List<WordRow<Word>> notReviewedWords;
	private int nextWordToReview = 0;
	//TODO change notReviewedWords variable to keep ListElement objects,
	// add to my list a method: get row number of word
	private MyList<Word> wordsToReviewList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private ProblematicWordsDisplayer<Word> problematicWordsDisplayer;
	private boolean wordsReviewed = false;
	private TypeOfWordForRepeating typeOfWordForRepeating;

	public ProblematicWordsController(ApplicationWindow applicationWindow) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
		notReviewedWords = new ArrayList<>();
	}

	public void setProblematicWordsDisplayer(
			ProblematicWordsDisplayer<Word> problematicWordsDisplayer,
			TypeOfWordForRepeating japaneseWords) {
		this.problematicWordsDisplayer = problematicWordsDisplayer;
		wordsToReviewList = problematicWordsDisplayer.getWordsToReviewList();
		this.typeOfWordForRepeating = japaneseWords;
	}

	public void initialize() {
		problematicWordsDisplayer.initializeWebPages();
	}

	public void addProblematicWordsAndHighlightFirst(
			Set<Word> problematicWords) {
		if (haveAllWordsBeenRepeated()) {
			wordsToReviewList.cleanWords();
			notReviewedWords.clear();
		}
		else if (!notReviewedWords.isEmpty()) {
			for (int i = 0; i < nextWordToReview + 1; i++) {
				notReviewedWords.remove(0);
			}
			notReviewedWords.forEach(wordRow -> wordsToReviewList
					.addWord(wordRow.getListElement()));

		}
		nextWordToReview = 0;

		wordsReviewed = false;
		for (Word word : problematicWords) {
			addWord(word);
		}

		wordsToReviewList.scrollToTop();
		goToNextResource();
		SwingUtilities.invokeLater(
				() -> wordsToReviewList.getPanel().requestFocusInWindow());
	}

	private void addWord(Word word) {
		wordsToReviewList.addWord(word, InputGoal.NO_INPUT);
		WordRow<Word> wordRow = new WordRow<>(word,
				wordsToReviewList.getNumberOfWords() - 1);
		if (!notReviewedWords.contains(wordRow)) {
			notReviewedWords.add(wordRow);
		}
	}

	private void goToNextResource() {
		WordRow<Word> row = notReviewedWords.get(nextWordToReview);
		showResource(row);
	}

	public void showResource(WordRow<Word> row) {
		problematicWordsDisplayer.browseWord(row);
		wordsToReviewList.highlightRow(
				wordsToReviewList.get1BasedRowNumberOfWord(row.getListElement())
						- 1);
	}

	private boolean haveAllWordsBeenRepeated() {
		return wordsReviewed;
	}

	public AbstractAction exitProblematicWordsPanel() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.enableShowProblematicWordsButton();
				if (haveAllWordsBeenRepeated()) {
					applicationController.finishedRepeating();
					applicationController.saveProject();
				}
				applicationWindow.showPanel(ApplicationPanels.STARTING_PANEL);
			}
		};

	}

	private AbstractAction createActionShowNextWord(MoveDirection direction) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!problematicWordsDisplayer.isListPanelFocused()
						|| wordsToReviewList.isInEditMode() || wordsToReviewList
						.isFilterInputFocused()) {
					return;
				}
				moveToNextWord(direction);
			}
		};

	}

	private void moveToNextWord(MoveDirection direction) {

		nextWordToReview = nextWordToReview + direction.getIncrementValue();
		if (nextWordToReview < 0) {
			nextWordToReview = 0;
			return;
		}
		if (nextWordToReview == notReviewedWords.size() - 1) {
			wordsReviewed = true;
		}
		if (nextWordToReview == notReviewedWords.size()) {
			applicationWindow
					.showMessageDialog(Prompts.NO_MORE_WORDS_TO_REVIEW);
			nextWordToReview = 0;
			wordsToReviewList.clearHighlightedWords();
		}

		goToNextResource();

	}

	public int getNumberOfWords() {
		return wordsToReviewList == null ?
				0 :
				wordsToReviewList.getNumberOfWords();
	}

	@Override
	public SavingInformation getApplicationState() {
		ProblematicWordsState<Word> information = new ProblematicWordsState<>(
				wordsToReviewList.getHighlightedWords(),
				wordsToReviewList.getNotHighlightedWords());

		SavingInformation savingInformation = applicationController
				.getApplicationState();
		savingInformation.setProblematicWordsState(information,
				typeOfWordForRepeating.getAssociatedSaveableState());

		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		if (savingInformation.containsProblematicJapaneseWords()
				|| savingInformation.containsProblematicKanji()) {
			applicationController.switchToList(typeOfWordForRepeating);
			initialize();
		}
		applicationWindow.showProblematicWordsDialog(
				savingInformation.getProblematicWordsState());
	}

	public void addProblematicWordsHighlightReviewed(List<Word> reviewedWords,
			List<Word> notReviewedWords) {
		for (int i = 0; i < reviewedWords.size(); i++) {
			Word listWord = reviewedWords.get(i);
			wordsToReviewList.addWord(listWord);
			wordsToReviewList.highlightRow(i);
			this.notReviewedWords.add(new WordRow<>(listWord,
					wordsToReviewList.getNumberOfWords() - 1));
			nextWordToReview = i;
		}

		for (Word listWord : notReviewedWords) {
			addWord(listWord);
		}
		goToNextResource();

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

	private void initializeActionBrowseNextWord() {
		initializeAction(KeyEvent.VK_SPACE,
				createActionShowNextWord(MoveDirection.BELOW),
				HotkeysDescriptions.SHOW_NEXT_PROBLEMATIC_WORD);

	}

	private void initializeActionBrowsePreviousWord() {
		initializeAction(KeyEvent.VK_BACK_SPACE,
				createActionShowNextWord(MoveDirection.ABOVE),
				HotkeysDescriptions.SHOW_PREVIOUS_PROBLEMATIC_WORD);
	}

	public void initializeWindowListener() {
		getPanel().getDialog().getContainer()
				.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						exitProblematicWordsPanel();
					}
				});
	}

	@Override
	public void update(Word word,
			ListElementModificationType modificationType) {
		if (isProblematicWordsListEmpty()) {
			return;
		}
		wordsToReviewList.update(word, modificationType);
		if (modificationType.equals(ListElementModificationType.DELETE)) {
			boolean removed = removeFromNotReviewed(word);
			if (removed && nextWordToReview >= notReviewedWords.size()) {
				nextWordToReview = 0;
			}
			if (removed) {
				goToNextResource();
			}

		}
		else {
			if (!notReviewedWordsContainsWord(word)) {
				wordsToReviewList.highlightRow(
						wordsToReviewList.get1BasedRowNumberOfWord(word) - 1);
			}
		}

	}

	public boolean isProblematicWordsListEmpty() {
		return wordsToReviewList.getWords().isEmpty();
	}

	private boolean notReviewedWordsContainsWord(Word word) {
		for (WordRow notReviewedWord : notReviewedWords) {
			if (notReviewedWord.getListElement().equals(word)) {
				return true;
			}
		}
		return false;
	}

	private boolean removeFromNotReviewed(Word word) {
		return notReviewedWords.removeIf(
				notReviewedWord -> notReviewedWord.getListElement()
						.equals(word));
	}

	public void focusPreviouslyFocusedElement() {
		if (problematicWordsDisplayer != null) {
			problematicWordsDisplayer.focusPreviouslyFocusedElement();
		}
	}
}
