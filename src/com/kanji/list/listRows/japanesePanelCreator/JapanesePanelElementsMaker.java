package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

public class JapanesePanelElementsMaker {

	private JapanesePanelEditOrAddModeAction actionsMaker;
	private JapanesePanelActions actionsCreator;

	public JapanesePanelElementsMaker(
			JapanesePanelEditOrAddModeAction actionsMaker,
			JapanesePanelActions actionsCreator) {
		this.actionsMaker = actionsMaker;
		this.actionsCreator = actionsCreator;
	}

	private JTextComponent viewOnlyTextInput(JTextComponent field) {
		field.setEnabled(false);
		field.setBackground(TextFieldSelectionHandler.NOT_SELECTED_COLOR);
		return field;
	}

	public JTextComponent createKanaInputWithValidation(
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean enabled, boolean isForSearchDialog) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(japaneseWriting.getKanaWriting(), enabled,
						true), japaneseWriting, japaneseWord, true,
				isForSearchDialog);
	}

	public JTextComponent createKanjiInputWithValidation(String text,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean isForSearchDialog) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(text, true, false), japaneseWriting,
				japaneseWord, false, isForSearchDialog);
	}

	public JTextComponent createWritingsInput(String text, boolean enabled,
			boolean isKana) {
		JTextComponent kanjiTextInput = createWritingsInput(text, isKana);
		if (!enabled) {
			return viewOnlyTextInput(kanjiTextInput);
		}
		else {
			return kanjiTextInput;
		}
	}

	private JTextComponent createWritingsInput(String initialValue,
			boolean isKana) {
		return actionsCreator.withSwitchToJapaneseActionOnClick(
				GuiMaker.createTextField(
						new TextComponentOptions().text(initialValue)
								.editable(true)
								.font(ApplicationWindow.getKanjiFont())
								.focusable(true).fontSize(30f).promptWhenEmpty(
								JapaneseWritingUtilities
										.getDefaultValueForWriting(isKana))));
	}

	private AbstractButton createButton(String buttonLabel,
			AbstractAction actionOnClick) {
		return GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON, buttonLabel,
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
			boolean isForSearchDialog) {
		AbstractButton button = createButton(ButtonsNames.ADD_KANJI_WRITING,
				null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rowPanel.insertElementInPlaceOfElement(
						createKanjiInputWithValidation("", japaneseWriting,
								japaneseWord, isForSearchDialog), button);
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
}
