package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.list.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;

public class WordsAndRepeatingInformationsPanel extends AbstractPanelWithHotkeysInfo {

	private JSplitPane listsSplitPane;
	private MyList wordsList;
	private MyList repeatingList;
	private TypeOfWordForRepeating typeOfWordForRepeating;

	public WordsAndRepeatingInformationsPanel(MyList wordsList, MyList repeatingList,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		createSplitPane(wordsList, repeatingList);
		this.wordsList = wordsList;
		this.repeatingList = repeatingList;
		this.typeOfWordForRepeating = typeOfWordForRepeating;
	}

	@Override
	public void createElements() {
		createSplitPane(wordsList, repeatingList);
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, listsSplitPane));
	}

	private void createSplitPane (MyList wordsList, MyList repeatingList){
		listsSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL,wordsList.getPanel(), repeatingList.getPanel(),
				0.5);
	}

	public MyList getRepeatingList() {
		return repeatingList;
	}

	public MyList getWordsList() {
		return wordsList;
	}

	public TypeOfWordForRepeating getTypeOfWordForRepeating() {
		return typeOfWordForRepeating;
	}
}