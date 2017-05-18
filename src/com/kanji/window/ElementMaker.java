package com.kanji.window;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.EOFException;
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
import javax.swing.SwingWorker;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingList;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.MenuTexts;
import com.kanji.constants.Titles;
import com.kanji.dialogs.MyDialog;
import com.kanji.fileReading.CustomFileReader;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiInformations;
import com.kanji.myList.RowInRepeatingList;

public class ElementMaker {

	private CustomFileReader fileReader;
	private List<JButton> buttons;
	private ClassWithDialog parent;
	private Map<Integer, String> words;
	private MyList<KanjiWords> listOfWords;
	private MyList<RepeatingList> repeats;
	private JMenuBar menuBar;
	private File fileToSave;
	private SavingStatus savingStatus;

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {

			if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown())
				searchWord();
			return false;
		}
	}

	public ElementMaker(ClassWithDialog parent) {

		savingStatus = SavingStatus.NO_CHANGES;
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
		this.parent = parent;

		initElements();
		addListeners(buttons);

	}

	private void initElements() {
		fileReader = new CustomFileReader();
		buttons = new ArrayList<JButton>();
		for (String name : ButtonsNames.buttonNames)
			buttons.add(new JButton(name));
		initListOfWords();
		initRepeatsList();
		createMenu();
	}

	private void createMenu() {
		menuBar = new JMenuBar();
		menuBar.setBackground(Color.orange);
		JMenu menu = new JMenu(MenuTexts.menuBarFile);
		menuBar.add(menu);
		JMenuItem item = new JMenuItem(MenuTexts.menuOpen);

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileToSave = openFile();
				if (!fileToSave.exists())
					return;

				try {

					FileInputStream fout = new FileInputStream(fileToSave);
					ObjectInputStream oos = new ObjectInputStream(fout);
					listOfWords.updateWords();
					final Object readed = oos.readObject();

					SwingWorker s = new SwingWorker<Void, Void>() {
						@Override
						public Void doInBackground() {

							// b.updateLeft();
							if (readed instanceof KanjiWords) {
								final KanjiWords wordss = (KanjiWords) readed;
								listOfWords.setWords(wordss);
								wordss.setList(listOfWords);
								wordss.initialize();
								listOfWords.getWords().addAll();
							}
							else {
								Map<Integer, String> map = (Map<Integer, String>) readed;
								listOfWords.setWords(new KanjiWords(listOfWords));
								int i = 1;
								for (Map.Entry<Integer, String> entry : map.entrySet()) {
									listOfWords.getWords().addRow(entry.getValue(), entry.getKey(),
											i);
									i++;
								}
							}

							return null;
						}

						@Override
						public void done() {
							BaseWindow b = (BaseWindow) parent;
							listOfWords.repaint();
							listOfWords.scrollToBottom();

							b.closeDialog();
						}
					};
					s.execute();
					BaseWindow b = (BaseWindow) parent;
					b.showMessageDialog("loading", true);
					b.updateTitle(fileToSave.toString());
					b.changeSaveStatus(SavingStatus.NO_CHANGES);

					final Object read = oos.readObject();

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

					if (parent instanceof BaseWindow) {
						try {
							Set<Integer> problematics = (Set<Integer>) oos.readObject();
							BaseWindow p = (BaseWindow) parent;
							p.setProblematicKanjis(problematics);
						}
						catch (EOFException localEOFException) {
						}
					}

					fout.close();
				}
				catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		menu.add(item);
	}

	private void initListOfWords() {

		listOfWords = new MyList<KanjiWords>(parent, Titles.wordsListTitle,
				new RowInKanjiInformations(listOfWords), this);

		KanjiWords words = new KanjiWords(listOfWords);
		listOfWords.setWords(words);
		for (int i = 1; i <= 10; i++) {
			listOfWords.getWords().addRow("Word no. " + i, i, i);

		}
	}

	private void initRepeatsList() {
		repeats = new MyList<RepeatingList>(parent, Titles.repeatedWordsListTitle,
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
		int chosenOption = fileChooser.showOpenDialog(parent);
		if (chosenOption == JFileChooser.CANCEL_OPTION)
			return new File("");
		File file = fileChooser.getSelectedFile();
		return file;
	}

	private void loadWordsListInNewThread() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				MyDialog d = new MyDialog(parent);
				d.showErrorDialogInNewWindow("Wait");
				listOfWords.updateWords();
				listOfWords.setWords(new KanjiWords(listOfWords));
				int i = 1;
				for (Map.Entry<Integer, String> entry : words.entrySet()) {

					listOfWords.getWords().addRow(entry.getValue(), entry.getKey(), i);
					i++;
				}
				d.dispose();
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
			parent.showMessageDialog(e.getMessage(), false);
			words = new HashMap<Integer, String>();
		}
		return words;
	}

	private void addWord() {
		parent.showDialogToAddWord(listOfWords);
	}

	private void searchWord() {
		parent.showDialogToSearch(listOfWords);
	}

	private void start() {
		System.out.println(listOfWords);
		parent.showLearnStartDialog(repeats, ((KanjiWords) listOfWords.getWords()).getKanjis());
		System.out.println(((KanjiWords) listOfWords.getWords()).getKanjis());
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
		BaseWindow p = null;
		if (parent instanceof BaseWindow) {
			p = (BaseWindow) parent;
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
			if ((this.parent instanceof BaseWindow)) {
				BaseWindow b = (BaseWindow) this.parent;
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
		int option = j.showSaveDialog(this.parent);
		if (option == 0) {
			this.fileToSave = j.getSelectedFile();
		}
		save();
	}

}
