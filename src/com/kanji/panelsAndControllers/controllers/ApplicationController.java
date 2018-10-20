package com.kanji.panelsAndControllers.controllers;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ListElementModificationType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.enums.WordSearchOptions;
import com.guimaker.list.ListElement;
import com.guimaker.utilities.SetOfRanges;
import com.kanji.application.ApplicationChangesManager;
import com.guimaker.application.ApplicationConfiguration;
import com.kanji.application.ApplicationStateController;
import com.kanji.application.WordStateController;
import com.kanji.constants.Colors;
import com.kanji.constants.enums.*;
import com.kanji.constants.strings.MenuTexts;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.guimaker.customPositioning.CustomPositioner;
import com.kanji.customPositioning.PositionerOnRightPartOfSplitPane;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.guimaker.list.ListObserver;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.listRows.RowInRepeatingList;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordParticlesData;
import com.kanji.model.WordsAndRepeatingInfo;
import com.kanji.panelsAndControllers.panels.*;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.problematicWords.ProblematicWordsDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.FileSavingManager;
import com.kanji.saving.ProblematicWordsState;
import com.kanji.saving.SavingInformation;
import com.kanji.swingWorkers.LoadingProjectWorker;
import com.kanji.utilities.JapaneseWordsAdjuster;
import com.kanji.utilities.WordsListReadWrite;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicationController
		implements ApplicationStateManager, ListObserver,
		ApplicationChangesManager {

	private ApplicationWindow applicationWindow;
	private MyList<Kanji> kanjiList;
	private MyList<RepeatingData> kanjiRepeatingDates;
	private MyList<RepeatingData> japaneseWordsRepeatingDates;
	private MyList<JapaneseWord> japaneseWords;
	private Set<Kanji> problematicKanjis;
	private Set<JapaneseWord> problematicJapaneseWords;
	private boolean isClosingSafe;
	private ApplicationStateManager applicationStateManager;
	private SavingInformation savingInformation;
	private boolean loadingInProgress = false;
	private FileSavingManager fileSavingManager;
	private ApplicationStateController applicationStateController;
	private StartingPanel startingPanel;

	public ApplicationController() {
		problematicKanjis = new HashSet<>();
		problematicJapaneseWords = new HashSet<>();
		isClosingSafe = true;
		applicationStateManager = this;
		applicationStateController = new ApplicationStateController();
		fileSavingManager = new FileSavingManager();
		startingPanel = new StartingPanel();
		applicationWindow = new ApplicationWindow(this, startingPanel,
				createApplicationConfiguration());
	}

	private ApplicationConfiguration createApplicationConfiguration() {
		CustomPositioner customPositioner = new PositionerOnRightPartOfSplitPane(
				startingPanel, this);
		return new ApplicationConfiguration(Titles.APPLICATION)
				.setInsertWordPanelPositioner(customPositioner)
				.setListRowEditTemporarilyColor(
						Colors.LIST_ROW_EDIT_TEMPORARILY_COLOR)
				.setListRowHighlightColor(Colors.LIST_ROW_HIGHLIGHT_COLOR)
				.setContentPanelColor(Colors.CONTENT_PANEL_COLOR)
				.setPanelBackgroundColor(Colors.BACKGROUND_PANEL_COLOR);
	}

	public void initiate() {
		validatePreferIpV4PropertySet();
		initializeListsElements();
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
		applicationWindow.getContainer().addWindowListener(
				focusLastFocusedElementWhenWindowRegainsFocus());
	}

	private void initializePanels() {

		startingPanel.setApplicationController(this);
		startingPanel.createListPanels();

		RepeatingWordsPanel repeatingKanjiPanel = getRepeatingWordsPanel(
				Kanji.MEANINGFUL_NAME);
		RepeatingWordsPanel repeatingJapaneseWordsPanel = getRepeatingWordsPanel(
				JapaneseWord.MEANINGFUL_NAME);
		AbstractPanelWithHotkeysInfo problematicKanjiPanel = getProblematicWordsPanel(
				Kanji.MEANINGFUL_NAME);
		AbstractPanelWithHotkeysInfo problematicJapaneseWordsPanel = getProblematicWordsPanel(
				JapaneseWord.MEANINGFUL_NAME);

		applicationWindow
				.initiate(repeatingKanjiPanel, repeatingJapaneseWordsPanel,
						problematicKanjiPanel, problematicJapaneseWordsPanel);
	}

	private void validatePreferIpV4PropertySet() {
		String preferIpV4Property = System
				.getProperty("java.net.preferIPv4Stack");
		if (preferIpV4Property == null) {
			String message = "Aplikacja moze byc uruchomiona tylko z pliku exe.";
			applicationWindow.showMessageDialog(message);
			System.exit(1);
		}
	}

	private WindowListener focusLastFocusedElementWhenWindowRegainsFocus() {
		return new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				getActiveProblematicWordsController()
						.focusPreviouslyFocusedElement();

			}
		};
	}

	private RepeatingWordsPanel getRepeatingWordsPanel(String meaningfulName) {
		return applicationStateController.getController(meaningfulName)
				.getRepeatingWordsController().getRepeatingWordsPanel();
	}

	private AbstractPanelWithHotkeysInfo getProblematicWordsPanel(
			String meaningfulName) {

		return applicationStateController.getController(meaningfulName)
				.getProblematicWordsController().getPanel();
	}

	public void initializeApplicationStateManagers() {
		applicationStateController.initialize(this);

	}

	//TODO dependencies between classes are weird and should be reconsidered

	public void initializeListsElements() {
		initializeKanjiList();
		initializeKanjiRepeatingDates();
		initializeJapaneseWordsList();
		initializeJapaneseRepeatingDates();
	}

	private void initializeJapaneseWordsList() {
		JapaneseWordPanelCreator japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				this, applicationWindow, PanelDisplayMode.EDIT);
		RowInJapaneseWordInformations rowInJapaneseWordInformations = new RowInJapaneseWordInformations(
				japaneseWordPanelCreator);
		japaneseWords = new MyList<>(applicationWindow, this,
				rowInJapaneseWordInformations, Titles.JAPANESE_WORDS_LIST,
				JapaneseWord.getInitializer());
		japaneseWordPanelCreator.setWordsList(japaneseWords);

		JapaneseWord cat = new JapaneseWord(PartOfSpeech.NOUN, "kot");
		cat.addWritingsForKana("ねこ", "頭骨");
		cat.addParticleData(new WordParticlesData(JapaneseParticle.DE));
		cat.setPartOfSpeech(PartOfSpeech.EXPRESSION);
		japaneseWords.addWord(cat);
		JapaneseWord dog2 = new JapaneseWord(PartOfSpeech.NOUN, "pies");
		dog2.addWritingsForKana("いぬ");
		japaneseWords.addWord(dog2);
		JapaneseWord verb = new JapaneseWord(PartOfSpeech.VERB, "otwierać");
		verb.addWritingsForKana("あける", "開ける", "空ける", "明ける");
		verb.addWritingsForKana("ひらける", "開ける", "空ける", "明ける");
		JapaneseWord japaneseWord = new JapaneseWord(PartOfSpeech.NOUN, "Test");
		japaneseWord.addWritingsForKana("らけ1", "務");
		japaneseWords.addWord(japaneseWord);
		JapaneseWord japaneseWord2 = new JapaneseWord(PartOfSpeech.NOUN,
				"trykot");
		japaneseWord2.addWritingsForKana("らけ2", "務");
		japaneseWords.addWord(japaneseWord2);
		JapaneseWord japaneseWord3 = new JapaneseWord(PartOfSpeech.NOUN,
				"splot");
		japaneseWord3.addWritingsForKana("らけ3", "務");
		japaneseWords.addWord(japaneseWord3);
		JapaneseWord japaneseWord4 = new JapaneseWord(PartOfSpeech.NOUN,
				"przykazanie");
		japaneseWord4.addWritingsForKana("らけ4", "務");
		japaneseWords.addWord(japaneseWord4);
		JapaneseWord japaneseWord5 = new JapaneseWord(PartOfSpeech.NOUN,
				"opowieść, historia, legenda");
		japaneseWord5.addWritingsForKana("らけ5", "務");
		japaneseWords.addWord(japaneseWord5);
		JapaneseWord japaneseWord6 = new JapaneseWord(PartOfSpeech.NOUN,
				"pies z kotem");
		japaneseWord6.addWritingsForKana("らけ6", "務");
		japaneseWords.addWord(japaneseWord6);
		japaneseWords.addWord(verb);

	}

	private void initializeJapaneseRepeatingDates() {
		japaneseWordsRepeatingDates = new MyList<>(applicationWindow, this,
				new RowInRepeatingList(), Titles.JAPANESE_REPEATING_LIST,
				new ListConfiguration().enableWordAdding(false)
						.showButtonsLoadNextPreviousWords(false),
				RepeatingData.getInitializer());
		japaneseWordsRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(1993, 11, 13, 13, 25),
						true, "3 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2005, 1, 1, 11, 11),
						true, "4 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2000, 12, 31, 10, 0),
						true, "5 minut"));
	}

	private JFileChooser createFileChooser() {
		// TODO think about some more clever way of determining whether we are
		// in test or deployment directory
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getWorkingDirectory(fileChooser));
		return fileChooser;
	}

	public void loadWordsFromTextFiles() {
		JFileChooser fileChooser = createFileChooser();
		int option = fileChooser
				.showOpenDialog(applicationWindow.getContainer());
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
		WordsAndRepeatingInfo<Kanji, Integer> words = fileReader
				.getKanjisAndRepeatingData();
		List<Kanji> kanjiInformations = words.getWords();
		List<RepeatingData> repeatingInformations = words
				.getRepeatingInformations();
		Set<Integer> problematicKanjis = words.getProblematicWords();

		WordsAndRepeatingInfo<JapaneseWord, String> japaneseWords = fileReader
				.getJapaneseWordsAndRepeatingData();
		List<JapaneseWord> japaneseWordsInfo = japaneseWords.getWords();
		Set<String> problematicJapaneseWords = japaneseWords
				.getProblematicWords();
		List<RepeatingData> repeatingJapaneseWordsInformation = japaneseWords
				.getRepeatingInformations();

		kanjiList.cleanWords();
		kanjiRepeatingDates.cleanWords();

		for (Kanji kanji : kanjiInformations) {
			kanjiList.addWord(kanji);
		}
		for (RepeatingData repeatingData : repeatingInformations) {
			getKanjiRepeatingDates().addWord(repeatingData);
		}
		setProblematicWordsAndUpdateInformation(
				convertIdsToKanjiInformations(problematicKanjis));

		getJapaneseWords().cleanWords();
		getJapaneseWordsRepeatingDates().cleanWords();
		for (JapaneseWord japaneseWord : japaneseWordsInfo) {
			getJapaneseWords().addWord(japaneseWord);
		}
		for (RepeatingData repeatingInformation : repeatingJapaneseWordsInformation) {
			getJapaneseWordsRepeatingDates().addWord(repeatingInformation);
		}
		setProblematicWordsAndUpdateInformation(
				getWordsByMeanings(problematicJapaneseWords));
	}

	private Set<JapaneseWord> getWordsByMeanings(Set<String> meanings) {
		Set<JapaneseWord> japaneseWordInformations = new HashSet<>();
		for (String meaning : meanings) {
			japaneseWordInformations.add(getJapaneseWords()
					.findRowBasedOnPropertyStartingFromBeginningOfList(
							new JapaneseWordMeaningChecker(
									WordSearchOptions.BY_FULL_EXPRESSION),
							meaning, MoveDirection.BELOW, true));
		}
		return japaneseWordInformations;
	}

	public Set<Kanji> convertIdsToKanjiInformations(Set<Integer> ids) {
		Set<Kanji> kanjis = new HashSet<>();
		for (Integer i : ids) {
			kanjis.add(getKanjiList()
					.findRowBasedOnPropertyStartingFromBeginningOfList(
							new KanjiIdChecker(), i, MoveDirection.BELOW,
							true));
		}
		return kanjis;
	}

	public void openKanjiProject() {
		File fileToSave = openFile();
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

		try {
			if (savingInformation.getKanjiKoohiiCookiesHeaders() != null) {
				//TODO this should go to application controller's "restore
				// state method along with filling the mylists"
				ProblematicWordsDisplayer problematicWordsDisplayer = applicationStateController
						.getController(Kanji.MEANINGFUL_NAME)
						.getProblematicWordsController()
						.getProblematicWordsDisplayer();
				((ProblematicKanjiDisplayer) problematicWordsDisplayer).
						setLoginDataCookie(savingInformation
								.getKanjiKoohiiCookiesHeaders());
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
			new JapaneseWordsAdjuster()
					.replaceSuruInMeaningToAdditionalInformation(
							savingInformation.getJapaneseWords());
			save();
		}

		kanjiList.cleanWords();
		japaneseWords.cleanWords();
		japaneseWordsRepeatingDates.cleanWords();
		kanjiRepeatingDates.cleanWords();
		setProblematicWordsAndUpdateInformation(
				savingInformation.getProblematicKanjis());
		setProblematicWordsAndUpdateInformation(
				savingInformation.getProblematicJapaneseWords());
		applicationWindow.updateTitle(fileToSave.toString());
		changeSaveStatus(SavingStatus.NO_CHANGES);

		applicationWindow.setPanel(startingPanel);
		savingInformation.getJapaneseWords().removeIf(JapaneseWord::isEmpty);
		LoadingPanel loadingPanel = showProgressDialog();
		LoadingProjectWorker loadingProjectWorker = new LoadingProjectWorker(
				this, loadingPanel);
		loadingProjectWorker
				.load(japaneseWords, savingInformation.getJapaneseWords());
		loadingProjectWorker.load(kanjiList, savingInformation.getKanjiWords());
		loadingProjectWorker.load(japaneseWordsRepeatingDates,
				savingInformation.getJapaneseWordsRepeatingInformations());
		loadingProjectWorker.load(kanjiRepeatingDates,
				savingInformation.getRepeatingList());
		recalculateLoadDialogPositionAndSize(loadingPanel);
	}

	private LoadingPanel showProgressDialog() {
		LoadingPanel panel = new LoadingPanel();
		applicationWindow.createPanel(panel, Titles.MESSAGE_DIALOG, false,
				DialogWindow.Position.CENTER);
		return panel;
	}

	private void recalculateLoadDialogPositionAndSize(
			LoadingPanel loadingPanel) {
		Window container = loadingPanel.getDialog().getContainer();
		container.pack();
		container.setLocationRelativeTo(null);
	}

	private ApplicationStateManager getStateManagerForHandlingState(
			ApplicationSaveableState state) {
		if (state == null) {
			return this;
		}
		WordStateController wordStateController = applicationStateController
				.getController(state.getMeaningfulName());
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
					getApplicationSaveableState())
					.restoreState(savingInformation);
		}
		loadingInProgress = false;
	}

	private void initializeKanjiList() {
		kanjiList = new MyList<>(applicationWindow, this,
				new RowInKanjiInformations(this, PanelDisplayMode.EDIT),
				Titles.KANJI_LIST, Kanji.getInitializer());

		for (int i = 1; i <= 510; i++) {
			kanjiList.addWord(new Kanji("Word no. " + i, i));
		}
		kanjiList.addWord(new Kanji(
				"Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);",
				11));
	}

	private void initializeKanjiRepeatingDates() {
		kanjiRepeatingDates = new MyList<>(applicationWindow, this,
				new RowInRepeatingList(), Titles.KANJI_REPEATING_LIST,
				new ListConfiguration().showButtonsLoadNextPreviousWords(false)
						.enableWordAdding(false),
				RepeatingData.getInitializer());
		kanjiRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(1993, 11, 13, 13, 25),
						true, "3 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2005, 1, 1, 11, 11),
						true, "4 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2000, 12, 31, 10, 0),
						true, "5 minut"));
	}

	private File openFile() {
		JFileChooser fileChooser = createFileChooser();

		int chosenOption = fileChooser
				.showOpenDialog(applicationWindow.getContainer());
		if (chosenOption == JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		return file;
	}

	private File getWorkingDirectory(JFileChooser fileChooser) {
		String directory;
		if (System.getProperty("user.dir").contains("dist")) {
			directory = "Powtórki kanji";
		}
		else {
			directory = "Testy do kanji";
		}
		return new File(
				fileChooser.getCurrentDirectory() + File.separator + directory);
	}

	public boolean showConfirmDialog(String message) {
		return applicationWindow.showConfirmDialog(message);
	}

	public void showProblematicWordsDialogForCurrentList() {
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			Set<Element> problematicWords) {

		ProblematicWordsController activeProblematicWordsController = getActiveProblematicWordsController();
		applicationWindow.setPanel(activeProblematicWordsController.getPanel());
		activeProblematicWordsController
				.addProblematicWordsAndHighlightFirst(problematicWords);
		showProblematicWordsDialog();
	}

	public <Element extends ListElement> void showProblematicWordsDialog(
			ProblematicWordsState<Element> problematicWordsState) {
		displayMessageAboutUnfinishedRepeating();

		getActiveProblematicWordsController()
				.addProblematicWordsHighlightReviewed(
						problematicWordsState.getReviewedWords(),
						problematicWordsState.getNotReviewedWords());
		showProblematicWordsDialog();
	}

	public void displayMessageAboutUnfinishedRepeating() {
		applicationWindow.showMessageDialog(Prompts.UNFINISHED_REPEATING);
	}

	private void showProblematicWordsDialog() {

		ProblematicWordsController activeProblematicWordsController = getActiveProblematicWordsController();
		if (activeProblematicWordsController.isProblematicWordsListEmpty()) {
			return;
		}
		AbstractPanelWithHotkeysInfo problematicWordsPanel = activeProblematicWordsController
				.getPanel();
		if (problematicWordsPanel.isReady()) {
			activeProblematicWordsController.focusPreviouslyFocusedElement();
		}

		applicationWindow.showPanel(
				getActiveProblematicWordsController().getPanel()
						.getUniqueName());

		switchStateManager(activeProblematicWordsController);

	}

	public void showLearningStartDialog() {
		TypeOfWordForRepeating typeForRepeating = getActiveWordsListType();
		getCurrentlyActiveWordsController(typeForRepeating)
				.setTypeOfWordForRepeating(typeForRepeating);

		applicationWindow
				.createPanel(new LearningStartPanel(this, typeForRepeating),
						Titles.LEARNING_START_DIALOG, false,
						DialogWindow.Position.CENTER);
	}

	private RepeatingWordsController getCurrentlyActiveWordsController(
			TypeOfWordForRepeating typeForRepeating) {

		WordStateController wordStateController = applicationStateController
				.getController(
						typeForRepeating.getAssociatedRepeatingWordsState()
								.getMeaningfulName());
		//TODO use method: get active words list type; replace all ocurrences
		return wordStateController.getRepeatingWordsController();
	}

	public MyList<JapaneseWord> getJapaneseWords() {
		return japaneseWords;
	}

	public MyList<RepeatingData> getJapaneseWordsRepeatingDates() {
		return japaneseWordsRepeatingDates;
	}

	public MyList<Kanji> getKanjiList() {
		return kanjiList;
	}

	public MyList<RepeatingData> getActiveRepeatingList() {
		return startingPanel.getActiveRepeatingList();
	}

	public MyList getActiveWordsList() {
		return startingPanel.getActiveWordsList();
	}

	public MyList<RepeatingData> getKanjiRepeatingDates() {
		return kanjiRepeatingDates;
	}

	public void saveList() {
		JFileChooser fileChooser = createFileChooser();

		int option = fileChooser
				.showSaveDialog(applicationWindow.getContainer());
		if (option != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fileChooser.getSelectedFile();
		f = new File(f.toString() + ".txt");
		WordsListReadWrite reader = new WordsListReadWrite();
		try {
			reader.writeKanjiListToFile(f, kanjiList, kanjiRepeatingDates,
					getProblematicKanjis());
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
			int option = fileChooser
					.showSaveDialog(this.applicationWindow.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				fileSavingManager.setFileToSave(file);
				save();
			}
		}

	}

	public <Word extends ListElement> void setProblematicWordsAndUpdateInformation(
			Set<Word> problematicWords) {
		if (problematicWords.isEmpty()) {
			return;
		}
		Class wordClass = problematicWords.iterator().next().getClass();

		if (wordClass.equals(Kanji.class)) {
			problematicKanjis.clear();
			problematicKanjis.addAll((Set<Kanji>) problematicWords);
			//TODO ugly solution
		}
		else if (wordClass.equals(JapaneseWord.class)) {
			problematicJapaneseWords.clear();
			problematicJapaneseWords
					.addAll((Set<JapaneseWord>) problematicWords);
		}
		else {
			throw new IllegalArgumentException(
					"Invalid active words class name: " + wordClass);
		}
		if (wordClass.equals(getActiveWordsList().getListElementClass())) {
			updateProblematicWordsAmount();
		}

	}

	public Set<Kanji> getProblematicKanjis() {
		return problematicKanjis;
	}

	public Set<? extends ListElement> getProblematicWordsBasedOnCurrentTab() {
		Class activeWordsElementClass = getActiveWordsList()
				.getListElementClass();
		if (activeWordsElementClass.equals(Kanji.class)) {
			return problematicKanjis;
		}
		else if (activeWordsElementClass.equals(JapaneseWord.class)) {
			return problematicJapaneseWords;
		}
		else {
			throw new IllegalArgumentException(
					"Invalid active words class" + activeWordsElementClass);
		}
	}

	public int getProblematicWordsAmountBasedOnCurrentTab() {
		return getProblematicWordsBasedOnCurrentTab().size();
	}

	public void addWordToRepeatingList(RepeatingData word) {
		MyList<RepeatingData> repeatingList = startingPanel
				.getActiveRepeatingList();
		repeatingList.addWord(word);
		repeatingList.scrollToBottom();
	}

	public void setRepeatingInformation(RepeatingData info,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		getCurrentlyActiveWordsController(typeOfWordForRepeating)
				.setRepeatingData(info);
	}

	public void initiateWordsLists(SetOfRanges ranges, boolean withProblematic,
			TypeOfWordForRepeating typeOfWordForRepeating) {
		getCurrentlyActiveWordsController(typeOfWordForRepeating)
				.resetAndInitializeWordsLists(ranges,
						getProblematicWordsBasedOnCurrentTab(),
						withProblematic);
	}

	public void startRepeating() {
		RepeatingWordsController activeRepeatingWordsController = getActiveRepeatingWordsController();
		applicationWindow.showPanel(
				activeRepeatingWordsController.getPanel().getUniqueName());
		isClosingSafe = false;
		activeRepeatingWordsController.startRepeating();
		applicationStateManager = activeRepeatingWordsController;
	}

	public TypeOfWordForRepeating getActiveWordsListType() {
		MyList currentList = startingPanel.getActiveWordsList();
		Class listClass = currentList.getListElementClass();
		TypeOfWordForRepeating typeOfWordForRepeating = null;
		if (listClass.equals(Kanji.class)) {
			typeOfWordForRepeating = TypeOfWordForRepeating.KANJIS;
		}
		else if (listClass.equals(JapaneseWord.class)) {
			typeOfWordForRepeating = TypeOfWordForRepeating.JAPANESE_WORDS;
		}
		return typeOfWordForRepeating;
	}

	public void finishedRepeating() {
		isClosingSafe = true;
		applicationStateManager = this;
	}

	public Set<JapaneseWord> getProblematicJapaneseWords() {
		return problematicJapaneseWords;
	}

	@Override
	public boolean isClosingSafe() {
		return isClosingSafe;
	}

	@Override
	public void save() {
		if (!fileSavingManager.hasFileToSave() || loadingInProgress) {
			return;
		}
		changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = applicationStateManager
				.getApplicationState();

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
				kanjiList.getWords(), kanjiRepeatingDates.getWords(),
				getProblematicKanjis(), getProblematicJapaneseWords(),
				japaneseWords.getWords(),
				japaneseWordsRepeatingDates.getWords());
		savingInformation.setLastBackupFileNumber(
				fileSavingManager.getLastBackupFileNumber());
		String koohiiLoginDataCookie = applicationStateController.getController(
				getActiveWordsListType().getAssociatedRepeatingWordsState()
						.getMeaningfulName()).getProblematicWordsController()
				.getProblematicWordsDisplayer()
				.getKanjiKoohiLoginCookieHeader();
		savingInformation.clearApplicationState();

		if (!koohiiLoginDataCookie.isEmpty()) {
			savingInformation
					.setKanjiKoohiiCookiesHeaders(koohiiLoginDataCookie);
		}
		return savingInformation;
	}

	@Override
	public void restoreState(SavingInformation savingInformation) {
		applicationStateManager.restoreState(savingInformation);
	}

	public void switchStateManager(ApplicationStateManager stateManager) {
		this.applicationStateManager = stateManager;
		isClosingSafe = false;
	}

	public ProblematicWordsController getActiveProblematicWordsController() {
		return applicationStateController.getController(
				getActiveWordsListType().getAssociatedRepeatingWordsState()
						.getMeaningfulName()).getProblematicWordsController();
	}

	private RepeatingWordsController getActiveRepeatingWordsController() {
		return applicationStateController.getController(
				getActiveWordsListType().getAssociatedRepeatingWordsState()
						.getMeaningfulName()).getRepeatingWordsController();
	}

	public void switchToList(TypeOfWordForRepeating typeOfWordForRepeating) {
		startingPanel.switchToList(typeOfWordForRepeating);
	}

	public StartingPanel getStartingPanel() {
		return startingPanel;
	}

	@Override
	public ApplicationWindow getApplicationWindow() {
		return applicationWindow;
	}

	@Override
	public void update(ListElement changedListElement,
			ListElementModificationType modificationType) {
		if (changedListElement.getClass().equals(Kanji.class)) {

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
		startingPanel.updateProblematicWordsAmount(
				getProblematicWordsAmountBasedOnCurrentTab());
	}

	private void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.changeSaveStatus(savingStatus);
		startingPanel.getPanel().repaint();
	}

	public void enableShowProblematicWordsButton() {
		startingPanel.enableShowProblematicWordsButton();
	}
}
