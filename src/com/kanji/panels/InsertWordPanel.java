package com.kanji.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.listElements.KanjiInformation;
import com.kanji.listElements.ListElement;
import com.kanji.listElements.ListElementData;
import com.kanji.listSearching.PropertyManager;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.strings.Prompts;
import com.kanji.controllers.ApplicationController;
import com.kanji.controllers.InsertWordController;
import com.kanji.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static com.kanji.enums.ListElementType.STRING_SHORT_WORD;

public class InsertWordPanel<Word extends ListElement> extends AbstractPanelWithHotkeysInfo {

	private JTextComponent insertWordTextComponent;
	private JTextComponent insertNumberTextComponent;
	private InsertWordController controller;
	private MyList<Word> list;
	private Map<JTextComponent, PropertyManager> textComponentToPropertyManager;

	public InsertWordPanel(MyList<Word> list,
			ApplicationController applicationController) {
		controller = new InsertWordController(list, applicationController);
		this.list = list;
		textComponentToPropertyManager = new HashMap<>();
	}

	@Override
	public void createElements() {

		controller.setParentDialog(parentDialog);
		MainPanel addWordPanel = new MainPanel(null);

		for (ListElementData listElementData: list.getListElementData()){
			JTextComponent component;
			switch (listElementData.getListElementType()) {
				case NUMERIC_INPUT:
					component = CommonGuiElementsMaker.createKanjiIdInput();
					break;
				case STRING_SHORT_WORD:
					component = CommonGuiElementsMaker.createTextField("");
					break;
				case STRING_LONG_WORD:
					component = CommonGuiElementsMaker.createKanjiWordInput("");
					break;
				default:
					throw new RuntimeException("Invalid element type in insert word panel");
			}
			PropertyManager propertyManager = listElementData.getPropertyManager();
			textComponentToPropertyManager.put(component, propertyManager);

			addWordPanel.addRows(
					SimpleRowBuilder.createRow(FillType.BOTH,
							new JLabel(listElementData.getElementLabel()), component)
							.fillHorizontallySomeElements(insertWordTextComponent)
							.fillVertically(component));

		}

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(
				SimpleRowBuilder.createRow(FillType.BOTH, addWordPanel.getPanel()).useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateAndAddWord(textComponentToPropertyManager), text, HotkeysDescriptions.ADD_WORD);
	}

}
