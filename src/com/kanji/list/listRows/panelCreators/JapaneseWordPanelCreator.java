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
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.ListElementData;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class JapaneseWordPanelCreator {

	private Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritingsTextComponents = new HashMap<>();
	private JComboBox partOfSpeechCombobox;
	private JTextComponent wordMeaningText;
	private Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> propertyManagersOfTextFields = new HashMap<>();
	private ApplicationWindow applicationWindow;
	private JapaneseWordWritingsChecker japaneseWordWritingsChecker;

	public JapaneseWordPanelCreator(ApplicationWindow applicationWindow) {
		this.applicationWindow = applicationWindow;
	}

	public MainPanel createPanelInGivenMode(
			JapaneseWordInformation japaneseWord,
			ListPanelViewMode listPanelViewMode,
			CommonListElements listElements) {

		japaneseWordWritingsChecker = new JapaneseWordWritingsChecker(this);
		MainPanel addWordPanel = new MainPanel(null);
		//TODO separate it into create elements, and add to panel, do the inserts to maps in 1 place

		List<ListElementData<JapaneseWordInformation>> listElementData = new ArrayList<>();
		listElementData.add(new ListElementData<>(Labels.WORD_MEANING,
				new JapaneseWordMeaningChecker(),
				ListElementPropertyType.STRING_SHORT_WORD,
				Labels.COMBOBOX_OPTION_SEARCH_BY_WORD_MEANING));
		listElementData.add(new ListElementData<>(Labels.WORD_IN_KANA,
				new JapaneseWordWritingsChecker(null),
				ListElementPropertyType.STRING_SHORT_WORD,
				Labels.COMBOBOX_OPTION_SEARCH_BY_KANA));

		wordMeaningText = CommonGuiElementsMaker
				.createShortInput(japaneseWord.getWordMeaning());
		wordMeaningText.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWord,
						applicationWindow.getApplicationController()
								.getJapaneseWords(), applicationWindow,
						new JapaneseWordMeaningChecker(),
						ExceptionsMessages.JAPANESE_WORD_MEANING_ALREADY_DEFINED));
		wordMeaningText.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) {
				System.out
						.println(wordMeaningText.getInputContext().getLocale());
				super.focusGained(e);
			}
		});
		propertyManagersOfTextFields
				.put(wordMeaningText, new JapaneseWordMeaningChecker());
		JLabel wordMeaningLabel = GuiMaker
				.createLabel(new ComponentOptions().text(Labels.WORD_MEANING));
		JLabel partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH));

		partOfSpeechCombobox = CommonGuiElementsMaker
				.createComboboxForPartOfSpeech();
		partOfSpeechCombobox.setSelectedItem(
				japaneseWord.getPartOfSpeech().getPolishMeaning());

		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			partOfSpeechCombobox.addItemListener(new ItemListener() {
				@Override public void itemStateChanged(ItemEvent e) {
					String newValue = (String) e.getItem();
					japaneseWord.setPartOfSpeech(PartOfSpeech
							.getPartOfSpeachByPolishMeaning(newValue));
				}
			});
		}

		List<JComponent> firstRow = new ArrayList<>();

		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			wordMeaningLabel.setForeground(Color.WHITE);
			partOfSpeechLabel.setForeground(Color.WHITE);
			listElements.getRowNumberLabel().setForeground(Color.WHITE);
			firstRow.add(listElements.getRowNumberLabel());
		}
		else {
			firstRow.add(new JLabel());
			//			TODO dummy label to preserve layout
		}

		firstRow.add(wordMeaningLabel);
		firstRow.add(wordMeaningText);

		addWordPanel.addElementsInColumnStartingFromColumn(wordMeaningText, 0,
				firstRow.toArray(new JComponent[] {}));
		addWordPanel
				.addElementsInColumnStartingFromColumn(partOfSpeechCombobox, 1,
						partOfSpeechLabel, partOfSpeechCombobox);

		AbstractButton addKanaAndKanjiWritingsButton = createButtonAddKanaAndKanjiWritings(
				japaneseWord, addWordPanel, null, listPanelViewMode);
		addWordPanel.addElementsInColumnStartingFromColumn(1,
				addKanaAndKanjiWritingsButton);

		if (listPanelViewMode.equals(ListPanelViewMode.ADD)) {
			addKanaAndKanjiWritingRow(japaneseWord, addWordPanel, null,
					listPanelViewMode);
		}
		else {
			addWordPanel.addElementsInColumnStartingFromColumn(1,
					listElements.getButtonDelete());
			if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
				for (Map.Entry<String, List<String>> kanaToKanjiWritings : japaneseWord
						.getKanaToKanjiWritingsMap().entrySet()) {
					addKanaAndKanjiWritingRow(japaneseWord, addWordPanel,
							kanaToKanjiWritings, listPanelViewMode);
				}

			}
		}
		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
		//TODO make a property manager that can get all kana and kanji writing text fields
		// when given a specific text field of kanji writing, wrap kana and kanji buttons as
		// a class that will act as property in property manager, use a cache in property manager
		// that is a map between kana text field and kanji text fields found in last check
		return addWordPanel;
	}

	public MainPanel createPanelInViewMode(
			JapaneseWordInformation japaneseWordInformation,
			CommonListElements commonListElements,
			RowInJapaneseWordInformations rowInJapaneseWordInformations) {

		return createPanelInGivenMode(japaneseWordInformation,
				ListPanelViewMode.VIEW_AND_EDIT, commonListElements);

	}

	private void addKanaAndKanjiWritingRow(
			JapaneseWordInformation japaneseWordInformation,
			MainPanel rootPanel,
			Map.Entry<String, List<String>> kanaAndKanjiWritingsValues,
			ListPanelViewMode listPanelViewMode) {
		JLabel kanaWritingLabel = GuiMaker
				.createLabel(new ComponentOptions().text(Labels.WORD_IN_KANA));
		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			kanaWritingLabel.setForeground(Color.WHITE);
		}
		JTextComponent kanaWritingText;
		List<JTextComponent> kanjiTextComponents = new ArrayList<>();
		if (kanaAndKanjiWritingsValues == null) {
			kanaWritingText = CommonGuiElementsMaker
					.createShortInputWithPrompt(Prompts.KANA_TEXT);
			kanjiTextComponents.add(CommonGuiElementsMaker
					.createShortInputWithPrompt(Prompts.KANJI_TEXT));

		}
		else {
			kanaWritingText = CommonGuiElementsMaker
					.createShortInput(kanaAndKanjiWritingsValues.getKey());

			for (String kanji : kanaAndKanjiWritingsValues.getValue()) {
				kanjiTextComponents
						.add(CommonGuiElementsMaker.createShortInput(kanji));
			}
		}
		//TODO figure out how to switch back to pl, currently it switches to en on
		//focus lost
		kanaWritingText.addFocusListener(new FocusAdapter() {
			boolean already = false;

			@Override public void focusGained(FocusEvent e) {
				if (already) {
					return;
				}
				kanaWritingText.getInputContext()
						.selectInputMethod(Locale.JAPAN);
				kanaWritingText.getInputContext().setCharacterSubsets(
						new Character.Subset[] {
								Character.UnicodeBlock.HIRAGANA });
				super.focusGained(e);
			}

			@Override public void focusLost(FocusEvent e) {
				super.focusLost(e);
				if (!already) {
					kanaWritingText.getInputContext()
							.selectInputMethod(Locale.getDefault());
				}
				System.out
						.println(kanaWritingText.getInputContext().getLocale());

				already = true;
			}
		});

		propertyManagersOfTextFields
				.put(kanaWritingText, japaneseWordWritingsChecker);
		for (JTextComponent kanjiTextComponent : kanjiTextComponents) {
			propertyManagersOfTextFields
					.put(kanjiTextComponent, japaneseWordWritingsChecker);
		}
		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			Stream.concat(kanjiTextComponents.stream(),
					Stream.of(kanaWritingText)).forEach(
					textComponent -> textComponent.addFocusListener(
							new ListPropertyChangeHandler<>(
									japaneseWordInformation,
									applicationWindow.getApplicationController()
											.getJapaneseWords(),
									applicationWindow,
									japaneseWordWritingsChecker,
									ExceptionsMessages.
											JAPANESE_WORD_WRITINGS_ALREADY_DEFINED,
									Prompts.KANJI_TEXT)));
		}
		List<JTextComponent> kanjiWritingsComponents = new ArrayList<>();
		//TODO duplicated variable kanjiwritings components
		kanjiWritingsComponents.addAll(kanjiTextComponents);
		kanaToKanjiWritingsTextComponents
				.put(kanaWritingText, kanjiWritingsComponents);
		MainPanel kanaAndKanjiWritings = new MainPanel(null);
		kanaAndKanjiWritings.setSkipInsetsForExtremeEdges(true);

		List<JComponent> elementsInRow = new ArrayList<>();
		elementsInRow.add(kanaWritingText);
		elementsInRow.addAll(kanjiTextComponents);

		AbstractButton addKanjiWritingButton = createButtonAddKanjiWriting(
				japaneseWordInformation, kanaWritingText, rootPanel,
				listPanelViewMode);
		AbstractButton removeKanaAndKanjiWritingsButton = createButtonRemoveKanaAndKanjiWritings(
				rootPanel, rootPanel.getNumberOfRows() - 2, kanaWritingText,
				listPanelViewMode, japaneseWordInformation);
		if (kanaToKanjiWritingsTextComponents.size() == 1) {
			removeKanaAndKanjiWritingsButton.setEnabled(false);
		}
		else if (kanaToKanjiWritingsTextComponents.size() == 2) {
			removeKanaAndKanjiWritingsButton.setEnabled(true);
		}
		elementsInRow.add(addKanjiWritingButton);
		elementsInRow.add(removeKanaAndKanjiWritingsButton);

		kanaAndKanjiWritings.addRow(SimpleRowBuilder.createRow(FillType.NONE,
				elementsInRow.toArray(new JComponent[] {})));

		rootPanel.insertRowStartingFromColumn(1,
				listPanelViewMode.equals(ListPanelViewMode.ADD) ?
						rootPanel.getNumberOfRows() - 1 :
						rootPanel.getNumberOfRows() - 2, kanaWritingLabel,
				kanaAndKanjiWritings.getPanel());
	}

	private AbstractButton createButtonAddKanjiWriting(
			JapaneseWordInformation japaneseWordInformation,
			JTextComponent kanaWritingText, MainPanel panel,
			ListPanelViewMode viewMode) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON,
						ButtonsNames.ADD_KANJI_WRITING, null);
		button.addActionListener(new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				JTextComponent kanjiTextComponent = CommonGuiElementsMaker
						.createShortInputWithPrompt(Prompts.KANJI_TEXT);
				propertyManagersOfTextFields
						.put(kanjiTextComponent, japaneseWordWritingsChecker);
				kanaToKanjiWritingsTextComponents.get(kanaWritingText)
						.add(kanjiTextComponent);
				kanjiTextComponent.addFocusListener(
						new ListPropertyChangeHandler<>(japaneseWordInformation,
								applicationWindow.getApplicationController()
										.getJapaneseWords(), applicationWindow,
								japaneseWordWritingsChecker, ExceptionsMessages.
								JAPANESE_WORD_WRITINGS_ALREADY_DEFINED,
								Prompts.KANJI_TEXT));

				panel.insertElementBeforeOtherElement(button,
						kanjiTextComponent);
				if (viewMode.equals(ListPanelViewMode.ADD)) {
					Window w = SwingUtilities.windowForComponent(button);
					if (w != null) {
						w.pack();
					}
					SwingUtilities.invokeLater(
							() -> kanjiTextComponent.requestFocusInWindow());
				}
			}
		});
		return button;
	}

	private AbstractButton createButtonRemoveKanaAndKanjiWritings(
			MainPanel panel, int rowNumber, JTextComponent kanaTextComponent,
			ListPanelViewMode viewMode, JapaneseWordInformation japaneseWord) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON,
						ButtonsNames.REMOVE_KANA_AND_KANJI_WRITINGS,
						(new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								kanaToKanjiWritingsTextComponents
										.remove(kanaTextComponent);
								panel.removeRowInAColumnWay(rowNumber);
								if (viewMode.equals(ListPanelViewMode.ADD)) {
									SwingUtilities.windowForComponent(
											panel.getPanel()).pack();
								}
								japaneseWord.getKanaToKanjiWritingsMap()
										.remove(kanaTextComponent.getText());
								applicationWindow.getApplicationController()
										.saveProject();
							}
						}));
		return button;
	}

	private AbstractButton createButtonAddKanaAndKanjiWritings(
			JapaneseWordInformation japaneseWordInformation, MainPanel panel,
			Map.Entry<String, List<String>> kanaAndKanjiWritingsValues,
			ListPanelViewMode listPanelViewMode) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON,
						ButtonsNames.ADD_KANA_AND_KANJI_WRITINGS,
						(new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								addKanaAndKanjiWritingRow(
										japaneseWordInformation, panel,
										kanaAndKanjiWritingsValues,
										listPanelViewMode);
								if (listPanelViewMode
										.equals(ListPanelViewMode.ADD)) {
									Window w = SwingUtilities.
											windowForComponent(
													panel.getPanel());
									if (w != null) {
										w.pack();
									}
								}
							}
						}));
		return button;
	}

	public Map<JTextComponent, List<JTextComponent>> getKanaToKanjiWritingsTextComponents() {
		return kanaToKanjiWritingsTextComponents;
	}

	public JComboBox getPartOfSpeechCombobox() {
		return partOfSpeechCombobox;
	}

	public JTextComponent getWordMeaningText() {
		return wordMeaningText;
	}

	public Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> getPropertyManagersOfTextFields() {
		return propertyManagersOfTextFields;
	}

}
