package com.kanji.constants.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum JapaneseParticle {
	TO("To"), NI("Ni"), DE("De"), EMPTY("");

	private String displayedValue;

	private JapaneseParticle(String displayedValue) {
		this.displayedValue = displayedValue;
	}

	public String getDisplayedValue() {
		return displayedValue;
	}

	public static JapaneseParticle getByString(String value) {
		for (JapaneseParticle japaneseParticle : JapaneseParticle.values()) {
			if (japaneseParticle.getDisplayedValue()
								.equals(value)) {
				return japaneseParticle;
			}
		}
		throw new IllegalArgumentException("Value not found: " + value);
	}

	public static List<String> getPossibleParticles() {
		return Arrays.stream(JapaneseParticle.values())
					 .filter(p -> !p.equals(JapaneseParticle.EMPTY))
					 .map(JapaneseParticle::getDisplayedValue)
					 .collect(Collectors.toList());

	}

}
