package com.kanji.list.listElementPropertyManagers;

import com.guimaker.list.ListElementPropertyManager;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.WordParticlesData;

public class AdditionalInformationChecker implements
		ListElementPropertyManager<String, WordParticlesData> {
	@Override
	public String getInvalidPropertyReason() {
		return null;
	}

	@Override
	public boolean isPropertyFound(String property,
			WordParticlesData wordToCheck,
			WordParticlesData wordParticlesData) {
		return false;
	}

	@Override
	public String getPropertyValue(WordParticlesData wordParticlesData) {
		return wordParticlesData.getAdditionalInformation();
	}

	@Override
	public String getPropertyDefinedException(String property) {
		return null;
	}

	@Override
	public void setProperty(WordParticlesData wordParticlesData,
			String newValue, String previousValue) {
		wordParticlesData.setAdditionalInformation(newValue);
	}
}
