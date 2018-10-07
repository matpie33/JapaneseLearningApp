package com.kanji.panelsAndControllers.validation;

import com.kanji.constants.strings.ExceptionsMessages;

import java.awt.event.KeyEvent;

public class LearningStartPanelInputValidation {

	private int numberOfWords;

	public LearningStartPanelInputValidation(int numberOfWords) {
		this.numberOfWords = numberOfWords;
	}

	public void validateTypedKey(KeyEvent e) {
		if (!(e.getKeyChar() + "").matches("\\d")) {
			e.consume();
		}
	}

	public String validateRangesInput(int rangeStart, int rangeEnd) {
		String error = "";
		if (rangeStart == 0) {
			error = ExceptionsMessages.RANGE_START_MUST_BE_POSITIVE;
		}
		else if (rangeEnd <= rangeStart) {
			error = ExceptionsMessages.RANGE_TO_VALUE_LESS_THAN_RANGE_FROM_VALUE;
		}
		else if (isNumberHigherThanMaximum(rangeStart)
				|| isNumberHigherThanMaximum(rangeEnd)) {
			error = String
					.format(ExceptionsMessages.RANGE_VALUE_HIGHER_THAN_MAXIMUM_WORD_NUMBER,
							numberOfWords);
		}

		return error;
	}

	private boolean isNumberHigherThanMaximum(int number) {
		return number > numberOfWords;
	}

}
