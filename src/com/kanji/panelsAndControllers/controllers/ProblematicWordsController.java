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
import java.util.List;
import java.util.Set;

public class ProblematicWordsController<Word extends ListElement>
		implements ApplicationStateManager, ListObserver<Word> {
	private int nextWordToReview = 0;
	private MyList<Word> wordsToReviewList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private ProblematicWordsDisplayer<Word> problematicWordsDisplayer;
	private boolean wordsReviewed = false;
	private TypeOfWordForRepeating typeOfWordForRepeating;

	public ProblematicWordsController(ApplicationWindow applicationWindow) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
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
		}
		else if (!wordsToReviewList.isEmpty()) {
			for (int i = 0; i < nextWordToReview + 1; i++) {
				wordsToReviewList.removeWordInRow(0);
			}

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
	}

	private void goToNextResource() {
		Word word = wordsToReviewList.getWordInRow(nextWordToReview + 1);
		showResource(word);
	}

	public void showResource(Word word) {
		problematicWordsDisplayer.browseWord(word);
		wordsToReviewList.highlightRow(
				wordsToReviewList.get1BasedRowNumberOfWord(word) - 1);
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
		if (nextWordToReview == wordsToReviewList.getNumberOfWords() - 1) {
			wordsReviewed = true;
		}
		if (nextWordToReview == wordsToReviewList.getNumberOfWords()) {
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
			Word word = reviewedWords.get(i);
			wordsToReviewList.addWord(word);
			wordsToReviewList.highlightRow(i);
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
		boolean hasWord = wordsToReviewList.containsWord(word);
		wordsToReviewList.update(word, modificationType);
		if (modificationType.equals(ListElementModificationType.DELETE)) {
			if (hasWord && nextWordToReview >= wordsToReviewList
					.getNumberOfWords()) {
				nextWordToReview = 0;
			}
			if (hasWord) {
				goToNextResource();
			}
		}
	}

	public boolean isProblematicWordsListEmpty() {
		return wordsToReviewList.getWords().isEmpty();
	}

	public void focusPreviouslyFocusedElement() {
		if (problematicWordsDisplayer != null) {
			problematicWordsDisplayer.focusPreviouslyFocusedElement();
		}
	}
}
