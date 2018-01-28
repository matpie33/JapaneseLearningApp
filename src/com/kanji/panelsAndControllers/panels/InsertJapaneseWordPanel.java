package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.*;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.ListElementData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.InsertWordController;
import com.kanji.utilities.CommonGuiElementsMaker;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsertJapaneseWordPanel<Word extends ListElement> extends AbstractPanelWithHotkeysInfo {

	private InsertWordController controller;
	private MyList<Word> list;
	private Map<JComponent, ListElementPropertyManager> textComponentToPropertyManager;
	private Map <JTextComponent, List<JTextComponent>> kanaToKanjiWritingsTextComponents =
			new HashMap<>();

	public InsertJapaneseWordPanel(MyList<Word> list,
			ApplicationController applicationController) {
		controller = new InsertWordController(list, applicationController);
		this.list = list;
		textComponentToPropertyManager = new HashMap<>();
	}

	@Override
	public void createElements() {

		controller.setParentDialog(parentDialog);
		MainPanel addWordPanel = new MainPanel(null);

		List<ListElementData<JapaneseWordInformation>> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData<>(Labels.WORD_MEANING,
				new JapaneseWordMeaningChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_WORD_MEANING));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANA,
				new JapaneseWordKanaChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANA));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANJI,
				new JapaneseWordKanjiChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANJI));
		listElementData.add(new ListElementData<>(Labels.PART_OF_SPEECH,
				new NotChecker(), ListElementPropertyType.COMBOBOX_OPTION, Labels.COMBOBOX_OPTION_SEARCH_BY_PART_OF_SPEECH));

		JLabel wordMeaningLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WORD_MEANING));
		JTextComponent wordMeaningText = CommonGuiElementsMaker.createShortInput("");
		JLabel partOfSpeechLabel =GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.PART_OF_SPEECH));
		JComboBox partOfSpeechCombobox = CommonGuiElementsMaker.createComboboxForPartOfSpeech();

		AbstractButton addKanaAndKanjiWritingsButton = createButtonAddKanaAndKanjiWritings(addWordPanel);

		addWordPanel.addElementsInColumnStartingFromColumn(wordMeaningText,0,
				wordMeaningLabel, wordMeaningText);
		addWordPanel.addElementsInColumnStartingFromColumn(partOfSpeechCombobox,0,
				partOfSpeechLabel, partOfSpeechCombobox);
		addWordPanel.addElementsInColumnStartingFromColumn(1,
				addKanaAndKanjiWritingsButton);

		addKanaAndKanjiWritingRow(addWordPanel);


		SwingUtilities.invokeLater(()->wordMeaningText.requestFocusInWindow());

		AbstractButton cancel = createButtonClose();
		AbstractButton approve = createButtonValidate(ButtonsNames.ADD_WORD);

		mainPanel.addRows(
				SimpleRowBuilder.createRow(FillType.BOTH, addWordPanel.getPanel()).useAllExtraVerticalSpace());
		setNavigationButtons(cancel, approve);

	}

	private void addKanaAndKanjiWritingRow (MainPanel rootPanel){
		JLabel kanaWritingLabel =GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WORD_IN_KANA));
		JTextComponent kanaWritingText = CommonGuiElementsMaker.createShortInputWithPrompt(Prompts.KANA_TEXT);
		JTextComponent kanjiWritingText = CommonGuiElementsMaker.createShortInputWithPrompt(Prompts.KANJI_TEXT);
		List <JTextComponent> kanjiWritingsComponents = new ArrayList<>();
		kanjiWritingsComponents.add(kanjiWritingText);
		kanaToKanjiWritingsTextComponents.put(kanaWritingText, kanjiWritingsComponents);
		MainPanel kanaAndKanjiWritings = new MainPanel(null);
		kanaAndKanjiWritings.setSkipInsetsForExtremeEdges(true);

		AbstractButton addKanjiWritingButton = createButtonAddKanjiWriting(rootPanel);
		AbstractButton removeKanaAndKanjiWritingsButton = createButtonRemoveKanaAndKanjiWritings(
				rootPanel,rootPanel.getNumberOfRows()-1, kanaWritingText);
		if (kanaToKanjiWritingsTextComponents.size() == 1){
			removeKanaAndKanjiWritingsButton.setEnabled(false);
		}
		else if (kanaToKanjiWritingsTextComponents.size() == 2){
			removeKanaAndKanjiWritingsButton.setEnabled(true);
		}

		kanaAndKanjiWritings.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				kanaWritingText, kanjiWritingText, addKanjiWritingButton, removeKanaAndKanjiWritingsButton));



		rootPanel.insertRowStartingFromColumn(0,
				rootPanel.getNumberOfRows() -1,
				kanaWritingLabel, kanaAndKanjiWritings.getPanel());
	}

	private AbstractButton createButtonValidate(String text) {
		return createButtonWithHotkey(KeyEvent.VK_ENTER,
				controller.createActionValidateAndAddWord(textComponentToPropertyManager), text, HotkeysDescriptions.ADD_WORD);
	}

	private AbstractButton createButtonAddKanjiWriting (MainPanel panel){
		AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.ADD_KANJI_WRITING, null);
		button.addActionListener(new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				panel.insertElementBeforeOtherElement(button,
						CommonGuiElementsMaker.createShortInputWithPrompt(Prompts.KANJI_TEXT));
			}
		});
		return button;
	}

	private AbstractButton createButtonRemoveKanaAndKanjiWritings(MainPanel panel,
			int rowNumber, JTextComponent kanaTextComponent){
		AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.REMOVE_KANA_AND_KANJI_WRITINGS, (new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e) {
						kanaToKanjiWritingsTextComponents.remove(kanaTextComponent);
						panel.removeRowInAColumnWay(rowNumber);
					}
				}));
		return button;
	}

	private AbstractButton createButtonAddKanaAndKanjiWritings(MainPanel panel){
		AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.ADD_KANA_AND_KANJI_WRITINGS, (new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				addKanaAndKanjiWritingRow(panel);
			}
		}));
		return button;
	}

}
