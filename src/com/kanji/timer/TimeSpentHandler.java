package com.kanji.timer;

public class TimeSpentHandler {

	private Thread timerThread;
	private boolean timerRunning;
	private double timeElapsed;
	private int secondsPassed = 0;
	private int minutesPassed = 0;
	private int hoursPassed = 0;
	private double interval = 0.1D;
	private TimeSpentMonitor timeMonitor;

	public TimeSpentHandler(TimeSpentMonitor monitor) {
		this.timerRunning = false;
		timeMonitor = monitor;
	}

	public void stopTimer() {
		timerRunning = false;
	}

	public void startTimer() {
		this.timerRunning = true;
		Runnable runnable = new Runnable() {
			public void run() {
				while (timerRunning) {
					timeElapsed += interval;
					if (timeElapsed >= 1) {
						timeElapsed = 0;
						secondsPassed++;
					}
					if (secondsPassed >= 60) {
						secondsPassed = 0;
						minutesPassed++;
					}
					if (minutesPassed >= 60) {
						minutesPassed = 0;
						hoursPassed++;
					}
					timeMonitor.updateTime(getTimePassed());
					try {
						Thread.sleep((int) (interval * 1000));
					}
					catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		};
		this.timerThread = new Thread(runnable);
		this.timerThread.start();
	}

	public String getTimePassed() {
		String hoursSuffix = adjustSuffixForHours();
		String minutesSuffix = adjustSuffixForMinutes();
		String secondsSuffix = adjustSuffixForSeconds();
		return hoursPassed > 0 ? hoursSuffix + ", "
				: "" + (minutesPassed > 0 ? minutesSuffix + ", " : "") + secondsSuffix + ".";
	}

	private String adjustSuffixForHours() {
		return hoursPassed + " godzin" + adjustSuffix(hoursPassed);
	}

	private String adjustSuffixForMinutes() {
		return minutesPassed + " minut" + adjustSuffix(minutesPassed);
	}

	private String adjustSuffixForSeconds() {
		return secondsPassed + " sekund" + adjustSuffix(secondsPassed);
	}

	private String adjustSuffix(int timeValue) {
		int moduloRemainder = timeValue % 10;
		if (moduloRemainder > 1 && moduloRemainder < 5 && (timeValue < 10 || timeValue > 20)) {
			return "y";
		}
		else if (timeValue == 1) {
			return "a";
		}
		else if ((moduloRemainder >= 5 && moduloRemainder <= 9) || moduloRemainder == 0
				|| (timeValue >= 11 && timeValue <= 14)
				|| (moduloRemainder == 1 && timeValue >= 20)) {
			return "";
		}
		return "Nie wylapany if.";
	}

	public void reset() {
		this.timeElapsed = 0.0D;
		secondsPassed = 0;
		minutesPassed = 0;
		hoursPassed = 0;
	}

}
