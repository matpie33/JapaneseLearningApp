package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.row.KanjiWords;
import com.kanji.row.RepeatingInformation;
import com.kanji.timer.TimeSpentHandler;
import com.kanji.timer.TimeSpentMonitor;
import com.kanji.windows.ApplicationWindow;
import com.sun.glass.events.KeyEvent;

public class RepeatingWordsPanel extends JPanel implements TimeSpentMonitor { // TODO
																				// don't
																				// extend
																				// jpanel
	private static final long serialVersionUID = 5557984078176822840L;
	private MyList words;
	private List<String> wordsToRepeat;
	private ExcelReader excel;
	private ApplicationWindow parent;
	private Set<Integer> problematicKanjis;
	private Set<Integer> currentProblematicKanjis;
	private String currentWord;
	private JLabel remainingLabel;
	private JLabel time;
	private String timeLabel = "Czas: ";
	private JTextPane kanjiTextArea;
	private JTextPane wordTextArea;
	private JButton pauseOrResume;
	private JButton showWord;
	private JButton recognizedWord;
	private JButton notRecognizedWord;
	private MainPanel repeatingPanel;
	private final Color repeatingBackgroundColor = Color.white;
	private final Color windowBackgroundColor = BasicColors.OCEAN_BLUE;
	private MainPanel centerPanel;
	private MainPanel mainPanel;
	private int maxCharactersInRow = 15;
	private JButton showPreviousWord;
	private String previousWord = "";
	private TimeSpentHandler timeSpentHandler;

	private RepeatingInformation repeatInfo;

	public RepeatingWordsPanel(ApplicationWindow parent) {
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		excel = new ExcelReader();
		excel.load();
		currentProblematicKanjis = new HashSet<>();
		this.wordsToRepeat = new LinkedList();
		this.parent = parent;
		initialize();
		createPanel();
		mainPanel.addRow(
				RowMaker.createUnfilledRow(GridBagConstraints.CENTER, centerPanel.getPanel()));
		timeSpentHandler = new TimeSpentHandler(this);

	}

	private void initialize() {
		// setLayout(new GridBagLayout());
		setBackground(this.windowBackgroundColor);
		this.repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
	}

