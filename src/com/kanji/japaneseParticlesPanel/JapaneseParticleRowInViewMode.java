package com.kanji.japaneseParticlesPanel;

import com.kanji.list.listElements.JapaneseWord;
import com.kanji.model.WordParticlesData;
import com.guimaker.utilities.CommonListElements;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class JapaneseParticleRowInViewMode
		implements JapaneseParticleRowCreatingService {
	private JapaneseParticleElementsCreator elementsCreator;

	public JapaneseParticleRowInViewMode() {
		this.elementsCreator = new JapaneseParticleElementsCreator();
	}

	@Override
	public JComponent[] createRowElements(WordParticlesData wordParticlesData,
			JapaneseWord japaneseWord, CommonListElements commonListElements) {
		JLabel particleLabel = elementsCreator.createLabel(
				wordParticlesData.getJapaneseParticle().getDisplayedValue());
		JLabel particleAdditionalInformation = elementsCreator
				.createLabel(wordParticlesData.getAdditionalInformation());
		List<JComponent> components = new ArrayList<>();
		components.add(particleLabel);
		components.add(particleAdditionalInformation);
		return components.toArray(new JComponent[] {});
	}
}
