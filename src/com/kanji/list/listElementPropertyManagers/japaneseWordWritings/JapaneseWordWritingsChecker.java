package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.kanji.constants.enums.InputGoal;
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
	private InputGoal inputGoal;
	private JapaneseWriting japaneseWritingToCheck;
	private JapaneseWordWritingsInputManager writingsInputManager;

	public JapaneseWordWritingsChecker(JapaneseWriting japaneseWritingToCheck,
			InputGoal inputGoal) {
		this.japaneseWritingToCheck = japaneseWritingToCheck;
		this.inputGoal = inputGoal;
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
		//TODO try to define the logic in japanese writing equals method/
		// otherwise we duplicate code
		//the only problem is how to put there boolean of whether we add or search for word
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

		if (inputGoal.equals(InputGoal.SEARCH)) {
			if (JapaneseWritingUtilities.areKanjiWritingsEmpty(searchedKanji)) {
				return true;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji);
			}
		}
		else{
			if (JapaneseWritingUtilities
					.areKanjiWritingsEmpty(existingKanjiWritings)) {
				return true;
			}
			else if (!JapaneseWritingUtilities
					.areKanjiWritingsEmpty(existingKanjiWritings)
					&& JapaneseWritingUtilities
					.areKanjiWritingsEmpty(searchedKanji)) {
				return false;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji)
						|| searchedKanji.containsAll(existingKanjiWritings);
			}
		}
	}

	@Override
	public JapaneseWriting validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {

		errorDetails = "";
		String newValue = valueToConvert.getText();
		boolean isKana = writingsInputManager.getKanaInput() == valueToConvert;
		JapaneseWriting writingToAdd;
		if (JapaneseWritingUtilities.isInputEmpty(newValue, isKana)) {
			writingToAdd = japaneseWritingToCheck;
		}
		else {
			if (!JapaneseWritingUtilities.isInputValid(newValue, isKana)) {
				String exceptionMessage = isKana ?
						ExceptionsMessages.KANA_WRITING_INCORRECT :
						ExceptionsMessages.KANJI_WRITING_INCORRECT;
				errorDetails += String.format(exceptionMessage, newValue);
				writingToAdd = null;
			}
			else {
				if (isKana) {
					japaneseWritingToCheck.setKanaWriting(newValue);
					writingToAdd = japaneseWritingToCheck;
				}
				else {
					boolean isNewWriting = writingsInputManager
							.addKanjiInput(valueToConvert);
					if (!isNewWriting) {
						errorDetails = String
								.format(ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW,
										newValue);
						writingToAdd = null;
					}
					else {
						japaneseWritingToCheck
								.replaceKanji(findKanjiPreviousValue(),
										newValue);
						writingToAdd = japaneseWritingToCheck;
					}
				}
			}
		}

		removeEmptyValues();
		return writingToAdd;

	}

	private void removeEmptyValues() {
		Set<String> notEmptyKanjiValues = new HashSet<>();
		for (String kanjiWriting : japaneseWritingToCheck.getKanjiWritings()) {
			if (!JapaneseWritingUtilities.isInputEmpty(kanjiWriting, false)) {
				notEmptyKanjiValues.add(kanjiWriting);
			}
		}
		japaneseWritingToCheck.setKanjiWritings(notEmptyKanjiValues);
	}

	private String findKanjiPreviousValue() {
		Set<String> kanjiWritings = japaneseWritingToCheck.getKanjiWritings();
		Set<String> copiedKanjiWritings = new HashSet<>(kanjiWritings);
		for (JTextComponent kanjiInput : writingsInputManager
				.getKanjiInputs()) {
			copiedKanjiWritings.remove(kanjiInput.getText());
		}
		if (copiedKanjiWritings.size() > 1) {
			throw new IllegalStateException(
					"Found more than 1 previous value.");
		}
		if (copiedKanjiWritings.isEmpty()) {
			return "";
		}
		return copiedKanjiWritings.iterator().next();

	}

	@Override
	public void setProperty(JapaneseWord japaneseWord,
			JapaneseWriting newWriting) {
		japaneseWord.addWriting(newWriting);
	}

	@Override
	public String getPropertyDefinedException(JapaneseWriting writing) {
		return String
				.format(ExceptionsMessages.DUPLICATED_KANJI_WRITING_WITHIN_ROW,
						writing.getDisplayedText());
	}
}
