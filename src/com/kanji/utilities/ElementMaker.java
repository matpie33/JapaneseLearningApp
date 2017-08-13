package com.kanji.utilities;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.guimaker.colors.BasicColors;
import com.kanji.Row.KanjiInformation;
import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingList;
import com.kanji.constants.MenuTexts;
import com.kanji.constants.SavingStatus;
import com.kanji.constants.Titles;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiInformations;
import com.kanji.myList.RowInRepeatingList;
import com.kanji.panels.LoadingPanel;
import com.kanji.windows.ApplicationWindow;

public class ElementMaker {

	private ApplicationWindow parent;
	private MyList<KanjiWords> listOfWords;
	private MyList<RepeatingList> repeats;
	private JMenuBar menuBar;
	private File fileToSave;
	private LoadingAndSaving loadingAndSaving;

	public ElementMaker(ApplicationWindow parent) {
		this.parent = parent;
		initElements();
		loadingAndSaving = new LoadingAndSaving();
	}

	private void initElements() {
		initListOfWords();
		initRepeatsList();
		createMenu();
	}

	private void createMenu() {
		menuBar = new JMenuBar();
		menuBar.setBackground(BasicColors.OCEAN_BLUE);
		JMenu menu = new JMenu(MenuTexts.menuBarFile);
		menuBar.add(menu);
		JMenuItem item = new JMenuItem(MenuTexts.menuOpen);

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openKanjiFile();
			}
		});

		menu.add(item);
	}

	public void openKanjiFile() {
		fileToSave = openFile();
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
		// TODO we cannot rename com.kanji.row package to lower case - it
		// breaks loading from file - fix that
		listOfWords.cleanWords();

		parent.getStartingController()
				.addProblematicKanjis(savingInformation.getProblematicKanjis());

		parent.updateTitle(fileToSave.toString());
		parent.changeSaveStatus(SavingStatus.NO_CHANGES);

		showLoadedKanjisInPanel(savingInformation.getKanjiWords());
		showLoadedRepeatingInformations(savingInformation.getRepeatingList());

	}

	private void showLoadedKanjisInPanel(KanjiWords kanjiWords) {
		final LoadingPanel loadingPanel = parent.showProgressDialog();
		SwingWorker s = new SwingWorker<Void, Integer>() {

			private JProgressBar progress;

			@Override
			public Void doInBackground() throws Exception {
				progress = new JProgressBar();
				populateWordsList(kanjiWords);
				loadingPanel.setProgressBar(progress);
				progress.setMaximum(kanjiWords.getNumberOfKanjis());
				List<KanjiInformation> wordsList = listOfWords.getWords().getAllWords();
				List<Integer> ints = new ArrayList<>();
				for (int i = 0; i < kanjiWords.getNumberOfKanjis(); i++) {
					kanjiWords.addRow(wordsList.get(i), i + 1);
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
				listOfWords.repaint();
				listOfWords.scrollToBottom();
			}
		};
		s.execute();
	}

	private void showLoadedRepeatingInformations(RepeatingList mapOfRepeats) {
		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				repeats.setWords(mapOfRepeats);
				mapOfRepeats.setList(repeats);
				mapOfRepeats.initialize();
				repeats.getWords().addAll();
				repeats.repaint();
				repeats.scrollToBottom();
			}
		};
		Thread t2 = new Thread(r2);
		t2.start();
	}

	private void populateWordsList(KanjiWords kanjiWords) {
		listOfWords.setWords(kanjiWords);
		kanjiWords.setList(listOfWords);
		kanjiWords.initialize();
	}

	private void initListOfWords() {

		listOfWords = new MyList<KanjiWords>(parent, Titles.wordsList,
				new RowInKanjiInformations(listOfWords), this);

		KanjiWords words = new KanjiWords(listOfWords);
		listOfWords.setWords(words);
		for (int i = 1; i <= 10; i++) {
			listOfWords.getWords().addRow("Word no. " + i, i, i);
		}
		listOfWords.getWords()
				.addRow("Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);", 11, 11);
	}

	private void initRepeatsList() {
		repeats = new MyList<RepeatingList>(parent, Titles.repeatedWordsList,
				new RowInRepeatingList(repeats), this);
		repeats.setWords(new RepeatingList(repeats));
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

	public void addWord() {
		parent.showInsertDialog(listOfWords);
	}

	public void searchWord() {
		parent.showSearchWordDialog(listOfWords);
	}

	public void startLearning() {
		parent.showLearningStartDialog(repeats,
				((KanjiWords) listOfWords.getWords()).getNumberOfKanjis());
	}

	public MyList getWordsList() {
		return listOfWords;
	}

	public MyList<RepeatingList> getRepeatsList() {
		return repeats;
	}

	public JMenuBar getMenu() {
		return menuBar;
	}

	public void save() {
		if (this.fileToSave == null) {
			return;
		}
		parent.changeSaveStatus(SavingStatus.SAVING);
		SavingInformation savingInformation = new SavingInformation(listOfWords.getWords(),
				repeats.getWords(), parent.getStartingController().getProblematicKanjis());
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
			List<KanjiInformation> list = listOfWords.getWords().getAllWords();
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
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showSaveDialog(this.parent.getContainer());
		if (option == 0) {
			this.fileToSave = fileChooser.getSelectedFile();
		}
		save();
	}

}
