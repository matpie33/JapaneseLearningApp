package com.kanji.list.listElementAdditionalInformations;

import com.kanji.constants.enums.AdditionalInformationTag;

import java.io.Serializable;
import java.util.Objects;

public class AdditionalInformation implements Serializable {

	private AdditionalInformationTag tag;
	private String value;

	public AdditionalInformation(AdditionalInformationTag tag, String value) {
		this.tag = tag;
		this.value = value;
	}

	public AdditionalInformationTag getTag() {
		return tag;
	}

	public String getValue() {
		return value;
	}

	@Override public boolean equals(Object o) {
		if (o.getClass() != getClass()) {
			return false;
		}
		AdditionalInformation otherObject = (AdditionalInformation) o;
		return tag.equals(otherObject.getTag()) && value
				.equals(otherObject.getValue());
	}

	@Override public int hashCode() {
		return Objects.hash(tag, value);
	}

}
