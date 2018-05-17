package com.kanji.list.myList;

import com.guimaker.enums.MoveDirection;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
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
import java.util.*;

public class ListWordsController<Word extends ListElement> {
	private Map<Integer, ListRow<Word>> allWordsToRowNumberMap = new HashMap<>();
	private ListPanelCreator<Word> listPanelCreator;
	private ApplicationController applicationController;
	private final int MAXIMUM_WORDS_TO_SHOW = 200;
	private int lastRowVisible;
	private final List<LoadWordsForFoundWord> strategiesForFoundWord = new ArrayList<>();
	private ListRow<Word> currentlyHighlightedWord;
	private ListElementInitializer<Word> wordInitializer;
	private List<SwitchBetweenInputsFailListener> switchBetweenInputsFailListeners = new ArrayList<>();
	//TODO switchBetweenInputsFailListeners should be deleted from here

	public ListWordsController(ListConfiguration listConfiguration,
			ListRowCreator<Word> listRowCreator, String title,
			ApplicationController applicationController,
			ListElementInitializer<Word> wordInitializer) {
		this.applicationController = applicationController;
		listPanelCreator = new ListPanelCreator<>(listConfiguration,
				applicationController, listRowCreator, this);
		listPanelCreator.createPanel();
		this.listPanelCreator.setTitle(title);
		this.wordInitializer = wordInitializer;
		initializeFoundWordStrategies();

	}

	public void inheritScrollPane() {
		listPanelCreator.inheritScrollPane();
	}

	private void initializeFoundWordStrategies() {
		strategiesForFoundWord.add(new FoundWordInsideVisibleRangeStrategy());
		strategiesForFoundWord
				.add(new FoundWordInsideVisibleRangePlusMaximumWordsStrategy(
						MAXIMUM_WORDS_TO_SHOW, this,
						listPanelCreator.getLoadPreviousWordsHandler(),
						listPanelCreator.getLoadNextWordsHandler()));
		strategiesForFoundWord
				.add(new FoundWordOutsideRangeStrategy(MAXIMUM_WORDS_TO_SHOW,
						this));
	}

	public int getMaximumWordsToShow() {
		return MAXIMUM_WORDS_TO_SHOW;
	}

	public boolean add(Word r, InputGoal inputGoal) {
		if (r != null && !isWordDefined(r).exists()) {
			boolean canNewWordBeDisplayed = canNewWordBeDisplayed();
			ListRow<Word> newWord = listPanelCreator
					.addRow(r, allWordsToRowNumberMap.size() + 1,
							canNewWordBeDisplayed,
							listPanelCreator.getLoadNextWordsHandler(),
							inputGoal);
			allWordsToRowNumberMap.put(allWordsToRowNumberMap.size(), newWord);
			if (canNewWordBeDisplayed) {
				lastRowVisible = allWordsToRowNumberMap.size() - 1;
			}

			return true;
		}
		return false;
	}

	private boolean canNewWordBeDisplayed() {
		return listPanelCreator.getNumberOfListRows() < MAXIMUM_WORDS_TO_SHOW;
	}

	public void remove(Word word) {
		ListRow<Word> listRow = findListRowContainingWord(word);
		int panelRowNumber = listPanelCreator.removeRow(listRow.getJPanel());
		int listRowNumber = panelRowNumber - 1;
		allWordsToRowNumberMap.remove(listRowNumber);
		updateRowNumbers(listRowNumber);

	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex + 1;
			 i <= allWordsToRowNumberMap.size(); i++) {
			ListRow<Word> listRow = allWordsToRowNumberMap.get(i);
			JLabel label = listRow.getIndexLabel();
			label.setText(listPanelCreator.createTextForRowNumber(i));
			allWordsToRowNumberMap.remove(i);
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
		ListRow<Word> foundWord = allWordsToRowNumberMap.get(rowNumber);
		foundWord.setHighlighted(true);
		if (clearLastHighlightedWord && currentlyHighlightedWord != null) {
			listPanelCreator
					.clearHighlightedRow(currentlyHighlightedWord.getJPanel());
		}
		listPanelCreator.highlightRowAndScroll(foundWord.getJPanel());
		currentlyHighlightedWord = foundWord;
	}

	public void clearHighlightedWords() {
		for (ListRow<Word> listRow : allWordsToRowNumberMap.values()) {
			if (listRow.isHighlighted()) {
				listPanelCreator.clearHighlightedRow(listRow.getJPanel());
			}
		}
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
						.map(Map.Entry::getKey).findFirst()
						.orElseThrow(IllegalArgumentException::new) :
				-1;
	}

	public void scrollToBottom() {
		loadWordsIfNecessary(allWordsToRowNumberMap.size() - 1);
		listPanelCreator.scrollToBottom();
	}

	public JPanel getPanel() {
		return listPanelCreator.getPanel();
	}

	public void clear() {
		allWordsToRowNumberMap.clear();
		listPanelCreator.clear();
		lastRowVisible = 0;
	}

