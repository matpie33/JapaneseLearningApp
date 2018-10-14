package com.kanji.panelsAndControllers.controllers;

import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.kanji.constants.enums.*;
import com.kanji.constants.strings.Titles;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElementPropertyManagers.JapaneseWordMeaningChecker;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.listObserver.ListObserver;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.listRows.RowInRepeatingList;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordParticlesData;
import com.kanji.model.WordsAndRepeatingInfo;
import com.kanji.panelsAndControllers.panels.LoadingPanel;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.range.SetOfRanges;
import com.kanji.repeating.RepeatingJapaneseWordsDisplayer;
import com.kanji.repeating.RepeatingKanjiDisplayer;
import com.kanji.repeating.RepeatingWordsDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.FileSavingManager;
import com.kanji.saving.SavingInformation;
import com.kanji.swingWorkers.LoadingProjectWorker;
import com.kanji.utilities.JapaneseWordsAdjuster;
import com.kanji.utilities.JapaneseWordsFileReader;
import com.kanji.utilities.WordsListReadWrite;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class ApplicationController
		implements ApplicationStateManager, ListObserver {

	private RepeatingWordsController repeatingWordsPanelController;
	private ApplicationWindow parent;
	private MyList<Kanji> kanjiList;
	private MyList<RepeatingData> kanjiRepeatingDates;
	private MyList<RepeatingData> japaneseWordsRepeatingDates;
	private MyList<JapaneseWord> japaneseWords;
	private Set<Kanji> problematicKanjis;
	private Set<JapaneseWord> problematicJapaneseWords;
	private boolean isClosingSafe;
	private ApplicationStateManager applicationStateManager;
	private Map<ApplicationSaveableState, ApplicationStateManager> applicationStateToManagerMap;
	private ProblematicWordsController<Kanji> problematicKanjisController;
	private ProblematicWordsController<JapaneseWord> problematicJapaneseWordsController;
	private JapaneseWordsFileReader japaneseWordsFileReader;
	private SavingInformation savingInformation;
	private RepeatingKanjiDisplayer kanjiWordDisplayer;
	private RepeatingJapaneseWordsDisplayer repeatingJapaneseWordsDisplayer;
	private ProblematicKanjiDisplayer problematicKanjiDisplayer;
	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;
	private RowInJapaneseWordInformations rowInJapaneseWordInformations;
	private boolean loadingInProgress = false;
	private boolean adjustJapaneseWords = false;
	private Set<ListObserver> listObservers = new HashSet<>();

	private FileSavingManager fileSavingManager;

	public ApplicationController(ApplicationWindow parent) {
		problematicKanjis = new HashSet<>();
		problematicJapaneseWords = new HashSet<>();
		this.parent = parent;
		isClosingSafe = true;
		applicationStateManager = this;
		applicationStateToManagerMap = new HashMap<>();

		japaneseWordsFileReader = new JapaneseWordsFileReader();

		kanjiWordDisplayer = new RepeatingKanjiDisplayer(parent.getKanjiFont());
		fileSavingManager = new FileSavingManager();
	}

	public ProblematicWordsController getProblematicKanjisController() {
		return problematicKanjisController;
	}

	public RepeatingWordsPanel getRepeatingWordsPanel() {
		return repeatingWordsPanelController.getRepeatingWordsPanel();
	}

	public void initializeApplicationStateManagers() {
		problematicKanjisController = new ProblematicWordsController<>(parent);
		problematicJapaneseWordsController = new ProblematicWordsController<>(
				parent);
		getJapaneseWords().addListObserver(problematicJapaneseWordsController);
		getKanjiList().addListObserver(problematicKanjisController);
		problematicKanjiDisplayer = new ProblematicKanjiDisplayer(parent,
				problematicKanjisController);
		problematicJapaneseWordsDisplayer = new ProblematicJapaneseWordsDisplayer(
				parent, problematicJapaneseWordsController);

		this.repeatingWordsPanelController = new RepeatingWordsController(
				parent);
		applicationStateToManagerMap
				.put(ApplicationSaveableState.REPEATING_WORDS,
						repeatingWordsPanelController);
		applicationStateToManagerMap
				.put(ApplicationSaveableState.REVIEWING_PROBLEMATIC_KANJIS,
						problematicKanjisController);
		applicationStateToManagerMap
				.put(ApplicationSaveableState.REVIEWING_PROBLEMATIC_JAPANESE_WORDS,
						problematicJapaneseWordsController);
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
				this, parent, PanelDisplayMode.EDIT);
		rowInJapaneseWordInformations = new RowInJapaneseWordInformations(
				japaneseWordPanelCreator);
		japaneseWords = new MyList<>(parent, this,
				rowInJapaneseWordInformations, Titles.JAPANESE_WORDS_LIST,
				JapaneseWord.getInitializer());
		japaneseWordPanelCreator.setWordsList(japaneseWords);
		JapaneseWordPanelCreator wordPanelCreator = new JapaneseWordPanelCreator(
				this, parent, PanelDisplayMode.VIEW);
		wordPanelCreator.setWordsList(japaneseWords);
		repeatingJapaneseWordsDisplayer = new RepeatingJapaneseWordsDisplayer(
				wordPanelCreator);

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
		japaneseWordsRepeatingDates = new MyList<>(parent, this,
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
		int option = fileChooser.showOpenDialog(parent.getContainer());
		if (option == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			getWordsAndFillLists(fileChooser.getSelectedFile());
		}
		catch (DuplicatedWordException | IOException e) {
			parent.showMessageDialog(e.getMessage());
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
			parent.showMessageDialog("Exception loading from file.");
			e1.printStackTrace();
			return;
		}

		try {
			fileSavingManager.doBackupFile(fileToSave, savingInformation);
		}
		catch (IOException e1) {
			parent.showMessageDialog("Exception while saving.");
			e1.printStackTrace();
		}

		try {
			if (savingInformation.getKanjiKoohiiCookiesHeaders() != null) {
				//TODO this should go to application controller's "restore
				// state method along with filling the mylists"
				problematicKanjiDisplayer.setLoginDataCookie(
						savingInformation.getKanjiKoohiiCookiesHeaders());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			parent.showMessageDialog("Error setting cookies");
		}

		loadingInProgress = true;
		if (adjustJapaneseWords) {
			new JapaneseWordsAdjuster()
					.replaceSuruInMeaningToAdditionalInformation(
							savingInformation.getJapaneseWords());
			saveProject();
		}

		kanjiList.cleanWords();
		japaneseWords.cleanWords();
		japaneseWordsRepeatingDates.cleanWords();
		kanjiRepeatingDates.cleanWords();
		setProblematicWordsAndUpdateInformation(
				savingInformation.getProblematicKanjis());
		setProblematicWordsAndUpdateInformation(
				savingInformation.getProblematicJapaneseWords());
		parent.updateTitle(fileToSave.toString());
		parent.changeSaveStatus(SavingStatus.NO_CHANGES);

		parent.setPanel(parent.getStartingPanel());
		savingInformation.getJapaneseWords().removeIf(JapaneseWord::isEmpty);
		LoadingPanel loadingPanel = parent.showProgressDialog();
		LoadingProjectWorker loadingProjectWorker = new LoadingProjectWorker(
				parent, loadingPanel);
		loadingProjectWorker
				.load(japaneseWords, savingInformation.getJapaneseWords());
		loadingProjectWorker.load(kanjiList, savingInformation.getKanjiWords());
		loadingProjectWorker.load(japaneseWordsRepeatingDates,
				savingInformation.getJapaneseWordsRepeatingInformations());
		loadingProjectWorker.load(kanjiRepeatingDates,
				savingInformation.getRepeatingList());
		recalculateLoadDialogPositionAndSize(loadingPanel);
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
		return applicationStateToManagerMap.get(state);
	}

	public void finishedLoadingProject() {
		parent.closeDialog();
		if (savingInformation.hasStateToRestore()) {
			getStateManagerForHandlingState(savingInformation.
					getApplicationSaveableState())
					.restoreState(savingInformation);
		}
		loadingInProgress = false;
	}

	private void initializeKanjiList() {
		kanjiList = new MyList<>(parent, this,
				new RowInKanjiInformations(parent, PanelDisplayMode.EDIT),
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
		kanjiRepeatingDates = new MyList<>(parent, this,
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

		int chosenOption = fileChooser.showOpenDialog(parent.getContainer());
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
		return parent.showConfirmDialog(message);
	}

	public void showInsertWordDialog() {
		parent.showInsertDialog(parent.getStartingPanel().getActiveWordsList());
	}

	public void showLearningStartDialog() {

		TypeOfWordForRepeating typeForRepeating = getActiveWordsListType();
		repeatingWordsPanelController
				.setTypeOfWordForRepeating(typeForRepeating);
		parent.showLearningStartDialog(typeForRepeating);
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

	public MyList getActiveWordsList() {
		return parent.getStartingPanel().getActiveWordsList();
	}

	public MyList<RepeatingData> getKanjiRepeatingDates() {
		return kanjiRepeatingDates;
	}

	public void saveProject() {
		if (!fileSavingManager.hasFileToSave() || loadingInProgress) {
			return;
		}
		parent.changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = applicationStateManager
				.getApplicationState();

		try {
			fileSavingManager.saveFile(savingInformation);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		parent.changeSaveStatus(SavingStatus.SAVED);
	}

	public void saveList() {
		JFileChooser fileChooser = createFileChooser();

		int option = fileChooser.showSaveDialog(parent.getContainer());
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
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showSaveDialog() {
		if (!fileSavingManager.hasFileToSave()) {
			JFileChooser fileChooser = createFileChooser();
			int option = fileChooser.showSaveDialog(this.parent.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				fileSavingManager.setFileToSave(file);
				saveProject();
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
			parent.updateProblematicWordsAmount();
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
		MyList<RepeatingData> repeatingList = parent.getStartingPanel()
				.getActiveRepeatingList();
		repeatingList.addWord(word);
		repeatingList.scrollToBottom();
	}

	public void setRepeatingInformation(RepeatingData info) {
		repeatingWordsPanelController.setRepeatingData(info);
	}

	public void initiateWordsLists(SetOfRanges ranges,
			boolean withProblematic) {
		repeatingWordsPanelController.resetAndInitializeWordsLists(ranges,
				getProblematicWordsBasedOnCurrentTab(), withProblematic);
	}

	public void startRepeating() {
		Class activeWordsList = getActiveWordsList().getListElementClass();
		if (activeWordsList.equals(Kanji.class)) {
			problematicKanjisController.initialize();
		}
		else {
			problematicJapaneseWordsController.initialize();
		}
		parent.showPanel(ApplicationPanels.REPEATING_PANEL);
		isClosingSafe = false;
		repeatingWordsPanelController.startRepeating();
		applicationStateManager = repeatingWordsPanelController;
	}

	public TypeOfWordForRepeating getActiveWordsListType() {
		MyList currentList = parent.getStartingPanel().getActiveWordsList();
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

	public RepeatingWordsDisplayer getWordDisplayerForWordType(
			TypeOfWordForRepeating wordType) {
		if (wordType.equals(TypeOfWordForRepeating.KANJIS)) {
			return kanjiWordDisplayer;
		}
		else if (wordType.equals(TypeOfWordForRepeating.JAPANESE_WORDS)) {
			return repeatingJapaneseWordsDisplayer;
		}
		return null;
	}

	public void finishedRepeating() {
		isClosingSafe = true;
		applicationStateManager = this;
	}

	public Set<JapaneseWord> getProblematicJapaneseWords() {
		return problematicJapaneseWords;
	}

	public boolean isClosingSafe() {
		return isClosingSafe;
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
		String koohiiLoginDataCookie = problematicKanjiDisplayer
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

	public ProblematicWordsController getProblematicWordsControllerBasedOnWordType(
			Class wordType) {
		if (wordType.equals(Kanji.class)) {
			return problematicKanjisController;
		}
		else if (wordType.equals(JapaneseWord.class)) {
			return problematicJapaneseWordsController;
		}
		return null;
	}

	public void switchToList(TypeOfWordForRepeating typeOfWordForRepeating) {
		parent.getStartingPanel().switchToList(
				typeOfWordForRepeating);
	}

	@Override
	public void update(ListElement changedListElement,
			ListElementModificationType modificationType) {
		if (changedListElement.getClass().equals(Kanji.class)) {

		}
		else {
			if (modificationType.equals(ListElementModificationType.DELETE)) {
				getProblematicJapaneseWords().remove(changedListElement);
				parent.updateProblematicWordsAmount();

			}
			else {
				getJapaneseWords().update((JapaneseWord) changedListElement,
						ListElementModificationType.EDIT);
			}

		}
	}

	public ProblematicWordsController getActiveProblematicWordsController() {
		Class listELementClass = getActiveWordsList().getListElementClass();
		if (listELementClass.equals(Kanji.class)) {
			return problematicKanjisController;
		}
		else {
			return problematicJapaneseWordsController;
		}
	}
}
