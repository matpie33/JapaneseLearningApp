package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.SavingStatus;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.Kanji;
import com.kanji.panelsAndControllers.panels.StartingPanel;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;

public class StartingController {

	private StartingPanel startingPanel;
	private ApplicationController applicationController;

	public StartingController(ApplicationController applicationController) {
		startingPanel = new StartingPanel(this, applicationController);
		this.applicationController = applicationController;
	}

	public ChangeListener createTabChangedListener() {
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				applicationController.setActiveWordStateController(
						startingPanel.getActiveWordListControllerName());
				applicationController.updateProblematicWordsAmount();
			}
		};
	}

	public AbstractAction createActionSwitchTab(JTabbedPane tabbedPane) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabbedPane.getSelectedIndex()
						== tabbedPane.getTabCount() - 1) {
					tabbedPane.setSelectedIndex(0);
				}
				else {
					tabbedPane.setSelectedIndex(
							tabbedPane.getSelectedIndex() + 1);
				}

			}
		};
	}

	public void setInitialStartingPanelState() {
		applicationController.setActiveWordStateController(
				startingPanel.getActiveWordListControllerName());
		changeSaveStatus(SavingStatus.NO_CHANGES);
	}

	public void switchToList(TypeOfWordForRepeating wordType) {
		if (wordType.equals(TypeOfWordForRepeating.KANJIS)) {
			startingPanel.getTabbedPane().setSelectedIndex(0);
		}
		else if (wordType.equals(TypeOfWordForRepeating.JAPANESE_WORDS)) {
			startingPanel.getTabbedPane().setSelectedIndex(1);
			//TODO use enum instead of class checking, and tab index to enum and use it instead of
			// listToLabel map
		}
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.getSaveInfoLabel()
				.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
		startingPanel.getPanel().repaint();
	}

	public JSplitPane getSplitPaneFor(Class listClass) {
		if (listClass.equals(Kanji.class)) {
			return startingPanel.getKanjiRepeatingPanel().getListsSplitPane();
		}
		else {
			return startingPanel.getJapaneseWordsRepeatingPanel()
					.getListsSplitPane();
		}
	}

	public StartingPanel getStartingPanel() {
		return startingPanel;
	}

	public void updateProblematicWordsAmount(int problematicKanjisNumber) {
		String wordType = JapaneseWritingUtilities
				.getTextForTypeOfWordForRepeating(
						applicationController.getActiveWordsListType());
		startingPanel.getProblematicKanjisLabel().setText(
				String.format(Prompts.PROBLEMATIC_WORDS_AMOUNT, wordType,
						problematicKanjisNumber));
	}

	public void enableShowProblematicWordsButton() {
		startingPanel.getShowProblematicWordsButton().setEnabled(true);
	}
}
