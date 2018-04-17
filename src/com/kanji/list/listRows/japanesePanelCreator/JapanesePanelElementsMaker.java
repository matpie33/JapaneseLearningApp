package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElementPropertyManagers.japaneseWordWritings.JapaneseWordWritingsInputManager;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class JapanesePanelElementsMaker {

	private JapanesePanelEditOrAddModeAction actionsMaker;
	private JapanesePanelActions actionsCreator;
	private Map<JapaneseWriting, JapaneseWordWritingsInputManager> writingsInputManagers = new HashMap<>();

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

	public JapaneseWordWritingsInputManager createJapaneseWritingsTextFields(
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean enabled) {
		//TODO can I use just one of them? only japanese writing or only japanese word?
		JTextComponent kanaInput = createWritingsInput(
				japaneseWriting.getKanaWriting(), enabled, true);
		JapaneseWordWritingsInputManager inputManager = new JapaneseWordWritingsInputManager(
				kanaInput);
		writingsInputManagers.put(japaneseWriting, inputManager);
		actionsCreator.withJapaneseWritingValidation(kanaInput, inputManager,
				japaneseWriting, japaneseWord, true);

		for (String kanjiWriting : japaneseWriting.getKanjiWritings()) {
			createKanjiInputWithValidation(kanjiWriting, inputManager,
					japaneseWriting, japaneseWord);
		}

		return inputManager;

	}

	private JTextComponent createKanjiInputWithValidation(String text,
			JapaneseWordWritingsInputManager inputManager,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(text, true, false), inputManager,
				japaneseWriting, japaneseWord, false);
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
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord) {
		AbstractButton button = createButton(ButtonsNames.ADD_KANJI_WRITING,
				null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rowPanel.insertElementInPlaceOfElement(
						createKanjiInputWithValidation("",
								writingsInputManagers.get(japaneseWriting),
								japaneseWriting, japaneseWord), button);
			}
		});
		return button;
	}

}
