package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.utilities.Pair;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;

import javax.swing.text.JTextComponent;
import java.util.*;

public class JapaneseWordChecker implements
		ListElementPropertyManager<Set<JapaneseWriting>, JapaneseWord> {

	private Map<JTextComponent, JapaneseWriting> inputToWritingMap = new LinkedHashMap<>();
	private Map<JapaneseWriting, JapaneseWordWritingsChecker> writingToCheckerMap = new HashMap<>();
	private InputGoal inputGoal;
	private String invalidPropertyReason;

	public JapaneseWordChecker(InputGoal inputGoal) {
		this.inputGoal = inputGoal;
	}

	public void addInput(JTextComponent input,
			JapaneseWriting writingToWhichThisInputBelongs,
			TypeOfJapaneseWriting typeOfWriting) {
		JapaneseWordWritingsChecker checkerForWriting = getCheckerForInput(
				writingToWhichThisInputBelongs);
		checkerForWriting.addInput(input, typeOfWriting);
		remember(input, writingToWhichThisInputBelongs, checkerForWriting);
	}

	public Pair<JapaneseWriting, JapaneseWordWritingsChecker> getWritingForInput(
			JTextComponent input) {
		for (Map.Entry<JTextComponent, JapaneseWriting> inputWithWriting : inputToWritingMap.entrySet()) {
			if (inputWithWriting.getKey()
								.equals(input)) {
				return new Pair<>(inputWithWriting.getValue(),
						writingToCheckerMap.get(inputWithWriting.getValue()));
			}
		}
		return null;
	}

	private void remember(JTextComponent input,
			JapaneseWriting writingContainingThisInput,
			JapaneseWordWritingsChecker checkerForWriting) {
		inputToWritingMap.put(input, writingContainingThisInput);
		writingToCheckerMap.put(writingContainingThisInput, checkerForWriting);
	}

	private JapaneseWordWritingsChecker getCheckerForInput(
			JapaneseWriting writingContainingThisKana) {
		JapaneseWordWritingsChecker checkerForWriting = writingToCheckerMap.get(
				writingContainingThisKana);
		if (checkerForWriting == null) {
			checkerForWriting = new JapaneseWordWritingsChecker(
					writingContainingThisKana, inputGoal);
		}
		return checkerForWriting;
	}

	@Override
	public String getPropertyValue(JapaneseWord japaneseWord) {
		StringBuilder writingsText = new StringBuilder();
		for (JapaneseWriting japaneseWriting : japaneseWord.getWritings()) {
			writingsText.append(japaneseWriting.getKanaWriting());
			japaneseWriting.getKanjiWritings()
						   .forEach(kanji -> writingsText.append(" " + kanji));
		}
		return writingsText.toString();
	}

	@Override
	public String getInvalidPropertyReason() {
		return invalidPropertyReason;
	}

	@Override
	public boolean isPropertyFound(Set<JapaneseWriting> property,
			JapaneseWord japaneseWord) {
		for (JapaneseWriting writing : japaneseWord.getWritings()) {
			for (JapaneseWriting propertyWriting : property) {
				if (JapaneseWordWritingsChecker.areWritingsEqual(
						propertyWriting, writing, inputGoal)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Set<JapaneseWriting> validateInputAndConvertToProperty(
			JTextComponent textInput, JapaneseWord propertyHolder) {
		JapaneseWriting existingWritingForInput = inputToWritingMap.get(
				textInput);
		JapaneseWordWritingsChecker checkerForInput = writingToCheckerMap.get(
				existingWritingForInput);
		JapaneseWriting convertedProperty = checkerForInput.validateInputAndConvertToProperty(
				textInput, propertyHolder);
		if (convertedProperty == null) {
			invalidPropertyReason = checkerForInput.getInvalidPropertyReason();
			return null;
		}
		for (JapaneseWriting writing : writingToCheckerMap.keySet()) {
			if (writing != convertedProperty && writing.equals(
					convertedProperty)) {
				invalidPropertyReason = ExceptionsMessages.DUPLICATED_WRITINGS_IN_WORD;
				return null;
			}
		}

		updateWritingsInMaps(existingWritingForInput, convertedProperty);
		return filterNotEmptyWritings();
	}

	private Set<JapaneseWriting> filterNotEmptyWritings() {
		Set<JapaneseWriting> writings = new HashSet<>();
		for (JapaneseWriting writing : writingToCheckerMap.keySet()) {
			if (!writing.isEmpty()) {
				writings.add(writing);
			}
		}
		return writings;
	}

	private void updateWritingsInMaps(JapaneseWriting oldWriting,
			JapaneseWriting newWriting) {
		for (Map.Entry<JTextComponent, JapaneseWriting> inputAndWriting : inputToWritingMap.entrySet()) {
			if (inputAndWriting.getValue()
							   .equals(oldWriting)) {
				inputToWritingMap.put(inputAndWriting.getKey(), newWriting);
			}

		}
		JapaneseWordWritingsChecker checker = writingToCheckerMap.remove(
				oldWriting);
		writingToCheckerMap.put(newWriting, checker);
	}

	@Override
	public void setProperty(JapaneseWord japaneseWord,
			Set<JapaneseWriting> newWritings,
			Set<JapaneseWriting> previousValue) {
		japaneseWord.setWritings(newWritings);
	}

	@Override
	public String getPropertyDefinedException(Set<JapaneseWriting> property) {
		return String.format(
				ExceptionsMessages.JAPANESE_WORD_WRITINGS_ALREADY_DEFINED,
				property);
	}

	public JTextComponent getAnyKanjiInput() {
		Map<JTextComponent, ListElementPropertyManager> inputToChecker = new LinkedHashMap<>();
		return writingToCheckerMap.values()
								  .iterator()
								  .next()
								  .getAnyKanjiInput();
	}

	public void removeWriting(JapaneseWriting writing) {
		writingToCheckerMap.remove(writing);
		List<JTextComponent> inputsRemoved = new ArrayList<>();
		for (Map.Entry<JTextComponent, JapaneseWriting> inputToWriting : inputToWritingMap.entrySet()) {
			if (inputToWriting.getValue()
							  .equals(writing)) {
				inputsRemoved.add(inputToWriting.getKey());
			}
		}
		inputsRemoved.forEach(input -> inputToWritingMap.remove(input));
	}

	public InputGoal getInputGoal() {
		return inputGoal;
	}
}
