package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.application.DialogWindow;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.enums.WordSearchOptions;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.CommonListElements;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.list.listRows.RowInJapaneseWritingsList;
import com.kanji.list.listRows.RowInParticlesInformation;
import com.kanji.list.listRows.japanesePanelCreatingService.JapanesePanelCreatingService;
import com.kanji.model.AdditionalInformation;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JapanesePanelElementsCreator {

	private JapanesePanelActionsCreator actionsCreator;
	private ApplicationController applicationController;
	private DialogWindow dialogWindow;
	private JTextComponent kanaOrKanjiInputForFiltering;

	public JapanesePanelElementsCreator(
			JapanesePanelActionsCreator actionsCreator,
			ApplicationController applicationController,
			DialogWindow parentDialog) {
		this.actionsCreator = actionsCreator;
		this.applicationController = applicationController;
		this.dialogWindow = parentDialog;
	}

	public JTextComponent getKanaOrKanjiInputForFiltering() {
		return kanaOrKanjiInputForFiltering;
	}

	public void createKanaOrKanjiInputForFiltering(
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean enabled, InputGoal inputGoal, boolean selectable,
			CommonListElements commonListElements) {
		if (kanaOrKanjiInputForFiltering == null) {
			kanaOrKanjiInputForFiltering = actionsCreator.withJapaneseWritingValidation(
					createWritingsInput(japaneseWriting.getKanaWriting(),
							TypeOfJapaneseWriting.KANA_OR_KANJI, enabled,
							selectable), japaneseWriting, japaneseWord,
					TypeOfJapaneseWriting.KANA_OR_KANJI, inputGoal, enabled,
					commonListElements);
		}

	}

	public JTextComponent createKanaInputWithValidation(
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			boolean enabled, InputGoal inputGoal, boolean selectable,
			CommonListElements<JapaneseWriting> commonListElements) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(japaneseWriting.getKanaWriting(),
						TypeOfJapaneseWriting.KANA, enabled, selectable),
				japaneseWriting, japaneseWord, TypeOfJapaneseWriting.KANA,
				inputGoal, enabled, commonListElements);
	}

	public MyList<WordParticlesData> createParticlesDataList(
			JapaneseWord japaneseWord, PanelDisplayMode displayMode,
			CommonListElements<JapaneseWord> commonListElements) {
		MyList<WordParticlesData> particlesList = new MyList<>(
				new ListConfiguration<>(Prompts.JAPANESE_PARTICLE_DELETE,
						new RowInParticlesInformation(japaneseWord,
								applicationController, displayMode),
						() -> WordParticlesData.createParticleNotIncludedInWord(
								japaneseWord), //TODO this should be in word
						// particles data initializer
						"", dialogWindow,
						applicationController).showButtonsLoadNextPreviousWords(
						false)
											  .enableWordAdding(false)
											  .enableWordSearching(false)
											  .scrollBarFitsContent(true)
											  .inheritScrollbar(true)
											  .parentListAndWordContainingThisList(
													  commonListElements.getList(),
													  japaneseWord,
													  japaneseWord.getTakenParticles()));

		if (japaneseWord.getTakenParticles() != null) {
			japaneseWord.getTakenParticles()
						.forEach(particlesList::addWord);
		}

		return particlesList;
	}

	public JTextComponent createKanjiInputWithValidation(String text,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			InputGoal inputGoal, boolean enabled, boolean selectable,
			CommonListElements<JapaneseWriting> commonListElements) {
		return actionsCreator.withJapaneseWritingValidation(
				createWritingsInput(text, TypeOfJapaneseWriting.KANJI, enabled,
						selectable), japaneseWriting, japaneseWord,
				TypeOfJapaneseWriting.KANJI, inputGoal, enabled,
				commonListElements);
	}

	public JTextComponent createWritingsInput(String initialValue,
			TypeOfJapaneseWriting typeOfJapaneseWriting, boolean editable,
			boolean selectable) {

		JTextComponent input = actionsCreator.repaintParentOnFocusLost(
				actionsCreator.withSwitchToJapaneseActionOnClick(
						GuiElementsCreator.createTextField(
								new TextComponentOptions().text(initialValue)
														  .editable(editable)
														  //TODO editable/non editable should be handled automatically
														  // by main panel's display mode -> remove from here
														  .selectable(
																  selectable)
														  .font(ApplicationWindow.getKanjiFont())
														  .focusable(true)
														  .fontSize(30f)
														  .promptWhenEmpty(
																  JapaneseWritingUtilities.getDefaultValueForWriting(
																		  typeOfJapaneseWriting)))));
		if (!editable) {
			int padding = 7;
			input.setBorder(
					BorderFactory.createEmptyBorder(padding, padding, padding,
							padding));

		}
		return input;
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
		JComboBox<String> comboBox = actionsCreator.addAdditionalInformationOnPartOfSpeechChange(
				additionalInformationValue, additionalInformationLabel,
				GuiElementsCreator.createCombobox(
						new ComboboxOptions().setComboboxValues(
								Arrays.stream(PartOfSpeech.values())
									  .map(PartOfSpeech::getPolishMeaning)
									  .collect(Collectors.toList()))),
				japaneseWord);

		comboBox.setSelectedItem(partOfSpeechToSelect.getPolishMeaning());
		return comboBox;
	}

	public AbstractButton createButtonAddKanjiWriting(MainPanel rowPanel,
			JapaneseWriting japaneseWriting, JapaneseWord japaneseWord,
			InputGoal inputGoal, boolean editMode, boolean selectable,
			CommonListElements commonListElements) {
		AbstractButton button = createButton(
				JapaneseApplicationButtonsNames.ADD_KANJI_WRITING, null);
		button.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextComponent input = createKanjiInputWithValidation("",
						japaneseWriting, japaneseWord, inputGoal, editMode,
						selectable, commonListElements);
				rowPanel.insertElementInPlaceOfElement(input, button);
				SwingUtilities.invokeLater(input::requestFocusInWindow);
			}
		});
		return button;
	}

	public JComponent createButonDelete(AbstractButton buttonDelete,
			JapaneseWord japaneseWord, JapaneseWriting writing,
			InputGoal inputGoal) {
		return actionsCreator.updateWritingsInWordWhenDeleteWriting(
				buttonDelete, japaneseWord, writing, inputGoal);
	}

	public JComboBox createComboboxForAdditionalInformation(
			JapaneseWord japaneseWord) {
		AdditionalInformation additionalInformation = japaneseWord.getAdditionalInformation();
		List<String> possibleValues = additionalInformation.getPossibleValues();
		boolean hasPossibleAdditionalInformation =
				!possibleValues.isEmpty() && !possibleValues.get(0)
															.equals(Labels.NO_ADDITIONAL_INFORMATION);
		JComboBox comboBox = actionsCreator.changeAdditionalInformationOnComboboxChange(
				GuiElementsCreator.createCombobox(
						new ComboboxOptions().setComboboxValues(possibleValues)
											 .setEnabled(
													 hasPossibleAdditionalInformation)),
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
		AdditionalInformation additionalInformation = japaneseWord.getAdditionalInformation();
		return GuiElementsCreator.createLabel(new ComponentOptions().text(
				additionalInformation.getTag()
									 .getLabel())
																	.foregroundColor(
																			labelColor)
																	.setEnabled(
																			!additionalInformation.isEmpty()));
	}

	public JLabel createWordMeaningLabel(Color labelsColor) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.WORD_MEANING)
									  .foregroundColor(labelsColor));
	}

	public JTextComponent createWordMeaningText(JapaneseWord japaneseWord,
			PanelDisplayMode displayMode, InputGoal inputGoal,
			CommonListElements<JapaneseWord> commonListElements) {

		JTextComponent wordMeaningTextField = CommonGuiElementsCreator.createShortInput(
				japaneseWord.getMeaning(), displayMode);
		return displayMode.equals(PanelDisplayMode.EDIT) ?
				actionsCreator.withWordMeaningChangeListener(
						wordMeaningTextField, japaneseWord,
						inputGoal.equals(InputGoal.SEARCH) ?
								WordSearchOptions.BY_WORD_FRAGMENT :
								WordSearchOptions.BY_FULL_EXPRESSION, inputGoal,
						commonListElements) :
				wordMeaningTextField;

	}

	public JLabel createPartOfSpeechLabel(Color labelsColor) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.PART_OF_SPEECH)
									  .foregroundColor(labelsColor));
	}

	public MyList<JapaneseWriting> createJapaneseWritingsList(
			JapaneseWord japaneseWord, boolean inheritScrollBar,
			DialogWindow parentDialog, PanelDisplayMode displayMode,
			ListInputsSelectionManager listInputsSelectionManager,
			JapanesePanelCreatingService panelCreatingService,
			CommonListElements<JapaneseWord> commonListElements) {

		return new MyList<>(
				new ListConfiguration<>(Prompts.JAPANESE_WRITING_DELETE,
						new RowInJapaneseWritingsList(panelCreatingService,
								japaneseWord, displayMode),
						JapaneseWriting.getInitializer(),
						Labels.WRITING_WAYS_IN_JAPANESE, parentDialog,
						applicationController).enableWordAdding(false)
											  .displayMode(displayMode)
											  .inheritScrollbar(
													  inheritScrollBar)
											  .enableWordSearching(false)
											  .parentListAndWordContainingThisList(
													  commonListElements.getList(),
													  japaneseWord,
													  japaneseWord.getWritings())
											  .showButtonsLoadNextPreviousWords(
													  false)
											  .scrollBarFitsContent(false)
											  .allInputsSelectionManager(
													  listInputsSelectionManager)
											  .skipTitle(true));
	}

	public JLabel createWritingsLabel(Color labelsColor) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.WRITING_WAYS_IN_JAPANESE)
									  .foregroundColor(labelsColor));
	}

	public JLabel createParticlesTakenLabel(Color labelsColor) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(Labels.TAKING_PARTICLE)
									  .foregroundColor(labelsColor));
	}
}
