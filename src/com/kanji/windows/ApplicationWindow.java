package com.kanji.windows;

import com.guimaker.colors.BasicColors;
import com.kanji.constants.enums.ApplicationPanels;
import com.kanji.constants.enums.SavingStatus;
import com.kanji.constants.strings.MenuTexts;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.customPositioning.PositionerOnMyList;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.myList.MyList;
import com.kanji.model.DuplicatedJapaneseWordInformation;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private JPanel mainApplicationPanel;
	private ProblematicWordsController activeProblematicWordsController;
	private StartingPanel startingPanel;
	private JFrame container;
	private ApplicationController applicationController;
	private Optional<TimeSpentHandler> timeSpentHandler;
	private static Font kanjiFont = new Font("MS Mincho", Font.PLAIN, 100);

	public ApplicationWindow() {
		super(null);
		container = new JFrame();
		mainApplicationPanel = new JPanel(new CardLayout());
		timeSpentHandler = Optional.empty();
	}

	public void initiate() {
		applicationController = new ApplicationController(this);
		startingPanel = new StartingPanel(this, mainApplicationPanel);
		setPanel(startingPanel);
		applicationController.initializeListsElements();
		applicationController.initializeApplicationStateManagers();
		startingPanel.createListPanels();

		mainApplicationPanel.add(startingPanel.createPanel(),
				ApplicationPanels.STARTING_PANEL.getPanelName());
		mainApplicationPanel.add(applicationController.getRepeatingWordsPanel()
						.createPanel(),
				ApplicationPanels.REPEATING_PANEL.getPanelName());

		setWindowProperties();

	}

	public static Font getKanjiFont() {
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
		container.setExtendedState(
				container.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private WindowAdapter createClosingAdapter() {
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

		createDialog(
				new LearningStartPanel(applicationController, maximumNumber),
				Titles.LEARNING_START_DIALOG, false, Position.CENTER);

	}

	// TODO dialogs should either be jframe or modal in order for alt tab to
	// switch focus to the right window
	public <Word extends ListElement> void showInsertDialog(MyList<Word> list) {
		customPositioner = new PositionerOnMyList(
				getStartingPanel().getSplitPaneFor(list.getListElementClass()));
		AbstractPanelWithHotkeysInfo panel = new InsertWordPanel<>(list, this);
		createDialog(panel, Titles.INSERT_WORD_DIALOG, false, Position.CUSTOM);
	}

	public void showSearchWordDialog(MyList list) {
		customPositioner = new PositionerOnMyList(
				getStartingPanel().getSplitPaneFor(list.getListElementClass()));
		createDialog(new SearchWordPanel(this, list), Titles.WORD_SEARCH_DIALOG,
				false, Position.CUSTOM);
	}

	//TODO why some dialogs like problematic and search word are in application window,
	// and the others are in application controller?

	public void showProblematicWordsDialogForCurrentList() {
		activeProblematicWordsController = applicationController
				.getProblematicWordsControllerBasedOnActiveWordList();
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			Set<Element> problematicWords) {
		activeProblematicWordsController = applicationController
				.getProblematicWordsControllerBasedOnActiveWordList();
		activeProblematicWordsController.addProblematicWords(problematicWords);
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			ProblematicKanjisState<Element> problematicWordsState) {
		displayMessageAboutUnfinishedRepeating();
		activeProblematicWordsController = applicationController
				.getProblematicWordsControllerBasedOnActiveWordList();
		activeProblematicWordsController.addProblematicWordsHighlightReviewed(
				problematicWordsState.getReviewedWords(),
				problematicWordsState.getNotReviewedWords());
		showProblematicWordsDialog();
	}

	public void showProblematicWordsDialog() {
		if (activeProblematicWordsController.isDialogHidden()) {
			showReadyPanel(activeProblematicWordsController.getDialog());
		}
		else {
			//TODO when no words to review for given list exist, null pointer is thrown
			activeProblematicWordsController.initializeHotkeyActions();
			createDialog(activeProblematicWordsController.getPanel(),
					Titles.PROBLEMATIC_KANJIS_WINDOW, true, Position.CENTER);
			activeProblematicWordsController.initializeWindowListener();

		}
		applicationController
				.switchStateManager(activeProblematicWordsController);
	}

	public void showDuplicatedJapaneseWordsDialog(
			List<DuplicatedJapaneseWordInformation> duplicatedJapaneseWordInformationList) {
		DuplicatedImportedJapaneseWordsPanel dup = new DuplicatedImportedJapaneseWordsPanel(
				this, duplicatedJapaneseWordInformationList);
		createDialog(dup, Titles.DUPLICATED_WORDS_PANEL, false,
				Position.CENTER);
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
