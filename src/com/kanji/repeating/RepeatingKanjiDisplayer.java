package com.kanji.repeating;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.list.listElements.Kanji;
import com.kanji.utilities.KanjiCharactersReader;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class RepeatingKanjiDisplayer implements RepeatingWordsDisplayer<Kanji> {

	private static final String UNIQUE_NAME = "Repeating kanji";
	private JTextComponent wordTextArea;
	private KanjiCharactersReader kanjiCharactersReader;

	public RepeatingKanjiDisplayer() {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordTextArea = GuiElementsCreator.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.JUSTIFIED)
									 .text("")
									 .editable(false)
									 .font(ApplicationWindow.getKanjiFont())
									 .border(BorderFactory.createLineBorder(
											 BasicColors.BLUE_DARK_2)));
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	@Override
	public void showFullWordDetailsPanel(Kanji kanji,
			MainPanel wordAssessmentPanel) {
		wordAssessmentPanel.clear();
		wordAssessmentPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
						wordTextArea));
		wordTextArea.setText(kanjiCharactersReader.getKanjiById(kanji.getId()));
	}

	@Override
	public String getWordHint(Kanji kanji) {
		return kanji.getKeyword();
	}

}
