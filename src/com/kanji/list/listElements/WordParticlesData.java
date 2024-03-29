package com.kanji.list.listElements;

import com.guimaker.list.ListElement;
import com.kanji.constants.enums.JapaneseParticle;

import java.io.Serializable;
import java.util.Objects;

public class WordParticlesData implements ListElement, Serializable {

	private static final long serialVersionUID = -3606244668613031057L;
	private JapaneseParticle japaneseParticle;
	private String additionalInformation = "";

	public WordParticlesData(JapaneseParticle japaneseParticle) {
		this.japaneseParticle = japaneseParticle;
	}

	public WordParticlesData setAdditionalInformation(
			String additionalInformation) {
		this.additionalInformation = additionalInformation;
		return this;
	}


	public JapaneseParticle getJapaneseParticle() {
		return japaneseParticle;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public static WordParticlesData createParticleNotIncludedInWord(
			JapaneseWord word) {
		for (JapaneseParticle particle : JapaneseParticle.values()) {
			if (!particle.equals(JapaneseParticle.EMPTY) && !word.hasParticle(
					particle)) {
				return new WordParticlesData(particle);
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (!o.getClass()
			  .equals(WordParticlesData.class)) {
			return false;
		}
		WordParticlesData wordParticlesData = (WordParticlesData) o;
		return !wordParticlesData.isEmpty() && !isEmpty()
				&& wordParticlesData.getJapaneseParticle()
									.equals(getJapaneseParticle());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getJapaneseParticle());
	}

	@Override
	public boolean isEmpty() {
		return getJapaneseParticle().equals(JapaneseParticle.EMPTY);
	}

	@Override
	public String getDisplayedText() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String toString() {
		return japaneseParticle + ": " + additionalInformation;
	}

	public void setParticle(JapaneseParticle particle) {
		this.japaneseParticle = particle;
	}
}
