package com.kanji.repeating;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.TypeOfWordForRepeating;
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

public class RepeatingKanjiDisplayer implements RepeatingWordsDisplayer<Kanji> {

	private MainPanel fullWordInformationPanel;
	private MainPanel recognizingWordPanel;
	private JTextComponent wordTextArea;
	private KanjiCharactersReader kanjiCharactersReader;


	public RepeatingKanjiDisplayer(Font kanjiFont) {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordTextArea = GuiElementsCreator.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.JUSTIFIED)
						.text("").editable(false).font(kanjiFont).border(BorderFactory
						.createLineBorder(BasicColors.BLUE_DARK_2)));
		fullWordInformationPanel = new MainPanel(null);
		fullWordInformationPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, wordTextArea));
		recognizingWordPanel = new MainPanel(null);

	}



	@Override
	public void showWordAssessmentPanel(Kanji kanji) {
		wordTextArea.setText(kanjiCharactersReader.getKanjiById(kanji.getId()));

	}





	@Override
	public void showWordGuessingPanel() {

	}

	@Override
	public JPanel getWordAssessmentPanel() {
		return fullWordInformationPanel.getPanel();
	}

	@Override
	public JPanel getWordGuessingPanel() {
		return recognizingWordPanel.getPanel();
	}

	@Override
	public String getWordHint(Kanji kanji) {
		return kanji.getKeyword();
	}

}
