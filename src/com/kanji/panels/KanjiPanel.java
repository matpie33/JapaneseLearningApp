package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.controllers.ProblematicKanjisController;

public class KanjiPanel extends AbstractPanelWithHotkeysInfo {
	private String kanjiToDisplay;
	private ProblematicKanjisController problematicKanjiPanel;
	private JTextComponent kanjiArea;
	private Font kanjiFont;

	public KanjiPanel(Font kanjiFont, String kanji,
			ProblematicKanjisController problematicKanjiPanel) {
		this.kanjiFont = kanjiFont;
		this.kanjiToDisplay = kanji;
		this.problematicKanjiPanel = problematicKanjiPanel;
	}

	public void changeKanji(String kanji) {
		kanjiArea.setText(kanji);
	}

	@Override
	void createElements() {
		kanjiArea = addPromptAtLevel(kanjiToDisplay);
		AbstractButton buttonNext = createButtonShowNextKanji();
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, kanjiArea));
		setNavigationButtons(Anchor.CENTER, createButtonClose(), buttonNext);
	}

	private JTextComponent addPromptAtLevel(String message) {
		JTextComponent pane = GuiMaker.createTextPane(new TextPaneOptions().editable(false)
				.textAlignment(TextAlignment.CENTERED).text(message));
		pane.setFont(kanjiFont);
		pane.setBackground(Color.white);
		return pane;
	}

	private AbstractButton createButtonShowNextKanji() {
		AbstractAction al = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				problematicKanjiPanel.showNextKanjiOrCloseChildDialog();
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_SPACE, al, ButtonsNames.FIND_NEXT,
				HotkeysDescriptions.SHOW_NEXT_KANJI);
	}

}
