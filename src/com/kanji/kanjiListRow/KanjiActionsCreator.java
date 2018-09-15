package com.kanji.kanjiListRow;

import com.guimaker.enums.PanelDisplayMode;
import com.kanji.constants.enums.InputGoal;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElementPropertyManagers.KanjiKeywordChecker;
import com.kanji.list.listElementPropertyManagers.ListElementPropertyManager;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListPropertyChangeHandler;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class KanjiActionsCreator {
	private ApplicationController applicationController;
	private DialogWindow parentDialog;
	private Set<InputValidationListener<Kanji>> validationListeners = new HashSet<>();
	private KanjiKeywordChecker keywordChecker;
	private KanjiIdChecker idChecker;
	private PanelDisplayMode displayMode;

	public KanjiActionsCreator(ApplicationController applicationController,
			DialogWindow parentDialog, PanelDisplayMode panelDisplayMode) {
		this.applicationController = applicationController;
		this.parentDialog = parentDialog;
		this.displayMode = panelDisplayMode;
	}

	public JTextComponent withKeywordValidation(JTextComponent keywordInput,
			Kanji kanji, InputGoal inputGoal) {
		if (displayMode.equals(PanelDisplayMode.VIEW)) {
			return keywordInput;
		}
		keywordChecker = new KanjiKeywordChecker();
		keywordInput.addFocusListener(
				createPropertyChangeHandler(kanji, keywordChecker, inputGoal));
		//TODO keyword checker, kanji id checker are stateless - thats
		//why we can create one instance and reuse instead of creating it
		//everytime
		return keywordInput;
	}

	public JTextComponent withKanjiIdValidation(JTextComponent kanjiIdInput,
			Kanji kanji, InputGoal inputGoal) {
		if (displayMode.equals(PanelDisplayMode.VIEW)) {
			return kanjiIdInput;
		}
		idChecker = new KanjiIdChecker();
		kanjiIdInput.addFocusListener(
				createPropertyChangeHandler(kanji, idChecker, inputGoal));
		return kanjiIdInput;
	}

	private ListPropertyChangeHandler<?, Kanji> createPropertyChangeHandler(
			Kanji kanji, ListElementPropertyManager<?, Kanji> propertyManager,
			InputGoal inputGoal) {

		ListPropertyChangeHandler listPropertyChangeHandler = new ListPropertyChangeHandler<>(
				kanji, applicationController.getKanjiList(), parentDialog,
				propertyManager, inputGoal);
		validationListeners
				.forEach(listPropertyChangeHandler::addValidationListener);
		return listPropertyChangeHandler;
	}

	public AbstractButton withActionShowKanjiStories(
			AbstractButton abstractButton,
			ProblematicWordsController problematicWordsController,
			ListElement kanji) {
		int rowNumber = problematicWordsController.getNumberOfRows();
		abstractButton.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				problematicWordsController
						.showResource(new WordRow(kanji, rowNumber));
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
