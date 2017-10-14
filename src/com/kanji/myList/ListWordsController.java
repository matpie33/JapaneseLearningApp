package com.kanji.myList;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.listSearching.PropertyManager;
import com.kanji.model.ListRow;

public class ListWordsController<Word> {
	private static final long serialVersionUID = -3144332338336535803L;
	private List<ListRow<Word>> wordsList;
	private ListPanelMaker<Word> rowCreator;
	private ApplicationController applicationController;

	public ListWordsController(ListRowMaker<Word> listRowMaker, String title,
			ApplicationController applicationController) {
		this.applicationController = applicationController;
		wordsList = new ArrayList<>();
		rowCreator = new ListPanelMaker<>(listRowMaker, this);
		this.rowCreator.setTitle(title);
	}

	public boolean add(Word r) {
		if (!isWordDefined(r)) {
			wordsList.add(rowCreator.addRow(r));
			return true;
		}
		return false;
	}

	private boolean isWordDefined(Word r) {
		for (ListRow<Word> listRow : wordsList) {
			if (listRow.getWord().equals(r)) {
				return true;
			}
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

	public <Property> boolean isPropertyDefined(PropertyManager<Property, Word> propertyManager,
			Property propertyToCheck) {
		for (ListRow<Word> listRow : wordsList) {
			if (propertyManager.isPropertyFound(propertyToCheck, listRow.getWord())) {
				return true;
			}
		}
		return false;
	}

	public AbstractAction createDeleteRowAction(Word word) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String rowSpecificPrompt = "";
				if (word instanceof KanjiInformation) {
					rowSpecificPrompt = Prompts.KANJI_ROW;
				}
				if (word instanceof RepeatingInformation) {
					rowSpecificPrompt = Prompts.REPEATING_ELEMENT;
				}

				if (!applicationController.showConfirmDialog(
						String.format(Prompts.DELETE_ELEMENT, rowSpecificPrompt))) {
					return;
				}
				remove(word);
				applicationController.saveProject();
			}
		};
	}

}