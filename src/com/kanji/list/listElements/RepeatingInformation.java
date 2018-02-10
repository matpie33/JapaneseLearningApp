package com.kanji.list.listElements;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepeatingInformation implements Serializable, ListElement {

	private static final long serialVersionUID = 6124164088342544292L;
	private String repeatingRange;
	private LocalDateTime repeatingDate;
	private boolean wasRepeated;
	private String timeSpentOnRepeating;

	public RepeatingInformation(String repeatingRange,
			LocalDateTime repeatingDate, boolean wasRepeated) {
		this(repeatingRange, repeatingDate, wasRepeated, "nie wiadomo");
	}

	public RepeatingInformation(String repeatingRange,
			LocalDateTime repeatingDate, boolean wasRepeated,
			String timeSpentOnRepeating) {
		this.repeatingRange = repeatingRange;
		this.repeatingDate = repeatingDate;
		this.wasRepeated = wasRepeated;
		this.timeSpentOnRepeating = timeSpentOnRepeating;
	}

	public String getTimeSpentOnRepeating() {
		return timeSpentOnRepeating;
	}

	public void setTimeSpentOnRepeating(String timeSpentOnRepeating) {
		this.timeSpentOnRepeating = timeSpentOnRepeating;
	}

	public String getRepeatingRange() {
		return repeatingRange;
	}

	public void setRepeatingRange(String repeatingRange) {
		this.repeatingRange = repeatingRange;
	}

	public LocalDateTime getRepeatingDate() {
		return repeatingDate;
	}

	public void setRepeatingDate(LocalDateTime repeatingDate) {
		this.repeatingDate = repeatingDate;
	}

	public boolean isWasRepeated() {
		return wasRepeated;
	}

	public void setWasRepeated(boolean wasRepeated) {
		this.wasRepeated = wasRepeated;
	}

	@Override public String toString() {
		return "date:" + repeatingDate + "range" + repeatingRange + " timeSpent"
				+ timeSpentOnRepeating;
	}

	public static List<ListElementData<RepeatingInformation>> getElementsTypesAndLabels() {
		return new ArrayList<>();
	}

	public static ListElementInitializer<RepeatingInformation> getInitializer() {
		return () -> new RepeatingInformation("", null, false);
	}

	@Override public boolean isSameAs(ListElement element) {
		if (element instanceof RepeatingInformation) {
			RepeatingInformation otherWord = (RepeatingInformation) element;
			return otherWord.getRepeatingDate().isEqual(repeatingDate);
		}
		return false;
	}

}
