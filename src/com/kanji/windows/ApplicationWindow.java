package com.kanji.windows;

import com.guimaker.colors.BasicColors;
import com.kanji.constants.enums.SavingStatus;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.customPositioning.CustomPositioner;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.*;
import com.kanji.saving.ProblematicWordsState;
import com.kanji.timer.TimeSpentHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class ApplicationWindow extends DialogWindow {

	private JPanel mainApplicationPanel;
	private ProblematicWordsController activeProblematicWordsController;
	private StartingPanel startingPanel;
	private JFrame container;
	private ApplicationController applicationController;
	private Optional<TimeSpentHandler> timeSpentHandler;
	private static Font kanjiFont = new Font("MS Mincho", Font.PLAIN, 100);
	private JMenuBar menuBar;

	public ApplicationWindow(ApplicationController applicationController,
			StartingPanel startingPanel) {
		super(null);
		this.startingPanel = startingPanel;
		this.applicationController = applicationController;
		container = new JFrame();
		mainApplicationPanel = new JPanel(new CardLayout());
		timeSpentHandler = Optional.empty();
		setPanel(mainApplicationPanel);
		setPanel(startingPanel);
		startingPanel.setApplicationWindow(applicationController);

	}

	public void setMenuBar(JMenuBar menuBar){
		this.menuBar = menuBar;
	}

	public void initiate( AbstractPanelWithHotkeysInfo... panels) {
		//TODO put this to another class
		UIManager.put("ComboBox.disabledBackground", BasicColors.PURPLE_DARK_1);
		UIManager.put("Label.disabledForeground", Color.WHITE);
		startingPanel.createListPanels();

		mainApplicationPanel.add(startingPanel.createPanel(),
				startingPanel.getUniqueName());
		Arrays.stream(panels).forEach(panel -> mainApplicationPanel
				.add(panel.createPanel(), panel.getUniqueName()));

		setWindowProperties();
	}

	public static Font getKanjiFont() {
		return kanjiFont;
	}

	private void setWindowProperties() {
		container = new JFrame();
		container.setJMenuBar(menuBar);
		container.setContentPane(mainApplicationPanel);
		container.pack();
		container.setMinimumSize(container.getSize());
		container.setTitle(Titles.APPLICATION);
		container.setLocationRelativeTo(null);
		container.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		container.setVisible(true);
		container.addWindowListener(createActionCheckIfClosingIsSafe());
		container.setExtendedState(
				container.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		container.addWindowListener(
				createListenerSwitchToSubdialogWhenFocusGain());
	}



	private WindowAdapter createListenerSwitchToSubdialogWhenFocusGain() {
		return new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				if (childWindow != null && childWindow.getContainer() != null) {
					childWindow.getContainer().toFront();
				}

			}
		};
	}

	private WindowAdapter createActionCheckIfClosingIsSafe() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopTimeMeasuring();
				boolean shouldClose = applicationController.isClosingSafe();
				if (!shouldClose) {
					shouldClose = showConfirmDialog(Prompts.CLOSE_APPLICATION);
				}
				if (shouldClose) {
					applicationController.saveProject();
					System.exit(0);
				}
				else {
					resumeTimeMeasuring();
				}
			}
		};
	}

	public void showPanel(String name) {
		((CardLayout) mainApplicationPanel.getLayout())
				.show(mainApplicationPanel, name);
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.changeSaveStatus(savingStatus);
		container.repaint();
	}

	public void updateTitle(String update) {
		container.setTitle(Titles.APPLICATION + "   " + update);
	}

	public void enableShowProblematicWordsButton() {
		startingPanel.enableShowProblematicWordsButton();
	}

	public void createPanel(AbstractPanelWithHotkeysInfo panel, String title,
			boolean modal, Position position){
		setPanel(panel);
		createDialog(panel, title, modal, position);
	}

	public void createPanel(AbstractPanelWithHotkeysInfo panel, String title,
			boolean modal, CustomPositioner customPositioner){
		this.customPositioner = customPositioner;
		createPanel(panel, title, modal, Position.CUSTOM);
	}

	//TODO why some dialogs like problematic and search word are in application window,
	// and the others are in application controller?

	public void showProblematicWordsDialogForCurrentList() {
		MyList activeWordList = getStartingPanel().getActiveWordsList();
		activeProblematicWordsController = applicationController
				.getActiveProblematicWordsController();
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			Set<Element> problematicWords) {
		activeProblematicWordsController = applicationController
				.getActiveProblematicWordsController();
		setPanel(activeProblematicWordsController.getPanel());
		activeProblematicWordsController
				.addProblematicWordsAndHighlightFirst(problematicWords);
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			ProblematicWordsState<Element> problematicWordsState) {
		displayMessageAboutUnfinishedRepeating();
		activeProblematicWordsController = applicationController
				.getActiveProblematicWordsController();
		activeProblematicWordsController.addProblematicWordsHighlightReviewed(
				problematicWordsState.getReviewedWords(),
				problematicWordsState.getNotReviewedWords());
		showProblematicWordsDialog();
	}

	private void showProblematicWordsDialog() {

		if (activeProblematicWordsController.isProblematicWordsListEmpty()) {
			return;
		}
		AbstractPanelWithHotkeysInfo problematicWordsPanel = activeProblematicWordsController
				.getPanel();
		if (problematicWordsPanel.isReady()) {
			activeProblematicWordsController.focusPreviouslyFocusedElement();
		}

		showPanel(applicationController.getActiveProblematicWordsController()
				.getPanel().getUniqueName());

		applicationController
				.switchStateManager(activeProblematicWordsController);

	}

	public void closeDialog() {
		childWindow.getContainer().dispose();
	}

	public JFrame getContainer() {
		return container;
	}


	public StartingPanel getStartingPanel() {
		return startingPanel;
	}

	public void setTimeSpentHandler(TimeSpentHandler timeSpentHandler) {
		this.timeSpentHandler = Optional.of(timeSpentHandler);
	}

	private void stopTimeMeasuring() {
		timeSpentHandler.ifPresent(TimeSpentHandler::stopTimer);
	}

	private void resumeTimeMeasuring() {
		timeSpentHandler.ifPresent(TimeSpentHandler::startTimer);
	}

	public void displayMessageAboutUnfinishedRepeating() {
		showMessageDialog(Prompts.UNFINISHED_REPEATING);
	}

}
