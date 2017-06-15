package com.kanji.dialogs;

import javax.swing.JPanel;

public interface PanelCreator {

	public JPanel createPanel();

	public void setParentDialog(DialogWindow parent);

}
