package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.ListElementData;
import com.kanji.list.listRows.japanesePanelCreator.JapanesePanelElementsMaker;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.InsertKanjiController;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertKanjiPanel<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private InsertKanjiController controller;
	private MyList<Word> list;
	private Map<JComponent, ListElementPropertyManager> textComponentToPropertyManager;

	public InsertKanjiPanel(MyList<Word> list,
			ApplicationController applicationController) {
		controller = new InsertKanjiController(list, applicationController);
		this.list = list;
		textComponentToPropertyManager = new HashMap<>();
	}

	@Override
	public void createElements() {

		controller.setParentDialog(parentDialog);
		MainPanel addWordPanel = new MainPanel(null);

		List<ListElementData<KanjiInformation>> listElements = new ArrayList<>();
		listElements.add(new ListElementData<>(Labels.KANJI_KEYWORD_LABEL,
				new KanjiKeywordChecker(),
				ListElementPropertyType.STRING_LONG_WORD,
				Labels.COMBOBOX_OPTION_SEARCH_BY_KEYWORD));
		listElements.add(new ListElementData<>(Labels.KANJI_ID_LABEL,
				new KanjiIdChecker(), ListElementPropertyType.NUMERIC_INPUT,
				Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI_ID));

		boolean firstElement = true;
		for (ListElementData listElementData : listElements) {
			JComponent component;
			switch (listElementData.getListElementPropertyType()) {
			case NUMERIC_INPUT:
				component = CommonGuiElementsMaker.createKanjiIdInput();
				break;
			case STRING_SHORT_WORD:
				component = CommonGuiElementsMaker.createTextField("");
				break;
			case STRING_LONG_WORD:
				component = CommonGuiElementsMaker.createKanjiWordInput("");
				break;
			case COMBOBOX_OPTION:
				component = JapanesePanelElementsMaker
						.createComboboxForPartOfSpeech(PartOfSpeech.NOUN);
				break;
			default:
				throw new RuntimeException(
						"Invalid element type in insert word panel");
			}
			ListElementPropertyManager listElementPropertyManager = listElementData
					.getListElementPropertyManager();
			textComponentToPropertyManager
					.put(component, listElementPropertyManager);

			addWordPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH,
					new JLabel(listElementData.getElementLabel()), component)
					.fillVertically(component));
			if (firstElement) {
				firstElement = false;
				SwingUtilities
						.invokeLater(() -> component.requestFocusInWindow());
			}

		}

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(SimpleRowBuilder
				.createRow(FillType.BOTH, addWordPanel.getPanel())
				.useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private AbstractButton createButtonValidate(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER, controller
						.createActionValidateAndAddWord(textComponentToPropertyManager),
				text, HotkeysDescriptions.ADD_WORD);
	}

}
