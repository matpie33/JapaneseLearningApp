package com.kanji.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;

public class KanjiPanel extends AbstractPanelWithHotkeysInfo {
	private String kanjiToDisplay;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private JTextArea kanjiArea;

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
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, buttonNext));
	}

	private JTextArea addPromptAtLevel(String message) {
		JTextArea elem = new JTextArea(1, 1);
		Font f = new Font("MS PMincho", 1, 80);
		elem.setFont(f);
		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setEditable(false);
		return elem;
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
