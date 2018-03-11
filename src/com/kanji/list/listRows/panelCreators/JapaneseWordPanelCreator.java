package com.kanji.list.listRows.panelCreators;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.ListPanelViewMode;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.WordSearchOptions;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.JapaneseWordWritingsChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.CommonListElements;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class JapaneseWordPanelCreator {

	private Map<JTextComponent, List<JTextComponent>> kanaToKanjiWritingsTextComponents = new HashMap<>();
	private Map<JTextComponent, ListElementPropertyManager<?, JapaneseWordInformation>> propertyManagersOfTextFields = new HashMap<>();
	private ApplicationWindow applicationWindow;
	private JapaneseWordWritingsChecker japaneseWordWritingsChecker;
	private DialogWindow parentDialog;

	private JComboBox partOfSpeechCombobox;
	private JTextComponent wordMeaningText;
	private JLabel wordMeaningLabel;
	private JLabel partOfSpeechLabel;
	private List<KanaAndKanjiInputRow> kanaAndKanjiInputRows = new ArrayList<>();

	public JapaneseWordPanelCreator(ApplicationWindow applicationWindow,
			boolean isKanaRequired, DialogWindow parentDialog) {
		this.applicationWindow = applicationWindow;
		japaneseWordWritingsChecker = new JapaneseWordWritingsChecker(this,
				isKanaRequired);
		this.parentDialog = parentDialog;
	}

	private void createElements(JapaneseWordInformation japaneseWord,
			ListPanelViewMode listPanelViewMode,
			CommonListElements listElements, boolean kanaRequired,
			MainPanel addWordPanel) {
		wordMeaningText = CommonGuiElementsMaker
				.createShortInput(japaneseWord.getWordMeaning());
		WordSearchOptions meaningSearchOptions;
		if (listPanelViewMode.equals(ListPanelViewMode.ADD)){
			meaningSearchOptions = WordSearchOptions.BY_FULL_EXPRESSION;
		}
		else{
			meaningSearchOptions = WordSearchOptions.BY_WORD_FRAGMENT;
		}
		wordMeaningText.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWord,
						applicationWindow.getApplicationController()
								.getJapaneseWords(), parentDialog,
						new JapaneseWordMeaningChecker(meaningSearchOptions),
						ExceptionsMessages.JAPANESE_WORD_MEANING_ALREADY_DEFINED,
						true));
		wordMeaningLabel = GuiMaker
				.createLabel(new ComponentOptions().text(Labels.WORD_MEANING));
		partOfSpeechLabel = GuiMaker.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH));

		partOfSpeechCombobox = CommonGuiElementsMaker
				.createComboboxForPartOfSpeech();
		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			wordMeaningLabel.setForeground(Color.WHITE);
			partOfSpeechLabel.setForeground(Color.WHITE);
			listElements.getRowNumberLabel().setForeground(Color.WHITE);
		}

		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			partOfSpeechCombobox.addItemListener(new ItemListener() {
				@Override public void itemStateChanged(ItemEvent e) {
					String newValue = (String) e.getItem();
					japaneseWord.setPartOfSpeech(PartOfSpeech
							.getPartOfSpeachByPolishMeaning(newValue));
					//TODO auto save when editing part of speech
				}
			});
		}
		if (listPanelViewMode.equals(ListPanelViewMode.ADD)) {
			createKanaAndKanjiRowGuiElements(japaneseWord, addWordPanel, null,
					listPanelViewMode, kanaRequired);
		}
		else {

			for (Map.Entry<String, List<String>> kanaToKanjiWritings : japaneseWord
					.getKanaToKanjiWritingsMap().entrySet()) {
				createKanaAndKanjiRowGuiElements(japaneseWord, addWordPanel,
						kanaToKanjiWritings, listPanelViewMode, kanaRequired);
			}

		}

	}

	public void createAndAddKanaAndKanjiRowGuiElements(
			JapaneseWordInformation japaneseWordInformation,
			MainPanel rootPanel,
			Map.Entry<String, List<String>> kanaAndKanjiWritingsValues,
			ListPanelViewMode listPanelViewMode, boolean withValidation) {
		createKanaAndKanjiRowGuiElements(japaneseWordInformation, rootPanel,
				kanaAndKanjiWritingsValues, listPanelViewMode, withValidation);
		addKanaAndKanjiWritingRow(rootPanel);
	}

	private void createKanaAndKanjiRowGuiElements(
			JapaneseWordInformation japaneseWordInformation,
			MainPanel rootPanel,
			Map.Entry<String, List<String>> kanaAndKanjiWritingsValues,
			ListPanelViewMode listPanelViewMode, boolean withValidation) {
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
		Stream.concat(kanjiTextComponents.stream(), Stream.of(kanaWritingText))
				.forEach(textComponent -> {
					textComponent
							.setFont(textComponent.getFont().deriveFont(30f));
					addListenerSwitchToJapaneseKeyboardOnFocus(textComponent);
					String defaultValue;
					boolean isRequired;
					if (textComponent.equals(kanaWritingText)){
						defaultValue = Prompts.KANA_TEXT;
						isRequired = true;
					}
					else{
						defaultValue = Prompts.KANJI_TEXT;
						isRequired = false;
					}
					if (withValidation){
						addPropertyChangeHandler(textComponent,
								japaneseWordInformation, isRequired, defaultValue);
					}

				});
		AbstractButton addKanjiWritingButton = createButtonAddKanjiWriting(
				japaneseWordInformation, kanaWritingText, rootPanel,
				listPanelViewMode, withValidation);
		AbstractButton removeKanaAndKanjiWritingsButton = createButtonRemoveKanaAndKanjiWritings(
				rootPanel, rootPanel.getNumberOfRows() - 2, kanaWritingText,
				listPanelViewMode, japaneseWordInformation);
		if (kanaToKanjiWritingsTextComponents.size() == 1) {
			removeKanaAndKanjiWritingsButton.setEnabled(false);
		}
		else if (kanaToKanjiWritingsTextComponents.size() == 2) {
			removeKanaAndKanjiWritingsButton.setEnabled(true);
		}
		AbstractButton addKanaAndKanjiWritingsButton = createButtonAddKanaAndKanjiWritings(
				japaneseWordInformation, rootPanel, null, listPanelViewMode,
				withValidation);
		kanaAndKanjiInputRows
				.add(new KanaAndKanjiInputRow(kanaWritingLabel, kanaWritingText,
						kanjiTextComponents, addKanjiWritingButton,
						removeKanaAndKanjiWritingsButton,
						addKanaAndKanjiWritingsButton));
	}

	public MainPanel createPanelInGivenMode(
			JapaneseWordInformation japaneseWord,
			ListPanelViewMode listPanelViewMode,
			CommonListElements listElements) {
		MainPanel addWordPanel = new MainPanel(null);
		createElements(japaneseWord, listPanelViewMode, listElements, true,
				addWordPanel);
		WordSearchOptions meaningSearchOptions;
		if (listPanelViewMode.equals(ListPanelViewMode.ADD)){
			meaningSearchOptions = WordSearchOptions.BY_FULL_EXPRESSION;
		} //TODO duplicated code
		else{
			meaningSearchOptions = WordSearchOptions.BY_WORD_FRAGMENT;
		}

		propertyManagersOfTextFields
				.put(wordMeaningText, new JapaneseWordMeaningChecker(meaningSearchOptions));
		//TODO now we do double validation - after focus lost and after clicking add word
		List<JComponent> firstRow = new ArrayList<>();
		if (listPanelViewMode.equals(ListPanelViewMode.VIEW_AND_EDIT)) {
			firstRow.add(listElements.getRowNumberLabel());
		}
		else {
			firstRow.add(new JLabel());
			//TODO dummy label to preserve layout
		}
		firstRow.add(wordMeaningLabel);
		firstRow.add(wordMeaningText);
		addWordPanel.addElementsInColumnStartingFromColumn(wordMeaningText, 0,
				firstRow.toArray(new JComponent[] {}));
		addWordPanel
				.addElementsInColumnStartingFromColumn(partOfSpeechCombobox, 1,
						partOfSpeechLabel, partOfSpeechCombobox);

		addWordPanel.addElementsInColumnStartingFromColumn(1, new JLabel());
		//TODO dummy label that gets removed in "add kana and kanji row" method
		addKanaAndKanjiWritingRow(addWordPanel);

		SwingUtilities
				.invokeLater(() -> wordMeaningText.requestFocusInWindow());
		return addWordPanel;
	}

	public MainPanel createPanelInViewMode(
			JapaneseWordInformation japaneseWordInformation,
			CommonListElements commonListElements,
			RowInJapaneseWordInformations rowInJapaneseWordInformations) {

		return createPanelInGivenMode(japaneseWordInformation,
				ListPanelViewMode.VIEW_AND_EDIT, commonListElements);

	}

	public void addKanaAndKanjiWritingRow(MainPanel rootPanel) {

		JTextComponent lastKanaTextFieldAdded = null;
		for (KanaAndKanjiInputRow kanaAndKanjiInputRow : kanaAndKanjiInputRows) {
			AbstractButton addKanaAndKanjiWritingsButton = kanaAndKanjiInputRow
					.getAddKanaAndKanjiWritingsButton();
			AbstractButton addKanjiWritingButton = kanaAndKanjiInputRow
					.getAddKanjiWritingButton();
			AbstractButton removeKanaAndKanjiWritingsButton = kanaAndKanjiInputRow
					.getRemoveKanaAndKanjiWritingsButton();
			JLabel kanaWritingLabel = kanaAndKanjiInputRow
					.getKanaWritingLabel();
			JTextComponent kanaWritingText = kanaAndKanjiInputRow
					.getKanaWritingText();
			List<JTextComponent> kanjiTextComponents = kanaAndKanjiInputRow
					.getKanjiTextComponents();

			propertyManagersOfTextFields
					.put(kanaWritingText, japaneseWordWritingsChecker);
			kanaToKanjiWritingsTextComponents
					.put(kanaWritingText, kanjiTextComponents);

			MainPanel kanaAndKanjiWritings = new MainPanel(null);
			kanaAndKanjiWritings.setSkipInsetsForExtremeEdges(true);
			List<JComponent> elementsInRow = new ArrayList<>();
			elementsInRow.add(kanaWritingText);
			elementsInRow.addAll(kanjiTextComponents);
			elementsInRow.add(addKanjiWritingButton);
			elementsInRow.add(removeKanaAndKanjiWritingsButton);

			kanaAndKanjiWritings.addRow(SimpleRowBuilder
					.createRow(FillType.NONE,
							elementsInRow.toArray(new JComponent[] {})));
			rootPanel.removeLastRow();

			rootPanel.addElementsInColumnStartingFromColumn(
					kanaAndKanjiWritings.getPanel(), 1, kanaWritingLabel,
					kanaAndKanjiWritings.getPanel());

			rootPanel.addElementsInColumnStartingFromColumn(1,
					addKanaAndKanjiWritingsButton);
			lastKanaTextFieldAdded = kanaWritingText;

		}
		final JTextComponent finalFieldLastKanaText = lastKanaTextFieldAdded;
		SwingUtilities.invokeLater(() -> {
			if (finalFieldLastKanaText != null)
				finalFieldLastKanaText.requestFocusInWindow();
		});
		kanaAndKanjiInputRows.clear();

	}

	private void addPropertyChangeHandler(JTextComponent textComponent,
			JapaneseWordInformation japaneseWordInformation,
			boolean kanaRequired, String defaultValue) {
		textComponent.addFocusListener(
				new ListPropertyChangeHandler<>(japaneseWordInformation,
						applicationWindow.getApplicationController()
								.getJapaneseWords(), parentDialog,
						japaneseWordWritingsChecker, ExceptionsMessages.
						JAPANESE_WORD_WRITINGS_ALREADY_DEFINED, defaultValue,
						kanaRequired));
	}

	private void addListenerSwitchToJapaneseKeyboardOnFocus(
			JTextComponent textComponent) {
		textComponent.addFocusListener(new FocusAdapter() {

			@Override public void focusGained(FocusEvent e) {
				textComponent.getInputContext().selectInputMethod(Locale.JAPAN);
				textComponent.getInputContext().setCharacterSubsets(
						new Character.Subset[] {
								Character.UnicodeBlock.HIRAGANA });
				super.focusGained(e);
			}

			@Override public void focusLost(FocusEvent e) {
				super.focusLost(e);
				textComponent.getInputContext()
						.selectInputMethod(Locale.getDefault());
			}
		});
	}

	private AbstractButton createButtonAddKanjiWriting(
			JapaneseWordInformation japaneseWordInformation,
			JTextComponent kanaWritingText, MainPanel panel,
			ListPanelViewMode viewMode, boolean withValidation) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON,
						ButtonsNames.ADD_KANJI_WRITING, null);
		button.addActionListener(new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				JTextComponent kanjiTextComponent = CommonGuiElementsMaker
						.createShortInputWithPrompt(Prompts.KANJI_TEXT);
				kanjiTextComponent.setFont(kanjiTextComponent.getFont().deriveFont(30f));
				kanaToKanjiWritingsTextComponents.get(kanaWritingText)
						.add(kanjiTextComponent);
				if (withValidation) {
					addPropertyChangeHandler(kanjiTextComponent,
							japaneseWordInformation, true, Prompts.KANJI_TEXT);
				}

				addListenerSwitchToJapaneseKeyboardOnFocus(kanjiTextComponent);
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
			ListPanelViewMode listPanelViewMode, boolean withValidation) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON,
						ButtonsNames.ADD_KANA_AND_KANJI_WRITINGS,
						(new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								createAndAddKanaAndKanjiRowGuiElements(
										japaneseWordInformation, panel,
										kanaAndKanjiWritingsValues,
										listPanelViewMode, withValidation);
								if (listPanelViewMode
										.equals(ListPanelViewMode.ADD)) {
									Window w = SwingUtilities.
											windowForComponent(
													panel.getPanel());
									//TODO instead of panel, use the button and then we don't need the panel here
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

	public void clear (){
		propertyManagersOfTextFields.clear();
		kanaAndKanjiInputRows.clear();
	}

}
