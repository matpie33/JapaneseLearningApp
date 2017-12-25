package com.kanji.webPanel;

import com.kanji.context.KanjiContext;

import javax.swing.*;

public interface ConnectionFailPageHandler {

	public JPanel getConnectionFailPage ();
	public void modifyConnectionFailPage (KanjiContext context);
}
