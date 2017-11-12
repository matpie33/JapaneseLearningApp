package com.kanji.panels;

import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.text.JTextComponent;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.controllers.ProblematicKanjisController;

public class KanjiPanel extends AbstractPanelWithHotkeysInfo {
	private String kanjiToDisplay;
	private ProblematicKanjisController problematicKanjiController;
	private JTextComponent kanjiArea;
	private Font kanjiFont;

	public KanjiPanel(Font kanjiFont, String kanji,
			ProblematicKanjisController problematicKanjiController) {
		this.kanjiFont = kanjiFont;
		this.kanjiToDisplay = kanji;
		this.problematicKanjiController = problematicKanjiController;
	}

	public void changeKanji(String kanji) {
		kanjiArea.setText(kanji);
	}

	@Override
	void createElements() {
		kanjiArea = addPromptAtLevel(kanjiToDisplay);
		AbstractButton buttonNext = createButtonShowNextKanji();
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, kanjiArea).setNotOpaque());
		setNavigationButtons(Anchor.CENTER, createButtonClose(), buttonNext);
	}

	private JTextComponent addPromptAtLevel(String message) {
		JTextComponent pane = GuiMaker.createTextPane(new TextPaneOptions().border(null).editable(false)
				.textAlignment(TextAlignment.CENTERED).text(message));
		pane.setFont(kanjiFont);
		return pane;
	}

	private AbstractButton createButtonShowNextKanji() {
		return createButtonWithHotkey(KeyEvent.VK_SPACE,
				problematicKanjiController.createActionShowNextKanjiOrCloseDialog(),
				ButtonsNames.FIND_NEXT,	HotkeysDescriptions.SHOW_NEXT_KANJI);
	}

}
