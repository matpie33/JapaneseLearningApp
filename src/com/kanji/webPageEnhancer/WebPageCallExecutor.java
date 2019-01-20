package com.kanji.webPageEnhancer;

import com.guimaker.webPanel.WebPagePanel;

import java.util.Timer;
import java.util.TimerTask;

public class WebPageCallExecutor {

	//TODO move it to gui maker and use always in web page panels

	private String lastUrlRequested = "";
	private final int sleepTimeMilliseconds = 1000;
	private boolean isSleeping;
	private WebPagePanel webPagePanel;
	private boolean newTasksAppearedWhileSleeping;

	public WebPageCallExecutor(WebPagePanel webPagePanel) {
		this.webPagePanel = webPagePanel;
	}

	public void openUrl(String url) {
		if (isSleeping) {
			lastUrlRequested = url;
			newTasksAppearedWhileSleeping = true;
		}
		else {
			callWebPageAndSleep(url);
		}
	}

	private void callWebPageAndSleep(String url) {
		isSleeping = true;
		showPageInWebPanel(url);
		new Timer().schedule(finishSleepingTask(), sleepTimeMilliseconds);
	}

	private void showPageInWebPanel(String url) {
		webPagePanel.showPageWithoutGrabbingFocus(url);
	}

	private TimerTask finishSleepingTask() {
		return new TimerTask() {
			@Override
			public void run() {
				if (newTasksAppearedWhileSleeping) {
					newTasksAppearedWhileSleeping = false;
					new Timer().schedule(finishSleepingTask(),
							sleepTimeMilliseconds);
				}
				else {
					if (!lastUrlRequested.isEmpty()) {
						showPageInWebPanel(lastUrlRequested);
					}
					setExecutorToReadyToUseState();
				}
			}
		};
	}

	private void setExecutorToReadyToUseState() {
		isSleeping = false;
		newTasksAppearedWhileSleeping = false;
		lastUrlRequested = "";
	}

}
