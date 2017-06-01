package com.kanji.dialogs;

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
import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.Range;
import com.kanji.range.SetOfRanges;
import com.kanji.window.BaseWindow;
import com.sun.glass.events.KeyEvent;

public class RepeatingWordsPanel extends JPanel {
	private static final long serialVersionUID = 5557984078176822840L;
	private MyList words;
	private List<String> wordsToRepeat;
	private ExcelReader excel;
	private BaseWindow parent;
	private Set<Integer> problematicKanjis;
	private Set<Integer> currentProblematicKanjis;
	private String currentWord;
	private JLabel remainingLabel;
	private JLabel time;
	private double timeElapsed;
	private double interval = 0.1D;
	private String timeLabel = "Czas: ";
	private Thread timerThread;
	private boolean timerRunning;
	private JTextPane kanjiTextArea;
	private JTextPane wordTextArea;
	private JButton pauseOrResume;
	private JButton showWord;
	private JButton recognizedWord;
	private JButton notRecognizedWord;
	private MainPanel repeatingPanel;
	private final Color repeatingBackgroundColor = Color.white;
	private final Color windowBackgroundColor = BasicColors.OCEAN_BLUE;
	private int secondsLeft = 0;
	private int minutesLeft = 0;
	private int hoursLeft = 0;
	private MainPanel centerPanel;
	private MainPanel mainPanel;
	private int maxCharactersInRow = 15;

	private RepeatingInformation repeatInfo;

	public RepeatingWordsPanel(BaseWindow parent) {
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		excel = new ExcelReader();
		excel.load();
		currentProblematicKanjis = new HashSet<>();
		this.wordsToRepeat = new LinkedList();
		this.parent = parent;
		this.timerRunning = false;
		initialize();
		createPanel();
		mainPanel.addRow(
				RowMaker.createUnfilledRow(GridBagConstraints.CENTER, centerPanel.getPanel()));

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
		return new JButton[] { this.pauseOrResume, this.showWord };
	}

	private void createElementsForRepeatingPanel() {
		createWordLabel();
		createWordArea();
		createShowWordButton();
		createPauseOrResumeButton();
		createRecognizedWordButton();
		createNotRecognizedWordButton();
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

	private String pickRandomWord() {
		Random randomizer = new Random();
		int index = randomizer.nextInt(this.wordsToRepeat.size());
		this.currentWord = ((String) this.wordsToRepeat.get(index));
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
		return (String) this.wordsToRepeat.get(index) + " "
				+ ((KanjiWords) words.getWords()).getIdOfTheWord(this.currentWord);
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
		return new JButton[] { this.pauseOrResume, this.recognizedWord, this.notRecognizedWord };
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
		stopTimer();
		parent.showMessageDialog("Pauza", true);
		startTimer();

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
		this.wordsToRepeat.remove(this.currentWord);

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

		stopTimer();
		problematicKanjis.addAll(currentProblematicKanjis);

		this.parent.setProblematicKanjis(this.problematicKanjis);
		this.parent.showCardPanel(BaseWindow.LIST_PANEL);
		repeatInfo.setWasRepeated(true);
		repeatInfo.setTimeSpentOnRepeating(getTimePassed());
		parent.addToRepeatsList(repeatInfo);
		this.parent.save();
		parent.scrollToBottom();

		String message = Prompts.repeatingIsDonePrompt;
		message += Prompts.repeatingTimePrompt;
		message += getTimePassed();
		this.parent.showMessageDialog(message, true);
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
				RepeatingWordsPanel.this.parent.showCardPanel(BaseWindow.LIST_PANEL);
				RepeatingWordsPanel.this.stopTimer();
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
		startTimer();
		getNextWord();

		showWord.requestFocusInWindow();
		System.out.println("P: " + this.problematicKanjis);
	}

	public void reset() {
		this.timeElapsed = 0.0D;
		secondsLeft = 0;
		minutesLeft = 0;
		hoursLeft = 0;
		this.problematicKanjis = new HashSet();
		currentProblematicKanjis.clear();
		currentWord = "";
	}

	private void startTimer() {
		this.timerRunning = true;
		Runnable runnable = new Runnable() {
			public void run() {
				while (timerRunning) {
					System.out.println("running");
					RepeatingWordsPanel.this.timeElapsed += RepeatingWordsPanel.this.interval;
					if (timeElapsed >= 1) {
						timeElapsed = 0;
						secondsLeft++;
					}
					if (secondsLeft >= 60) {
						secondsLeft = 0;
						minutesLeft++;
					}
					if (minutesLeft >= 60) {
						minutesLeft = 0;
						hoursLeft++;
					}
					RepeatingWordsPanel.this.time
							.setText(RepeatingWordsPanel.this.timeLabel + getTimePassed());
					try {
						Thread.sleep((int) (RepeatingWordsPanel.this.interval * 1000));
					}
					catch (InterruptedException e) {
						parent.showMessageDialog(e.getMessage(), true);
					}
				}
			}
		};
		this.timerThread = new Thread(runnable);
		this.timerThread.start();
	}

	public String getTimePassed() {
		String hoursSuffix = adjustSuffixForHours();
		String minutesSuffix = adjustSuffixForMinutes();
		String secondsSuffix = adjustSuffixForSeconds();
		return hoursSuffix + ", " + minutesSuffix + ", " + secondsSuffix + ".";
	}

	private String adjustSuffixForHours() {
		return hoursLeft + " godzin" + adjustSuffix(hoursLeft);
	}

	private String adjustSuffixForMinutes() {
		return minutesLeft + " minut" + adjustSuffix(minutesLeft);
	}

	private String adjustSuffixForSeconds() {
		return secondsLeft + " sekund" + adjustSuffix(secondsLeft);
	}

	private String adjustSuffix(int timeValue) {
		int moduloRemainder = timeValue % 10;
		if (moduloRemainder > 1 && moduloRemainder < 5 && (timeValue < 10 || timeValue > 20)) {
			return "y";
		}
		else if (timeValue == 1) {
			return "a";
		}
		else if ((moduloRemainder >= 5 && moduloRemainder <= 9) || moduloRemainder == 0
				|| (timeValue >= 11 && timeValue <= 14)
				|| (moduloRemainder == 1 && timeValue >= 20)) {
			return "";
		}
		return "Nie wylapany if.";
	}

	private void stopTimer() {
		timerRunning = false;
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

}
