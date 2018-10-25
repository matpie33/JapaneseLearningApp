package com.kanji.panelsAndControllers.controllers;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListObserver;
import com.guimaker.list.myList.MyList;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.Prompts;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.ProblematicWordsState;
import com.kanji.saving.SavingInformation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Set;

public class ProblematicWordsController<Word extends ListElement>
		implements ApplicationStateManager, ListObserver<Word> {
	private int nextWordToReview = 0;
	private MyList<Word> wordsToReviewList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private ProblematicWordsDisplayer<Word> problematicWordsDisplayer;
	private boolean wordsReviewFinished = false;

	public ProblematicWordsController(
			ApplicationController applicationController,
			ProblematicWordsDisplayer<Word> problematicWordsDisplayer) {
		this.applicationController = applicationController;
		this.applicationWindow = applicationController.getApplicationWindow();
		this.problematicWordsDisplayer = problematicWordsDisplayer;

	}

	public ProblematicWordsDisplayer<Word> getProblematicWordsDisplayer() {
		return problematicWordsDisplayer;
	}

	public void initialize() {
		wordsToReviewList = problematicWordsDisplayer.getWordsToReviewList();
		problematicWordsDisplayer.initializeWebPages();
	}

	public void addProblematicWordsAndHighlightFirst(
			Set<Word> problematicWords) {
		if (haveAllWordsBeenReviewed()) {
			wordsToReviewList.cleanWords();
		}
		else if (!wordsToReviewList.isEmpty()) {
			for (int i = 0; i < nextWordToReview + 1; i++) {
				wordsToReviewList.removeWordInRow(0);
			}

		}
		nextWordToReview = 0;

		wordsReviewFinished = false;
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

	private boolean haveAllWordsBeenReviewed() {
		return wordsReviewFinished;
	}

	public AbstractAction exitProblematicWordsPanel() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.enableShowProblematicWordsButton();
				if (haveAllWordsBeenReviewed()) {
					applicationController.finishedRepeating();
					applicationController.save();
				}
				applicationWindow.showPanel(
						applicationController.getStartingPanel()
								.getUniqueName());
			}
		};

	}

	public AbstractAction createActionShowNextWord(MoveDirection direction) {
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
			wordsReviewFinished = true;
		}
		if (nextWordToReview == wordsToReviewList.getNumberOfWords()) {
			applicationWindow
					.showMessageDialog(Prompts.NO_MORE_WORDS_TO_REVIEW);
			nextWordToReview = 0;
			wordsToReviewList.clearHighlightedWords();
		}

		goToNextResource();

	}

	@Override
	public SavingInformation getApplicationState() {
		ProblematicWordsState<Word> problematicWordsState = new ProblematicWordsState<>(
				wordsToReviewList.getHighlightedWords(),
				wordsToReviewList.getNotHighlightedWords());

		SavingInformation savingInformation = applicationController
				.getApplicationState();
		savingInformation.setProblematicWordsState(problematicWordsState,
				applicationController.getActiveWordsListType()
						.getAssociatedReviewingWordsState());

		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		if (savingInformation.containsProblematicJapaneseWords()
				|| savingInformation.containsProblematicKanji()) {
			applicationController.switchToList(TypeOfWordForRepeating
					.withMeaningfulName(
							savingInformation.getApplicationSaveableState()
									.getMeaningfulName()));
		}
		applicationController.showProblematicWordsDialog(
				savingInformation.getApplicationSaveableState()
						.getMeaningfulName(),
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
