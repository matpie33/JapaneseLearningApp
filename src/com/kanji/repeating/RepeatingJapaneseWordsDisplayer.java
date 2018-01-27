package com.kanji.repeating;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.VerbConjugationType;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.utilities.StringConcatenator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class RepeatingJapaneseWordsDisplayer implements
		RepeatingWordDisplayer<JapaneseWordInformation> {

	private final Dimension wordPanelsSize = new Dimension (300, 200);
	private MainPanel fullWordInformationPanel;
	private MainPanel recognizingWordPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private Set<JapaneseWordInformation> currentProblematicWords;
	private Set<JapaneseWordInformation> problematicJapaneseWords;
	private JTextComponent partOfSpeechText;
	private JLabel partOfSpeechLabel;
	private JComboBox <String> partOfSpeechCombobox;
	private JLabel verbConjugationLabel;
	private JComboBox <String> verbConjugationCombobox;
	private JTextComponent verbConjugationText;
	private Map<Integer, Function<JapaneseWordInformation, Set<String>>> hintTypeIntValues;

	public RepeatingJapaneseWordsDisplayer(Font kanjiFont){
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		//TODO kanjis can be loaded just once in the "get instance" method
		fullWordInformationPanel = new MainPanel (null, true);
		recognizingWordPanel = new MainPanel(null, true);
		recognizingWordPanel.getPanel().setPreferredSize(wordPanelsSize);
		fullWordInformationPanel.getPanel().setPreferredSize(wordPanelsSize);

		problematicJapaneseWords = new HashSet<>();
		currentProblematicWords = new HashSet<>();
		initializeHintTypeValues();
		initializeGuiElements();
		recognizingWordPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				partOfSpeechLabel, partOfSpeechCombobox));


	}

	private void initializeHintTypeValues (){
		hintTypeIntValues = new HashMap<>();
		hintTypeIntValues.put(1, japaneseWordInformation -> new HashSet<>(
				Arrays.asList(japaneseWordInformation.getWordMeaning())));
		hintTypeIntValues.put(2, JapaneseWordInformation::getKanaWritings);
		hintTypeIntValues.put(3, JapaneseWordInformation::getKanjiWritings);

	}

	private void initializeGuiElements (){
		partOfSpeechCombobox = new JComboBox<>();
		partOfSpeechCombobox.setFocusable(false);
		Arrays.stream(PartOfSpeech.values()).forEach(partOfSpeech ->
			partOfSpeechCombobox.addItem(partOfSpeech.getPolishMeaning()));
		verbConjugationCombobox = new JComboBox<>();
		verbConjugationCombobox.setFocusable(false);
		Arrays.stream(VerbConjugationType.values()).forEach(verbConjugationType ->
				verbConjugationCombobox.addItem(verbConjugationType.getDisplayedText()));
		partOfSpeechLabel = GuiMaker.createLabel(new ComponentOptions().text(
				Labels.PART_OF_SPEECH));
		verbConjugationLabel = GuiMaker.createLabel(new ComponentOptions().text(
				Labels.VERB_CONJUGATION));
		partOfSpeechText = GuiMaker.createTextArea(new TextAreaOptions().editable(false));
		verbConjugationText = GuiMaker.createTextArea(new TextAreaOptions().editable(false));
	}

	@Override public void showWordFullInformation(JapaneseWordInformation kanjiInformation) {
		fullWordInformationPanel.clear();
		partOfSpeechText.setText(kanjiInformation.getPartOfSpeech().getPolishMeaning());
		fullWordInformationPanel.addElementsInColumnStartingFromColumn(partOfSpeechText,
				0, FillType.HORIZONTAL, partOfSpeechLabel, partOfSpeechText);
		if (kanjiInformation.hasAdditionalVerbConjugationInformation()){
			fullWordInformationPanel.addElementsInColumnStartingFromColumn(verbConjugationText,
					0, FillType.HORIZONTAL, verbConjugationLabel, verbConjugationText);
			verbConjugationText.setText(kanjiInformation.getVerbConjugationInformation());
		}

		CommonGuiElementsMaker.addKanaAndKanjiWritingsToPanel(kanjiInformation,
				fullWordInformationPanel, 0, Color.BLACK);

	}

	@Override public JPanel getFullInformationPanel() {
		return fullWordInformationPanel.getPanel();
	}

	@Override public JPanel getRecognizingWordPanel() {
		return recognizingWordPanel.getPanel();
	}

	@Override public void showRecognizingWordPanel() {
		partOfSpeechCombobox.setSelectedIndex(0);
	}

	@Override public void markWordAsProblematic(JapaneseWordInformation wordInformation) {
		problematicJapaneseWords.add(wordInformation);
		currentProblematicWords.add(wordInformation);
	}

	@Override public void removeWordFromProblematic(JapaneseWordInformation wordInformation) {
		problematicJapaneseWords.remove(wordInformation);
		currentProblematicWords.remove(wordInformation);
	}

	@Override
	public String getWordHint (JapaneseWordInformation kanjiInformation){
		int possibilitiesAmount;
		if (kanjiInformation.hasKanjiWriting()){
			possibilitiesAmount = 3;
		}
		else{
			possibilitiesAmount = 2;
		}
		Random random = new Random();
		int randomNumber = random.nextInt(possibilitiesAmount)+1;
		Function <JapaneseWordInformation, Set <String>> hintGetter
				= hintTypeIntValues.get(randomNumber);
		return StringConcatenator.concatenateStrings(new ArrayList<>(
				(hintGetter.apply(kanjiInformation))));
	}



	@Override public Set<JapaneseWordInformation> getProblematicWords() {
		return currentProblematicWords;
	}

	@Override
	public RepeatingState getRepeatingState (TimeSpent timeSpent,
			RepeatingInformation repeatingInformation, Set <JapaneseWordInformation> words){
		RepeatingState <JapaneseWordInformation> kanjiRepeatingState =
				new RepeatingState<>(timeSpent, repeatingInformation, currentProblematicWords,
						words);
		return kanjiRepeatingState;
	}

	@Override public boolean hasProblematicWords() {
		return !currentProblematicWords.isEmpty();
	}

	@Override
	public void clearRepeatingData(){
		currentProblematicWords.clear();
	}

	@Override public void setAllProblematicWords(Set<JapaneseWordInformation> problematicWords) {
		problematicJapaneseWords = problematicWords;
	}

}
