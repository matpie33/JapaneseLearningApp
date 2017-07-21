package com.kanji.utilities;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
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
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.MenuTexts;
import com.kanji.constants.SavingStatus;
import com.kanji.constants.Titles;
import com.kanji.fileReading.CustomFileReader;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiInformations;
import com.kanji.myList.RowInRepeatingList;
import com.kanji.panels.LoadingPanel;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

public class ElementMaker {

	private CustomFileReader fileReader;
	private List<JButton> buttons;
	private ApplicationWindow parent;
	private Map<Integer, String> words;
	private MyList<KanjiWords> listOfWords;
	private MyList<RepeatingList> repeats;
	private JMenuBar menuBar;
	private File fileToSave;
	private JButton problematicKanjiButton;
	private boolean openingFile;

	private class MyDispatcher implements KeyEventDispatcher {

		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			return false;
		}
	}

	public ElementMaker(ApplicationWindow parent) {

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
		this.parent = parent;

		initElements();
		createShowProblematicKanjiButton();
		addListeners(buttons);

	}

	private void initElements() {
		fileReader = new CustomFileReader();
		buttons = new ArrayList<JButton>();
		for (String name : ButtonsNames.mainPageButtonNames)
			buttons.add(new JButton(name));
		initListOfWords();
		initRepeatsList();
		createMenu();
	}

	private void createShowProblematicKanjiButton() {
		problematicKanjiButton = new JButton(ButtonsNames.buttonShowProblematicKanji);
		problematicKanjiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parent.showProblematicKanjiDialog();
			}
		});
	}

	public JButton getProblematicKanjiButton() {
		return problematicKanjiButton;
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
		if (!openingFile) {
			openingFile = true;
		}
		fileToSave = openFile();
		if (!fileToSave.exists())
			return;

		try {

			final FileInputStream fout = new FileInputStream(fileToSave);
			final ObjectInputStream oos = new ObjectInputStream(fout);
			// TODO we cannot rename com.kanji.row package to lower case - it
			// breaks loading from file - fix that
			listOfWords.updateWords();
			final Object readed = oos.readObject();
			final Object read = oos.readObject();

			final ApplicationWindow b = (ApplicationWindow) parent;

			Set<Integer> problematics;
			try {
				problematics = (Set<Integer>) oos.readObject();
				b.setProblematicKanjis(problematics);
			}
			catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			final LoadingPanel bar = b.showProgressDialog();
			b.updateTitle(fileToSave.toString());
			b.changeSaveStatus(SavingStatus.NO_CHANGES);
			// b.repaint();

			SwingWorker s = new SwingWorker<Void, Integer>() {

				private JProgressBar progress;

				@Override
				public Void doInBackground() {

					// b.updateLeft();
					progress = new JProgressBar();
					progress.setIndeterminate(false);
					if (readed instanceof KanjiWords) {
						System.out.println("here is performed");
						bar.setProgressBar(progress);
						final KanjiWords wordss = (KanjiWords) readed;
						progress.setMaximum(wordss.getNumberOfKanjis());
						listOfWords.setWords(wordss);
						wordss.setList(listOfWords);
						wordss.initialize();
						List<KanjiInformation> wordsList = listOfWords.getWords().getAllWords();
						KanjiWords kanjiWords = listOfWords.getWords();
						List<Integer> ints = new ArrayList<>();
						for (int i = 0; i < kanjiWords.getNumberOfKanjis(); i++) {
							kanjiWords.addRow(wordsList.get(i), i + 1);
							process(ints);
							ints.add(1);
						}
					}
					else {
						Map<Integer, String> map = (Map<Integer, String>) readed;
						listOfWords.setWords(new KanjiWords(listOfWords));
						int i = 1;
						for (Map.Entry<Integer, String> entry : map.entrySet()) {
							listOfWords.getWords().addRow(entry.getValue(), entry.getKey(), i);
							i++;
						}
					}

					return null;
				}

				@Override
				public void process(List<Integer> something) {
					progress.setValue(something.size());
				}

				@Override
				public void done() {

					b.closeDialog();
					listOfWords.repaint();
					listOfWords.scrollToBottom();
					// b.repaint();
					try {
						fout.close();
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			s.execute();

			Runnable r2 = new Runnable() {
				@Override
				public void run() {
					if (read instanceof RepeatingList) {
						RepeatingList mapOfRepeats = (RepeatingList) read;

						repeats.setWords(mapOfRepeats);
						mapOfRepeats.setList(repeats);
						mapOfRepeats.initialize();
						repeats.getWords().addAll();
					}
					else {
						Map<Integer, String> map = (Map<Integer, String>) read;

						for (Map.Entry<Integer, String> entry : map.entrySet()) {
							repeats.getWords().add(entry.getValue(),
									new Date(((long) entry.getKey()) * 1000L), false);
						}
						repeats.setWords(new RepeatingList(repeats));
					}
					repeats.repaint();
					repeats.scrollToBottom();
				}
			};
			Thread t2 = new Thread(r2);
			t2.start();

		}
		catch (IOException | ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally {
			openingFile = false;
		}

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

	private void addListeners(List<JButton> buttons) {

		for (JButton button : buttons) {
			switch (button.getText()) {
			case ButtonsNames.buttonOpenText:
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadWordsListFromFile();
					}
				});
				break;
			case ButtonsNames.buttonAddText:
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addWord();
					}
				});
				break;
			case ButtonsNames.buttonSearchText:
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						searchWord();
					}
				});
				break;
			case ButtonsNames.buttonStartText:
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						start();
					}
				});
				break;
			case ButtonsNames.buttonSaveText:
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showSaveDialog();
					}
				});
				break;
			case ButtonsNames.buttonSaveListText:
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						exportList();
					}
				});
				break;
			}

		}
	}

	private void loadWordsListFromFile() {
		File file = openFile();
		if (!file.exists())
			return;
		words = tryToReadWordsFromFile(file);
		loadWordsListInNewThread();
	}

	private File openFile() {
		JFileChooser fileChooser = new JFileChooser();
		String directory;
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
		return file;
	}

	private void loadWordsListInNewThread() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				DialogWindow d = new DialogWindow(parent);
				d.showMsgDialog("Wait");
				listOfWords.updateWords();
				listOfWords.setWords(new KanjiWords(listOfWords));
				int i = 1;
				for (Map.Entry<Integer, String> entry : words.entrySet()) {

					listOfWords.getWords().addRow(entry.getValue(), entry.getKey(), i);
					i++;
				}
				d.getContainer().dispose();
			}
		};
		Thread t = new Thread(r);
		t.start();
	}

	private Map<Integer, String> tryToReadWordsFromFile(File file) {
		try {
			words = fileReader.readFile(file);

		}
		catch (Exception e) {
			parent.showMsgDialog(e.getMessage());
			words = new HashMap<Integer, String>();
		}
		return words;
	}

	private void addWord() {
		parent.showInsertDialog(listOfWords);
	}

	private void searchWord() {
		parent.showSearchWordDialog(listOfWords);
	}

	private void start() {
		System.out.println(listOfWords);
		parent.showLearningStartDialog(repeats,
				((KanjiWords) listOfWords.getWords()).getNumberOfKanjis());
		System.out.println(((KanjiWords) listOfWords.getWords()).getNumberOfKanjis());
	}

	public List<JButton> getButtons() {
		return buttons;
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
		ApplicationWindow p = null;
		if (parent instanceof ApplicationWindow) {
			p = (ApplicationWindow) parent;
			p.changeSaveStatus(SavingStatus.SAVING);
		}
		else
			return;
		try {
			if (this.fileToSave == null) {
				System.out.println("no save");
				return;
			}
			FileOutputStream fout = new FileOutputStream(this.fileToSave);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(this.listOfWords.getWords());
			oos.writeObject(this.repeats.getWords());
			if ((this.parent instanceof ApplicationWindow)) {
				ApplicationWindow b = (ApplicationWindow) this.parent;
				oos.writeObject(b.getProblematicKanjis());
			}
			System.out.println("saved");
			fout.close();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		p.changeSaveStatus(SavingStatus.SAVED);
	}

	private void exportList() {
		File f = new File("list.txt");
		try {
			// Map<Integer, String> words = this.listOfWords.getWords();
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

	private void showSaveDialog() {
		JFileChooser j = new JFileChooser();
		int option = j.showSaveDialog(this.parent.getContainer());
		if (option == 0) {
			this.fileToSave = j.getSelectedFile();
		}
		save();
	}

}
