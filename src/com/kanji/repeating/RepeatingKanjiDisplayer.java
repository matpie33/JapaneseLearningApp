package com.kanji.repeating;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;
import com.kanji.utilities.KanjiCharactersReader;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RepeatingKanjiDisplayer implements RepeatingWordDisplayer<Kanji> {

	private MainPanel fullWordInformationPanel;
	private MainPanel recognizingWordPanel;
	private JTextComponent wordTextArea;
	private KanjiCharactersReader kanjiCharactersReader;
	private Set<Kanji> currentProblematicKanjis;
	private Set<Kanji> problematicKanjis;

	public RepeatingKanjiDisplayer(Font kanjiFont) {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordTextArea = GuiElementsCreator.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.JUSTIFIED)
						.text("").editable(false).border(BorderFactory
						.createLineBorder(BasicColors.BLUE_DARK_2)));
		wordTextArea.setFont(kanjiFont);
		fullWordInformationPanel = new MainPanel(null);
		fullWordInformationPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, wordTextArea));
		recognizingWordPanel = new MainPanel(null);
		problematicKanjis = new HashSet<>();
		currentProblematicKanjis = new HashSet<>();
	}

	public void addProblematicKanjis(Set<Kanji> integers) {
		problematicKanjis.addAll(integers);
	}

	@Override
	public void showWordFullInformation(Kanji kanji) {
		wordTextArea.setText(kanjiCharactersReader.getKanjiById(kanji.getId()));

	}

	@Override
	public void setAllProblematicWords(Set<Kanji> problematicWords) {
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
	public void markWordAsProblematic(Kanji kanji) {
		problematicKanjis.add(kanji);
		currentProblematicKanjis.add(kanji);
	}

	@Override
	public void removeWordFromProblematic(Kanji kanji) {
		problematicKanjis.remove(kanji);
		currentProblematicKanjis.remove(kanji);
	}

	@Override
	public String getWordHint(Kanji kanji) {
		return kanji.getKeyword();
	}

	@Override
	public Set<Kanji> getProblematicWords() {
		return currentProblematicKanjis;
	}

	@Override
	public RepeatingState getRepeatingState(TimeSpent timeSpent,
			RepeatingData repeatingData, Set<Kanji> words) {
		RepeatingState<Kanji> kanjiRepeatingState = new RepeatingState<>(
				timeSpent, repeatingData, currentProblematicKanjis, words);
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
