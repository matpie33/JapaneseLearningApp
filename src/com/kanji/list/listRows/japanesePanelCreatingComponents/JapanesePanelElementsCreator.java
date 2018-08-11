package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listRows.RowInParticlesInformation;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.AdditionalInformation;
import com.kanji.model.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JapanesePanelElementsCreator {

	private JapanesePanelActionsCreator actionsCreator;
	private ApplicationController applicationController;
	private DialogWindow dialogWindow;

	public JapanesePanelElementsCreator(
			JapanesePanelActionsCreator actionsCreator,
			ApplicationController applicationController,
			DialogWindow parentDialog) {
		this.actionsCreator = actionsCreator;
		this.applicationController = applicationController;
		this.dialogWindow = parentDialog;
	}

	public JTextComponent createKanaInputWithValidation(
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean enabled, InputGoal inputGoal, boolean selectable) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(japaneseWriting.getKanaWriting(), true,
						enabled, selectable), japaneseWriting, japaneseWord,
				true, inputGoal, enabled);
	}

	public MyList<WordParticlesData> createParticlesDataList(
			JapaneseWord japaneseWord, PanelDisplayMode displayMode) {
		MyList<WordParticlesData> particlesList = new MyList<>(dialogWindow,
				applicationController,
				new RowInParticlesInformation(japaneseWord,
						applicationController, displayMode), "",
				new ListConfiguration().showButtonsLoadNextPreviousWords(false)
						.enableWordAdding(false).enableWordSearching(false)
						.scrollBarFitsContent(true).inheritScrollbar(true)
						.parentListAndWordContainingThisList(
								applicationController.getJapaneseWords(),
								japaneseWord), () -> WordParticlesData
				.createParticleNotIncludedInWord(japaneseWord));
		if (japaneseWord.getTakenParticles() != null) {
			japaneseWord.getTakenParticles().forEach(particlesList::addWord);
		}

		return particlesList;
	}

	public JTextComponent createKanjiInputWithValidation(String text,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			InputGoal inputGoal, boolean enabled, boolean selectable) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(text, false, enabled, selectable),
				japaneseWriting, japaneseWord, false, inputGoal, enabled);
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
			PartOfSpeech partOfSpeechToSelect,
			JLabel additionalInformationLabel,
			JComboBox additionalInformationValue, JapaneseWord japaneseWord) {
		JComboBox<String> comboBox = actionsCreator
				.addAdditionalInformationOnPartOfSpeechChange(
						additionalInformationValue, additionalInformationLabel,
						GuiElementsCreator.createCombobox(new ComboboxOptions()
								.setComboboxValues(
										Arrays.stream(PartOfSpeech.values())
												.map(PartOfSpeech::getPolishMeaning)
												.collect(Collectors.toList()))),
						japaneseWord);

		comboBox.setSelectedItem(partOfSpeechToSelect.getPolishMeaning());
		return comboBox;
	}

	public AbstractButton createButtonAddKanjiWriting(MainPanel rowPanel,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			InputGoal inputGoal, boolean editMode, boolean selectable) {
		AbstractButton button = createButton(ButtonsNames.ADD_KANJI_WRITING,
				null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rowPanel.insertElementInPlaceOfElement(
						createKanjiInputWithValidation("", japaneseWriting,
								japaneseWord, inputGoal, editMode, selectable),
						button);
			}
		});
		return button;
	}

	public JComponent createButonDelete(AbstractButton buttonDelete,
			JapaneseWord japaneseWord, JapaneseWriting writing,
			InputGoal inputGoal) {
		return actionsCreator
				.updateWritingsInWordWhenDeleteWriting(buttonDelete,
						japaneseWord, writing, inputGoal);
	}

	public void addValidationListeners(
			Set<InputValidationListener<JapaneseWord>> validationListeners) {
		actionsCreator.setInputValidationListeners(validationListeners);
	}

	public JComboBox createComboboxForAdditionalInformation(
			JapaneseWord japaneseWord) {
		AdditionalInformation additionalInformation = japaneseWord
				.getAdditionalInformation();
		List<String> possibleValues = additionalInformation.getPossibleValues();
		boolean hasPossibleAdditionalInformation = !additionalInformation
				.getPossibleValues().isEmpty();
		JComboBox comboBox = actionsCreator
				.changeAdditionalInformationOnComboboxChange(GuiElementsCreator
								.createCombobox(new ComboboxOptions()
										.setComboboxValues(possibleValues)
										.setEnabled(hasPossibleAdditionalInformation)),
						japaneseWord);

		if (hasPossibleAdditionalInformation) {
			String value = additionalInformation.getValue();
			comboBox.setSelectedItem(
					value != null ? value : possibleValues.get(0));
		}

		return comboBox;
	}

	public JLabel createAdditionalInformationLabel(JapaneseWord japaneseWord,
			Color labelColor) {
		AdditionalInformation additionalInformation = japaneseWord
				.getAdditionalInformation();
		return GuiElementsCreator.createLabel(new ComponentOptions()
				.text(additionalInformation.getTag().getLabel())
				.foregroundColor(labelColor)
				.setEnabled(!additionalInformation.isEmpty()));
	}
}
