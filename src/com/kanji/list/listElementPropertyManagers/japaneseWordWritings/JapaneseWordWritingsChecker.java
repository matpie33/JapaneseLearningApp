package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.text.JTextComponent;
import java.util.Set;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<JapaneseWriting, JapaneseWord> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private String errorDetails = "";
	private boolean addingWord; //TODO rename in other places too (from kanaRequired)
	private JapaneseWriting japaneseWritingToCheck;
	private JapaneseWordWritingsInputManager writingsInputManager;

	public JapaneseWordWritingsChecker(
			JapaneseWordWritingsInputManager inputManager,
			JapaneseWriting japaneseWritingToCheck, boolean addingWord) {
		this.japaneseWritingToCheck = japaneseWritingToCheck;
		this.addingWord = addingWord;
		this.writingsInputManager = inputManager;
	}

	@Override
	public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override
	public boolean isPropertyFound(JapaneseWriting searchedWriting,
			JapaneseWord word) {
		boolean japaneseWordContainsTheseWritings = false;
		for (JapaneseWriting writing : word.getWritings()) {
			Set<String> searchedKanjiWritings = searchedWriting
					.getKanjiWritings();
			String searchedKanaWriting = searchedWriting.getKanaWriting();
			if (kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
					searchedKanaWriting, writing.getKanaWriting(),
					searchedKanjiWritings, writing.getKanjiWritings())) {
				japaneseWordContainsTheseWritings = true;
				break;
			}
		}
		if (!japaneseWordContainsTheseWritings) {
			return false;
		}

		return true;
	}

	private boolean kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
			String searchedKana, String existingWordKana,
			Set<String> searchedKanji, Set<String> existingKanjiWritings) {
		//TODO move the logic checking if textfield is empty (default value or no value) to one place and use it everywhere, now its scattered

		if (JapaneseWritingUtilities.isInputEmpty(searchedKana, true)) {
			return areKanjisSame(searchedKanji, existingKanjiWritings);
		}
		else {
			if (searchedKana.equals(existingWordKana)) {
				return areKanjisSame(searchedKanji, existingKanjiWritings);
			}
			else {
				return false;
			}
		}

	}

	private boolean areKanjisSame(Set<String> searchedKanji,
			Set<String> existingKanjiWritings) {
		if (addingWord) {
			if (JapaneseWritingUtilities.areKanjiWritingsEmpty(searchedKanji)) {
				return JapaneseWritingUtilities
						.areKanjiWritingsEmpty(existingKanjiWritings);
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji)
						|| searchedKanji.containsAll(existingKanjiWritings);
			}
		}
		else {
			if (JapaneseWritingUtilities.areKanjiWritingsEmpty(searchedKanji)) {
				return true;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji);
			}
		}

	}

	@Override
	public JapaneseWriting validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {

		errorDetails = "";
		String textValue = valueToConvert.getText();
		boolean isKana = writingsInputManager.getKanaInput() == valueToConvert;
		if (JapaneseWritingUtilities.isInputEmpty(textValue, isKana)) {
			return japaneseWritingToCheck;
		}
		if (!JapaneseWritingUtilities.isInputValid(textValue, isKana)) {
			//TODO validate if kana is duplicated inside the word - isSameAs method for JapaneseWriting
			String exceptionMessage = isKana ?
					ExceptionsMessages.KANA_WRITING_INCORRECT :
					ExceptionsMessages.KANJI_WRITING_INCORRECT;
			errorDetails += String.format(exceptionMessage, textValue);
			return null;
		}
		else {
			if (isKana) {
				japaneseWritingToCheck.setKanaWriting(textValue);
				return japaneseWritingToCheck;
			}
			boolean isNewWriting = writingsInputManager
					.addKanjiInput(valueToConvert);
			if (!isNewWriting) {
				errorDetails = ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW;
				return null;
			}
			else {
				if (isKana) {
					japaneseWritingToCheck.setKanaWriting(textValue);
				}
				else {
					japaneseWritingToCheck.addKanjiWriting(textValue);
				}
				return japaneseWritingToCheck;
			}
		}

	}


	private String findKanjiPreviousValue() {
		Set<String> kanjiWritings = japaneseWritingToCheck.getKanjiWritings();
		for (String kanjiWriting : kanjiWritings) {
			boolean foundKanjiWriting = false;
			for (JTextComponent kanjiInput : writingsInputManager
					.getKanjiInputs()) {
				if (kanjiInput.getText().equals(kanjiWriting)) {
					foundKanjiWriting = true;
					break;
				}
			}
			if (!foundKanjiWriting) {
				return kanjiWriting;
			}
		}
		throw new IllegalStateException("No previuos value found");
	}

	@Override
	public void setProperty(JapaneseWord japaneseWord,
			JapaneseWriting writingChange) {
		japaneseWord.setWriting(writingChange);
	}

	@Override
	public String getPropertyDefinedException(JapaneseWriting writing) {
		return String.format(ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW,
				writing.getDisplayedText());
	}
}
