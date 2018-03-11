package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.ApplicationSaveableState;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.KanjiInformation;
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

public class ProblematicWordsController<Element extends ListElement>
		implements ApplicationStateManager {
	private List<WordRow> wordsToReview;
	//TODO 2 variables with similar names -> maybe remove WordRow class and use map ->
	// row number to list element
	private MyList<Element> wordsToReviewList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private ProblematicWordsDisplayer<Element> problematicWordsDisplayer;

	public ProblematicWordsController(ApplicationWindow applicationWindow) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
		wordsToReview = new ArrayList<>();
	}

	public void setProblematicWordsDisplayer(
			ProblematicWordsDisplayer<Element> problematicWordsDisplayer) {
		this.problematicWordsDisplayer = problematicWordsDisplayer;
		problematicWordsDisplayer.getPanel().setMaximize(true);
		getPanel().getDialog().maximize();
		wordsToReviewList = problematicWordsDisplayer.getWordsToReviewList();
	}

	public void initialize() {
		problematicWordsDisplayer.initialize();
	}

	public void createProblematicWordsList(List<Element> reviewedWords,
			List<Element> notReviewedWords) {
		for (int i = 0; i < reviewedWords.size(); i++) {
			Element reviewedElement = reviewedWords.get(i);
			wordsToReviewList.addWord(reviewedElement);

		}
		int firstUnreviewedWordRowNumber = reviewedWords.size();
		for (int i = 0; i < notReviewedWords.size(); i++) {
			Element notReviewedElement = notReviewedWords.get(i);
			wordsToReviewList.addWord(notReviewedElement);
			wordsToReview.add(problematicWordsDisplayer
					.createWordRow(notReviewedElement,
							firstUnreviewedWordRowNumber + i));
		}
	}

	public void highlightReviewedWords(int numberOfReviewedWords) {
		for (int i = 0; i < numberOfReviewedWords; i++) {
			wordsToReviewList.highlightRow(i);
		}
	}

	public void addProblematicWords(Set<Element> problematicWords) {
		if (wordsToReview.isEmpty()) {
			wordsToReviewList.cleanWords();
		}
		for (Element word : problematicWords) {
			addWord(word);
		}
		wordsToReviewList.scrollToTop();
	}

	private void addWord(Element word) {
		boolean addedToList = wordsToReviewList.addWord(word);
		if (addedToList) {
			wordsToReview.add(problematicWordsDisplayer.createWordRow(word,
					wordsToReviewList.getNumberOfWords() - 1));
		}
	}

	private void goToNextResource() {
		WordRow row = wordsToReview.get(0);
		goToSpecifiedResource(row);
	}

	public void goToSpecifiedResource(WordRow row) {
		wordsToReview.remove(row);
		//TODO do I really need the kanji context as separate class? theres so many already,
		//I should reconsider some of the modelling objects
		problematicWordsDisplayer.browseWord(row);
		wordsToReviewList.highlightRow(row.getRowNumber());
	}

	public boolean haveAllWordsBeenRepeated() {
		return wordsToReviewList.areAllWordsHighlighted();
	}

	public boolean hasWordsToReview() {
		return !wordsToReview.isEmpty();
	}

	public void closeDialogAndManageState(DialogWindow parentDialog) {
		assert (parentDialog.getParent() instanceof ApplicationWindow);
		ApplicationWindow parent = (ApplicationWindow) parentDialog.getParent();
		parent.addButtonIcon();
		if (haveAllWordsBeenRepeated()) {
			applicationController.finishedRepeating();
			applicationController.saveProject();
		}
		parentDialog.getContainer().dispose();
	}

	public AbstractAction createActionShowNextWordOrCloseDialog() {
		return new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				if (!problematicWordsDisplayer.isListPanelFocused()) {
					return;
				}
				if (hasWordsToReview())
					goToNextResource();
				else {
					problematicWordsDisplayer.getPanel().getDialog()
							.showMessageDialog(Prompts.NO_MORE_WORDS_TO_REVIEW);
				}
			}
		};
	}

	public int getNumberOfRows() {
		return wordsToReviewList.getNumberOfWords();
	}

	public MyList getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override public SavingInformation getApplicationState() {
		ProblematicKanjisState<Element> information = new ProblematicKanjisState<>(
				wordsToReviewList.getHighlightedWords(),
				wordsToReviewList.getNotHighlightedWords());

		SavingInformation savingInformation = applicationController
				.getApplicationState();
		savingInformation.setProblematicKanjisState(information,
				wordsToReview.get(0).getListElement().getClass()
						.equals(KanjiInformation.class) ?
						ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS :
						ApplicationSaveableState.REVIEWING_PROBLEMATIC_JAPANESE_WORDS);

		return savingInformation;
	}

	@Override public void restoreState(SavingInformation savingInformation) {
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

	public void addProblematicWordsHighlightReviewed(
			List<Element> reviewedWords, List<Element> notReviewedWords) {
		int i = 0;
		for (Element listElement : reviewedWords) {
			wordsToReviewList.addWord(listElement);
			wordsToReviewList.highlightRow(i);
			i++;
		}
		for (Element listElement : notReviewedWords) {
			addWord(listElement);
		}

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

	public void initializeSpaceBarAction() {

		getPanel().addHotkey(KeyEvent.VK_SPACE,
				createActionShowNextWordOrCloseDialog(), getPanel().getPanel(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);

	}

	public void initializeWindowListener() {
		getPanel().getDialog().getContainer()
				.addWindowListener(new WindowAdapter() {
					@Override public void windowClosed(WindowEvent e) {
						closeDialogAndManageState(getPanel().getDialog());
					}
				});
	}

}
