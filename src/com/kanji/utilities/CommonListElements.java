package com.kanji.utilities;

import javax.swing.JButton;
import javax.swing.JLabel;

public class CommonListElements {

	private JButton buttonDelete;
	private JLabel rowNumberLabel;

	public CommonListElements(JButton buttonDelete, JLabel rowNumberLabel) {
		this.buttonDelete = buttonDelete;
		this.rowNumberLabel = rowNumberLabel;
	}

	public JButton getButtonDelete() {
		return buttonDelete;
	}

	public JLabel getRowNumberLabel() {
		return rowNumberLabel;
	}

}
