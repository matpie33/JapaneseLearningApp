package com.kanji.utilities;

import javax.swing.*;

public class CommonListElements {

	private AbstractButton buttonDelete;
	private JLabel rowNumberLabel;
	private AbstractButton buttonAddRow;

	public CommonListElements(AbstractButton buttonDelete, JLabel rowNumberLabel,
			AbstractButton buttonAddRow) {
		this.buttonDelete = buttonDelete;
		this.rowNumberLabel = rowNumberLabel;
		this.buttonAddRow = buttonAddRow;
	}

	public AbstractButton getButtonDelete() {
		return buttonDelete;
	}

	public JLabel getRowNumberLabel() {
		return rowNumberLabel;
	}

	public AbstractButton getButtonAddRow() {
		return buttonAddRow;
	}
}