	public WordInMyListExistence<Word> isWordDefined(Word word) {
		Iterator<ListRow<Word>> iterator = allWordsToRowNumberMap.values()
				.iterator();
		for (int indexOfWord = 0; iterator.hasNext(); indexOfWord++) {
			ListRow<Word> listRow = iterator.next();
			if (listRow.getWord().equals(word)) {
				return new WordInMyListExistence<>(true, listRow.getWord(),
						indexOfWord + 1);
			}
		}
		return new WordInMyListExistence<>(false, null, -1);
	}

	public AbstractAction createDeleteRowAction(Word word) {
		return new AbstractAction() {
			private static final long serialVersionUID = 5946111397005824819L;

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
		listPanelCreator.scrollToTop();
	}

	public int addNextHalfOfMaximumWords(LoadWordsHandler loadWordsHandler) {
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
		ListRow<Word> addedWord = listPanelCreator
				.addRow(allWordsToRowNumberMap.get(rowNumber).getWord(),
						rowNumber + 1, true, loadPreviousWords, InputGoal.EDIT);
		allWordsToRowNumberMap.put(rowNumber, addedWord);

	}

	public void showNextWord(LoadNextWordsHandler loadNextWords) {
		lastRowVisible++;
		ListRow<Word> visibleRow = listPanelCreator
				.addRow(allWordsToRowNumberMap.get(lastRowVisible).getWord(),
						lastRowVisible + 1, true, loadNextWords,
						InputGoal.EDIT);
		allWordsToRowNumberMap.put(lastRowVisible, visibleRow);
	}

	public void showWordsStartingFromRow(int firstRowToLoad) {
		listPanelCreator.clear();
		if (firstRowToLoad > 0) {
			listPanelCreator.enableButtonShowPreviousWords();
		}
		lastRowVisible = Math.max(firstRowToLoad - getMaximumWordsToShow(), -1);
		LoadNextWordsHandler loadNextWordsHandler = listPanelCreator
				.getLoadNextWordsHandler();
		for (int i = 0; i < getMaximumWordsToShow() && loadNextWordsHandler
				.shouldContinue(lastRowVisible,
						allWordsToRowNumberMap.size() - 1); i++) {
			showNextWord(loadNextWordsHandler);
		}
	}

	public void clearVisibleRows() {
		listPanelCreator.removeWordsFromRangeInclusive(
				new Range(1, listPanelCreator.getNumberOfListRows()));
	}

	public void removeRowsFromRangeInclusive(Range range) {
		listPanelCreator.removeWordsFromRangeInclusive(range);
	}

	//TODO not the best idea to pass the boolean "is for search panel" - maybe keep it as field
	public void addNewWord(InputGoal inputGoal) {
		add(wordInitializer.initializeElement(), inputGoal);
	}

	public MainPanel getPanelWithSelectedInput() {
		return getRowWithSelectedInput().getWrappingPanel();
	}

	public void addSwitchBetweenInputsFailListener(
			SwitchBetweenInputsFailListener listener) {
		switchBetweenInputsFailListeners.add(listener);
		for (ListRow<Word> listRow : allWordsToRowNumberMap.values()) {
			listRow.getWrappingPanel()
					.addSwitchBetweenInputsFailedListener(listener);
		}
	}

	public ListRow<Word> getRowWithSelectedInput() {
		for (ListRow<Word> listRow : allWordsToRowNumberMap.values()) {
			if (listRow.getWrappingPanel().hasSelectedInput()) {
				return listRow;
			}
		}
		return null;
	}

	public void selectPanelBelowOrAboveSelected(MoveDirection moveDirection) {
		//TODO this should also be handled in main panel in his selection manager
		// -> in order for this to be possible, all list rows should be contained in one
		// main panel, currently for each row theres new main panel created
		ListRow<Word> selectedRow = getRowWithSelectedInput();
		int rowNumberOfSelectedPanel = selectedRow.getRowNumber();
		int columnNumber = selectedRow.getWrappingPanel()
				.getSelectedInputIndex();
		MainPanel panelBelowOrAbove = null;
		for (ListRow<Word> listRow : allWordsToRowNumberMap.values()) {
			if (listRow.getRowNumber()
					== rowNumberOfSelectedPanel + moveDirection
					.getIncrementValue()) {
				panelBelowOrAbove = listRow.getWrappingPanel();
			}
		}
		if (panelBelowOrAbove != null) {
			panelBelowOrAbove.selectInputInColumn(columnNumber);
			listPanelCreator.scrollTo(panelBelowOrAbove.getPanel());
		}
		else {
			switchBetweenInputsFailListeners.forEach(listener -> listener
					.switchBetweenInputsFailed(
							selectedRow.getWrappingPanel().getSelectedInput(),
							moveDirection));
		}
	}

	public void toggleEnabledState() {
		listPanelCreator.toggleEnabledState();
	}
}
