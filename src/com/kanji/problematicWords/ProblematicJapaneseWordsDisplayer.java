package com.kanji.problematicWords;

import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordsReviewingList;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;
import com.kanji.windows.ApplicationWindow;

import javax.swing.text.JTextComponent;
import java.awt.*;

public class ProblematicJapaneseWordsDisplayer
		implements ProblematicWordsDisplayer<JapaneseWordInformation> {

	private MyList<JapaneseWordInformation> wordsToReviewList;
	private ProblematicJapaneseWordsPanel problematicJapaneseWordsPanel;
	private JTextComponent selectedWord;

	public ProblematicJapaneseWordsDisplayer(
			ApplicationWindow applicationWindow,
			ProblematicWordsController controller) {
		problematicJapaneseWordsPanel = new ProblematicJapaneseWordsPanel(
				controller, applicationWindow);
		this.wordsToReviewList = new MyList<>(applicationWindow, null,
				new RowInJapaneseWordsReviewingList(this),
				Titles.PROBLEMATIC_KANJIS, false,
				JapaneseWordInformation.getElementsTypesAndLabels(),
				JapaneseWordInformation.getInitializer());

	}

	@Override public MyList<JapaneseWordInformation> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override public void browseWord(WordRow<JapaneseWordInformation> wordRow) {
	}

	@Override public WordRow createWordRow(JapaneseWordInformation listElement,
			int rowNumber) {
		return new WordRow(listElement, rowNumber);
	}

	@Override public void initialize() {
		problematicJapaneseWordsPanel.initialize();
	}

	@Override public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicJapaneseWordsPanel;
	}

	public void setSelectedWord(JTextComponent component) {

		if (selectedWord != null) {
			selectedWord.setBackground(Color.WHITE);
		}
		if (selectedWord == component) {
			selectedWord.setBackground(Color.WHITE);
			selectedWord = null;
			return;
		}
		selectedWord = component;
		selectedWord.setBackground(Color.GRAY);

	}

	public void searchCurrentWordInDictionary() {
		if (selectedWord != null) {
			problematicJapaneseWordsPanel.searchWord(selectedWord.getText());
		}
		else {
			//TODO add message about not selected words
		}
	}

	@Override public boolean isListPanelFocused() {
		return wordsToReviewList.getPanel().hasFocus();
	}

}
