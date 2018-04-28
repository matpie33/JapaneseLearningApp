package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;

import javax.swing.text.JTextComponent;
import java.util.*;

public class JapaneseWordChecker implements
		ListElementPropertyManager<Set<JapaneseWriting>, JapaneseWord> {

	private Map<JTextComponent, JapaneseWriting> inputToWritingMap = new HashMap<>();
	private Map<JapaneseWriting, JapaneseWordWritingsChecker> writingToCheckerMap = new HashMap<>();
	private InputGoal inputGoal;
	private String invalidPropertyReason;

	public JapaneseWordChecker(InputGoal inputGoal) {
		this.inputGoal = inputGoal;
	}

	public void addKanaInput(JTextComponent kanaInput,
			JapaneseWriting writingContainingThisKana) {
		JapaneseWordWritingsChecker checkerForWriting = getCheckerForInput(
				writingContainingThisKana);
		checkerForWriting.setKanaInput(kanaInput);
		remember(kanaInput, writingContainingThisKana, checkerForWriting);
	}

	private void remember(JTextComponent input,
			JapaneseWriting writingContainingThisInput,
			JapaneseWordWritingsChecker checkerForWriting) {
		inputToWritingMap.put(input, writingContainingThisInput);
		writingToCheckerMap.put(writingContainingThisInput, checkerForWriting);
	}

	private JapaneseWordWritingsChecker getCheckerForInput(
			JapaneseWriting writingContainingThisKana) {
		JapaneseWordWritingsChecker checkerForWriting = writingToCheckerMap
				.get(writingContainingThisKana);
		if (checkerForWriting == null) {
			checkerForWriting = new JapaneseWordWritingsChecker(
					writingContainingThisKana, inputGoal);
		}
		return checkerForWriting;
	}

	public void addKanjiInput(JTextComponent kanjiInput,
			JapaneseWriting writingContainingThisKanji) {
		JapaneseWordWritingsChecker checkerForWriting = getCheckerForInput(
				writingContainingThisKanji);
		checkerForWriting.addKanjiInput(kanjiInput);
		remember(kanjiInput, writingContainingThisKanji, checkerForWriting);
	}

	@Override
	public String getInvalidPropertyReason() {
		return invalidPropertyReason;
	}

	@Override
	public boolean isPropertyFound(Set<JapaneseWriting> property,
			JapaneseWord japaneseWord) {
		for (JapaneseWriting writing : property) {
			if (!writingToCheckerMap.get(writing).
					isPropertyFound(writing, japaneseWord)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Set<JapaneseWriting> validateInputAndConvertToProperty(
			JTextComponent textInput) {
		JapaneseWriting existingWritingForInput = inputToWritingMap
				.get(textInput);
		JapaneseWordWritingsChecker checkerForInput = writingToCheckerMap
				.get(existingWritingForInput);
		JapaneseWriting convertedProperty = checkerForInput
				.validateInputAndConvertToProperty(textInput);
		if (convertedProperty == null) {
			invalidPropertyReason = checkerForInput.getInvalidPropertyReason();
			return null;
		}
		for (JapaneseWriting writing : writingToCheckerMap.keySet()) {
			if (writing != convertedProperty && writing
					.equals(convertedProperty)) {
				invalidPropertyReason = ExceptionsMessages.DUPLICATED_WRITINGS_IN_WORD;
				return null;
			}
		}

		updateWritingsInMaps(existingWritingForInput, convertedProperty);
		return filterNotEmptyWritings();
	}

	private Set<JapaneseWriting> filterNotEmptyWritings() {
		Set <JapaneseWriting> writings = new HashSet<>();
		for (JapaneseWriting writing : writingToCheckerMap.keySet()) {
			if (!writing.isEmpty()){
				writings.add(writing);
			}
		}
		return writings;
	}

	private void updateWritingsInMaps(JapaneseWriting oldWriting,
			JapaneseWriting newWriting) {
		for (Map.Entry<JTextComponent, JapaneseWriting> inputAndWriting : inputToWritingMap
				.entrySet()) {
			if (inputAndWriting.getValue().equals(oldWriting)) {
				inputToWritingMap.put(inputAndWriting.getKey(), newWriting);
			}

		}
		JapaneseWordWritingsChecker checker = writingToCheckerMap
				.remove(oldWriting);
		writingToCheckerMap.put(newWriting, checker);
	}

	@Override
	public void setProperty(JapaneseWord japaneseWord,
			Set<JapaneseWriting> newWritings) {
		japaneseWord.setWritings(newWritings);
	}

	@Override
	public String getPropertyDefinedException(Set<JapaneseWriting> property) {
		return String
				.format(ExceptionsMessages.JAPANESE_WORD_WRITINGS_ALREADY_DEFINED,
						property);
	}

	public Map<JTextComponent, ListElementPropertyManager> getInputToCheckerMap() {
		Map<JTextComponent, ListElementPropertyManager> inputToChecker = new HashMap<>();
		for (JTextComponent input : inputToWritingMap.keySet()) {
			inputToChecker.put(input, this);
		}
		return inputToChecker;
	}

	public void removeWriting(JapaneseWriting writing) {
		writingToCheckerMap.remove(writing);
		List<JTextComponent> inputsRemoved = new ArrayList<>();
		for (Map.Entry<JTextComponent, JapaneseWriting> inputToWriting : inputToWritingMap
				.entrySet()) {
			if (inputToWriting.getValue().equals(writing)) {
				inputsRemoved.add(inputToWriting.getKey());
			}
		}
		inputsRemoved.forEach(input -> inputToWritingMap.remove(input));
	}

	public InputGoal getInputGoal() {
		return inputGoal;
	}
}
