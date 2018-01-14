package com.kanji.panelsAndControllers.controllers;

import java.awt.event.ActionEvent;
import java.util.*;

import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.list.myList.MyList;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.SavingInformation;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;

public class ProblematicWordsController<Element extends ListElement>
		implements ApplicationStateManager {
	//TODO is there any advantage of making problematic kanjis controller generic with
	//"element extends listelement"?
	private List<WordRow<Element>> wordsToReview;
	//TODO 2 variables with similar names -> maybe remove WordRow class and use map ->
	// row number to list element
	private MyList<Element> wordsToReviewList;
	private ApplicationController applicationController;
	private ApplicationWindow applicationWindow;
	private ProblematicWordsDisplayer <Element> problematicWordsDisplayer;

	public ProblematicWordsController(ApplicationWindow applicationWindow) {
		applicationController = applicationWindow.getApplicationController();
		this.applicationWindow = applicationWindow;
		wordsToReview = new ArrayList<>();
	}

	public void setProblematicWordsDisplayer (
			ProblematicWordsDisplayer<Element> problematicWordsDisplayer){
		this.problematicWordsDisplayer = problematicWordsDisplayer;
		wordsToReviewList = problematicWordsDisplayer.getWordsToReviewList();
	}

	public void initialize(){
		problematicWordsDisplayer.initialize();
	}

	public void createProblematicWordsList(List <Element> reviewedWords,
			List <Element> notReviewedWords){
		for (int i=0; i< reviewedWords.size(); i++){
			Element reviewedElement = reviewedWords.get(i);
			wordsToReviewList.addWord(reviewedElement);

		}
		int firstUnreviewedWordRowNumber = reviewedWords.size();
		for (int i=0; i< notReviewedWords.size(); i++){
			Element notReviewedElement = notReviewedWords.get(i);
			wordsToReviewList.addWord(notReviewedElement);
			wordsToReview.add(problematicWordsDisplayer.createWordRow(notReviewedElement,
					firstUnreviewedWordRowNumber+i));
		}
	}

	public void highlightReviewedWords(int numberOfReviewedWords){
		for (int i=0; i<numberOfReviewedWords; i++){
			wordsToReviewList.highlightRow(i);
		}
	}

	public void addProblematicWords(Set<Element> problematicWords){
		if (wordsToReview.isEmpty()){
			wordsToReviewList.cleanWords();
		}
		for (Element word: problematicWords){
			boolean addedToList = wordsToReviewList.addWord(word);
			if (addedToList){
				wordsToReview.add(problematicWordsDisplayer.createWordRow(
						word, wordsToReviewList.getNumberOfWords()-1
				));
			}

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
		return new AbstractAction(){
			@Override
			public void actionPerformed(ActionEvent e){
				if (hasWordsToReview())
					goToNextResource();
				else {
					problematicWordsDisplayer.getPanel().getDialog().showMessageDialog(
							Prompts.NO_MORE_WORDS_TO_REVIEW);
				}
			}
		};
	}

	public int getNumberOfRows() {
		return wordsToReviewList.getNumberOfWords();
	}

	public MyList <Element> getWordsToReviewList(){
		return wordsToReviewList;
	}

	@Override public SavingInformation getApplicationState() {
		ProblematicKanjisState information = new ProblematicKanjisState(
				wordsToReviewList.getHighlightedWords(), wordsToReviewList.getNotHighlightedWords());
		SavingInformation savingInformation = applicationController.getApplicationState();
		savingInformation.setProblematicKanjisState(information);
		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation){
		//TODO reimplement restoring state
		applicationWindow.showProblematicWordsDialog(savingInformation.getProblematicKanjisState());
	}

	public boolean isPanelInitialized (){
		return problematicWordsDisplayer.getPanel().isDisplayable();
	}

	public DialogWindow getDialog (){
		return problematicWordsDisplayer.getPanel().getDialog();
	}

	public AbstractPanelWithHotkeysInfo getPanel (){
		return problematicWordsDisplayer.getPanel();
	}

}