	private void createPanel() {
		JLabel titleLabel = new JLabel(Titles.repeatingWordsTitle);
		this.time = new JLabel(this.timeLabel);
		centerPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.NORTH, titleLabel, time));
		// if (!this.wordsToRepeat.isEmpty()) {
		initiateRepeatingPanel();
		// }
		addButtons();
		// add(main.getPanel());
	}

	private void initiateRepeatingPanel() {
		createElementsForRepeatingPanel();
		setButtonsToLearningAndAddThem();
		centerPanel.addRow(RowMaker.createBothSidesFilledRow(repeatingPanel.getPanel()));
	}

	private void setButtonsToLearningAndAddThem() {
		addElementsToRepeatingPanel(showWordButtons());
	}

	private JButton[] showWordButtons() {
		if (!previousWord.isEmpty()) {
			return new JButton[] { this.pauseOrResume, this.showWord, this.showPreviousWord };
		}
		else {
			return new JButton[] { this.pauseOrResume, this.showWord };
		}

	}

	private void createElementsForRepeatingPanel() {
		createWordLabel();
		createWordArea();
		createShowWordButton();
		createPauseOrResumeButton();
		createRecognizedWordButton();
		createNotRecognizedWordButton();
		createButtonGoToPreviousWord();
	}

	private void createButtonGoToPreviousWord() {
		showPreviousWord = new JButton(ButtonsNames.buttonShowPreviousWord);
		showPreviousWord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				showWord(previousWord);
				currentWord = previousWord;
				removeWordFromCurrentProblematics();
				showKanji();
				setButtonsToRecognizeWord();
				repeatingPanel.removeLastElementFromRow(2);
			}
		});
	}

	private void createWordLabel() {
		this.wordTextArea = new JTextPane();
		wordTextArea.setEditable(false);
		StyledDocument doc = wordTextArea.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}

	private void createWordArea() {
		Font f = new Font(this.excel.getFontName(), 1, 80);

		this.kanjiTextArea = new JTextPane();
		kanjiTextArea.setEditable(false);
		kanjiTextArea.setFont(f);
		StyledDocument doc = wordTextArea.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
	}

	private void pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(this.wordsToRepeat.size());
		this.currentWord = ((String) this.wordsToRepeat.get(index));
		showWord(currentWord);
	}

	private void showWord(String currentWord) {
		wordTextArea.setText(currentWord);
		if (currentWord.length() > maxCharactersInRow) {
			StyledDocument doc = wordTextArea.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_JUSTIFIED);
			wordTextArea.setStyledDocument(doc);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}
		else {
			StyledDocument doc = wordTextArea.getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			wordTextArea.setStyledDocument(doc);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}
	}

	private void createShowWordButton() {
		this.showWord = new JButton(ButtonsNames.buttonShowKanjiText);
		this.showWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pauseOrResume.getText().equals(ButtonsNames.buttonResumeText)) {
					return;
				}
				RepeatingWordsPanel.this.setButtonsToRecognizeWord();
				RepeatingWordsPanel.this.showKanji();
			}
		});
	}

	private void setButtonsToRecognizeWord() {
		addElementsToRepeatingPanel(recognizeWordButtons());
	}

	private JButton[] recognizeWordButtons() {
		if (!previousWord.isEmpty()) {
			return new JButton[] { this.pauseOrResume, this.recognizedWord, this.notRecognizedWord,
					showPreviousWord };
		}
		else {
			return new JButton[] { this.pauseOrResume, this.recognizedWord,
					this.notRecognizedWord };
		}

	}

	private void showKanji() {
		System.out.println("current word: " + currentWord);
		this.kanjiTextArea.setText(this.excel
				.getKanjiById(((KanjiWords) words.getWords()).getIdOfTheWord(this.currentWord)));
	}

	private void createPauseOrResumeButton() {
		this.pauseOrResume = new JButton("Pauza");
		pauseOrResume.setFocusable(false);
		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				pauseOrResume();
			}
		};
		pauseOrResume.addActionListener(a);
		pauseOrResume.getInputMap(2).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "pressed");
		pauseOrResume.getActionMap().put("pressed", a);
	}

	private void pauseOrResume() {
		timeSpentHandler.stopTimer();
		parent.showMsgDialog("Pauza");
		timeSpentHandler.startTimer();

	}

	private void createRecognizedWordButton() {
		this.recognizedWord = new JButton(ButtonsNames.buttonRecognizedWordText);
		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (pauseOrResume.getText().equals(ButtonsNames.buttonResumeText)) {
					return;
				}
				removeWordIfItsProblematic();
				getNextWord();

				System.out.println("problematics: " + RepeatingWordsPanel.this.problematicKanjis);
			}
		};
		this.recognizedWord.addActionListener(a);

		this.recognizedWord.getInputMap(2).put(KeyStroke.getKeyStroke(32, 0), "pressed");
		this.recognizedWord.getActionMap().put("pressed", a);
	}

	private void removeWordFromCurrentProblematics() {
		int id = getCurrentWordId();
		currentProblematicKanjis.remove(Integer.valueOf(id));
	}

	private void removeWordIfItsProblematic() {
		int id = getCurrentWordId();
		problematicKanjis.remove(Integer.valueOf(id));
	}

	private int getCurrentWordId() {
		return ((KanjiWords) words.getWords()).getIdOfTheWord(this.currentWord);
	}

	private void createNotRecognizedWordButton() {
		this.notRecognizedWord = new JButton(ButtonsNames.buttonNotRecognizedText);

		AbstractAction a = new AbstractAction() {
			private static final long serialVersionUID = 5973495342336199749L;

			public void actionPerformed(ActionEvent e) {
				if (pauseOrResume.getText().equals(ButtonsNames.buttonResumeText)) { // TODO
					// add
					// state:
					// paused
					// or
					// not
					return;
				}
				addToProblematic();
				getNextWord();
			}
		};
		this.notRecognizedWord.addActionListener(a);
		this.notRecognizedWord.getInputMap(2).put(KeyStroke.getKeyStroke(65, 0), "pressed");
		this.notRecognizedWord.getActionMap().put("pressed", a);
	}

	private void addToProblematic() {
		int num = getCurrentWordId();
		System.out.println(num);
		this.currentProblematicKanjis.add(Integer.valueOf(num));
	}

	private void getNextWord() {
		String word = this.wordTextArea.getText();
		this.wordsToRepeat.remove(currentWord);
		previousWord = currentWord;

		System.out.println("removed: " + word);
		if (!this.wordsToRepeat.isEmpty()) {
			setButtonsToLearningAndAddThem();
			pickRandomWord();
			this.kanjiTextArea.setText("");
		}
		else {
			displayFinishMessageAndStopTimer();
		}
		this.remainingLabel.setText(createRemainingPrompt());
		this.showWord.requestFocusInWindow();
	}

	private void displayFinishMessageAndStopTimer() {

		timeSpentHandler.stopTimer();
		problematicKanjis.addAll(currentProblematicKanjis);

		this.parent.setProblematicKanjis(this.problematicKanjis);
		this.parent.showCardPanel(ApplicationWindow.LIST_PANEL);
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(timeSpentHandler.getTimePassed());
		parent.addToRepeatsList(repeatInfo);
		this.parent.save();
		parent.scrollToBottom();

		String message = Prompts.repeatingIsDonePrompt;
		message += Prompts.repeatingTimePrompt;
		message += timeSpentHandler.getTimePassed();
		this.parent.showMsgDialog(message);
		System.out.println("done");
		if (currentProblematicKanjis.size() > 0)
			parent.showProblematicKanjiDialog((KanjiWords) words.getWords(),
					currentProblematicKanjis);
	}

	private void addElementsToRepeatingPanel(JButton[] buttons) {
		repeatingPanel.clear();
		repeatingPanel.addRow(RowMaker.createBothSidesFilledRow(wordTextArea));
		repeatingPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, kanjiTextArea));

		repeatingPanel
				.addRow(RowMaker.createHorizontallyFilledRow(buttons).fillHorizontallyEqually());

		repaint();

	}

	private void addButtons() {
		JButton returnButton = new JButton(ButtonsNames.buttonGoBackText);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RepeatingWordsPanel.this.parent.showCardPanel(ApplicationWindow.LIST_PANEL);
				timeSpentHandler.stopTimer();
			}
		});

		this.remainingLabel = new JLabel(createRemainingPrompt());

		centerPanel.addRow(
				RowMaker.createUnfilledRow(GridBagConstraints.SOUTH, remainingLabel, returnButton));
	}

	private String createRemainingPrompt() {
		return Prompts.remainingKanjiPrompt + " " + this.wordsToRepeat.size() + " "
				+ Prompts.kanjiPrompt;
	}

	public void setRepeatingWords(MyList wordsList) {
		this.wordsToRepeat = new LinkedList();
		this.words = wordsList;
	}

	public void setRangesToRepeat(SetOfRanges ranges) {
		for (Range range : ranges.getRangesAsList()) {
			for (int i = range.getRangeStart(); i <= range.getRangeEnd(); i++) {
				wordsToRepeat.add(words.findWordInRow(i - 1));
			}
		}
	}

	public void setProblematicKanjis(Set<Integer> problematicKanjis) {
		this.problematicKanjis = problematicKanjis;
		System.out.println("start");
		for (Iterator localIterator = problematicKanjis.iterator(); localIterator.hasNext();) {
			int i = ((Integer) localIterator.next()).intValue();
			String word = ((KanjiWords) words.getWords()).getWordForId(i);

			if (!this.wordsToRepeat.contains(word)) {
				this.wordsToRepeat.add(word);

			}
		}
		System.out.println("done");
	}

	public void startRepeating() {
		// removeAll();
		// createPanel();
		// pickRandomWord();
		revalidate();
		repaint();
		timeSpentHandler.startTimer();
		getNextWord();

		showWord.requestFocusInWindow();
		System.out.println("P: " + this.problematicKanjis);
	}

	public void reset() {
		timeSpentHandler.reset();
		this.problematicKanjis = new HashSet();
		currentProblematicKanjis.clear();
		currentWord = "";
	}

	public void setExcelReader(ExcelReader excel) {
		this.excel = excel;
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatInfo = info;
	}

	public JPanel getPanel() {
		return mainPanel.getPanel();
	}

	public void updateTime(String timePassed) {
		time.setText(timeLabel + timePassed);
	}

}
