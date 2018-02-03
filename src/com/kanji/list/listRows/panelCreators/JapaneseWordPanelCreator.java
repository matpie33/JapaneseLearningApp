package com.kanji.list.listRows.panelCreators;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListElementPropertyType;
import com.kanji.constants.enums.ListPanelViewMode;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.*;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.ListElementData;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JapaneseWordPanelCreator {

	private Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritingsTextComponents =
			new HashMap<>();
	private JComboBox partOfSpeechCombobox;
	private JTextComponent wordMeaningText;
	private Map <JTextComponent,
			ListElementPropertyManager<?, JapaneseWordInformation>>
			propertyManagersOfTextFields = new HashMap<>();

	public MainPanel createPanelForEditing (String meaning,
			Map <String, List<String>> writings, PartOfSpeech partOfSpeech,
			ListPanelViewMode listPanelViewMode, CommonListElements listElements){

		MainPanel addWordPanel = new MainPanel(null);
		//TODO separate it into create elements, and add to panel, do the inserts to maps in 1 place

		List<ListElementData<JapaneseWordInformation>> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData<>(Labels.WORD_MEANING,
				new JapaneseWordMeaningChecker(), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_WORD_MEANING));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANA,
				new JapaneseWordWritingsChecker(null), ListElementPropertyType.STRING_SHORT_WORD, Labels.COMBOBOX_OPTION_SEARCH_BY_KANA));

		wordMeaningText =
				CommonGuiElementsMaker.createShortInput(meaning);
		propertyManagersOfTextFields.put(wordMeaningText, new JapaneseWordMeaningChecker());
		JLabel wordMeaningLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WORD_MEANING));
		JLabel partOfSpeechLabel =GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.PART_OF_SPEECH));

		partOfSpeechCombobox = CommonGuiElementsMaker.createComboboxForPartOfSpeech();
		partOfSpeechCombobox.setSelectedItem(partOfSpeech);

		List <JComponent> firstRow = new ArrayList<>();

		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_ONLY)){
			partOfSpeechCombobox.setEnabled(false);
			wordMeaningLabel.setForeground(Color.WHITE);
			partOfSpeechLabel.setForeground(Color.WHITE);
			listElements.getRowNumberLabel().setForeground(Color.WHITE);
			firstRow.add(listElements.getRowNumberLabel());
			wordMeaningText.setEnabled(false);
		}
		else{
			firstRow.add(new JLabel());
			//TODO dummy label to preserve layout
		}

		firstRow.add(wordMeaningLabel);
		firstRow.add(wordMeaningText);

		addWordPanel.addElementsInColumnStartingFromColumn(wordMeaningText,
				0, firstRow.toArray(new JComponent[]{}));
		addWordPanel.addElementsInColumnStartingFromColumn(partOfSpeechCombobox,
				1,
				partOfSpeechLabel, partOfSpeechCombobox);

		if (listPanelViewMode.equals(ListPanelViewMode.EDIT)){
			AbstractButton addKanaAndKanjiWritingsButton = createButtonAddKanaAndKanjiWritings(addWordPanel,
					null, listPanelViewMode);
			addWordPanel.addElementsInColumnStartingFromColumn(2,
					addKanaAndKanjiWritingsButton);
			addKanaAndKanjiWritingRow(addWordPanel, null,
					listPanelViewMode);
		}
		else{
			for (Map.Entry <String, List <String>> kanaToKanjiWritings:
					writings.entrySet()){
				addKanaAndKanjiWritingRow(addWordPanel, kanaToKanjiWritings,
						listPanelViewMode);
			}
			addWordPanel.addElementsInColumnStartingFromColumn(1,
					listElements.getButtonDelete());

		}



		SwingUtilities.invokeLater(()->wordMeaningText.requestFocusInWindow());
		//TODO make a property manager that can get all kana and kanji writing text fields
		// when given a specific text field of kanji writing, wrap kana and kanji buttons as
		// a class that will act as property in property manager, use a cache in property manager
		// that is a map between kana text field and kanji text fields found in last check
		return addWordPanel;
	}

	public MainPanel createPanelForView(JapaneseWordInformation japaneseWordInformation,
			CommonListElements commonListElements){
		return createPanelForEditing(japaneseWordInformation.getWordMeaning(),
				japaneseWordInformation.getKanaToKanjiWritingsMap(),
				japaneseWordInformation.getPartOfSpeech(), ListPanelViewMode.VIEW_ONLY,
				commonListElements);

	}

	private void addKanaAndKanjiWritingRow (MainPanel rootPanel,
			Map.Entry <String, List <String>> kanaAndKanjiWritingsValues,
			ListPanelViewMode listPanelViewMode){
		JLabel kanaWritingLabel =GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WORD_IN_KANA));
		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_ONLY)){
			kanaWritingLabel.setForeground(Color.WHITE);
		}
		JTextComponent kanaWritingText;
		List <JTextComponent> kanjiWritingText = new ArrayList<>();
		if (kanaAndKanjiWritingsValues == null){
			kanaWritingText =CommonGuiElementsMaker.createShortInputWithPrompt(Prompts.KANA_TEXT);
			kanjiWritingText.add(CommonGuiElementsMaker.createShortInputWithPrompt
					(Prompts.KANJI_TEXT));

		}
		else{
			kanaWritingText = CommonGuiElementsMaker.createShortInput(
					kanaAndKanjiWritingsValues.getKey());

			for (String kanji: kanaAndKanjiWritingsValues.getValue()){
				kanjiWritingText.add(CommonGuiElementsMaker.createShortInput(kanji));
			}
		}
		propertyManagersOfTextFields.put(kanaWritingText, new JapaneseWordKanaChecker());
		for (JTextComponent kanjiTextComponents: kanjiWritingText){
			propertyManagersOfTextFields.put(kanjiTextComponents, new JapaneseWordKanjiChecker());
		}
		List <JTextComponent> kanjiWritingsComponents = new ArrayList<>();
		//TODO duplicated variable kanjiwritings components
		kanjiWritingsComponents.addAll(kanjiWritingText);
		kanaToKanjiWritingsTextComponents.put(kanaWritingText, kanjiWritingsComponents);
		MainPanel kanaAndKanjiWritings = new MainPanel(null);
		kanaAndKanjiWritings.setSkipInsetsForExtremeEdges(true);

		List <JComponent> elementsInRow = new ArrayList<>();
		elementsInRow.add(kanaWritingText);
		elementsInRow.addAll(kanjiWritingText);

		if (listPanelViewMode.equals(ListPanelViewMode.EDIT)){
			AbstractButton addKanjiWritingButton = createButtonAddKanjiWriting(rootPanel);
			AbstractButton removeKanaAndKanjiWritingsButton = createButtonRemoveKanaAndKanjiWritings(
					rootPanel,rootPanel.getNumberOfRows()-1, kanaWritingText);
			if (kanaToKanjiWritingsTextComponents.size() == 1){
				removeKanaAndKanjiWritingsButton.setEnabled(false);
			}
			else if (kanaToKanjiWritingsTextComponents.size() == 2){
				removeKanaAndKanjiWritingsButton.setEnabled(true);
			}
			elementsInRow.add(addKanjiWritingButton);
			elementsInRow.add(removeKanaAndKanjiWritingsButton);
		}

		kanaAndKanjiWritings.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				elementsInRow.toArray(new JComponent[]{})));

		rootPanel.insertRowStartingFromColumn(1,
				listPanelViewMode.equals(ListPanelViewMode.EDIT)?
						rootPanel.getNumberOfRows() -1: rootPanel.getNumberOfRows(),
				kanaWritingLabel, kanaAndKanjiWritings.getPanel());
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

	private AbstractButton createButtonAddKanaAndKanjiWritings(MainPanel panel,
			Map.Entry <String, List <String>> kanaAndKanjiWritingsValues,
			ListPanelViewMode listPanelViewMode){
		AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.ADD_KANA_AND_KANJI_WRITINGS, (new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e) {
						addKanaAndKanjiWritingRow(panel, kanaAndKanjiWritingsValues,
								listPanelViewMode);
					}
				}));
		return button;
	}

	public Map<JTextComponent, List<JTextComponent>> getKanaToKanjiWritingsTextComponents(){
		return kanaToKanjiWritingsTextComponents;
	}

	public JComboBox getPartOfSpeechCombobox() {
		return partOfSpeechCombobox;
	}

	public JTextComponent getWordMeaningText() {
		return wordMeaningText;
	}

	public Map <JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>>
		getPropertyManagersOfTextFields(){
		return propertyManagersOfTextFields;
	}

}
