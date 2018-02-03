package com.kanji.model;

import javax.swing.text.JTextComponent;
import java.util.List;

public class KanaAndKanjiTextFields {
	private JTextComponent kanaTextField;
	private List<JTextComponent> kanjiTextFields;

	public KanaAndKanjiTextFields(JTextComponent kanaTextField,
			List<JTextComponent> kanjiTextFields) {
		this.kanaTextField = kanaTextField;
		this.kanjiTextFields = kanjiTextFields;
	}

	public JTextComponent getKanaTextField() {
		return kanaTextField;
	}

	public List<JTextComponent> getKanjiTextFields() {
		return kanjiTextFields;
	}
}
