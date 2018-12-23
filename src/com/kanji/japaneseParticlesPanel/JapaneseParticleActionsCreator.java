package com.kanji.japaneseParticlesPanel;

import com.guimaker.enums.InputGoal;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.list.myList.ListPropertyChangeHandler;
import com.guimaker.model.CommonListElements;
import com.guimaker.utilities.ThreadUtilities;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElementPropertyManagers.AdditionalInformationChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class JapaneseParticleActionsCreator {

	private ApplicationController applicationController;

	public JapaneseParticleActionsCreator(
			ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	public JTextComponent saveAdditionalInformationOnFocusLost(
			JTextComponent additionalInformationInput,
			WordParticlesData wordParticlesData,
			CommonListElements<WordParticlesData> commonListElements) {
		additionalInformationInput.addFocusListener(
				new ListPropertyChangeHandler<>(wordParticlesData,
						commonListElements.getList(),
						applicationController.getApplicationWindow(),
						new AdditionalInformationChecker(), InputGoal.EDIT,
						Labels.ADDITIONAL_INFORMATION_GENERAL_TAG, false));
		return additionalInformationInput;
	}

	public void removeParticleWhenRowDeleted(
			WordParticlesData wordParticlesData, JapaneseWord japaneseWord,
			AbstractButton buttonDelete) {
		buttonDelete.addActionListener(e -> japaneseWord.removeParticle(
				wordParticlesData.getJapaneseParticle()));
	}

	public JComboBox saveParticleWhenChanged(JComboBox particleCombobox,
			JapaneseWord japaneseWord, WordParticlesData wordParticlesData) {
		SwingUtilities.invokeLater(() -> {
			particleCombobox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					JapaneseParticle particleByComboboxValue = JapaneseParticle.getByString(
							(String) e.getItem());
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						japaneseWord.removeParticle(particleByComboboxValue);
					}
					else if (e.getStateChange() == ItemEvent.SELECTED) {
						wordParticlesData.setParticle(particleByComboboxValue);
						japaneseWord.addParticleData(wordParticlesData);
						ThreadUtilities.callOnOtherThread(() -> {
							applicationController.save();
						});
					}
					applicationController.getJapaneseWords()
										 .updateObservers(japaneseWord,
												 ListElementModificationType.EDIT);

				}
			});
		});

		return particleCombobox;
	}

}
