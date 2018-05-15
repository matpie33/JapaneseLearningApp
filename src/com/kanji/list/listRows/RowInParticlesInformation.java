package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.model.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RowInParticlesInformation
		implements ListRowCreator<WordParticlesData> {

	private JapaneseWord japaneseWord;
	private ApplicationController applicationController;

	public RowInParticlesInformation(JapaneseWord japaneseWord,
			ApplicationController applicationController) {
		this.japaneseWord = japaneseWord;
		this.applicationController = applicationController;
	}

	@Override
	public ListRowData<WordParticlesData> createListRow(
			WordParticlesData wordParticlesData,
			CommonListElements commonListElements, InputGoal inputGoal) {
		List<String> possibleParticles = Arrays
				.stream(JapaneseParticle.values())
				.filter(p -> !p.equals(JapaneseParticle.EMPTY))
				.map(JapaneseParticle::getDisplayedValue)
				.collect(Collectors.toList());
		japaneseWord.addParticleData(wordParticlesData);
		MainPanel panel = new MainPanel(null);
		commonListElements.getButtonDelete().addActionListener(e -> japaneseWord
				.removeParticle(wordParticlesData.getJapaneseParticle()));
		addRowForParticle(possibleParticles, panel, wordParticlesData,
				commonListElements);

		return new ListRowData<>(panel);
	}

	private void addRowForParticle(List<String> possibleParticles,
			MainPanel panel, WordParticlesData wordParticlesData,
			CommonListElements commonListElements) {
		JComboBox particleCombobox = createComboboxForJapaneseParticle(
				possibleParticles, wordParticlesData.getJapaneseParticle(),
				wordParticlesData);
		panel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, particleCombobox,
						GuiElementsCreator.createTextField(
								new TextComponentOptions()
										.text(wordParticlesData
												.getAdditionalInformation())
										.promptWhenEmpty(
												Prompts.ADDITIONAL_INFORMATION)),
						commonListElements.getButtonAddRow(),
						commonListElements.getButtonDelete()));
	}

	private JComboBox createComboboxForJapaneseParticle(
			List<String> possibleParticles, JapaneseParticle particle,
			WordParticlesData wordParticlesData) {
		//TODO duplicated code for creating this combobox
		JComboBox particleCombobox = GuiElementsCreator.createCombobox(
				new ComboboxOptions().setComboboxValues(possibleParticles));
		particleCombobox.setSelectedItem(particle.getDisplayedValue());
		particleCombobox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JapaneseParticle particleByComboboxValue = getParticleByComboboxValue(
						e);
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					japaneseWord.removeParticle(particleByComboboxValue);
				}
				else if (e.getStateChange() == ItemEvent.SELECTED) {
					wordParticlesData.setParticle(particleByComboboxValue);
					japaneseWord.addParticleData(wordParticlesData);
					applicationController.saveProject();
				}
			}
		});
		return particleCombobox;
	}

	private JapaneseParticle getParticleByComboboxValue(ItemEvent e) {
		return JapaneseParticle.getByString((String) e.getItem());
	}

	@Override
	public void addValidationListener(
			InputValidationListener<WordParticlesData> inputValidationListener) {

	}

}
