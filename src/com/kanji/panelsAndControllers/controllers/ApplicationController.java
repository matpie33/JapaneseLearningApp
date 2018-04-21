package com.kanji.panelsAndControllers.controllers;

import com.kanji.constants.enums.*;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Titles;
import com.kanji.context.WordTypeContext;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.ListElement;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.listRows.RowInRepeatingList;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanjisAndRepeatingInfo;
import com.kanji.panelsAndControllers.panels.RepeatingWordsPanel;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.problematicWords.ProblematicKanjiDisplayer;
import com.kanji.range.SetOfRanges;
import com.kanji.repeating.RepeatingJapaneseWordsDisplayer;
import com.kanji.repeating.RepeatingKanjiDisplayer;
import com.kanji.repeating.RepeatingWordDisplayer;
import com.kanji.saving.ApplicationStateManager;
import com.kanji.saving.LoadingAndSaving;
import com.kanji.saving.SavingInformation;
import com.kanji.swingWorkers.LoadingProjectWorker;
import com.kanji.utilities.JapaneseWordsFileReader;
import com.kanji.utilities.KanjiListFileReader;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ApplicationController implements ApplicationStateManager {

	private RepeatingWordsController repeatingWordsPanelController;
	private ApplicationWindow parent;
	private MyList<Kanji> kanjiList;
	private MyList<RepeatingData> kanjiRepeatingDates;
	private MyList<RepeatingData> japaneseWordsRepeatingDates;
	private MyList<JapaneseWord> japaneseWords;
	private LoadingAndSaving loadingAndSaving;
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

	public ApplicationController(ApplicationWindow parent) {
		problematicKanjis = new HashSet<>();
		problematicJapaneseWords = new HashSet<>();
		this.parent = parent;
		isClosingSafe = true;
		applicationStateManager = this;
		applicationStateToManagerMap = new HashMap<>();
		loadingAndSaving = new LoadingAndSaving();
		japaneseWordsFileReader = new JapaneseWordsFileReader();
		repeatingJapaneseWordsDisplayer = new RepeatingJapaneseWordsDisplayer(
				createJapanesePanelCreator());
		kanjiWordDisplayer = new RepeatingKanjiDisplayer(parent.getKanjiFont());
	}

	private JapaneseWordPanelCreator createJapanesePanelCreator() {
		return new JapaneseWordPanelCreator(this, parent,
				JapanesePanelDisplayMode.VIEW);
		//TODO parent dialog is not needed without validation i.e. in view mode
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
				this, parent, JapanesePanelDisplayMode.EDIT);
		rowInJapaneseWordInformations = new RowInJapaneseWordInformations(
				japaneseWordPanelCreator);
		japaneseWords = new MyList<>(parent, this,
				rowInJapaneseWordInformations, Titles.JAPANESE_WORDS_LIST,
				JapaneseWord.getInitializer());

		JapaneseWord cat = new JapaneseWord(PartOfSpeech.NOUN, "kot");
		cat.addWritings("ねこ", "頭骨");
		japaneseWords.addWord(cat);
		JapaneseWord dog2 = new JapaneseWord(PartOfSpeech.NOUN, "pies");
		dog2.addWritings("いぬ", "二", "三", "四");
		japaneseWords.addWord(dog2);
		JapaneseWord verb = new JapaneseWord(PartOfSpeech.VERB, "otwierać");
		verb.addWritings("あける", "開ける", "空ける", "明ける");
		verb.addWritings("ひらける", "開ける", "空ける", "明ける");
		verb.addAditionalInformation(AdditionalInformationTag.VERB_CONJUGATION,
				Labels.VERB_CONJUGATION_GODAN);
		JapaneseWord japaneseWord = new JapaneseWord(PartOfSpeech.NOUN, "Test");
		japaneseWord.addWritings("らけ", "務");
		japaneseWords.addWord(japaneseWord);
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

	public void loadList(WordTypeContext wordTypeContext) {
		switch (wordTypeContext.getWordTypeForRepeating()) {
		case KANJIS:
			loadKanjiList();
			break;
		case JAPANESE_WORDS:
			loadJapaneseWordsList();
			break;
		}
	}

	private void loadJapaneseWordsList() {
		JFileChooser fileChooser = createFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		//TODO each file should have a label at the top, parser should parse the word type in kanji list:
		// noun (default), verb (ending in ru, su, mu etc), adjective ending in -i, expression
		int option = fileChooser.showOpenDialog(parent.getContainer());
		if (option == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			japaneseWordsFileReader.readFiles(fileChooser.getSelectedFiles());
			this.japaneseWords.cleanWords();

			for (JapaneseWord japaneseWord : japaneseWordsFileReader
					.getNewWords()) {
				this.japaneseWords.addWord(japaneseWord);
			}

			parent.showDuplicatedJapaneseWordsDialog(
					japaneseWordsFileReader.getDuplicatedWords());

		}
		catch (Exception e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadKanjiList() {
		JFileChooser fileChooser = createFileChooser();
		int option = fileChooser.showOpenDialog(parent.getContainer());
		if (option == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			loadKanjisListsFromTextFile(fileChooser.getSelectedFile());
		}
		catch (DuplicatedWordException | IOException e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	private void loadKanjisListsFromTextFile(File file)
			throws DuplicatedWordException, IOException {
		KanjiListFileReader fileReader = new KanjiListFileReader();
		KanjisAndRepeatingInfo words = fileReader.readFile(file);
		List<Kanji> kanjis = words.getKanjis();
		List<RepeatingData> repeatingDataList = words.getRepeatingData();
		Set<Integer> problematicKanjis = words.getProblematicKanjis();

		kanjiList.cleanWords();
		kanjiRepeatingDates.cleanWords();
		for (Kanji kanji : kanjis) {
			kanjiList.addWord(kanji);
		}
		for (RepeatingData repeatingData : repeatingDataList) {
			kanjiRepeatingDates.addWord(repeatingData);
		}
		setProblematicWordsAndUpdateInformation(
				convertIdsToKanjiInformations(problematicKanjis));
	}

	public Set<Kanji> convertIdsToKanjiInformations(Set<Integer> ids) {
		Set<Kanji> kanjis = new HashSet<>();
		for (Integer i : ids) {
			kanjis.add(getKanjiList()
					.findRowBasedOnPropertyStartingFromBeginningOfList(
							new KanjiIdChecker(), i, SearchingDirection.FORWARD,
							true));
		}
		return kanjis;
	}

	public void openKanjiProject() {
		File fileToSave = openFile();
		if (!fileToSave.exists())
			return;

		try {
			savingInformation = loadingAndSaving.load(fileToSave);
		}
		catch (Exception e1) {
			parent.showMessageDialog("Exception loading from file.");
			e1.printStackTrace();
			return;
		}
		loadingAndSaving.setFileToSave(fileToSave);
		//		rowInJapaneseWordInformations.getJapaneseWordPanelCreator().clear();
		//TODO reimplement
		try {
			if (savingInformation.getKanjiKoohiiCookiesHeaders() != null) {
				//TODO this should go to application controller's "restore
				// state method along with filling the mylists"
				problematicKanjiDisplayer.setCookies(
						savingInformation.getKanjiKoohiiCookiesHeaders());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			parent.showMessageDialog("Error setting cookies");
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

		LoadingProjectWorker loadingProjectWorker = new LoadingProjectWorker(
				parent, parent.showProgressDialog());
		loadingProjectWorker
				.load(japaneseWords, savingInformation.getJapaneseWords());
		loadingProjectWorker.load(kanjiList, savingInformation.getKanjiWords());
		loadingProjectWorker.load(japaneseWordsRepeatingDates,
				savingInformation.getJapaneseWordsRepeatingInformations());
		loadingProjectWorker.load(kanjiRepeatingDates,
				savingInformation.getRepeatingList());
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
	}

	private void initializeKanjiList() {
		kanjiList = new MyList<>(parent, this,
				new RowInKanjiInformations(parent), Titles.KANJI_LIST,
				Kanji.getInitializer());

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

	public void showSearchWordDialog() {
		parent.showSearchWordDialog(
				parent.getStartingPanel().getActiveWordsList());
	}

	public void showLearningStartDialog() {

		repeatingWordsPanelController
				.setWordDisplayer(getWordDisplayerForCurrentWordList());
		parent.showLearningStartDialog(
				parent.getStartingPanel().getActiveWordsList()
						.getNumberOfWords());
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
		if (!loadingAndSaving.hasFileToSave()) {
			return;
		}
		parent.changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = applicationStateManager
				.getApplicationState();

		try {
			loadingAndSaving.save(savingInformation);
		}
		catch (IOException e1) {
			parent.showMessageDialog("Exception while saving.");
			e1.printStackTrace();
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
		KanjiListFileReader reader = new KanjiListFileReader();
		try {
			reader.writeToFile(f, kanjiList, kanjiRepeatingDates,
					getProblematicKanjis());
		}
		catch (IOException e) {
			parent.showMessageDialog(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showSaveDialog() {
		if (!loadingAndSaving.hasFileToSave()) {
			JFileChooser fileChooser = createFileChooser();
			int option = fileChooser.showSaveDialog(this.parent.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				loadingAndSaving.setFileToSave(file);
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
			problematicKanjis.addAll((Set<Kanji>) problematicWords);
			//TODO ugly solution
		}
		else if (wordClass.equals(JapaneseWord.class)) {
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
		repeatingWordsPanelController.setRepeatingInformation(info);
	}

	public void initiateWordsLists(SetOfRanges ranges,
			boolean withProblematic) {
		repeatingWordsPanelController.initiateWordsLists(ranges,
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
		kanjiWordDisplayer.addProblematicKanjis(getProblematicKanjis());
		repeatingWordsPanelController.startRepeating();
		applicationStateManager = repeatingWordsPanelController;
	}

	private RepeatingWordDisplayer getWordDisplayerForCurrentWordList() {
		MyList currentList = parent.getStartingPanel().getActiveWordsList();
		Class listClass = currentList.getListElementClass();
		if (listClass.equals(Kanji.class)) {
			return kanjiWordDisplayer;
		}
		else if (listClass.equals(JapaneseWord.class)) {
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
		String kanjiKoohiCookiesHeaders = problematicKanjiDisplayer
				.getKanjiKoohiCookieHeader();
		if (!kanjiKoohiCookiesHeaders.isEmpty()) {
			savingInformation
					.setKanjiKoohiiCookiesHeaders(kanjiKoohiCookiesHeaders);
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

	public ProblematicWordsController getProblematicWordsControllerBasedOnActiveWordList() {
		MyList activeWordList = parent.getStartingPanel().getActiveWordsList();
		Class listElementsClass = activeWordList.getListElementClass();
		return getProblematicWordsControllerBasedOnWordType(listElementsClass);
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

	public void switchToList(Class listType) {
		parent.getStartingPanel().switchToList(listType);
	}

}
