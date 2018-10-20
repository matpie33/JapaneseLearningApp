package com.kanji.problematicWords;

import com.guimaker.webPanel.ContextOwner;
import com.guimaker.list.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;

public interface ProblematicWordsDisplayer<Word extends ListElement> extends
		ContextOwner {

	public MyList<Word> getWordsToReviewList();

	public void browseWord(Word word);

	public String getKanjiKoohiLoginCookieHeader();

	public void initializeWebPages();

	public AbstractPanelWithHotkeysInfo getPanel();

	public boolean isListPanelFocused();

	public void focusPreviouslyFocusedElement();
}
