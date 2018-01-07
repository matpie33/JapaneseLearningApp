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
import java.util.List;
import java.util.function.Function;

public class RepeatingJapaneseWordsDisplayer implements
		RepeatingWordDisplayer<JapaneseWordInformation> {

	private MainPanel fullWordInformationPanel;
	private MainPanel recognizingWordPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private Set<JapaneseWordInformation> currentProblematicKanjis;
	private Set<JapaneseWordInformation> problematicKanjis;
	private JTextComponent partOfSpeechText;
	private JLabel partOfSpeechLabel;
	private JComboBox <String> partOfSpeechCombobox;
	private JLabel verbConjugationLabel;
	private JComboBox <String> verbConjugationCombobox;
	private JTextComponent verbConjugationText;
	private JLabel writingWayLabel;
	private Map<Integer, Function<JapaneseWordInformation, Set<String>>> hintTypeIntValues;

	public RepeatingJapaneseWordsDisplayer(Font kanjiFont){
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		//TODO kanjis can be loaded just once in the "get instance" method
		fullWordInformationPanel = new MainPanel (null);
		recognizingWordPanel = new MainPanel(null);
		problematicKanjis = new HashSet<>();
		currentProblematicKanjis = new HashSet<>();
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
		writingWayLabel = GuiMaker.createLabel(new ComponentOptions().text(
				Labels.WRITING_WAYS_IN_JAPANESE));
		partOfSpeechText = GuiMaker.createTextArea(new TextAreaOptions().editable(false));
		verbConjugationText = GuiMaker.createTextArea(new TextAreaOptions().editable(false));
		writingWayLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WRITING_WAYS_IN_JAPANESE));
	}

	@Override public void showWordFullInformation(JapaneseWordInformation kanjiInformation) {
		fullWordInformationPanel.clear();
		partOfSpeechText.setText(kanjiInformation.getPartOfSpeech().getPolishMeaning());
		fullWordInformationPanel.addElementsInColumnStartingFromColumn(partOfSpeechText,
				0, partOfSpeechLabel, partOfSpeechText);
		if (kanjiInformation.hasAdditionalVerbConjugationInformation()){
			fullWordInformationPanel.addElementsInColumnStartingFromColumn(verbConjugationText,0,
					verbConjugationLabel, verbConjugationText);
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

	@Override public void markWordAsProblematic(JapaneseWordInformation kanjiInformation) {
		currentProblematicKanjis.add(kanjiInformation);
	}

	@Override public void removeWordFromProblematic(JapaneseWordInformation kanjiInformation) {
		problematicKanjis.remove(kanjiInformation);
		currentProblematicKanjis.remove(kanjiInformation);
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
		return currentProblematicKanjis;
	}

	@Override
	public RepeatingState getRepeatingState (TimeSpent timeSpent,
			RepeatingInformation repeatingInformation, Set <JapaneseWordInformation> words){
		RepeatingState <JapaneseWordInformation> kanjiRepeatingState =
				new RepeatingState<>(timeSpent, repeatingInformation,
						currentProblematicKanjis,
						words);
		return kanjiRepeatingState;
	}

	@Override public boolean hasProblematicWords() {
		return !currentProblematicKanjis.isEmpty();
	}

	@Override public void addProblematicWords(Set<JapaneseWordInformation> kanjiInformations) {

	}

	@Override
	public void clearRepeatingData(){
		currentProblematicKanjis.clear();
	}

}