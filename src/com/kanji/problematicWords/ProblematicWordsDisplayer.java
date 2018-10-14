package com.kanji.problematicWords;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;

public interface ProblematicWordsDisplayer<Word extends ListElement> {

	public MyList<Word> getWordsToReviewList();

	public void browseWord(Word word);


	public void initializeWebPages();

	public AbstractPanelWithHotkeysInfo getPanel();

	public boolean isListPanelFocused();

	public void focusPreviouslyFocusedElement();
}
