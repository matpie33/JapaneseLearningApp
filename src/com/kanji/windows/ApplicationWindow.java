package com.kanji.windows;

import com.guimaker.colors.BasicColors;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.enums.SavingStatus;
import com.kanji.constants.strings.MenuTexts;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.*;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.timer.TimeSpentHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("serial") public class ApplicationWindow
		extends DialogWindow {

	private JPanel mainApplicationPanel;
	private ProblematicWordsController problematicWordsController;
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
		startingPanel = new StartingPanel(this, mainApplicationPanel);

		applicationController.initializeApplicationStateManagers();
		problematicWordsController = applicationController
				.getProblematicWordsController();

		mainApplicationPanel.add(startingPanel.createPanel(),
				ApplicationPanels.STARTING_PANEL.getPanelName());
		mainApplicationPanel.add(applicationController.getRepeatingWordsPanel()
						.createPanel(),
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

	private WindowAdapter createClosingAdapter() {
		return new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
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

	public void showPanel(ApplicationPanels panel) {
		((CardLayout) mainApplicationPanel.getLayout())
				.show(mainApplicationPanel, panel.getPanelName());
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.changeSaveStatus(savingStatus);
		container.repaint();
	}

	public void updateProblematicWordsAmount() {
		startingPanel.updateProblematicWordsAmount(applicationController
						.getProblematicWordsAmountBasedOnCurrentTab(),
				startingPanel.getActiveWordsList().getListElementClass());
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

	public void showLearningStartDialog(int maximumNumber) {
		problematicWordsController.initialize();
		createDialog(
				new LearningStartPanel(applicationController, maximumNumber),
				Titles.LEARNING_START_DIALOG, false, Position.CENTER);

	}

	// TODO dialogs should either be jframe or modal in order for alt tab to
	// switch focus to the right window
	public void showInsertDialog(
			RowInJapaneseWordInformations rowInJapaneseWordInformation,
			MyList list) {
		AbstractPanelWithHotkeysInfo panel;
		if (list.getListElementClass().equals(KanjiInformation.class)) {
			panel = new InsertKanjiPanel(list, getApplicationController());
		}
		else if (list.getListElementClass()
				.equals(JapaneseWordInformation.class)) {
			panel = new InsertJapaneseWordPanel(rowInJapaneseWordInformation,
					list, this);
		}
		else {
			throw new RuntimeException("Unknown list word");
		}
		createDialog(panel, Titles.INSERT_WORD_DIALOG, false,
				Position.LEFT_CORNER);
	}

	public void showSearchWordDialog(MyList list) {
		createDialog(new SearchWordPanel(list), Titles.WORD_SEARCH_DIALOG,
				false, Position.LEFT_CORNER);
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			Set<Element> problematicWords) {
		problematicWordsController.addProblematicWords(problematicWords);
		showProblematicWordsDialog();
	}
	//TODO why some dialogs like problematic and search word are in application window,
	// and the others are in application controller?

	public void showProblematicWordsDialog() {

		if (!problematicWordsController.isPanelInitialized()) {
			showReadyPanel(problematicWordsController.getDialog());
		}
		else {
			problematicWordsController.initializeSpaceBarAction();
			createDialog(problematicWordsController.getPanel(),
					Titles.PROBLEMATIC_KANJIS_WINDOW, true, Position.CENTER);
			problematicWordsController.initializeWindowListener();

		}
		applicationController.switchStateManager(problematicWordsController);
	}

	public void showProblematicWordsDialog(
			ProblematicKanjisState problematicKanjisState) {
		displayMessageAboutUnfinishedRepeating();
		showProblematicWordsDialog();
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
			@Override public void actionPerformed(ActionEvent e) {
				applicationController.openKanjiProject();
			}
		});

		menu.add(item);
		return menuBar;
	}

	public StartingPanel getStartingPanel() {
		return startingPanel;
	}

	public void setTimeSpentHandler(TimeSpentHandler timeSpentHandler) {
		this.timeSpentHandler = Optional.of(timeSpentHandler);
	}

	public void stopTimeMeasuring() {
		timeSpentHandler.ifPresent(TimeSpentHandler::stopTimer);
	}

	public void resumeTimeMeasuring() {
		timeSpentHandler.ifPresent(TimeSpentHandler::startTimer);
	}

	public void displayMessageAboutUnfinishedRepeating() {
		showMessageDialog(Prompts.UNFINISHED_REPEATING);
	}

}
