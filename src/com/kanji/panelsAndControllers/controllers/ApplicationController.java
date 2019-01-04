package com.kanji.panelsAndControllers.controllers;

import com.guimaker.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationConfiguration;
import com.guimaker.application.ApplicationWindow;
import com.guimaker.application.DialogWindow;
import com.guimaker.colors.BasicColors;
import com.guimaker.customPositioning.CustomPositioner;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.WordSearchOptions;
import com.guimaker.list.ListElement;
import com.guimaker.list.ListObserver;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.ListColors;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.utilities.SetOfRanges;
import com.kanji.application.ApplicationStateController;
import com.kanji.application.WordStateController;
import com.kanji.constants.Colors;
import com.kanji.constants.enums.ApplicationSaveableState;
import com.kanji.constants.enums.SavingStatus;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.MenuTexts;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.customPositioning.PositionerOnRightPartOfSplitPane;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.model.WordsAndRepeatingInfo;
import com.kanji.model.saving.ProblematicWordsState;
import com.kanji.model.saving.SavingInformation;
import com.kanji.panelsAndControllers.panels.LearningStartPanel;
import com.kanji.panelsAndControllers.panels.LoadingPanel;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.panelsAndControllers.panels.StartingPanel;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.FileSavingManager;
import com.kanji.swingWorkers.LoadingProjectWorker;
import com.kanji.utilities.JapaneseWordsAdjuster;
import com.kanji.utilities.OldToNewestVersionConverter;
import com.kanji.utilities.WordsListReadWrite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationController
		implements ApplicationStateManager, ListObserver,
		ApplicationChangesManager {

	private ApplicationWindow applicationWindow;
	private ApplicationStateManager applicationStateManager;
	private SavingInformation savingInformation;
	private boolean loadingInProgress = false;
	private FileSavingManager fileSavingManager;
	private ApplicationStateController applicationStateController;
	private StartingController startingController;
	private final static boolean SHOULD_CONVERT_OLD_TO_NEWEST_VERSION = false;

	public ApplicationController() {
		applicationStateManager = this;
		applicationStateController = new ApplicationStateController(this,
				Kanji.MEANINGFUL_NAME);
		fileSavingManager = new FileSavingManager();
		startingController = new StartingController(this);
		applicationWindow = new ApplicationWindow(this,
				startingController.getStartingPanel(),
				createApplicationConfiguration());
		applicationWindow.setIconName("icon.png");
	}

	private ApplicationConfiguration createApplicationConfiguration() {
		CustomPositioner customPositioner = new PositionerOnRightPartOfSplitPane(
				startingController, this);
		return new ApplicationConfiguration(
				Titles.APPLICATION).setInsertWordPanelPositioner(
				customPositioner)
								   .setListColors(
										   new ListColors().backgroundColor(
												   Colors.LIST_BACKGROUND_COLOR)
														   .editRowColor(
																   Colors.EDIT_ROW_COLOR)
														   .filterPanelColor(
																   Colors.FILTER_PANEL_COLOR)
														   .selectedRowColor(
																   Colors.LIST_SELECTED_ROW_COLOR))
								   .setContentPanelColor(
										   Colors.CONTENT_PANEL_COLOR)
								   .setPanelBackgroundColor(
										   Colors.BACKGROUND_PANEL_COLOR)
								   .setHotkeysPanelColor(
										   Colors.HOTKEY_PANEL_COLOR);
	}

	public void initiate() {
		initializeApplicationStateManagers();
		initializePanels();
		initializeMenuBar();
		initializeAdditionalWindowListeners();

	}

	private void initializeMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(BasicColors.BLUE_NORMAL_2);
		JMenu menu = new JMenu(MenuTexts.MENU_BAR_FILE);
		menuBar.add(menu);
		JMenuItem item = new JMenuItem(MenuTexts.MENU_OPEN);

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openKanjiProject();
			}
		});

		menu.add(item);
		applicationWindow.setMenuBar(menuBar);
	}

	private void initializeAdditionalWindowListeners() {
		applicationWindow.getContainer()
						 .addWindowListener(
								 focusLastFocusedElementWhenWindowRegainsFocus());
	}

	private void initializePanels() {

		RepeatingWordsPanel repeatingKanjiPanel = getRepeatingWordsPanel(
				Kanji.MEANINGFUL_NAME);
		RepeatingWordsPanel repeatingJapaneseWordsPanel = getRepeatingWordsPanel(
				JapaneseWord.MEANINGFUL_NAME);
		AbstractPanelWithHotkeysInfo problematicKanjiPanel = getProblematicWordsPanel(
				Kanji.MEANINGFUL_NAME);
		AbstractPanelWithHotkeysInfo problematicJapaneseWordsPanel = getProblematicWordsPanel(
				JapaneseWord.MEANINGFUL_NAME);

		applicationWindow.initiate(repeatingKanjiPanel,
				repeatingJapaneseWordsPanel, problematicKanjiPanel,
				problematicJapaneseWordsPanel);
	}

	private WindowListener focusLastFocusedElementWhenWindowRegainsFocus() {
		return new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				getActiveProblematicWordsController().focusPreviouslyFocusedElement();

			}
		};
	}

	private RepeatingWordsPanel getRepeatingWordsPanel(String meaningfulName) {
		return applicationStateController.getController(meaningfulName)
										 .getRepeatingWordsController()
										 .getRepeatingWordsPanel();
	}

	private AbstractPanelWithHotkeysInfo getProblematicWordsPanel(
			String meaningfulName) {

		return applicationStateController.getController(meaningfulName)
										 .getProblematicWordsController()
										 .getPanel();
	}

	public void initializeApplicationStateManagers() {
		applicationStateController.initialize(this);

	}

	//TODO dependencies between classes are weird and should be reconsidered

	private JFileChooser createFileChooser() {
		// TODO think about some more clever way of determining whether we are
		// in test or deployment directory
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getWorkingDirectory(fileChooser));
		return fileChooser;
	}

	public void loadWordsFromTextFiles() {
		JFileChooser fileChooser = createFileChooser();
		int option = fileChooser.showOpenDialog(
				applicationWindow.getContainer());
		if (option == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			getWordsAndFillLists(fileChooser.getSelectedFile());
		}
		catch (DuplicatedWordException | IOException e) {
			applicationWindow.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	private void getWordsAndFillLists(File file)
			throws DuplicatedWordException, IOException {
		WordsListReadWrite fileReader = new WordsListReadWrite();
		fileReader.readFile(file);
		WordsAndRepeatingInfo<Kanji, Integer> words = fileReader.getKanjisAndRepeatingData();
		List<Kanji> kanjiInformations = words.getWords();
		List<RepeatingData> repeatingInformations = words.getRepeatingInformations();
		Set<Integer> problematicKanjis = words.getProblematicWords();

		WordsAndRepeatingInfo<JapaneseWord, String> japaneseWords = fileReader.getJapaneseWordsAndRepeatingData();
		List<JapaneseWord> japaneseWordsInfo = japaneseWords.getWords();
		Set<String> problematicJapaneseWords = japaneseWords.getProblematicWords();
		List<RepeatingData> repeatingJapaneseWordsInformation = japaneseWords.getRepeatingInformations();

		clearWordData();
		setWordData(kanjiInformations, repeatingInformations,
				convertIdsToKanjiInformations(problematicKanjis),
				Kanji.MEANINGFUL_NAME);
		setWordData(japaneseWordsInfo, repeatingJapaneseWordsInformation,
				getWordsByMeanings(problematicJapaneseWords),
				JapaneseWord.MEANINGFUL_NAME);

		updateProblematicWordsAmount();
	}

	private <Word extends ListElement> void setWordData(
			List<Word> kanjiInformations,
			List<RepeatingData> repeatingInformations,
			Set<Word> problematicKanjis, String wordControllerName) {
		WordStateController kanjiController = applicationStateController.getController(
				wordControllerName);
		kanjiController.setWords(kanjiInformations);
		kanjiController.setRepeatingDates(repeatingInformations);
		kanjiController.setProblematicWords(problematicKanjis);
	}

	private void clearWordData() {
		applicationStateController.getController(Kanji.MEANINGFUL_NAME)
								  .clearData();
		applicationStateController.getController(JapaneseWord.MEANINGFUL_NAME)
								  .clearData();
	}

	private Set<JapaneseWord> getWordsByMeanings(Set<String> meanings) {
		Set<JapaneseWord> japaneseWordInformations = new HashSet<>();
		for (String meaning : meanings) {
			japaneseWordInformations.add(
					getJapaneseWords().findRowBasedOnPropertyStartingFromBeginningOfList(
							new JapaneseWordMeaningChecker(
									WordSearchOptions.BY_FULL_EXPRESSION),
							meaning, MoveDirection.BELOW, true));
		}
		return japaneseWordInformations;
	}

	public Set<Kanji> convertIdsToKanjiInformations(Set<Integer> ids) {
		Set<Kanji> kanjis = new HashSet<>();
		for (Integer i : ids) {
			kanjis.add(
					getKanjiList().findRowBasedOnPropertyStartingFromBeginningOfList(
							new KanjiIdChecker(), i, MoveDirection.BELOW,
							true));
		}
		return kanjis;
	}

	public void openKanjiProject() {
		File fileToSave = openFile();
		if (SHOULD_CONVERT_OLD_TO_NEWEST_VERSION) {
			try {
				OldToNewestVersionConverter.convertPreviousToNewestFile(
						fileToSave);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!fileToSave.exists())
			return;

		try {
			savingInformation = fileSavingManager.load(fileToSave);
		}
		catch (Exception e1) {
			applicationWindow.showMessageDialog("Exception loading from file.");
			e1.printStackTrace();
			return;
		}

		try {
			fileSavingManager.doBackupFile(fileToSave, savingInformation);
		}
		catch (IOException e1) {
			applicationWindow.showMessageDialog("Exception while saving.");
			e1.printStackTrace();
		}

		WordStateController kanjiController = applicationStateController.getController(
				Kanji.MEANINGFUL_NAME);
		try {
			if (savingInformation.getKanjiKoohiiCookiesHeaders() != null) {
				//TODO this should go to application controller's "restore
				// state method along with filling the mylists"
				ProblematicWordsDisplayer problematicWordsDisplayer = kanjiController.getProblematicWordsController()
																					 .getProblematicWordsDisplayer();
				((ProblematicKanjiDisplayer) problematicWordsDisplayer).
																			   setLoginDataCookie(
																					   savingInformation.getKanjiKoohiiCookiesHeaders());
				applicationStateController.reinitializeProblematicWordsControllers();
				//TODO avoid cast
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			applicationWindow.showMessageDialog("Error setting cookies");
		}

		loadingInProgress = true;
		boolean adjustJapaneseWords = false;
		if (adjustJapaneseWords) {
			new JapaneseWordsAdjuster().replaceSuruInMeaningToAdditionalInformation(
					savingInformation.getJapaneseWords());
			save();
		}

		clearWordData();
		kanjiController.setProblematicWords(
				savingInformation.getProblematicKanjis());
		WordStateController japaneseWordsController = applicationStateController.getController(
				JapaneseWord.MEANINGFUL_NAME);
		japaneseWordsController.setProblematicWords(
				savingInformation.getProblematicJapaneseWords());
		applicationWindow.updateTitle(fileToSave.toString());
		changeSaveStatus(SavingStatus.NO_CHANGES);

		applicationWindow.setPanel(startingController.getStartingPanel());
		savingInformation.getJapaneseWords()
						 .removeIf(JapaneseWord::isEmpty);
		LoadingPanel loadingPanel = showProgressDialog();
		LoadingProjectWorker loadingProjectWorker = new LoadingProjectWorker(
				this, loadingPanel);
		loadingProjectWorker.load(japaneseWordsController.getWords(),
				savingInformation.getJapaneseWords());
		loadingProjectWorker.load(kanjiController.getWords(),
				savingInformation.getKanjiWords());
		loadingProjectWorker.load(japaneseWordsController.getRepeatingList(),
				savingInformation.getJapaneseWordsRepeatingInformations());
		loadingProjectWorker.load(kanjiController.getRepeatingList(),
				savingInformation.getRepeatingList());
		recalculateLoadDialogPositionAndSize(loadingPanel);
		updateProblematicWordsAmount();
	}

	private LoadingPanel showProgressDialog() {
		LoadingPanel panel = new LoadingPanel();
		applicationWindow.createPanel(panel, Titles.LOADING_DIALOG, false,
				DialogWindow.Position.CENTER);
		return panel;
	}

	private void recalculateLoadDialogPositionAndSize(
			LoadingPanel loadingPanel) {
		Window container = loadingPanel.getDialog()
									   .getContainer();
		container.pack();
		container.setLocationRelativeTo(null);
	}

	private ApplicationStateManager getStateManagerForHandlingState(
			ApplicationSaveableState state) {
		if (state == null) {
			return this;
		}
		WordStateController wordStateController = applicationStateController.getController(
				state.getMeaningfulName());
		switch (state.getLearningState()) {
		case REVIEWING:
			return wordStateController.getProblematicWordsController();
		case REPEATING:
			return wordStateController.getRepeatingWordsController();
		default:
			return null;
		}
	}

	public void finishedLoadingProject() {
		applicationWindow.closeDialog();
		if (savingInformation.hasStateToRestore()) {
			getStateManagerForHandlingState(savingInformation.
																	 getApplicationSaveableState()).restoreState(
					savingInformation);
		}
		loadingInProgress = false;
	}

	private File openFile() {
		JFileChooser fileChooser = createFileChooser();

		int chosenOption = fileChooser.showOpenDialog(
				applicationWindow.getContainer());
		if (chosenOption == JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		return file;
	}

	private File getWorkingDirectory(JFileChooser fileChooser) {
		String directory;
		if (System.getProperty("user.dir")
				  .contains("dist")) {
			directory = "Powtórki kanji";
		}
		else {
			directory = "Testy do kanji";
		}
		return new File(
				fileChooser.getCurrentDirectory() + File.separator + directory);
	}

	public void showProblematicWordsDialogForCurrentList() {
		showProblematicWordsDialog(getActiveProblematicWordsController());
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			Set<Element> problematicWords) {

		ProblematicWordsController activeProblematicWordsController = getActiveProblematicWordsController();
		applicationWindow.setPanel(activeProblematicWordsController.getPanel());
		activeProblematicWordsController.addProblematicWordsAndHighlightFirst(
				problematicWords);
		showProblematicWordsDialog(activeProblematicWordsController);
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			String controllerMeaningfulName,
			ProblematicWordsState<Element> problematicWordsState) {
		displayMessageAboutUnfinishedRepeating();
		ProblematicWordsController problematicWordsController = applicationStateController.getController(
				controllerMeaningfulName)
																						  .getProblematicWordsController();
		problematicWordsController.addProblematicWordsHighlightReviewed(
				problematicWordsState.getReviewedWords(),
				problematicWordsState.getNotReviewedWords());
		showProblematicWordsDialog(problematicWordsController);
	}

	public void displayMessageAboutUnfinishedRepeating() {
		applicationWindow.showMessageDialog(Prompts.UNFINISHED_REPEATING);
	}

	private void showProblematicWordsDialog(
			ProblematicWordsController activeProblematicWordsController) {

		AbstractPanelWithHotkeysInfo problematicWordsPanel = activeProblematicWordsController.getPanel();
		if (problematicWordsPanel.isReady()) {
			activeProblematicWordsController.focusPreviouslyFocusedElement();
		}

		applicationWindow.showPanel(activeProblematicWordsController.getPanel()
																	.getUniqueName());

		switchStateManager(activeProblematicWordsController);

	}

	public void showLearningStartDialog() {
		TypeOfWordForRepeating typeForRepeating = getActiveWordsListType();
		getActiveRepeatingWordsController().setTypeOfWordForRepeating(
				typeForRepeating);

		applicationWindow.createPanel(
				new LearningStartPanel(this, typeForRepeating),
				Titles.LEARNING_START_DIALOG, false,
				DialogWindow.Position.CENTER);
	}

	private RepeatingWordsController getCurrentlyActiveWordsController(
			TypeOfWordForRepeating typeForRepeating) {

		WordStateController wordStateController = applicationStateController.getController(
				typeForRepeating.getAssociatedRepeatingWordsState()
								.getMeaningfulName());
		//TODO use method: get active words list type; replace all ocurrences
		return wordStateController.getRepeatingWordsController();
	}

	public MyList<JapaneseWord> getJapaneseWords() {
		return applicationStateController.getController(
				JapaneseWord.MEANINGFUL_NAME)
										 .getWords();
	}

	public MyList<RepeatingData> getJapaneseWordsRepeatingDates() {
		return applicationStateController.getController(
				JapaneseWord.MEANINGFUL_NAME)
										 .getRepeatingList();
	}

	public MyList<Kanji> getKanjiList() {
		return applicationStateController.getController(Kanji.MEANINGFUL_NAME)
										 .getWords();
	}

	public MyList<RepeatingData> getActiveRepeatingList() {
		return applicationStateController.getActiveWordsStateController()
										 .getRepeatingList();
	}

	public MyList getActiveWordsList() {
		return applicationStateController.getActiveWordsStateController()
										 .getWords();
	}

	public MyList<RepeatingData> getKanjiRepeatingDates() {
		return applicationStateController.getController(Kanji.MEANINGFUL_NAME)
										 .getRepeatingList();
	}

	public void saveList() {
		JFileChooser fileChooser = createFileChooser();

		int option = fileChooser.showSaveDialog(
				applicationWindow.getContainer());
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fileChooser.getSelectedFile();
		f = new File(f.toString() + ".txt");
		WordsListReadWrite reader = new WordsListReadWrite();
		try {
			reader.writeKanjiListToFile(f, getKanjiList(),
					getKanjiRepeatingDates(), getProblematicKanjis());
			reader.writeJapaneseListToFile(f, getJapaneseWords(),
					getJapaneseWordsRepeatingDates(),
					getProblematicJapaneseWords());
		}
		catch (IOException e) {
			applicationWindow.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showSaveDialog() {
		if (!fileSavingManager.hasFileToSave()) {
			JFileChooser fileChooser = createFileChooser();
			int option = fileChooser.showSaveDialog(
					this.applicationWindow.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				fileSavingManager.setFileToSave(file);
				save();
			}
		}

	}

	public String getKanjiKeywordById(int id) {
		Kanji kanji = getKanjiList().findRowBasedOnPropertyStartingFromBeginningOfList(
				new KanjiIdChecker(), id, MoveDirection.BELOW, false);
		return kanji == null ? "" : kanji.getKeyword();
	}

	public Set<Kanji> getProblematicKanjis() {
		return applicationStateController.getController(Kanji.MEANINGFUL_NAME)
										 .getProblematicWords();
	}

	public Set<? extends ListElement> getProblematicWordsBasedOnCurrentTab() {
		return applicationStateController.getProblematicWordsForActiveTab();
	}

	public int getProblematicWordsAmountBasedOnCurrentTab() {
		return getProblematicWordsBasedOnCurrentTab().size();
	}

	public void addWordToRepeatingList(RepeatingData word) {
		MyList<RepeatingData> repeatingList = getActiveRepeatingList();
		repeatingList.addWord(word);
		repeatingList.scrollToBottom();
	}

	public void setRepeatingInformation(RepeatingData info,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		getCurrentlyActiveWordsController(
				typeOfWordForRepeating).setRepeatingData(info);
	}

	public void initiateWordsLists(SetOfRanges ranges, boolean withProblematic,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		getCurrentlyActiveWordsController(
				typeOfWordForRepeating).resetAndInitializeWordsLists(ranges,
				getProblematicWordsBasedOnCurrentTab(), withProblematic);
	}

	public void startRepeating() {
		RepeatingWordsController activeRepeatingWordsController = getActiveRepeatingWordsController();
		applicationWindow.showPanel(activeRepeatingWordsController.getPanel()
																  .getUniqueName());
		activeRepeatingWordsController.startRepeating();
		applicationStateManager = activeRepeatingWordsController;
	}

	public TypeOfWordForRepeating getActiveWordsListType() {
		return TypeOfWordForRepeating.withMeaningfulName(
				applicationStateController.getActiveControllerMeaningfulName());
	}

	public void finishedRepeating() {
		applicationStateManager = this;
	}

	public Set<JapaneseWord> getProblematicJapaneseWords() {
		return applicationStateController.getController(
				JapaneseWord.MEANINGFUL_NAME)
										 .getProblematicWords();
	}

	@Override
	public boolean isClosingSafe() {
		return applicationStateManager == this
				|| applicationStateManager.isClosingSafe();
	}

	@Override
	public void save() {
		if (!fileSavingManager.hasFileToSave() || loadingInProgress) {
			return;
		}
		changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = applicationStateManager.getApplicationState();

		try {
			fileSavingManager.saveFile(savingInformation);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		changeSaveStatus(SavingStatus.SAVED);
	}

	@Override
	public SavingInformation getApplicationState() {
		SavingInformation savingInformation = new SavingInformation(
				getKanjiList().getWords(), getKanjiRepeatingDates().getWords(),
				getProblematicKanjis(), getProblematicJapaneseWords(),
				getJapaneseWords().getWords(),
				getJapaneseWordsRepeatingDates().getWords());
		savingInformation.setLastBackupFileNumber(
				fileSavingManager.getLastBackupFileNumber());
		String koohiiLoginDataCookie = getActiveProblematicWordsController().getProblematicWordsDisplayer()
																			.getKanjiKoohiLoginCookieHeader();
		savingInformation.clearApplicationState();

		if (!koohiiLoginDataCookie.isEmpty()) {
			savingInformation.setKanjiKoohiiCookiesHeaders(
					koohiiLoginDataCookie);
		}
		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		applicationStateManager.restoreState(savingInformation);
	}

	private void switchStateManager(ApplicationStateManager stateManager) {
		this.applicationStateManager = stateManager;
	}

	private ProblematicWordsController getActiveProblematicWordsController() {
		return applicationStateController.getActiveProblematicWordsController();
	}

	private RepeatingWordsController getActiveRepeatingWordsController() {
		return applicationStateController.getActiveRepeatingWordsController();
	}

	public void switchToList(TypeOfWordForRepeating typeOfWordForRepeating) {
		startingController.switchToList(typeOfWordForRepeating);
	}

	public StartingPanel getStartingPanel() {
		return startingController.getStartingPanel();
	}

	@Override
	public ApplicationWindow getApplicationWindow() {
		return applicationWindow;
	}

	@Override
	public void update(ListElement changedListElement,
			ListElementModificationType modificationType) {
		if (changedListElement.getClass()
							  .equals(Kanji.class)) {

		}
		else {
			if (modificationType.equals(ListElementModificationType.DELETE)) {
				getProblematicJapaneseWords().remove(changedListElement);
				updateProblematicWordsAmount();

			}
			else {
				getJapaneseWords().update((JapaneseWord) changedListElement,
						ListElementModificationType.EDIT);
			}

		}
	}

	public void updateProblematicWordsAmount() {
		startingController.updateProblematicWordsAmount(
				getProblematicWordsAmountBasedOnCurrentTab());
	}

	private void changeSaveStatus(SavingStatus savingStatus) {
		startingController.changeSaveStatus(savingStatus);
	}

	public void enableShowProblematicWordsButton() {
		startingController.enableShowProblematicWordsButton();
	}

	public void setActiveWordStateController(
			String activeWordStateControllerName) {
		this.applicationStateController.setActiveWordStateControllerKey(
				activeWordStateControllerName);
	}

	public StartingController getStartingController() {
		return startingController;
	}
}
