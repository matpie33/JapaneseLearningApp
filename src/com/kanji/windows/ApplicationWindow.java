package com.kanji.windows;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.Set;

import javax.swing.*;

import com.guimaker.colors.BasicColors;
import com.kanji.listElements.KanjiInformation;
import com.kanji.enums.ApplicationPanels;
import com.kanji.strings.MenuTexts;
import com.kanji.strings.Prompts;
import com.kanji.enums.SavingStatus;
import com.kanji.strings.Titles;
import com.kanji.controllers.ApplicationController;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.myList.MyList;
import com.kanji.panels.InsertWordPanel;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panels.LoadingPanel;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.panels.SearchWordPanel;
import com.kanji.panels.StartingPanel;
import com.kanji.timer.TimeSpentHandler;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private JPanel mainApplicationPanel;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private StartingPanel startingPanel;
	private JFrame container;
	private ApplicationController applicationController;
	private Optional<TimeSpentHandler> timeSpentHandler;
	private Font kanjiFont = new Font("MS PMincho", Font.BOLD, 100);

	public ApplicationWindow() {
		super(null);
		container = new JFrame();
		mainApplicationPanel = new JPanel(new CardLayout());
		timeSpentHandler = Optional.empty();
	}

	public void initiate() {
		applicationController = new ApplicationController(this);
		applicationController.initializeListsElements();
		startingPanel = new StartingPanel(this);

		applicationController.initializeApplicationStateManagers();
		problematicKanjiPanel = applicationController.getProblematicKanjiPanel ();

		mainApplicationPanel.add(startingPanel.createPanel(),
				ApplicationPanels.STARTING_PANEL.getPanelName());
		mainApplicationPanel.add(applicationController.getRepeatingWordsPanel().createPanel(),
				ApplicationPanels.REPEATING_PANEL.getPanelName());

		setWindowProperties();
	}

	public Font getKanjiFont() {
		return kanjiFont;
	}

	public ApplicationController getApplicationController() {
		return applicationController;
	}

	private void setWindowProperties() {
		container = new JFrame();
		container.setJMenuBar(createMenuBar());
		container.setContentPane(mainApplicationPanel);
		container.pack();
		container.setMinimumSize(container.getSize());
		container.setTitle(Titles.APPLICATION);
		container.setLocationRelativeTo(null);
		container.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		container.setVisible(true);
		container.addWindowListener(createClosingAdapter());
	}


	private WindowAdapter createClosingAdapter (){
		return new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				stopTimeMeasuring();
				boolean shouldClose = applicationController.isClosingSafe();
				if (!shouldClose){
					shouldClose = showConfirmDialog(Prompts.CLOSE_APPLICATION);
				}
				if (shouldClose){
					applicationController.saveProject();
					System.exit(0);
				}
				else{
					resumeTimeMeasuring();
				}
			}
		};
	}

	public void showPanel(ApplicationPanels panel) {
		((CardLayout) mainApplicationPanel.getLayout()).show(mainApplicationPanel,
				panel.getPanelName());
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.changeSaveStatus(savingStatus);
		container.repaint();
	}

	public void updateProblematicKanjisAmount() {
		startingPanel
				.updateProblematicKanjisAmount(applicationController.getProblematicKanjis().size());
	}

	public void updateTitle(String update) {
		container.setTitle(Titles.APPLICATION + "   " + update);
	}

	public void scrollToBottom() {
		applicationController.getKanjiRepeatingDates().scrollToBottom();
	}

	public void addButtonIcon() {
		startingPanel.addProblematicKanjisButton();
	}

	public void showLearningStartDialog(MyList list, int maximumNumber) {
		createDialog(new LearningStartPanel(applicationController, maximumNumber, list),
				Titles.LEARNING_START_DIALOG, false, Position.CENTER);

	}

	// TODO dialogs should either be jframe or modal in order for alt tab to
	// switch focus to the right window
	public void showInsertDialog(MyList list) {
		createDialog(new InsertWordPanel(list, getApplicationController()), Titles.INSERT_WORD_DIALOG,
				false, Position.LEFT_CORNER);
	}

	public void showSearchWordDialog(MyList<KanjiInformation> list) {
		createDialog(new SearchWordPanel(list), Titles.WORD_SEARCH_DIALOG, false,
				Position.LEFT_CORNER);
	}

	public void showProblematicKanjiDialog(Set<Integer> problematicKanjis) {
		problematicKanjiPanel.addProblematicKanjis(problematicKanjis);
		showProblematicKanjiDialog();
	}

	public void showProblematicKanjiDialog() {
		if (!problematicKanjiPanel.isDisplayable()){
			showReadyPanel(problematicKanjiPanel.getDialog());
		}
		else{
			problematicKanjiPanel.showKanjiKoohiLoginPage();
			createDialog(problematicKanjiPanel, Titles.PROBLEMATIC_KANJIS_WINDOW,
					true, Position.CENTER);

		}
		applicationController.switchStateManager(problematicKanjiPanel.getController());
	}

	public void showProblematicKanjiDialog(ProblematicKanjisState problematicKanjisState) {
		problematicKanjiPanel.restoreState(problematicKanjisState);
		showProblematicKanjiDialog();
	}

	public LoadingPanel showProgressDialog() {
		LoadingPanel dialog = new LoadingPanel(Prompts.KANJI_LOADING);
		createDialog(dialog, Titles.MESSAGE_DIALOG, false, Position.CENTER);
		return dialog;
	}

	public void closeDialog() {
		childWindow.getContainer().dispose();
	}

	public JFrame getContainer() {
		return container;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(BasicColors.OCEAN_BLUE);
		JMenu menu = new JMenu(MenuTexts.MENU_BAR_FILE);
		menuBar.add(menu);
		JMenuItem item = new JMenuItem(MenuTexts.MENU_OPEN);

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.openKanjiProject();
			}
		});

		menu.add(item);
		return menuBar;
	}

	public JPanel getStartingPanel (){
		return startingPanel.getPanel();
	}

	public void setTimeSpentHandler(TimeSpentHandler timeSpentHandler){
		this.timeSpentHandler = Optional.of(timeSpentHandler);
	}

	public void stopTimeMeasuring(){
		timeSpentHandler.ifPresent(TimeSpentHandler::stopTimer);
	}

	public void resumeTimeMeasuring(){
		timeSpentHandler.ifPresent(TimeSpentHandler::startTimer);
	}

	public void displayMessageAboutUnfinishedRepeating (){
		showMessageDialog(Prompts.UNFINISHED_REPEATING);
	}

}
