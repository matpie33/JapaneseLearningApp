package com.kanji.model;

import javax.swing.text.JTextComponent;
import java.util.List;
import java.util.Objects;

public class KanaAndKanjiTextFields {
	private JTextComponent kanaTextField;
	private List<JTextComponent> kanjiTextFields;
	private String modifiedTextFieldValue;
	private boolean kanaModified;

	public KanaAndKanjiTextFields(JTextComponent kanaTextField,
			List<JTextComponent> kanjiTextFields, String modifiedTextFieldValue,
			boolean kanaModified) {
		this.kanaTextField = kanaTextField;
		this.kanjiTextFields = kanjiTextFields;
		this.modifiedTextFieldValue = modifiedTextFieldValue;
		this.kanaModified = kanaModified;
	}

	public JTextComponent getKanaTextField() {
		return kanaTextField;
	}

	public List<JTextComponent> getKanjiTextFields() {
		return kanjiTextFields;
	}

	@Override
	public String toString (){
		return modifiedTextFieldValue;
	}

	public boolean isKanaModified() {
		return kanaModified;
	}

	public String getModifiedTextFieldValue() {
		return modifiedTextFieldValue;
	}

	@Override
	public int hashCode(){
		return Objects.hash(getModifiedTextFieldValue());
	}

	@Override
	public boolean equals (Object other){
		if (!other.getClass().equals(getClass())){
			return false;
		}
		KanaAndKanjiTextFields otherKana = (KanaAndKanjiTextFields) other;
		return otherKana.getModifiedTextFieldValue()
				.equals(getModifiedTextFieldValue());
	}

}
