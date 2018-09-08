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
import java.util.*;
import java.util.List;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private JPanel mainApplicationPanel;
	private ProblematicWordsController activeProblematicWordsController;
	private StartingPanel startingPanel;
	private JFrame container;
	private ApplicationController applicationController;
	private Optional<TimeSpentHandler> timeSpentHandler;
	private static Font kanjiFont = new Font("MS Mincho", Font.PLAIN, 100);
	private RepeatingWordsPanel repeatingWordsPanel;
	private Map<String, AbstractPanelWithHotkeysInfo> panelsByNames = new HashMap<>();
	private JPanel problematicWordsPanel;

	public ApplicationWindow() {
		super(null);
		container = new JFrame();
		mainApplicationPanel = new JPanel(new CardLayout());
		timeSpentHandler = Optional.empty();
	}

	public void initiate() {
		applicationController = new ApplicationController(this);
		problematicWordsPanel = new JPanel(new BorderLayout());
		startingPanel = new StartingPanel(this, mainApplicationPanel);
		setPanel(startingPanel);
		applicationController.initializeListsElements();
		applicationController.initializeApplicationStateManagers();
		startingPanel.createListPanels();
		repeatingWordsPanel = applicationController.getRepeatingWordsPanel();
		mainApplicationPanel.add(startingPanel.createPanel(),
				ApplicationPanels.STARTING_PANEL.getPanelName());
		mainApplicationPanel.add(repeatingWordsPanel.createPanel(),
				ApplicationPanels.REPEATING_PANEL.getPanelName());
		mainApplicationPanel.add(problematicWordsPanel,
				ApplicationPanels.PROBLEMATIC_WORDS_PANEL.getPanelName());

		setWindowProperties();

		panelsByNames.put(ApplicationPanels.STARTING_PANEL.getPanelName(),
				startingPanel);
		panelsByNames.put(ApplicationPanels.REPEATING_PANEL.getPanelName(),
				repeatingWordsPanel);

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

	public void showPanel(ApplicationPanels panel) {
		((CardLayout) mainApplicationPanel.getLayout())
				.show(mainApplicationPanel, panel.getPanelName());
		setPanel(panelsByNames.get(panel.getPanelName()));
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

	public <Word extends ListElement> void showInsertDialog(MyList<Word> list) {
		customPositioner = new PositionerOnMyList(
				getStartingPanel().getSplitPaneFor(list.getListElementClass()));
		AbstractPanelWithHotkeysInfo panel = new InsertWordPanel<>(list, this);
		setPanel(panel);
		createDialog(panel, Titles.INSERT_WORD_DIALOG, false, Position.CUSTOM);
	}

	public void showSearchWordDialog(MyList list) {
		customPositioner = new PositionerOnMyList(
				getStartingPanel().getSplitPaneFor(list.getListElementClass()));
		AbstractPanelWithHotkeysInfo panel = new SearchWordPanel(this, list);
		setPanel(panel);
		//TODO create japanese panel creator with different parent windows,
		//currently all of them use application window as parent
		createDialog(panel, Titles.WORD_SEARCH_DIALOG, false, Position.CUSTOM);
	}

	//TODO why some dialogs like problematic and search word are in application window,
	// and the others are in application controller?

	public void showProblematicWordsDialogForCurrentList() {
		MyList activeWordList = getStartingPanel().getActiveWordsList();
		Class listElementsClass = activeWordList.getListElementClass();
		activeProblematicWordsController = applicationController
				.getProblematicWordsControllerBasedOnWordType(
						listElementsClass);
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			Set<Element> problematicWords) {
		activeProblematicWordsController = applicationController
				.getProblematicWordsControllerBasedOnWordType(
						problematicWords.iterator().next().getClass());
		setPanel(activeProblematicWordsController.getPanel());
		activeProblematicWordsController.addProblematicWords(problematicWords);
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			ProblematicKanjisState<Element> problematicWordsState) {
		displayMessageAboutUnfinishedRepeating();
		activeProblematicWordsController = applicationController
				.getProblematicWordsControllerBasedOnWordType(
						problematicWordsState.getNotReviewedWords().get(0)
								.getClass());
		activeProblematicWordsController.addProblematicWordsHighlightReviewed(
				problematicWordsState.getReviewedWords(),
				problematicWordsState.getNotReviewedWords());
		showProblematicWordsDialog();
	}

	public void showProblematicWordsDialog() {
		//TODO when no words to review for given list exist, null pointer is thrown


		AbstractPanelWithHotkeysInfo problematicWordsPanel = activeProblematicWordsController
				.getPanel();
		if (!problematicWordsPanel.isReady()) {
			activeProblematicWordsController.initializeHotkeyActions();
			activeProblematicWordsController.initializeWindowListener();
			panelsByNames.put(ApplicationPanels.PROBLEMATIC_WORDS_PANEL
					.getPanelName(), problematicWordsPanel);
			//TODO the approach with enum (application panels) is not extensible
		}
		else{
			activeProblematicWordsController.focusPreviouslyFocusedElement();
		}
		showPanel(ApplicationPanels.PROBLEMATIC_WORDS_PANEL);
		this.problematicWordsPanel.removeAll();
		JPanel panel = problematicWordsPanel.createPanel();
		this.problematicWordsPanel.add(panel);

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
		menuBar.setBackground(BasicColors.BLUE_NORMAL_2);
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
