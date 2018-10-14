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
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class RepeatingKanjiDisplayer implements RepeatingWordsDisplayer<Kanji> {

	private JTextComponent wordTextArea;
	private KanjiCharactersReader kanjiCharactersReader;

	public RepeatingKanjiDisplayer() {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		wordTextArea = GuiElementsCreator.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.JUSTIFIED)
						.text("").editable(false).font(ApplicationWindow
						.getKanjiFont())
						.border(BorderFactory
								.createLineBorder(BasicColors.BLUE_DARK_2)));
	}

	@Override
	public void showFullWordDetailsPanel(Kanji kanji,
			MainPanel wordAssessmentPanel) {
		wordAssessmentPanel.clear();
		wordAssessmentPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, wordTextArea));
		wordTextArea.setText(kanjiCharactersReader.getKanjiById(kanji.getId()));
	}

	@Override
	public String getWordHint(Kanji kanji) {
		return kanji.getKeyword();
	}

}
