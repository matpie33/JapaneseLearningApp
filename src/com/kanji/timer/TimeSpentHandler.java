package com.kanji.timer;

public class TimeSpentHandler {

	private Thread timerThread;
	private boolean timerRunning;
	private double timeElapsed;
	private int secondsLeft = 0;
	private int minutesLeft = 0;
	private int hoursLeft = 0;
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
						secondsLeft++;
					}
					if (secondsLeft >= 60) {
						secondsLeft = 0;
						minutesLeft++;
					}
					if (minutesLeft >= 60) {
						minutesLeft = 0;
						hoursLeft++;
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
		return hoursSuffix + ", " + minutesSuffix + ", " + secondsSuffix + ".";
	}

	private String adjustSuffixForHours() {
		return hoursLeft + " godzin" + adjustSuffix(hoursLeft);
	}

	private String adjustSuffixForMinutes() {
		return minutesLeft + " minut" + adjustSuffix(minutesLeft);
	}

	private String adjustSuffixForSeconds() {
		return secondsLeft + " sekund" + adjustSuffix(secondsLeft);
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
		secondsLeft = 0;
		minutesLeft = 0;
		hoursLeft = 0;
	}

}
