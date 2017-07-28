package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JTextPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.actions.TextAlignment;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;

public class KanjiPanel extends AbstractPanelWithHotkeysInfo {
	private String kanjiToDisplay;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private JTextPane kanjiArea;

	public KanjiPanel(String kanji, ProblematicKanjiPanel problematicKanjiPanel) {
		super(true);
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
		JButton buttonNext = createButtonShowNextKanji();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, kanjiArea));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, buttonNext));
	}

	private JTextPane addPromptAtLevel(String message) {
		JTextPane pane = GuiElementsMaker.createTextPane(message, TextAlignment.CENTERED);
		Font f = problematicKanjiPanel.getKanjisReader().getFont();
		pane.setFont(f);
		pane.setBackground(Color.white);
		return pane;
	}

	private JButton createButtonShowNextKanji() {
		AbstractAction al = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				problematicKanjiPanel.showNextKanji();
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_SPACE, al, ButtonsNames.buttonNextText,
				HotkeysDescriptions.SHOW_NEXT_KANJI);
	}

}
