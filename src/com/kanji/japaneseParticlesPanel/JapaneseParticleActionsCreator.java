package com.kanji.japaneseParticlesPanel;

import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.enums.ListElementModificationType;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.model.WordParticlesData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.ThreadUtilities;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
			WordParticlesData wordParticlesData) {
		additionalInformationInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				JTextComponent input = (JTextComponent) e.getSource();
				wordParticlesData.setAdditionalInformation(input.getText());
				applicationController.save();
			}
		});
		return additionalInformationInput;
	}

	public void removeParticleWhenRowDeleted(
			WordParticlesData wordParticlesData, JapaneseWord japaneseWord,
			AbstractButton buttonDelete) {
		buttonDelete.addActionListener(e -> japaneseWord
				.removeParticle(wordParticlesData.getJapaneseParticle()));
	}

	public JComboBox saveParticleWhenChanged(JComboBox particleCombobox,
			JapaneseWord japaneseWord, WordParticlesData wordParticlesData) {
		SwingUtilities.invokeLater(() -> {
			particleCombobox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					JapaneseParticle particleByComboboxValue = JapaneseParticle
							.getByString((String) e.getItem());
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
