package com.kanji.model;

import java.util.Objects;

import javax.swing.JTextField;

import com.kanji.range.Range;

public class RangesRow {
	private JTextField textFieldFrom;
	private JTextField textFieldTo;
	private String error;
	private int rowNumber;
	private Range range;

	public RangesRow(JTextField textFieldFrom, JTextField textFieldTo, int rowNumber) {
		this.textFieldFrom = textFieldFrom;
		this.textFieldTo = textFieldTo;
		error = "";
		this.rowNumber = rowNumber;
		range = new Range(0, 0);
	}

	public JTextField getTextFieldFrom() {
		return textFieldFrom;
	}

	public JTextField getTextFieldTo() {
		return textFieldTo;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean errorNotEmpty() {
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

	public boolean gotTextFields(JTextField textFieldFrom, JTextField textFieldTo) {
		return this.textFieldFrom == textFieldFrom && this.textFieldTo == textFieldTo;
	}

	public int getTextFieldsRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	@Override
	public int hashCode() {
		return Objects.hash(textFieldFrom.hashCode(), textFieldTo.hashCode());
	}

	@Override
	public String toString() {
		return "error: " + error + " row number " + rowNumber + " start" + range.getRangeStart()
				+ " end " + range.getRangeEnd();
	}

	public Range getRange() {
		return range;
	}

}
