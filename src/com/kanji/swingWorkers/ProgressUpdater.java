package com.kanji.swingWorkers;

import javax.swing.*;

public class ProgressUpdater {
	private JProgressBar progressBar;
	private int stepsExecuted = 1;

	public void startLongProcess(JProgressBar progressBar, int stepsToExecute) {
		this.progressBar = progressBar;
		progressBar.setValue(0);
		progressBar.setMaximum(stepsToExecute);
	}

	public void updateProgress() {
		if (progressBar != null)
			progressBar.setValue(stepsExecuted++);
	}

}
