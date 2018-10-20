package com.kanji.panelsAndControllers.controllers;

import com.guimaker.panels.MainPanel;
import com.guimaker.strings.ExceptionsMessages;
import com.kanji.application.ApplicationChangesManager;
import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElement;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.list.ListRowData;
import com.kanji.list.myList.MyList;
import com.guimaker.model.PropertyPostValidationData;
import com.kanji.panelsAndControllers.panels.InsertWordPanel;
import com.guimaker.utilities.CommonListElements;
import com.guimaker.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.FocusManager;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class InsertWordController<Word extends ListElement>
		implements InputValidationListener<Word> {

	private MyList<Word> list;
	private ApplicationChangesManager applicationChangesManager;
	private InsertWordPanel<Word> panel;
	private boolean addingWordWasRequested = false;
	private Word word;

	public InsertWordController(MyList<Word> list,
			ApplicationChangesManager applicationChangesManager,
			InsertWordPanel panel) {
		this.panel = panel;
		this.list = list;
		this.applicationChangesManager = applicationChangesManager;
		list.getListRowCreator().addValidationListener(this);
	}

	private void checkIfWordExistsOrIsEmptyAndAdd(Word word) {
		if (word.isEmpty()) {
			panel.getDialog()
					.showMessageDialog(ExceptionsMessages.NO_INPUT_SUPPLIED);
			return;
		}
		ThreadUtilities.callOnOtherThread(() -> {
			boolean addedWord = list.addWord(word);
			if (addedWord) {
				list.scrollToBottom();
				applicationChangesManager.save();
			}
			else{
				list.highlightRow(list.get1BasedRowNumberOfWord(word) - 1, true);
				panel.getDialog().showMessageDialog(
						String.format(ExceptionsMessages.WORD_ALREADY_EXISTS,
								list.get1BasedRowNumberOfWord(word)), false);
			}
		});
	}

	public AbstractAction createActionValidateAndAddFocusedElement() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component focusOwner = FocusManager.getCurrentManager()
						.getFocusOwner();
				if (focusOwner instanceof JTextComponent) {
					validateFocusedTextInput();
					addingWordWasRequested = true;
				}
				else {
					checkWordAndAddAndReinitializePanel();
				}
			}
		};

	}

	private void validateFocusedTextInput() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.clearGlobalFocusOwner();
	}

	@Override
	public <WordProperty> void inputValidated(
			PropertyPostValidationData<WordProperty, Word> postValidationData) {
		if (addingWordWasRequested && postValidationData.isValid()) {
			checkWordAndAddAndReinitializePanel();
		}
		addingWordWasRequested = false;
	}

	private void checkWordAndAddAndReinitializePanel() {
		checkIfWordExistsOrIsEmptyAndAdd(word);
		panel.reinitializePanel();
	}

	public MainPanel createListRowPanel() {
		word = list.getWordInitializer().initializeElement();
		ListRowData<Word> listRowData = list.getListRowCreator()
				.createListRow(word, CommonListElements
								.forSingleRowOnly(panel.getLabelsColor()),
						InputGoal.ADD);
		return listRowData.getRowPanel();
	}

}
