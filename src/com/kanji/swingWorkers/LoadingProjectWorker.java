package com.kanji.swingWorkers;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.panels.LoadingPanel;
import com.kanji.saving.SavingInformation;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class LoadingProjectWorker {

	private List <SwingWorker> swingWorkers;
	private ApplicationController applicationController;
	private LoadingPanel loadingPanel;

	public LoadingProjectWorker (ApplicationController applicationController,
			LoadingPanel loadingPanel){
		swingWorkers = new ArrayList<>();
		this.applicationController = applicationController;
		this.loadingPanel = loadingPanel;
	}


	public <Element extends ListElement> void load (MyList<Element> list,
			List <Element> elements){
		SwingWorker s = new SwingWorker<Void, Integer>() {

			private JProgressBar progressBar = loadingPanel.addProgressBar(list.getTitle());

			@Override
			public Void doInBackground() throws Exception {
				list.cleanWords();
				progressBar.setMaximum(elements.size());
				for (int i = 0; i < elements.size(); i++) {
					list.addWord(elements.get(i));
					//TODO create methods add list of words: it would not have update view in it
					// and method addWord should use updateView
					progressBar.setValue(i+1);
				}
				return null;
			}


			@Override
			public void done() {
				list.scrollToBottom();
				swingWorkers.remove(this);
				if (swingWorkers.isEmpty()) {
					applicationController.finishedLoadingProject();
				}

			}
		};
		s.execute();
		swingWorkers.add(s);

	}


}
