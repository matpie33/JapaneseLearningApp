package com.kanji.swingWorkers;

import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.LoadingPanel;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoadingProjectWorker {

	private List<SwingWorker> swingWorkers;
	private ApplicationWindow applicationWindow;
	private LoadingPanel loadingPanel;

	public LoadingProjectWorker(ApplicationWindow applicationController,
			LoadingPanel loadingPanel) {
		swingWorkers = new ArrayList<>();
		this.applicationWindow = applicationController;
		this.loadingPanel = loadingPanel;
	}

	public <Element extends ListElement> void load(MyList<Element> list,
			List<Element> words) {
		SwingWorker s = new SwingWorker<Void, Integer>() {

			private JProgressBar progressBar = loadingPanel
					.addProgressBar(list.getTitle());

			@Override
			public Void doInBackground() throws Exception {
				list.cleanWords();

				int maximumDisplayedWords = list.getMaximumDisplayedWords();
				int firstRowToLoad = Math
						.max(0, words.size() - maximumDisplayedWords);
				int numberOfWordsToLoad = words.size() - firstRowToLoad - 1;
				int stepsToExecute = words.size() + numberOfWordsToLoad;
				ProgressUpdater progressUpdater = list.getProgressUpdater();
				progressUpdater.startLongProcess(progressBar, stepsToExecute);
				list.addWords(words, InputGoal.EDIT, false);
				list.showWordsStartingFromRow(firstRowToLoad);
				//TODO create methods add list of words: it would not have update view in it
				// and method addWord should use updateView

				return null;
			}

			@Override
			public void done() {
				try {
					get();
				}
				catch (ExecutionException exception) {
					exception.printStackTrace();
					applicationWindow.showMessageDialog(String.format(
							ExceptionsMessages.EXCEPTION_WHILE_LOADING_KANJI_PROJECT,
							exception.getMessage()));
				}
				catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
					applicationWindow.showMessageDialog(String.format(
							ExceptionsMessages.EXCEPTION_WHILE_LOADING_KANJI_PROJECT,
							interruptedException.getMessage()));
				}
				finally {
					applicationWindow.getStartingPanel().refreshAllTabs();
					list.scrollToBottom();
					swingWorkers.remove(this);
					if (swingWorkers.isEmpty()) {
						applicationWindow.getApplicationController()
								.finishedLoadingProject();
					}
				}

			}
		};
		s.execute();
		swingWorkers.add(s);

	}

}
