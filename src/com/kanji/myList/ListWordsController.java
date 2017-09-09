package com.kanji.myList;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kanji.model.ListRow;

public class ListWordsController<Word> {
	private static final long serialVersionUID = -3144332338336535803L;
	private List<ListRow<Word>> wordsList;
	private ListPanelMaker<Word> rowCreator;

	public ListWordsController(ListRowMaker<Word> listRowMaker, String title, MyList<Word> list) {
		wordsList = new ArrayList<>();
		rowCreator = new ListPanelMaker<>(listRowMaker, this);
		this.rowCreator.setList(list);
		this.rowCreator.setTitle(title);
	}

	public boolean add(Word r) {
		if (!wordsList.contains(r)) {
			wordsList.add(rowCreator.addRow(r));
			return true;
		}
		return false;

	}

	public void remove(Word word) {
		ListRow<Word> listRow = findListRowContainingWord(word);
		int rowNumber = rowCreator.removeRow(listRow.getPanel());
		updateRowNumbers(rowNumber);
		wordsList.remove(listRow);
	}

	private void updateRowNumbers(int startingIndex) {
		for (int i = startingIndex; i < wordsList.size(); i++) {
			JLabel label = wordsList.get(i).getIndexLabel();
			label.setText(rowCreator.createTextForRowNumber(i));
		}

	}

	private ListRow<Word> findListRowContainingWord(Word r) {
		for (int i = 0; i < wordsList.size(); i++) {
			Word word = wordsList.get(i).getWord();
			if (word.equals(r)) {
				return wordsList.get(i);
			}
		}
		return null;
	}

	public List<Word> getWords() {
		List<Word> words = new ArrayList<>();
		for (ListRow<Word> listRow : wordsList) {
			words.add(listRow.getWord());
		}
		return words;
	}

	public int getNumberOfWords() {
		return wordsList.size();
	}

	public void replace(Word wordToReplace, Word newWord) {
		getWords().set(getWords().indexOf(wordToReplace), newWord);
	}

	public Word getWordInRow(int rowNumber1Based) {
		return wordsList.get(rowNumber1Based).getWord();
	}

	public void highlightRowAndScroll(int rowNumber, boolean clearLastHighlightedWord) {
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
		wordsList.clear();
		rowCreator.clear();
	}

}
