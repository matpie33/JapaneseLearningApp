package com.kanji.timer;

import java.io.Serializable;

public class TimeSpent implements Serializable{

	private static final long serialVersionUID = -8344824321926031156L;
	private int secondsPassed = 0;
	private int minutesPassed = 0;
	private int hoursPassed = 0;

	public TimeSpent(int seconds, int minutes, int hours){
		this.secondsPassed = seconds;
		this.minutesPassed = minutes;
		this.hoursPassed = hours;
	}

	public int getSecondsPassed() {
		return secondsPassed;
	}

	public int getMinutesPassed() {
		return minutesPassed;
	}

	public int getHoursPassed() {
		return hoursPassed;
	}
}
