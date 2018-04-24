package com.kanji.utilities;

import javax.swing.*;
import java.awt.*;

public class CommonListElements {

	private AbstractButton buttonDelete;
	private JLabel rowNumberLabel;
	private AbstractButton buttonAddRow;
	private Color labelsColor;
	private boolean forSingleRowOnly;

	public CommonListElements(AbstractButton buttonDelete,
			JLabel rowNumberLabel, AbstractButton buttonAddRow,
			Color labelsColor, boolean forSingleRowOnly) {
		this.buttonDelete = buttonDelete;
		this.rowNumberLabel = rowNumberLabel;
		this.buttonAddRow = buttonAddRow;
		this.labelsColor = labelsColor;
		this.forSingleRowOnly = forSingleRowOnly;
	}

	public boolean isForSingleRowOnly() {
		return forSingleRowOnly;
	}

	public Color getLabelsColor() {
		return labelsColor;
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

	public static CommonListElements forSingleRowOnly(Color labelsColor) {
		return new CommonListElements(null, null, null, labelsColor, true);
	}

}
