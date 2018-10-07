package com.kanji.model;

import com.kanji.range.Range;

import javax.swing.text.JTextComponent;
import java.util.Objects;

public class RangesRow {
	private JTextComponent textFieldFrom;
	private JTextComponent textFieldTo;
	private String error;
	private Range range;

	public RangesRow(JTextComponent textFieldFrom, JTextComponent textFieldTo) {
		this.textFieldFrom = textFieldFrom;
		this.textFieldTo = textFieldTo;
		error = "";
		range = new Range(0, 0);
	}

	public JTextComponent getTextFieldFrom() {
		return textFieldFrom;
	}

	public JTextComponent getTextFieldTo() {
		return textFieldTo;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean hasError() {
		return !error.isEmpty();
	}

	public void setRangeValues(int valueFrom, int valueTo) {
		range = new Range(valueFrom, valueTo);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RangesRow) {
			RangesRow r = (RangesRow) o;
			return r.getTextFieldFrom() == this.textFieldFrom
					&& r.getTextFieldTo() == this.textFieldTo;
		}
		else {
			return false;
		}

	}

	public boolean gotTextFields(JTextComponent textFieldFrom,
			JTextComponent textFieldTo) {
		return this.textFieldFrom == textFieldFrom
				&& this.textFieldTo == textFieldTo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(textFieldFrom.hashCode(), textFieldTo.hashCode());
	}

	@Override
	public String toString() {
		return "error: " + error + " start" + range.getRangeStart() + " end "
				+ range.getRangeEnd();
	}

	public Range getRange() {
		return range;
	}

}
