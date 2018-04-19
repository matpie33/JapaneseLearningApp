package com.kanji.list.myList;

import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.ListElementInitializer;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.loadAdditionalWordsHandling.FoundWordInsideVisibleRangePlusMaximumWordsStrategy;
import com.kanji.list.loadAdditionalWordsHandling.FoundWordInsideVisibleRangeStrategy;
import com.kanji.list.loadAdditionalWordsHandling.FoundWordOutsideRangeStrategy;
import com.kanji.list.loadAdditionalWordsHandling.LoadWordsForFoundWord;
import com.kanji.model.ListRow;
import com.kanji.model.WordInMyListExistence;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.range.Range;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWordsController<Word extends ListElement> {
	private static final long serialVersionUID = -3144332338336535803L;
	private Map<Integer, ListRow<Word>> allWordsToRowNumberMap = new HashMap<>();
	private ListPanelMaker<Word> listPanelMaker;
	private ApplicationController applicationController;
	private final int MAXIMUM_WORDS_TO_SHOW = 200;
	private int lastRowVisible;
	private final List<LoadWordsForFoundWord> strategiesForFoundWord = new ArrayList<>();
	private ListRow<Word> currentlyHighlightedWord;
	private ListElementInitializer<Word> wordInitializer;

	public ListWordsController(ListConfiguration listConfiguration,
			ListRowMaker<Word> listRowMaker, String title,
			ApplicationController applicationController,
			ListElementInitializer wordInitializer) {
		this.applicationController = applicationController;
		listPanelMaker = new ListPanelMaker<>(listConfiguration,
				applicationController, listRowMaker, this);
		listPanelMaker.createPanel();
		this.listPanelMaker.setTitle(title);
		this.wordInitializer = wordInitializer;
		initializeFoundWordStrategies();

	}

	public void inheritScrollPane() {
		listPanelMaker.inheritScrollPane();
	}

	private void initializeFoundWordStrategies() {
		strategiesForFoundWord.add(new FoundWordInsideVisibleRangeStrategy());
		strategiesForFoundWord
				.add(new FoundWordInsideVisibleRangePlusMaximumWordsStrategy(
						MAXIMUM_WORDS_TO_SHOW, this,
						listPanelMaker.getLoadPreviousWordsHandler(),
						listPanelMaker.getLoadNextWordsHandler()));
		strategiesForFoundWord
				.add(new FoundWordOutsideRangeStrategy(MAXIMUM_WORDS_TO_SHOW,
						this));
	}

	public int getMaximumWordsToShow() {
		return MAXIMUM_WORDS_TO_SHOW;
	}

	public boolean add(Word r, boolean forSearchPanel) {
		if (!isWordDefined(r).exists()) {
			boolean canNewWordBeDisplayed = canNewWordBeDisplayed();
			ListRow<Word> newWord = listPanelMaker
					.addRow(r, allWordsToRowNumberMap.size() + 1,
							canNewWordBeDisplayed,
							listPanelMaker.getLoadNextWordsHandler(),
							forSearchPanel);
			allWordsToRowNumberMap.put(allWordsToRowNumberMap.size(), newWord);
			if (canNewWordBeDisplayed) {
				lastRowVisible = allWordsToRowNumberMap.size() - 1;
			}

			return true;
		}
		return false;
	}

	private boolean canNewWordBeDisplayed() {
		return listPanelMaker.getNumberOfListRows() < MAXIMUM_WORDS_TO_SHOW;
	}

	public void remove(Word word) {
		ListRow<Word> listRow = findListRowContainingWord(word);
		int panelRowNumber = listPanelMaker.removeRow(listRow.getPanel());
		int listRowNumber = panelRowNumber - 1;
		allWordsToRowNumberMap.remove(listRowNumber);
		updateRowNumbers(listRowNumber);

	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex + 1;
			 i < allWordsToRowNumberMap.size(); i++) {
			ListRow<Word> listRow = allWordsToRowNumberMap.get(i);
			JLabel label = listRow.getIndexLabel();
			label.setText(listPanelMaker.createTextForRowNumber(i));
			allWordsToRowNumberMap.put(i - 1, listRow);
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
		loadWordsIfNecessary(rowNumber);
		ListRow foundWord = allWordsToRowNumberMap.get(rowNumber);
		foundWord.setHighlighted(true);
		if (clearLastHighlightedWord && currentlyHighlightedWord != null) {
			listPanelMaker
					.clearHighlightedRow(currentlyHighlightedWord.getPanel());
		}
		listPanelMaker.highlightRowAndScroll(foundWord.getPanel());
		currentlyHighlightedWord = foundWord;
	}

	private void loadWordsIfNecessary(int foundWordRowNumber) {
		for (LoadWordsForFoundWord strategyForFoundWord : strategiesForFoundWord) {
			if (strategyForFoundWord.isApplicable(foundWordRowNumber,
					new Range(getFirstVisibleRowNumber(), lastRowVisible))) {
				strategyForFoundWord.execute();
				break;
			}
		}
	}

	public Integer getHighlightedRowNumber() {
		return currentlyHighlightedWord != null ?
				allWordsToRowNumberMap.entrySet().stream()
						.filter(e -> e.getValue()
								.equals(currentlyHighlightedWord))
						.map(e -> e.getKey()).findFirst()
						.orElseThrow(IllegalArgumentException::new) :
				-1;
	}

	public void scrollToBottom() {
		loadWordsIfNecessary(allWordsToRowNumberMap.size() - 1);
		listPanelMaker.scrollToBottom();
	}

	public JPanel getPanel() {
		return listPanelMaker.getPanel();
	}

	public void clear() {
		allWordsToRowNumberMap.clear();
		listPanelMaker.clear();
		lastRowVisible = 0;
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
			@Override
			public void actionPerformed(ActionEvent e) {
				String rowSpecificPrompt = "";
				if (word instanceof Kanji) {
					rowSpecificPrompt = Prompts.KANJI_ROW;
				}
				if (word instanceof RepeatingData) {
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
		listPanelMaker.scrollToTop();
	}

	public int addNextHalfOfMaximumWords(LoadWordsHandler loadWordsHandler) {
		int i = 0;
		double numberOfElementsToAdd =
				(double) getMaximumWordsToShow() / (double) 2;
		return addSuccessiveWords(loadWordsHandler, numberOfElementsToAdd);
	}

	public int addSuccessiveWords(LoadWordsHandler loadWordsHandler,
			double numberOfElementsToAdd) {
		int i = 0;
		while (i < numberOfElementsToAdd && loadWordsHandler
				.shouldContinue(lastRowVisible,
						allWordsToRowNumberMap.size())) {
			loadWordsHandler.addWord();
			i++;
		}
		return i;
	}

	public int getFirstVisibleRowNumber() {
		return lastRowVisible - (MAXIMUM_WORDS_TO_SHOW - 1);
	}

	public void showPreviousWord(LoadPreviousWordsHandler loadPreviousWords) {
		//TODO lots of magic numbers
		lastRowVisible--;
		int rowNumber = getFirstVisibleRowNumber();
		ListRow addedWord = listPanelMaker
				.addRow(allWordsToRowNumberMap.get(rowNumber).getWord(),
						rowNumber + 1, true, loadPreviousWords, false);
		allWordsToRowNumberMap.put(rowNumber, addedWord);

	}

	public void showNextWord(LoadNextWordsHandler loadNextWords) {
		lastRowVisible++;
		ListRow visibleRow = listPanelMaker
				.addRow(allWordsToRowNumberMap.get(lastRowVisible).getWord(),
						lastRowVisible + 1, true, loadNextWords, false);
		allWordsToRowNumberMap.put(lastRowVisible, visibleRow);
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listPanelMaker.clear();
		if (firstRowToLoad > 0) {
			listPanelMaker.enableButtonShowPreviousWords();
		}
		lastRowVisible = Math.max(firstRowToLoad - getMaximumWordsToShow(), -1);
		LoadNextWordsHandler loadNextWordsHandler = listPanelMaker
				.getLoadNextWordsHandler();
		for (int i = 0; i < getMaximumWordsToShow() && loadNextWordsHandler
				.shouldContinue(lastRowVisible,
						allWordsToRowNumberMap.size() - 1); i++) {
			showNextWord(loadNextWordsHandler);
		}
	}

	public void clearVisibleRows() {
		listPanelMaker.removeWordsFromRangeInclusive(
				new Range(1, listPanelMaker.getNumberOfListRows()));
	}

	public void removeRowsFromRangeInclusive(Range range) {
		listPanelMaker.removeWordsFromRangeInclusive(range);
	}

	//TODO not the best idea to pass the boolean "is for search panel" - maybe keep it as field
	public void addNewWord() {
		add(wordInitializer.initializeElement(), false);
	}
}
