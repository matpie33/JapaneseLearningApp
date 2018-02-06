package com.kanji.problematicWords;

import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;

public interface ProblematicWordsDisplayer<Element extends ListElement> {

	public MyList<Element> getWordsToReviewList();

	public void browseWord(WordRow<Element> wordRow);

	public WordRow createWordRow(Element listElement, int rowNumber);

	public void initialize();

	public AbstractPanelWithHotkeysInfo getPanel();

	public boolean isListPanelFocused();

}
