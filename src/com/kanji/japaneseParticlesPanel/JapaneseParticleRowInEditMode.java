package com.kanji.japaneseParticlesPanel;

import com.guimaker.model.CommonListElements;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;

public class JapaneseParticleRowInEditMode
		implements JapaneseParticleRowCreatingService {
	private JapaneseParticleActionsCreator actionsCreator;
	private JapaneseParticleElementsCreator elementsCreator;
	private ApplicationController applicationController;

	public JapaneseParticleRowInEditMode(
			ApplicationController applicationController) {
		this.actionsCreator = new JapaneseParticleActionsCreator(
				applicationController);
		this.elementsCreator = new JapaneseParticleElementsCreator();
		this.applicationController = applicationController;
	}

	@Override
	public JComponent[] createRowElements(WordParticlesData wordParticlesData,
			JapaneseWord japaneseWord, CommonListElements<WordParticlesData>
			commonListElements) {
		actionsCreator.removeParticleWhenRowDeleted(wordParticlesData,
				japaneseWord, commonListElements.getButtonDelete());
		japaneseWord.addParticleData(wordParticlesData);
		applicationController.save();
		JComboBox particleCombobox = actionsCreator.saveParticleWhenChanged(
				elementsCreator.createParticlesCombobox(wordParticlesData),
				japaneseWord, wordParticlesData);
		JTextComponent input = actionsCreator.saveAdditionalInformationOnFocusLost(
				elementsCreator.createAdditionalInformationInput(
						wordParticlesData), wordParticlesData, commonListElements);
		List<JComponent> components = new ArrayList<>();
		components.add(particleCombobox);
		components.add(input);
		components.add(commonListElements.getButtonAddRow());
		components.add(commonListElements.getButtonDelete());
		return components.toArray(new JComponent[] {});
	}
}
