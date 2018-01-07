package com.kanji.utilities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWordInformation;

public class CommonGuiElementsMaker {

	public static JTextComponent createKanjiWordInput(String defaultContent) {
		return GuiMaker.createTextArea(
				new TextAreaOptions().text(defaultContent).rowsAndColumns(3, 15)
						.moveToNextComponentWhenTabbed(true));
	}

	public static JTextComponent createTextField(String defaultContent) {
		return GuiMaker.createTextField(
				new TextComponentOptions().text(defaultContent).rowsAndColumns(1, 15));
	}

	public static JTextComponent createKanjiIdInput() {
		return GuiMaker
				.createTextField(new TextComponentOptions().maximumCharacters(5).digitsOnly(true).rowsAndColumns(1,5));
	}

	public static JLabel createErrorLabel(String message) {
		return GuiMaker
				.createLabel(new ComponentOptions().text(message).foregroundColor(Color.RED));
	}

	public static JSplitPane createSplitPane(SplitPaneOrientation splitPaneOrientation) {
		JSplitPane splitPane = new JSplitPane(splitPaneOrientation.getValue());
		splitPane.setContinuousLayout(true);
		return splitPane;
	}

	public static JComboBox<String> createComboboxForPartOfSpeech() {
		JComboBox<String> comboBox = new JComboBox<>();
		for (PartOfSpeech partOfSpeech: PartOfSpeech.values()){
			comboBox.addItem(partOfSpeech.getPolishMeaning());
		}
		return comboBox;
	}

	public static List<JTextComponent> convertJapaneseWordWritingsToTextComponent (
			JapaneseWordInformation japaneseWordInformation){
		List <JTextComponent> textComponents = new ArrayList<>();
		for (Map.Entry<String, List<String>> writing:
				japaneseWordInformation.getKanjiToKanaWritingMapping().entrySet()){
			List <String> writings = new ArrayList<>();
			writings.add(writing.getKey());
			writings.addAll(writing.getValue());
			JTextComponent kanjiAndKanaWritings = CommonGuiElementsMaker.createTextField(
					StringConcatenator.concatenateStrings(writings));
			textComponents.add(kanjiAndKanaWritings);
		}
		return textComponents;
	}

	public static void addKanaAndKanjiWritingsToPanel (JapaneseWordInformation japaneseWordInformation,
			MainPanel panel, int firstColumnIndex, Color labelColor){
		List <JTextComponent> kanaAndKanjiTextfields = convertJapaneseWordWritingsToTextComponent(japaneseWordInformation);
		JLabel writingsLabel = GuiMaker.createLabel(new ComponentOptions()
				.text(Labels.WRITING_WAYS_IN_JAPANESE).foregroundColor(labelColor));
		boolean firstTextField = true;
		for (JTextComponent kanaAndKanjiTextfield: kanaAndKanjiTextfields){
			JComponent [] components;
			int columnNumber;
			if (firstTextField){
				components = new JComponent[] {writingsLabel, kanaAndKanjiTextfield};
				columnNumber = firstColumnIndex;
			}
			else{
				components = new JComponent[] {kanaAndKanjiTextfield};
				columnNumber = firstColumnIndex+1;
			}
			panel.addElementsInColumnStartingFromColumn(columnNumber,
					components);
			firstTextField = false;
		}
	}

}
