package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.list.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;

public class WordsAndRepeatingInformationsPanel extends AbstractPanelWithHotkeysInfo {

	private JSplitPane listsSplitPane;
	private MyList wordsList;
	private MyList repeatingList;

	public WordsAndRepeatingInformationsPanel(MyList wordsList, MyList repeatingList) {
		createSplitPane(wordsList, repeatingList);
		this.wordsList = wordsList;
		this.repeatingList = repeatingList;
	}

	@Override
	public void createElements() {
		createSplitPane(wordsList, repeatingList);
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, listsSplitPane));
	}

	private void createSplitPane (MyList wordsList, MyList repeatingList){
		listsSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL);
		listsSplitPane.setLeftComponent(wordsList.getPanel());
		listsSplitPane.setRightComponent(repeatingList.getPanel());
		listsSplitPane.setResizeWeight(0.5);
	}

}
