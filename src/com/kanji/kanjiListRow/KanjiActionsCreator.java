package com.kanji.kanjiListRow;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.ListElementPropertyManager;
import com.guimaker.list.myList.ListPropertyChangeHandler;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.CommonListElements;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElements.Kanji;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class KanjiActionsCreator {
	private DialogWindow parentDialog;
	private Set<InputValidationListener<Kanji>> validationListeners = new HashSet<>();
	private KanjiKeywordChecker keywordChecker;
	private KanjiIdChecker idChecker;

	public KanjiActionsCreator(DialogWindow parentDialog) {
		this.parentDialog = parentDialog;
	}

	public JTextComponent withKeywordValidation(JTextComponent keywordInput,
			Kanji kanji, InputGoal inputGoal,
			CommonListElements<Kanji> commonListElements) {
		if (inputGoal.equals(InputGoal.NO_INPUT)) {
			return keywordInput;
		}
		keywordChecker = new KanjiKeywordChecker();
		keywordInput.addFocusListener(
				createPropertyChangeHandler(kanji, keywordChecker, inputGoal,
						commonListElements));
		//TODO keyword checker, kanji id checker are stateless - thats
		//why we can create one instance and reuse instead of creating it
		//everytime
		return keywordInput;
	}

	public JTextComponent withKanjiIdValidation(JTextComponent kanjiIdInput,
			Kanji kanji, InputGoal inputGoal,
			CommonListElements<Kanji> commonListElements) {
		if (inputGoal.equals(InputGoal.NO_INPUT)) {
			return kanjiIdInput;
		}
		idChecker = new KanjiIdChecker();
		kanjiIdInput.addFocusListener(
				createPropertyChangeHandler(kanji, idChecker, inputGoal,
						commonListElements));
		return kanjiIdInput;
	}

	private ListPropertyChangeHandler<?, Kanji> createPropertyChangeHandler(
			Kanji kanji, ListElementPropertyManager<?, Kanji> propertyManager,
			InputGoal inputGoal, CommonListElements<Kanji> commonListElements) {

		ListPropertyChangeHandler listPropertyChangeHandler = new ListPropertyChangeHandler<>(
				kanji, commonListElements.getList(), parentDialog,
				propertyManager, inputGoal,
				JapaneseWritingUtilities.getDefaultValueForWriting(
						TypeOfJapaneseWriting.KANJI));
		validationListeners.forEach(
				listPropertyChangeHandler::addValidationListener);
		return listPropertyChangeHandler;
	}

	public AbstractButton withActionShowKanjiStories(
			AbstractButton abstractButton,
			ProblematicWordsController<Kanji> problematicWordsController,
			Kanji kanji) {
		abstractButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				problematicWordsController.showResource(kanji);
			}
		});

		return abstractButton;
	}

	public KanjiKeywordChecker getKeywordChecker() {
		return keywordChecker;
	}

	public KanjiIdChecker getIdChecker() {
		return idChecker;
	}

	public void addValidationListener(
			InputValidationListener<Kanji> inputValidationListener) {
		validationListeners.add(inputValidationListener);
	}
}
