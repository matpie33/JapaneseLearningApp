package com.kanji.list.myList;

import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.model.ListRow;
import com.kanji.model.WordInMyListExistence;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class ListWordsController<Word extends ListElement> {
	private static final long serialVersionUID = -3144332338336535803L;
	private Map<Integer, ListRow<Word>> allWordsToRowNumberMap = new HashMap<>();
	private ListPanelMaker<Word> rowCreator;
	private ApplicationController applicationController;
	private final int MAXIMUM_WORDS_TO_SHOW = 200;
	private Map<Integer, ListRow<Word>> visibleWordsToRowNumberMap = new LinkedHashMap<>();
	private int lastRowVisible;

	public ListWordsController(MyList list, boolean enableWordAdding,
			ListRowMaker<Word> listRowMaker, String title,
			ApplicationController applicationController) {
		this.applicationController = applicationController;
		rowCreator = new ListPanelMaker<>(list, enableWordAdding,
				applicationController, listRowMaker, this);
		rowCreator.createPanel();
		this.rowCreator.setTitle(title);
	}

	public int getMaximumWordsToShow() {
		return MAXIMUM_WORDS_TO_SHOW;
	}

	public boolean add(Word r) {
		if (!isWordDefined(r).exists()) {
			boolean canNewWordBeDisplayed = canNewWordBeDisplayed();
			ListRow<Word> newWord = rowCreator
					.addRow(r, allWordsToRowNumberMap.size() + 1,
							canNewWordBeDisplayed,
							rowCreator.getLoadNextWordsHandler());
			allWordsToRowNumberMap.put(allWordsToRowNumberMap.size(), newWord);
			if (canNewWordBeDisplayed) {
				visibleWordsToRowNumberMap
						.put(visibleWordsToRowNumberMap.size(), newWord);
				lastRowVisible = allWordsToRowNumberMap.size();
			}

			return true;
		}
		return false;
	}

	private boolean canNewWordBeDisplayed() {
		return visibleWordsToRowNumberMap.size() <= MAXIMUM_WORDS_TO_SHOW;
	}

	public void remove(Word word) {
		ListRow<Word> listRow = findListRowContainingWord(word);
		int rowNumber = rowCreator.removeRow(listRow.getPanel());
		updateRowNumbers(rowNumber);
		allWordsToRowNumberMap.remove(listRow);
		ListRow<Word> rowRemoved = visibleWordsToRowNumberMap.remove(rowNumber);
	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex; i < allWordsToRowNumberMap.size(); i++) {
			JLabel label = allWordsToRowNumberMap.get(i).getIndexLabel();
			label.setText(rowCreator.createTextForRowNumber(i));
		}

	}

	private ListRow<Word> findListRowContainingWord(Word r) {
		for (int i = 0; i < allWordsToRowNumberMap.size(); i++) {
			Word word = allWordsToRowNumberMap.get(i).getWord();
			if (word.equals(r)) {
				return allWordsToRowNumberMap.get(i);
			}
		}
		return null;
	}

	public List<Word> getWords() {
		List<Word> words = new ArrayList<>();
		for (ListRow<Word> listRow : allWordsToRowNumberMap.values()) {
			words.add(listRow.getWord());
		}
		return words;
	}

	public int getNumberOfWords() {
		return allWordsToRowNumberMap.size();
	}

	public Word getWordInRow(int rowNumber1Based) {
		return allWordsToRowNumberMap.get(rowNumber1Based).getWord();
	}

	public void highlightRowAndScroll(int rowNumber,
			boolean clearLastHighlightedWord) {
		allWordsToRowNumberMap.get(rowNumber).setHighlighted(true);
		rowCreator.highlightRowAndScroll(rowNumber, clearLastHighlightedWord);
	}

	public int getHighlightedRowNumber() {
		return rowCreator.getHighlightedRowNumber();
	}

	public void scrollToBottom() {
		rowCreator.scrollToBottom();
	}

	public JPanel getPanel() {
		return rowCreator.getPanel();
	}

	public void clear() {
		allWordsToRowNumberMap.clear();
		visibleWordsToRowNumberMap.clear();
		rowCreator.clear();
	}

	public WordInMyListExistence<Word> isWordDefined(Word word) {
		for (ListRow<Word> listRow : allWordsToRowNumberMap.values()) {
			if (listRow.getWord().isSameAs(word)) {
				return new WordInMyListExistence<>(true, listRow.getWord());
			}
		}
		return new WordInMyListExistence<>(false, null);
	}

	public AbstractAction createDeleteRowAction(Word word) {
		return new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				String rowSpecificPrompt = "";
				if (word instanceof KanjiInformation) {
					rowSpecificPrompt = Prompts.KANJI_ROW;
				}
				if (word instanceof RepeatingInformation) {
					rowSpecificPrompt = Prompts.REPEATING_ELEMENT;
				}

				if (!applicationController.showConfirmDialog(
						String.format(Prompts.DELETE_ELEMENT,
								rowSpecificPrompt))) {
					return;
				}
				remove(word);
				applicationController.saveProject();
			}
		};
	}

	public List<Word> getWordsByHighlight(boolean highlighted) {
		List<Word> highlightedWords = new ArrayList<>();
		for (ListRow<Word> word : allWordsToRowNumberMap.values()) {
			if (word.isHighlighted() == highlighted) {
				highlightedWords.add(word.getWord());
			}
		}
		return highlightedWords;
	}

	public void scrollToTop() {
		rowCreator.scrollToTop();
	}

	public int addNextHalfOfMaximumWords(LoadWordsHandler loadWordsHandler) {
		int i = 0;
		double numberOfElementsToAdd =
				(double) getMaximumWordsToShow() / (double) 2;

		while (i < numberOfElementsToAdd && loadWordsHandler
				.shouldContinue(lastRowVisible, allWordsToRowNumberMap.size())) {
			loadWordsHandler.addWord();
			i++;
		}
		return i;
	}

	public void showPreviousWord(LoadPreviousWordsHandler loadPreviousWords) {
		//TODO lots of magic numbers
		int rowNumber = lastRowVisible - MAXIMUM_WORDS_TO_SHOW - 2;
		rowCreator.addRow(allWordsToRowNumberMap.get(rowNumber).getWord(),
				rowNumber + 1, true, loadPreviousWords);
		lastRowVisible--;
	}

	public void showNextWord(LoadNextWordsHandler loadNextWords) {
		int rowNumber = lastRowVisible;
		ListRow visibleRow = rowCreator
				.addRow(allWordsToRowNumberMap.get(rowNumber).getWord(),
						rowNumber + 1, true, loadNextWords);
		allWordsToRowNumberMap.put(rowNumber, visibleRow);
		lastRowVisible++;
	}

}
