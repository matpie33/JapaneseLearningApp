package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.list.myList.MyList;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.utilities.CommonGuiElementsCreator;

import javax.swing.*;

public class WordsAndRepeatingInformationsPanel
		extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Words and repeating information panel";
	private JSplitPane listsSplitPane;
	private MyList wordsList;
	private MyList<RepeatingData> repeatingList;
	private TypeOfWordForRepeating typeOfWordForRepeating;

	public WordsAndRepeatingInformationsPanel(MyList wordsList,
			MyList<RepeatingData> repeatingList,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		this.wordsList = wordsList;
		this.repeatingList = repeatingList;
		this.typeOfWordForRepeating = typeOfWordForRepeating;
	}

	public JSplitPane getListsSplitPane() {
		return listsSplitPane;
	}

	@Override
	public void createElements() {
		listsSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						wordsList.getPanel(), repeatingList.getPanel(), 0.8);
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, listsSplitPane));
	}

	public TypeOfWordForRepeating getTypeOfWordForRepeating() {
		return typeOfWordForRepeating;
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}
}
