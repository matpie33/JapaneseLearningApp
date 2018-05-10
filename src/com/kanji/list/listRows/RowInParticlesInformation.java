package com.kanji.list.listRows;

import com.guimaker.enums.FillType;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.list.listeners.InputValidationListener;
import com.kanji.list.myList.ListRowCreator;
import com.kanji.list.myList.ListRowData;
import com.kanji.model.WordParticlesData;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RowInParticlesInformation
		implements ListRowCreator<WordParticlesData> {

	@Override
	public ListRowData<WordParticlesData> createListRow(
			WordParticlesData wordParticlesData,
			CommonListElements commonListElements, InputGoal inputGoal) {
		List<String> possibleParticles = Arrays
				.stream(JapaneseParticle.values())
				.map(JapaneseParticle::getDisplayedValue)
				.collect(Collectors.toList());
		MainPanel panel = new MainPanel(null);
		for (Map.Entry<JapaneseParticle, String> particleWithInformation : wordParticlesData
				.getParticleWithAdditionalInformation().entrySet()) {
			addRowForParticle(possibleParticles, panel,
					particleWithInformation.getKey(),
					particleWithInformation.getValue(), commonListElements);
		}

		ListRowData<WordParticlesData> rowData = new ListRowData<>(panel);
		return rowData;
	}

	private void addRowForParticle(List<String> possibleParticles,
			MainPanel panel, JapaneseParticle particle,
			String additionalInformation,
			CommonListElements commonListElements) {
		JComboBox particleCombobox = GuiElementsCreator.createCombobox(
				new ComboboxOptions().setComboboxValues(possibleParticles));
		particleCombobox.setSelectedItem(particle.getDisplayedValue());
		panel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, particleCombobox,
						GuiElementsCreator.createTextField(
								new TextComponentOptions()
										.text(additionalInformation)),
						commonListElements.getButtonAddRow(),
						commonListElements.getButtonDelete()));
	}

	@Override
	public void addValidationListener(
			InputValidationListener<WordParticlesData> inputValidationListener) {

	}
}
