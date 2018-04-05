package com.kanji.list.listRows.japanesePanelCreator;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.List;

public class KanaAndKanjiInputRow {
	private JLabel kanaWritingLabel;
	private JTextComponent kanaWritingText;
	private List<JTextComponent> kanjiTextComponents;
	private AbstractButton addKanjiWritingButton;
	private AbstractButton removeKanaAndKanjiWritingsButton;
	private AbstractButton addKanaAndKanjiWritingsButton;

	public KanaAndKanjiInputRow(JLabel kanaWritingLabel,
			JTextComponent kanaWritingText,
			List<JTextComponent> kanjiTextComponents,
			AbstractButton addKanjiWritingButton,
			AbstractButton removeKanaAndKanjiWritingsButton,
			AbstractButton addKanaAndKanjiWritingsButton) {
		this.kanaWritingLabel = kanaWritingLabel;
		this.kanaWritingText = kanaWritingText;
		this.kanjiTextComponents = kanjiTextComponents;
		this.addKanjiWritingButton = addKanjiWritingButton;
		this.removeKanaAndKanjiWritingsButton = removeKanaAndKanjiWritingsButton;
		this.addKanaAndKanjiWritingsButton = addKanaAndKanjiWritingsButton;
	}

	public JLabel getKanaWritingLabel() {
		return kanaWritingLabel;
	}

	public JTextComponent getKanaWritingText() {
		return kanaWritingText;
	}

	public List<JTextComponent> getKanjiTextComponents() {
		return kanjiTextComponents;
	}

	public AbstractButton getAddKanjiWritingButton() {
		return addKanjiWritingButton;
	}

	public AbstractButton getRemoveKanaAndKanjiWritingsButton() {
		return removeKanaAndKanjiWritingsButton;
	}

	public AbstractButton getAddKanaAndKanjiWritingsButton() {
		return addKanaAndKanjiWritingsButton;
	}
}
