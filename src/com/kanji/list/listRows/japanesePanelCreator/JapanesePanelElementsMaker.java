package com.kanji.list.listRows.japanesePanelCreator;

import com.guimaker.enums.ComponentType;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelActions;
import com.kanji.list.listRows.japanesePanelActionsCreator.JapanesePanelEditOrAddModeAction;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

public class JapanesePanelElementsMaker {

	private JapanesePanelEditOrAddModeAction actionsMaker;

	public JapanesePanelElementsMaker(
			JapanesePanelEditOrAddModeAction actionsMaker) {
		this.actionsMaker = actionsMaker;
	}

	private JTextComponent viewOnlyTextField(JTextComponent field) {
		field.setEnabled(false);
		field.setBackground(TextFieldSelectionHandler.NOT_SELECTED_COLOR);
		return field;
	}

	public JTextComponent createKanaTextField(String text,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation, boolean enabled) {
		JTextComponent kanaTextField = actionsMaker.withKanaValidation(
				createKanaOrKanjiTextField(text, Prompts.KANA_TEXT),
				japaneseWriting, japaneseWordInformation);
		if (!enabled){
			return viewOnlyTextField(kanaTextField);
		}
		else{
			return kanaTextField;
		}
	}

	public JTextComponent createKanjiTextField(String text,
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation, boolean enabled) {
		JTextComponent kanjiTextField =  actionsMaker.withKanjiValidation(
				createKanaOrKanjiTextField(text, Prompts.KANJI_TEXT),
				japaneseWriting, japaneseWordInformation);
		if (!enabled){
			return viewOnlyTextField(kanjiTextField);
		}
		else{
			return kanjiTextField;
		}
	}

	private JTextComponent createKanaOrKanjiTextField(String initialValue,
			String prompt) {
		return JapanesePanelActions.withSwitchToJapaneseActionOnClick(
				GuiMaker.createTextField(
						new TextComponentOptions().text(initialValue)
								.editable(true)
								.font(ApplicationWindow.getKanjiFont())
								.focusable(true).fontSize(30f)
								.promptWhenEmpty(prompt)));
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
			JapaneseWriting japaneseWriting,
			JapaneseWordInformation japaneseWordInformation) {
		AbstractButton button = createButton(ButtonsNames.ADD_KANJI_WRITING,
				null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rowPanel.insertElementInPlaceOfElement(
						createKanjiTextField("", japaneseWriting,
								japaneseWordInformation, true), button);
			}
		});
		return button;
	}

}
