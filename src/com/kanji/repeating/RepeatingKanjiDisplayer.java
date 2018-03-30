package com.kanji.repeating;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;
import com.kanji.utilities.KanjiCharactersReader;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RepeatingKanjiDisplayer
		implements RepeatingWordDisplayer<KanjiInformation> {

	private MainPanel fullWordInformationPanel;
	private MainPanel recognizingWordPanel;
	private JTextComponent wordTextArea;
	private KanjiCharactersReader kanjiCharactersReader;
	private Set<KanjiInformation> currentProblematicKanjis;
	private Set<KanjiInformation> problematicKanjis;

	public RepeatingKanjiDisplayer(Font kanjiFont) {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordTextArea = GuiMaker.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.JUSTIFIED)
						.text("").enabled(false).backgroundColor(Color.WHITE)
						.border(BorderFactory
								.createLineBorder(BasicColors.NAVY_BLUE)));
		wordTextArea.setFont(kanjiFont);
		fullWordInformationPanel = new MainPanel(null);
		fullWordInformationPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, wordTextArea));
		recognizingWordPanel = new MainPanel(null);
		problematicKanjis = new HashSet<>();
		currentProblematicKanjis = new HashSet<>();
	}

	public void addProblematicKanjis(Set<KanjiInformation> integers) {
		problematicKanjis.addAll(integers);
	}

	@Override
	public void showWordFullInformation(KanjiInformation kanjiInformation) {
		wordTextArea.setText(kanjiCharactersReader
				.getKanjiById(kanjiInformation.getKanjiID()));

	}

	@Override
	public void setAllProblematicWords(Set<KanjiInformation> problematicWords) {
		problematicKanjis = problematicWords;
	}

	@Override
	public void showRecognizingWordPanel() {

	}

	@Override
	public JPanel getFullInformationPanel() {
		return fullWordInformationPanel.getPanel();
	}

	@Override
	public JPanel getRecognizingWordPanel() {
		return recognizingWordPanel.getPanel();
	}

	@Override
	public void markWordAsProblematic(KanjiInformation kanjiInformation) {
		problematicKanjis.add(kanjiInformation);
		currentProblematicKanjis.add(kanjiInformation);
	}

	@Override
	public void removeWordFromProblematic(KanjiInformation kanjiInformation) {
		problematicKanjis.remove(kanjiInformation);
		currentProblematicKanjis.remove(kanjiInformation);
	}

	@Override
	public String getWordHint(KanjiInformation kanjiInformation) {
		return kanjiInformation.getKanjiKeyword();
	}

	@Override
	public Set<KanjiInformation> getProblematicWords() {
		return currentProblematicKanjis;
	}

	@Override
	public RepeatingState getRepeatingState(TimeSpent timeSpent,
			RepeatingInformation repeatingInformation,
			Set<KanjiInformation> words) {
		RepeatingState<KanjiInformation> kanjiRepeatingState = new RepeatingState<>(
				timeSpent, repeatingInformation, currentProblematicKanjis,
				words);
		return kanjiRepeatingState;
	}

	@Override
	public boolean hasProblematicWords() {
		return !currentProblematicKanjis.isEmpty();
	}

	@Override
	public void clearRepeatingData() {
		currentProblematicKanjis.clear();
	}

}
