package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JTextPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.controllers.ProblematicKanjisController;

public class KanjiPanel extends AbstractPanelWithHotkeysInfo {
	private String kanjiToDisplay;
	private ProblematicKanjisController problematicKanjiPanel;
	private JTextPane kanjiArea;
	private Font kanjiFont;

	public KanjiPanel(Font kanjiFont, String kanji,
			ProblematicKanjisController problematicKanjiPanel) {
		super(true);
		this.kanjiFont = kanjiFont;
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
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
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.CENTER, kanjiArea));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.CENTER, buttonNext));
	}

	private JTextPane addPromptAtLevel(String message) {
		JTextPane pane = GuiMaker.createTextPane(message, TextAlignment.CENTERED);
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
