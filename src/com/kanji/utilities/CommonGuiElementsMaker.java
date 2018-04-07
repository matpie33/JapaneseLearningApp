package com.kanji.utilities;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommonGuiElementsMaker {

	public static JTextComponent createKanjiWordInput(String defaultContent) {
		return GuiMaker.createTextArea(
				new TextAreaOptions().text(defaultContent).rowsAndColumns(2, 5)
						.moveToNextComponentWhenTabbed(true));
	}

	public static JTextComponent createShortInput(String defaultContent) {
		return GuiMaker.createTextField(
				new TextComponentOptions().text(defaultContent)
						.rowsAndColumns(1, 6));
	}

	public static JTextComponent createTextField(String defaultContent) {
		return GuiMaker.createTextField(
				new TextComponentOptions().text(defaultContent)
						.rowsAndColumns(1, 15));
	}

	public static JTextComponent createKanjiIdInput() {
		return GuiMaker.createTextField(
				new TextComponentOptions().maximumCharacters(5).digitsOnly(true)
						.rowsAndColumns(1, 5));
	}

	public static JLabel createErrorLabel(String message) {
		return GuiMaker.createLabel(new ComponentOptions().text(message)
				.foregroundColor(Color.RED));
	}

	public static JSplitPane createSplitPane(
			SplitPaneOrientation splitPaneOrientation,
			JComponent leftOrUpperComponent, JComponent rightOrDownComponent,
			double splittingWeight) {
		JSplitPane splitPane = new JSplitPane(splitPaneOrientation.getValue());
		splitPane.setContinuousLayout(true);
		splitPane.setLeftComponent(leftOrUpperComponent);
		splitPane.setRightComponent(rightOrDownComponent);
		splitPane.setResizeWeight(splittingWeight);
		return splitPane;
	}

	public static List<JTextComponent> convertJapaneseWordWritingsToTextComponent(
			JapaneseWordInformation japaneseWordInformation) {
		List<JTextComponent> textComponents = new ArrayList<>();
		for (Map.Entry<String, Set<String>> writing : japaneseWordInformation
				.getKanaToKanjiWritingsMap().entrySet()) {
			List<String> writings = new ArrayList<>();
			writings.add(writing.getKey());
			writings.addAll(writing.getValue());
			JTextComponent kanjiAndKanaWritings = CommonGuiElementsMaker
					.createTextField(
							StringUtilities.concatenateStrings(writings));
			kanjiAndKanaWritings.setFont(ApplicationWindow.getKanjiFont());
			kanjiAndKanaWritings
					.setFont(kanjiAndKanaWritings.getFont().deriveFont(40f));
			textComponents.add(kanjiAndKanaWritings);
		}
		return textComponents;
	}

	public static void addKanaAndKanjiWritingsToPanel(
			JapaneseWordInformation japaneseWordInformation, MainPanel panel,
			int firstColumnIndex, Color labelColor) {
		List<JTextComponent> kanaAndKanjiTextfields = convertJapaneseWordWritingsToTextComponent(
				japaneseWordInformation);
		JLabel writingsLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
						.foregroundColor(labelColor).fontSize(20f));
		boolean firstTextField = true;
		MainPanel panelWrapping = new MainPanel(null);

		for (JTextComponent kanaAndKanjiTextfield : kanaAndKanjiTextfields) {
			JComponent[] components;
			int columnNumber;
			if (firstTextField) {
				components = new JComponent[] { writingsLabel,
						kanaAndKanjiTextfield };
				columnNumber = firstColumnIndex;
			}
			else {
				components = new JComponent[] { kanaAndKanjiTextfield };
				columnNumber = firstColumnIndex + 1;
			}
			panelWrapping.addElementsInColumnStartingFromColumn(
					kanaAndKanjiTextfield, columnNumber, FillType.HORIZONTAL,
					components);
			firstTextField = false;
		}
		JScrollPane scrollPane = GuiMaker.createScrollPane(
				new ScrollPaneOptions()
						.componentToWrap(panelWrapping.getPanel())
						.backgroundColor(BasicColors.OCEAN_BLUE)
						.border(BorderFactory
								.createLineBorder(BasicColors.NAVY_BLUE)));
		panel.addElementsInColumnStartingFromColumn(scrollPane, 1,
				FillType.BOTH, scrollPane);
	}

}
