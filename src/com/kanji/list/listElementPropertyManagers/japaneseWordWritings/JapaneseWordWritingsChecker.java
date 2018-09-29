package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.swing.text.JTextComponent;
import java.util.HashSet;
import java.util.List;
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

	public JTextComponent getAnyKanjiInput() {
		List<JTextComponent> kanjiInputs = writingsInputManager
				.getKanjiInputs();

		return kanjiInputs.isEmpty() ? null : kanjiInputs.get(0);
	}

	@Override
	public String getInvalidPropertyReason() {
		return errorDetails;
	}

	@Override
	public boolean isPropertyFound(JapaneseWriting searchedWriting,
			JapaneseWord word) {
		for (JapaneseWriting writing : word.getWritings()) {
			return areWritingsEqual(writing, searchedWriting, inputGoal);
		}

		return true;
	}

	@Override
	public String getPropertyValue(JapaneseWord japaneseWord) {
		throw new NotImplementedException();
	}

	@Override
	public JapaneseWriting validateInputAndConvertToProperty(
			JTextComponent valueToConvert) {

		String newValue = valueToConvert.getText();
		char lastCharacter = newValue.charAt(newValue.length() - 1);
		if (lastCharacter == 'ｎ') {
			newValue = newValue.replace('ｎ', 'ん');
			valueToConvert.setText(newValue);
		}

		errorDetails = "";
		TypeOfJapaneseWriting typeOfJapaneseWriting;
		if (writingsInputManager.getKanaInput() == valueToConvert
				&& writingsInputManager.getKanjiInputs()
				.contains(valueToConvert)) {
			typeOfJapaneseWriting = TypeOfJapaneseWriting.KANA_OR_KANJI;
		}
		else if (writingsInputManager.getKanaInput() == valueToConvert) {
			typeOfJapaneseWriting = TypeOfJapaneseWriting.KANA;
		}
		else if (writingsInputManager.getKanjiInputs()
				.contains(valueToConvert)) {
			typeOfJapaneseWriting = TypeOfJapaneseWriting.KANJI;
		}
		else {
			throw new IllegalStateException(
					"input field is not kana or kanji or even both");
		}
		boolean isKana = typeOfJapaneseWriting
				.equals(TypeOfJapaneseWriting.KANA);

		JapaneseWriting writingToAdd;

		if (JapaneseWritingUtilities
				.isInputEmpty(newValue, typeOfJapaneseWriting)) {
			writingToAdd = japaneseWritingToCheck;
			if (typeOfJapaneseWriting.equals(TypeOfJapaneseWriting.KANJI)) {
				japaneseWritingToCheck
						.replaceKanji(findKanjiPreviousValue(), "");
			}
			else {
				japaneseWritingToCheck.setKanaWriting("");
			}

		}
		else {
			if (typeOfJapaneseWriting
					.equals(TypeOfJapaneseWriting.KANA_OR_KANJI)
					&& !JapaneseWritingUtilities.isInputValid(newValue,
					TypeOfJapaneseWriting.KANA_OR_KANJI)) {
				String exceptionMessage = ExceptionsMessages.KANA_OR_KANJI_WRITING_INCORRECT;
				errorDetails += String.format(exceptionMessage, newValue);
				writingToAdd = null;
			}
			else if (!JapaneseWritingUtilities
					.isInputValid(newValue, typeOfJapaneseWriting)) {
				String exceptionMessage = isKana ?
						ExceptionsMessages.KANA_WRITING_INCORRECT :
						ExceptionsMessages.KANJI_WRITING_INCORRECT;
				errorDetails += String.format(exceptionMessage, newValue);
				writingToAdd = null;
			}
			else {
				if (isKana || typeOfJapaneseWriting
						.equals(TypeOfJapaneseWriting.KANA_OR_KANJI)) {
					japaneseWritingToCheck.setKanaWriting(newValue);
					writingToAdd = japaneseWritingToCheck;
				}
				else {
					if (!inputGoal.equals(InputGoal.SEARCH)
							&& JapaneseWritingUtilities.isInputEmpty(
							japaneseWritingToCheck.getKanaWriting(),
							TypeOfJapaneseWriting.KANJI)) {
						errorDetails = ExceptionsMessages.KANA_INPUT_EMPTY;
						return null;
					}
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
			if (!JapaneseWritingUtilities
					.isInputEmpty(kanjiWriting, TypeOfJapaneseWriting.KANJI)) {
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

	public static boolean areWritingsEqual(JapaneseWriting searchedWriting,
			JapaneseWriting writing, InputGoal inputGoal) {
		Set<String> searchedKanjiWritings = searchedWriting.getKanjiWritings();
		String searchedKanaWriting = searchedWriting.getKanaWriting();
		return kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
				searchedKanaWriting, writing.getKanaWriting(),
				searchedKanjiWritings, writing.getKanjiWritings(), inputGoal);

	}

	private static boolean kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
			String searchedKana, String existingWordKana,
			Set<String> searchedKanji, Set<String> existingKanjiWritings,
			InputGoal inputGoal) {

		if (JapaneseWritingUtilities.isKanaEmpty(searchedKana)
				&& JapaneseWritingUtilities.isKanaEmpty(existingWordKana)) {
			return false;
		}

		if (JapaneseWritingUtilities
				.isInputEmpty(searchedKana, TypeOfJapaneseWriting.KANA)) {
			return areKanjisSame(searchedKanji, existingKanjiWritings,
					inputGoal);
		}
		else {
			if (searchedKana.equals(existingWordKana)) {
				return areKanjisSame(searchedKanji, existingKanjiWritings,
						inputGoal);
			}
			else {
				return false;
			}
		}

	}

	private static boolean areKanjisSame(Set<String> searchedKanji,
			Set<String> existingKanjiWritings, InputGoal inputGoal) {

		if (inputGoal.equals(InputGoal.SEARCH)) {
			if (JapaneseWritingUtilities.areKanjiWritingsEmpty(searchedKanji)) {
				return true;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji);
			}
		}
		else {
			if (JapaneseWritingUtilities
					.areKanjiWritingsEmpty(existingKanjiWritings)
					&& JapaneseWritingUtilities
					.areKanjiWritingsEmpty(searchedKanji)) {
				return true;
			}
			else if (JapaneseWritingUtilities
					.areKanjiWritingsEmpty(existingKanjiWritings)
					!= JapaneseWritingUtilities
					.areKanjiWritingsEmpty(searchedKanji)) {
				return false;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji)
						|| searchedKanji.containsAll(existingKanjiWritings);
			}
		}
	}

	public void addInput(JTextComponent input,
			TypeOfJapaneseWriting typeOfWriting) {
		switch (typeOfWriting) {
		case KANA:
			setKanaInput(input);
			break;
		case KANJI:
			addKanjiInput(input);
			break;
		case KANA_OR_KANJI:
			setKanaInput(input);
			addKanjiInput(input);
			break;
		}
	}
}
