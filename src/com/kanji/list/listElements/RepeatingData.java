package com.kanji.list.listElements;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RepeatingData implements Serializable, ListElement {

	private static final long serialVersionUID = 6124164088342544292L;
	private String repeatingRange;
	private LocalDateTime repeatingDate;
	private boolean wasRepeated;
	private String timeSpentOnRepeating;

	public RepeatingData(String repeatingRange,
			LocalDateTime repeatingDate, boolean wasRepeated) {
		this(repeatingRange, repeatingDate, wasRepeated, "nie wiadomo");
	}

	public RepeatingData(String repeatingRange,
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

	@Override
	public String toString() {
		return "date:" + repeatingDate + "range" + repeatingRange + " timeSpent"
				+ timeSpentOnRepeating;
	}

	public static ListElementInitializer<RepeatingData> getInitializer() {
		return () -> new RepeatingData("", null, false);
	}

	@Override
	public boolean isSameAs(ListElement element) {
		if (element instanceof RepeatingData) {
			RepeatingData otherWord = (RepeatingData) element;
			return otherWord.getRepeatingDate().isEqual(repeatingDate);
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		return repeatingDate == null || repeatingRange.isEmpty();
	}
}
