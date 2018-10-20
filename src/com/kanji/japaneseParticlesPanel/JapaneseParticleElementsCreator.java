package com.kanji.japaneseParticlesPanel;

import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.WordParticlesData;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class JapaneseParticleElementsCreator {

	public JTextComponent createAdditionalInformationInput(
			WordParticlesData wordParticlesData) {
		return GuiElementsCreator.createTextField(new TextComponentOptions()
				.text(wordParticlesData.getAdditionalInformation())
				.promptWhenEmpty(Prompts.ADDITIONAL_INFORMATION));
	}

	public JComboBox createParticlesCombobox(
			WordParticlesData wordParticlesData) {
		JComboBox particleCombobox = GuiElementsCreator.createCombobox(
				new ComboboxOptions().setComboboxValues(
						JapaneseParticle.getPossibleParticles()));
		particleCombobox.setSelectedItem(
				wordParticlesData.getJapaneseParticle().getDisplayedValue());
		return particleCombobox;
	}

	public JLabel createLabel(String text) {
		return GuiElementsCreator
				.createLabel(new ComponentOptions().text(text));
	}

}
