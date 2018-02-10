package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RowInJapaneseWordsReviewingList
		implements ListRowMaker<JapaneseWordInformation> {

	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;

	public RowInJapaneseWordsReviewingList(
			ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer) {
		this.problematicJapaneseWordsDisplayer = problematicJapaneseWordsDisplayer;
	}

	@Override
	public MainPanel createListRow(JapaneseWordInformation japaneseWord,
			CommonListElements commonListElements) {
		MainPanel panel = new MainPanel(null);

		JLabel meaningLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
						.foregroundColor(BasicColors.OCEAN_BLUE));
		String meaning = japaneseWord.getWordMeaning();
		JTextComponent meaningText = CommonGuiElementsMaker
				.createTextField(meaning);
		JLabel partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
						.foregroundColor(Color.WHITE));
		//TODO export white label as common

		JLabel partOfSpeech = GuiMaker.createLabel(new ComponentOptions()
				.text(japaneseWord.getPartOfSpeech().getPolishMeaning())
				.foregroundColor(Color.WHITE));

		panel.addElementsInColumnStartingFromColumn(0,
				commonListElements.getRowNumberLabel(), meaningLabel,
				meaningText);
		panel.addElementsInColumnStartingFromColumn(1, partOfSpeechLabel,
				partOfSpeech);

		MainPanel writingsPanel = new MainPanel(BasicColors.OCEAN_BLUE, true);
		List<String> headers = new ArrayList<>();
		headers.add("Kana");
		if (japaneseWord.hasKanjiWriting()) {
			headers.add("Kanji");
		}
		List<JComponent> labels = headers.stream().map(header -> GuiMaker
				.createLabel(new ComponentOptions().text(header)))
				.collect(Collectors.toList());
		writingsPanel.addElementsInColumnStartingFromColumn(0,
				labels.toArray(new JComponent[] {}));
		writingsPanel.setRowColor(Color.RED);
		for (Map.Entry<String, List<String>> writings : japaneseWord
				.getKanaToKanjiWritingsMap().entrySet()) {

			List<String> allWritings = new ArrayList<>();
			allWritings.add(writings.getKey());
			if (!writings.getValue().isEmpty()) {
				allWritings.addAll(writings.getValue());
			}
			List<JComponent> writingTextFields = allWritings.stream()
					.map(writing -> GuiMaker.createTextField(
							new TextComponentOptions().text(writing)
									.editable(false).focusable(false)
									.fontSize(20f)))
					.collect(Collectors.toList());
			writingTextFields
					.forEach(tf -> tf.addMouseListener(new MouseAdapter() {
						@Override public void mouseClicked(MouseEvent e) {
							problematicJapaneseWordsDisplayer.setSelectedWord(
									(JTextComponent) e.getSource());
							super.mouseClicked(e);
						}
					}));
			JComponent[] components = new JComponent[writingTextFields.size()
					+ 1];
			for (int i = 0; i < writingTextFields.size(); i++) {
				components[i] = writingTextFields.get(i);
			}
			components[writingTextFields.size()] = createButtonSearchWord();
			writingsPanel
					.addElementsInColumnStartingFromColumn(writingTextFields,
							FillType.HORIZONTAL, 0, components);
		}
		JScrollPane jScrollPane = new JScrollPane(writingsPanel.getPanel());
		panel.addElementsInColumnStartingFromColumn(2, jScrollPane);
		jScrollPane.setPreferredSize(new Dimension(200, 150));
		//TODO temporary workaround - investigate

		return panel;
	}

	private AbstractButton createButtonSearchWord() {
		return GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.SEARCH_IN_DICTIONARY, new AbstractAction() {
					@Override public void actionPerformed(ActionEvent e) {
						problematicJapaneseWordsDisplayer
								.searchCurrentWordInDictionary();
					}
				});
	}

}
