package com.kanji.problematicWords;

import com.guimaker.list.ListElement;
import com.guimaker.list.myList.MyList;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.webPanel.ContextOwner;

public interface ProblematicWordsDisplayer<Word extends ListElement>
		extends ContextOwner {

	public MyList<Word> getWordsToReviewList();

	public void browseWord(Word word);

	public String getKanjiKoohiLoginCookieHeader();

	public void initializeWebPages();

	public AbstractPanelWithHotkeysInfo getPanel();

	public boolean isListPanelFocused();

	public void focusPreviouslyFocusedElement();
}
