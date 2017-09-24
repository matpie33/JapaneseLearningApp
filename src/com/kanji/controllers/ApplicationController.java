package com.kanji.controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ApplicationPanels;
import com.kanji.constants.SavingStatus;
import com.kanji.constants.Titles;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiInformations;
import com.kanji.myList.RowInRepeatingList;
import com.kanji.panels.LoadingPanel;
import com.kanji.range.SetOfRanges;
import com.kanji.utilities.LoadingAndSaving;
import com.kanji.utilities.SavingInformation;
import com.kanji.windows.ApplicationWindow;

public class ApplicationController {

	private RepeatingWordsController repeatingWordsPanelController;
	private ApplicationWindow parent;
	private MyList<KanjiInformation> listOfWords;
	private MyList<RepeatingInformation> repeats;
	private LoadingAndSaving loadingAndSaving;
	private Set<Integer> problematicKanjis;

	public ApplicationController(ApplicationWindow parent,
			RepeatingWordsController repeatingWordsPanelController) {
		problematicKanjis = new HashSet<Integer>();
		this.parent = parent;
		listOfWords = new MyList<KanjiInformation>(parent, this, new RowInKanjiInformations(parent),
				Titles.KANJI_LIST);
		repeats = new MyList<RepeatingInformation>(parent, this, new RowInRepeatingList(),
				Titles.REPEATING_LIST);
		loadingAndSaving = new LoadingAndSaving();
		this.repeatingWordsPanelController = repeatingWordsPanelController;
	}

	public void initializeListsElements() {
		initWordsList();
		initRepeatsList();
	}

	public void openKanjiProject() {
		File fileToSave = openFile();
		loadingAndSaving.setFileToSave(fileToSave);
		if (!fileToSave.exists())
			return;
		SavingInformation savingInformation = null;
		try {
			savingInformation = loadingAndSaving.load();
		}
		catch (Exception e1) {
			parent.showMessageDialog("Exception loading from file.");
			e1.printStackTrace();
		}
		if (savingInformation == null) {
			return;
		}
		listOfWords.cleanWords();
		addProblematicKanjis(savingInformation.getProblematicKanjis());
		parent.updateTitle(fileToSave.toString());
		parent.changeSaveStatus(SavingStatus.NO_CHANGES);

		showLoadedKanjisInPanel(savingInformation.getKanjiWords());
		showLoadedRepeatingInformations(savingInformation.getRepeatingList());

	}

	private void showLoadedKanjisInPanel(List<KanjiInformation> kanjiWords) {
		final LoadingPanel loadingPanel = parent.showProgressDialog();
		SwingWorker s = new SwingWorker<Void, Integer>() {

			private JProgressBar progress;

			@Override
			public Void doInBackground() throws Exception {
				progress = new JProgressBar();
				listOfWords.cleanWords();
				loadingPanel.setProgressBar(progress);
				progress.setMaximum(kanjiWords.size());
				List<Integer> ints = new ArrayList<>();
				for (int i = 0; i < kanjiWords.size(); i++) {
					listOfWords.addWord(kanjiWords.get(i));
					process(ints);
					ints.add(1);
				}

				return null;
			}

			@Override
			public void process(List<Integer> something) {
				progress.setValue(something.size());
			}

			@Override
			public void done() {
				parent.closeDialog();
				listOfWords.getPanel().repaint();
				listOfWords.scrollToBottom();
			}
		};
		s.execute();
	}

	private void showLoadedRepeatingInformations(List<RepeatingInformation> mapOfRepeats) {
		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				repeats.cleanWords();
				repeats.addWordsList(mapOfRepeats);
				repeats.getPanel().repaint();
				repeats.scrollToBottom();
			}
		};
		Thread t2 = new Thread(r2);
		t2.start();
	}

	private void initWordsList() {

		for (int i = 1; i <= 15; i++) {
			listOfWords.addWord(new KanjiInformation("Word no. " + i, i));
		}
		listOfWords.addWord(
				new KanjiInformation("Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);", 11));
	}

	private void initRepeatsList() {

		repeats.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(1993, 11, 13, 13, 25), true));
		repeats.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2005, 1, 1, 11, 11), true));
		repeats.addWord(
				new RepeatingInformation("abc", LocalDateTime.of(2000, 12, 31, 10, 0), true));
	}

	private File openFile() {
		JFileChooser fileChooser = new JFileChooser();
		String directory;
		// TODO think about some more clever way of determining whether we are
		// in test or deployment directory
		if (System.getProperty("user.dir").contains("dist")) {
			directory = "Powtórki kanji";
		}
		else {
			directory = "Testy do kanji";
		}
		fileChooser.setCurrentDirectory(
				new File(fileChooser.getCurrentDirectory() + File.separator + directory));

		int chosenOption = fileChooser.showOpenDialog(parent.getContainer());
		if (chosenOption == JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		loadingAndSaving.setFileToSave(file);
		return file;
	}

	public boolean showConfirmDialog(String message) {
		return parent.showConfirmDialog(message);
	}

	public void showInsertWordDialog() {
		parent.showInsertDialog(listOfWords);
	}

	public void showSearchWordDialog() {
		parent.showSearchWordDialog(listOfWords);
	}

	public void showLearningStartDialog() {
		parent.showLearningStartDialog(repeats, listOfWords.getNumberOfWords());
	}

	public MyList<KanjiInformation> getWordsList() {
		return listOfWords;
	}

	public MyList<RepeatingInformation> getRepeatsList() {
		return repeats;
	}

	public void save() {
		if (!loadingAndSaving.hasFileToSave()) {
			return;
		}
		parent.changeSaveStatus(SavingStatus.SAVING);
		parent.updateProblematicKanjisAmount(); // TODO this is not needed when
												// we click save button
		SavingInformation savingInformation = new SavingInformation(listOfWords.getWords(),
				repeats.getWords(), getProblematicKanjis());

		try {
			loadingAndSaving.save(savingInformation);
		}
		catch (IOException e1) {
			parent.showMessageDialog("Exception while saving.");
			e1.printStackTrace();
		}
		parent.changeSaveStatus(SavingStatus.SAVED);
	}

	public void exportList() {
		File f = new File("list.txt");
		try {
			BufferedWriter p = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
			List<KanjiInformation> list = listOfWords.getWords();
			for (KanjiInformation kanji : list) {
				p.write(kanji.getKanjiKeyword() + " " + kanji.getKanjiID());
				p.newLine();
			}
			p.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showSaveDialog() {
		if (!loadingAndSaving.hasFileToSave()) {
			JFileChooser fileChooser = new JFileChooser();
			int option = fileChooser.showSaveDialog(this.parent.getContainer());
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				loadingAndSaving.setFileToSave(file);
			}
		}
		save();
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjiList) {
		parent.updateProblematicKanjisAmount();
		problematicKanjis = problematicKanjiList;
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public void addWordToRepeatingList(RepeatingInformation word) {
		repeats.addWord(word);
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsPanelController.setRepeatingInformation(info);
	}

	public void initiateWordsLists(SetOfRanges ranges, boolean withProblematic) {
		repeatingWordsPanelController.initiateWordsLists(ranges, withProblematic);
	}

	public void startRepeating() {
		parent.showPanel(ApplicationPanels.REPEATING_PANEL);
		repeatingWordsPanelController.startRepeating();
	}

}
