package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class JapaneseWordWritingsInputManager {

	private JTextComponent kanaInput;
	private List<JTextComponent> kanjiInputs = new ArrayList<>();

	public JapaneseWordWritingsInputManager(JTextComponent kanaInput) {
		this.kanaInput = kanaInput;
	}

	public JTextComponent getKanaInput() {
		return kanaInput;
	}

	public List<JTextComponent> getKanjiInputs() {
		return kanjiInputs;
	}

	public boolean addKanjiInput(JTextComponent input) {
		boolean inputIsNew = kanjiInputIsNew(input);
		if (inputIsNew) {
			kanjiInputs.add(input);
		}
		return inputIsNew;

	}

	private boolean kanjiInputIsNew(JTextComponent input) {
		for (JTextComponent kanjiInput : kanjiInputs) {
			if (input == kanjiInput){
				continue;
			}
			if (kanjiInput.getText().equals(input.getText())) {
				return false;
			}
		}
		return true;
	}
}
