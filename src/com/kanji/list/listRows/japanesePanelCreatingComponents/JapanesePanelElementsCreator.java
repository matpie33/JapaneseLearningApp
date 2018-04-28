package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.enums.ButtonType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.myList.InputValidationListener;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.Set;

public class JapanesePanelElementsCreator {

	private JapanesePanelActionsCreator actionsCreator;

	public JapanesePanelElementsCreator(
			JapanesePanelActionsCreator actionsCreator) {
		this.actionsCreator = actionsCreator;
	}

	public JTextComponent createKanaInputWithValidation(
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean enabled, boolean isForSearchDialog, boolean selectable) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(japaneseWriting.getKanaWriting(), true,
						enabled, selectable), japaneseWriting, japaneseWord,
				true, isForSearchDialog);
	}

	public JTextComponent createKanjiInputWithValidation(String text,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean isForSearchDialog, boolean enabled, boolean selectable) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(text, false, enabled, selectable),
				japaneseWriting, japaneseWord, false, isForSearchDialog);
	}

	public JTextComponent createWritingsInput(String initialValue,
			boolean isKana, boolean editable, boolean selectable) {
		return actionsCreator.repaintParentOnFocusLost(actionsCreator
				.withSwitchToJapaneseActionOnClick(GuiElementsCreator
						.createTextField(
								new TextComponentOptions().text(initialValue)
										.editable(editable)
										.selectable(selectable)
										.font(ApplicationWindow.getKanjiFont())
										.focusable(true).fontSize(30f)
										.promptWhenEmpty(
												JapaneseWritingUtilities
														.getDefaultValueForWriting(
																isKana)))));
	}

	private AbstractButton createButton(String buttonLabel,
			AbstractAction actionOnClick) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonLabel),
				actionOnClick);

	}

	public JComboBox<String> createComboboxForPartOfSpeech(
			PartOfSpeech partOfSpeechToSelect) {
		JComboBox<String> comboBox = new JComboBox<>();
		for (PartOfSpeech partOfSpeech : PartOfSpeech.values()) {
			comboBox.addItem(partOfSpeech.getPolishMeaning());
			if (partOfSpeech.equals(partOfSpeechToSelect)) {
				comboBox.setSelectedItem(
						partOfSpeechToSelect.getPolishMeaning());
			}
		}
		return comboBox;
	}

	public AbstractButton createButtonAddKanjiWriting(MainPanel rowPanel,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean isForSearchDialog, boolean editMode, boolean selectable) {
		AbstractButton button = createButton(ButtonsNames.ADD_KANJI_WRITING,
				null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rowPanel.insertElementInPlaceOfElement(
						createKanjiInputWithValidation("", japaneseWriting,
								japaneseWord, isForSearchDialog, editMode,
								selectable), button);
			}
		});
		return button;
	}

	public JComponent updateWritingsInWordCheckerWhenDeleteWriting(
			AbstractButton buttonDelete, JapaneseWord japaneseWord,
			JapaneseWriting writing, boolean isForSearchDialog) {
		return actionsCreator
				.updateWritingsInWordWhenDeleteWriting(buttonDelete,
						japaneseWord, writing, isForSearchDialog);
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		actionsCreator.setInputValidationListeners(validationListeners);
	}
}
