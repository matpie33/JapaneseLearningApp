package com.kanji.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.windows.DialogWindow;

public class KanjiPanel implements PanelCreator {
	private MainPanel main;
	private DialogWindow parentDialog;
	private String kanjiToDisplay;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private JTextArea kanjiArea;

	public KanjiPanel(String kanji, ProblematicKanjiPanel problematicKanjiPanel) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		this.kanjiToDisplay = kanji;
		this.problematicKanjiPanel = problematicKanjiPanel;
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	public void changeKanji(String kanji) {
		kanjiArea.setText(kanji);
	}

	@Override
	public JPanel createPanel() {
		kanjiArea = addPromptAtLevel(kanjiToDisplay);
		JButton buttonNext = createButtonShowNextKanji();

		main.addRow(RowMaker.createBothSidesFilledRow(kanjiArea));
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, buttonNext));
		return main.getPanel();
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
		return GuiElementsMaker.createButton(ButtonsNames.buttonNextText, al, KeyEvent.VK_SPACE);
	}

}
