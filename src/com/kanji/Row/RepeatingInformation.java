package com.kanji.Row;

import java.io.Serializable;
import java.util.Date;

import com.kanji.constants.Prompts;

public class RepeatingInformation implements Serializable {

	private static final long serialVersionUID = 6124164088342544292L;
	private String repeatingRange;
	private Date repeatingDate;
	private boolean wasRepeated;
	private String timeSpentOnRepeating;

	public RepeatingInformation(String s, Date d, boolean b) {
		repeatingRange = s;
		repeatingDate = d;
		wasRepeated = b;
		timeSpentOnRepeating = Prompts.repeatingTimeNotAvailablePrompt;
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

	public Date getRepeatingDate() {
		return repeatingDate;
	}

	public void setRepeatingDate(Date repeatingDate) {
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
		return "date:" + repeatingDate + "range" + repeatingRange;
	}

}
