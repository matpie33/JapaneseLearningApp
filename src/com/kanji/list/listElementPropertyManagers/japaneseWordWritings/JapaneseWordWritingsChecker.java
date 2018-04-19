package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.text.JTextComponent;
import java.util.HashSet;
import java.util.Set;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder
		implements ListElementPropertyManager<JapaneseWriting, JapaneseWord> {
	private String errorDetails = "";
	private boolean addingWord;
	private JapaneseWriting japaneseWritingToCheck;
	private JapaneseWordWritingsInputManager writingsInputManager;

	public JapaneseWordWritingsChecker(JapaneseWriting japaneseWritingToCheck,
			boolean addingWord) {
		this.japaneseWritingToCheck = japaneseWritingToCheck;
		this.addingWord = addingWord;
		writingsInputManager = new JapaneseWordWritingsInputManager();
	}

	public void setKanaInput(JTextComponent kanaInput) {
		writingsInputManager.setKanaInput(kanaInput);
		japaneseWritingToCheck.setKanaWriting(kanaInput.getText());
	}

	public void addKanjiInput(JTextComponent kanjiInput) {
		writingsInputManager.addKanjiInput(kanjiInput);
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
		String newValue = valueToConvert.getText();
		boolean isKana = writingsInputManager.getKanaInput() == valueToConvert;
		if (JapaneseWritingUtilities.isInputEmpty(newValue, isKana)) {
			return japaneseWritingToCheck;
		}
		if (!JapaneseWritingUtilities.isInputValid(newValue, isKana)) {
			//TODO validate if kana is duplicated inside the word - isSameAs method for JapaneseWriting
			String exceptionMessage = isKana ?
					ExceptionsMessages.KANA_WRITING_INCORRECT :
					ExceptionsMessages.KANJI_WRITING_INCORRECT;
			errorDetails += String.format(exceptionMessage, newValue);
			return null;
		}
		else {
			if (isKana) {
				japaneseWritingToCheck.setKanaWriting(newValue);
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
					japaneseWritingToCheck.setKanaWriting(newValue);
				}
				else {
					japaneseWritingToCheck
							.replaceKanji(findKanjiPreviousValue(), newValue);
				}
				return japaneseWritingToCheck;
			}
		}

	}

	private String findKanjiPreviousValue() {
		Set<String> kanjiWritings = japaneseWritingToCheck.getKanjiWritings();
		Set<String> copiedKanjiWritings = new HashSet<>(kanjiWritings);
		for (JTextComponent kanjiInput : writingsInputManager
				.getKanjiInputs()) {
			copiedKanjiWritings.remove(kanjiInput.getText());
		}
		if (copiedKanjiWritings.size() > 1) {
			throw new IllegalStateException("Found more than 1 previous value.");
		}
		if (copiedKanjiWritings.isEmpty()){
			return "";
		}
		return copiedKanjiWritings.iterator().next();

	}

	@Override
	public void setProperty(JapaneseWord japaneseWord,
			JapaneseWriting writingChange) {
		japaneseWord.setWriting(writingChange);
	}

	@Override
	public String getPropertyDefinedException(JapaneseWriting writing) {
		return String
				.format(ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW,
						writing.getDisplayedText());
	}
}
