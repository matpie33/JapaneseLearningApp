package com.kanji.webPanel;

import com.guimaker.panels.MainPanel;
import com.kanji.kanjiContext.KanjiContext;

import javax.swing.*;

public interface ConnectionFailPageHandler {

	public JPanel getConnectionFailPage ();
	public void modifyConnectionFailPage (KanjiContext context);
}
