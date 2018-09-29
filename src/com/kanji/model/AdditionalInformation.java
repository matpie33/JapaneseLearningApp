package com.kanji.model;

import com.kanji.constants.enums.AdditionalInformationTag;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AdditionalInformation implements Serializable {

	private static final long serialVersionUID = -4449794531096769222L;
	private AdditionalInformationTag tag;
	private String value;
	private List<String> possibleValues;
	//TODO possible values and tag are duplicated in part of speech class

	public AdditionalInformation(AdditionalInformationTag tag,
			String... possibleValues) {
		this.tag = tag;
		this.possibleValues = Arrays.asList(possibleValues);
	}

	public void setTag(AdditionalInformationTag additionalInformationTag) {
		this.tag = additionalInformationTag;
	}

	public void setValue(String value) {
		if (!possibleValues.contains(value)) {
			throw new IllegalArgumentException(
					"Additional information value is not contained in "
							+ "possible values list: " + value + " possible: "
							+ possibleValues);
		}
		this.value = value;
	}

	public AdditionalInformationTag getTag() {
		return tag;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() != getClass()) {
			return false;
		}
		AdditionalInformation otherObject = (AdditionalInformation) o;
		return tag.equals(otherObject.getTag()) && value
				.equals(otherObject.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(tag, value);
	}

	public static AdditionalInformation empty() {
		return new AdditionalInformation(AdditionalInformationTag.OTHER, "");
	}

	public List<String> getPossibleValues() {
		return possibleValues;
	}

	public boolean isEmpty() {
		return getTag().equals(AdditionalInformationTag.OTHER)
				&& possibleValues.size() == 1 && possibleValues.get(0)
				.isEmpty();
	}

}
