package com.kanji.model;

import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.list.listElements.ListElement;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WordParticlesData implements ListElement, Serializable {

	private Map<JapaneseParticle, String> particleWithAdditionalInformation = new HashMap<>();

	public void addParticleInformation(JapaneseParticle particle,
			String additionalInformation) {
		particleWithAdditionalInformation.put(particle, additionalInformation);
	}

	public Map<JapaneseParticle, String> getParticleWithAdditionalInformation() {
		return particleWithAdditionalInformation;
	}

	public static WordParticlesData initializeEmpty() {
		WordParticlesData wordParticlesData = new WordParticlesData();
		wordParticlesData.addParticleInformation(JapaneseParticle.DE, "");
		return wordParticlesData;
	}

	@Override
	public boolean isEmpty() {
		return particleWithAdditionalInformation.isEmpty();
	}

	@Override
	public String getDisplayedText() {
		throw new NotImplementedException();
	}
}
