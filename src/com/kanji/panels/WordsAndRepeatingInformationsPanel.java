package com.kanji.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.controllers.ApplicationController;
import com.kanji.enums.SavingStatus;
import com.kanji.enums.SplitPaneOrientation;
import com.kanji.myList.MyList;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.strings.Prompts;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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
