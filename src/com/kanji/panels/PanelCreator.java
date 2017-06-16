package com.kanji.panels;

import javax.swing.JPanel;

import com.kanji.windows.DialogWindow;

public interface PanelCreator {

	public JPanel createPanel();

	public void setParentDialog(DialogWindow parent);

}
