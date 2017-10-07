package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Titles;
import com.kanji.controllers.RepeatingWordsController;
import com.kanji.windows.ApplicationWindow;

public class RepeatingWordsPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton recognizedWord;
	private AbstractButton notRecognizedWord;
	private MainPanel repeatingPanel;
	private final Color repeatingBackgroundColor = Color.white;
	private AbstractButton showPreviousWord;

	private JLabel time;
	private String timeLabelText = "Czas: ";
	private MainPanel centerPanel;
	private JLabel remainingLabel;

	private JTextPane kanjiTextArea;
	private JTextPane wordTextArea;
	private AbstractButton pauseOrResume;
	private AbstractButton showWord;
	private RepeatingWordsController repeatingWordsController;
	private Font kanjiFont;

	public RepeatingWordsPanel(ApplicationWindow applicationWindow) {
		this.repeatingWordsController = new RepeatingWordsController(applicationWindow, this);
		kanjiFont = applicationWindow.getKanjiFont();
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
	}

	public RepeatingWordsController getController() {
		return repeatingWordsController;
	}

	@Override
	void createElements() {
		JLabel titleLabel = new JLabel(Titles.REPEATING_WORDS_DIALOG);
		time = new JLabel(this.timeLabelText);
		remainingLabel = new JLabel(repeatingWordsController.createRemainingKanjisPrompt());
		JButton returnButton = createReturnButton();

		createElementsForRepeatingPanel();
		setButtonsToLearningAndAddThem();

		centerPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH, titleLabel, time)
				.nextRow(FillType.BOTH, repeatingPanel.getPanel())
				.nextRow(FillType.NONE, Anchor.SOUTH, remainingLabel, returnButton));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, centerPanel.getPanel())
				.useAllExtraVerticalSpace());
	}

	public void setButtonsToLearningAndAddThem() {
		addElementsToRepeatingPanel(showWordButtons(repeatingWordsController.previousWordExists()));
	}

	private AbstractButton[] showWordButtons(boolean withPreviousWordButton) {
		if (withPreviousWordButton) {
			return new AbstractButton[] { this.pauseOrResume, this.showWord,
					this.showPreviousWord };
		}
		else {
			return new AbstractButton[] { this.pauseOrResume, this.showWord };
		}
	}

	private void createElementsForRepeatingPanel() {
		createWordDescriptionTextArea();
		createKanjiTextArea();
		createShowWordButton();
		createPauseOrResumeButton();
		createRecognizedWordButton();
		createNotRecognizedWordButton();
		createButtonGoToPreviousWord();
	}

	private void createButtonGoToPreviousWord() {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.goToPreviousWord();
			}
		};

		showPreviousWord = createButtonWithHotkey(KeyEvent.VK_G, action, ButtonsNames.PREVIOUS_WORD,
				HotkeysDescriptions.SHOW_PREVIOUS_KANJI);
		showPreviousWord.setFocusable(false);

	}

	public void showKanji(String kanji) {
		kanjiTextArea.setText(repeatingWordsController.getCurrentKanji());
	}

	private void createWordDescriptionTextArea() {
		this.wordTextArea = new JTextPane();
	}

	private void createKanjiTextArea() {
		kanjiTextArea = GuiMaker.createTextPane(new TextPaneOptions()
				.textAlignment(TextAlignment.JUSTIFIED).text("").enabled(false));
		kanjiTextArea.setFont(kanjiFont);
		kanjiTextArea.setOpaque(false);
	}

	public void setButtonsToRecognizeWord(boolean withShowPreviousWordButton) {
		addElementsToRepeatingPanel(recognizeWordButtons(withShowPreviousWordButton));
	}

	private AbstractButton[] recognizeWordButtons(boolean withShowPreviousWordButton) {
		if (withShowPreviousWordButton) {
			return new AbstractButton[] { this.pauseOrResume, this.recognizedWord,
					this.notRecognizedWord, showPreviousWord };
		}
		else {
			return new AbstractButton[] { this.pauseOrResume, this.recognizedWord,
					this.notRecognizedWord };
		}

	}

	private void createPauseOrResumeButton() {

		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedButtonPause();
			}
		};
		pauseOrResume = createButtonWithHotkey(KeyEvent.VK_P, a, ButtonsNames.PAUSE,
				HotkeysDescriptions.PAUSE);
	}

	private void createRecognizedWordButton() {
		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedRecognizedWordButton();
			}
		};
		recognizedWord = createButtonWithHotkey(KeyEvent.VK_SPACE, a, ButtonsNames.RECOGNIZED_WORD,
				HotkeysDescriptions.SHOW_KANJI_OR_SET_KANJI_AS_KNOWN_KANJI);
	}

	private void createNotRecognizedWordButton() {

		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedNotRecognizedWordButton();
			}
		};
		notRecognizedWord = createButtonWithHotkey(KeyEvent.VK_A, a, ButtonsNames.NOT_RECOGNIZED,
				HotkeysDescriptions.SET_KANJI_AS_PROBLEMATIC);
	}

	public void goToNextWord() {
		setButtonsToLearningAndAddThem();
		remainingLabel.setText(repeatingWordsController.createRemainingKanjisPrompt());
		wordTextArea.requestFocusInWindow();
	}

	private void addElementsToRepeatingPanel(AbstractButton[] buttons) {
		repeatingPanel.clear();
		repeatingPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, wordTextArea)
				.nextRow(FillType.NONE, Anchor.CENTER, kanjiTextArea)
				.nextRow(FillType.HORIZONTAL, buttons).fillHorizontallyEqually());
		mainPanel.getPanel().repaint();

	}

	private JButton createReturnButton() {
		JButton returnButton = new JButton(ButtonsNames.GO_BACK);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedButtonReturn();
			}
		});
		returnButton.setFocusable(false);
		return returnButton;

	}

	private void createShowWordButton() {
		this.showWord = new JButton(ButtonsNames.SHOW_KANJI);
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.presedButtonShowWord();
			}
		};
		showWord = createButtonWithHotkey(KeyEvent.VK_SPACE, action, ButtonsNames.SHOW_KANJI, "");
	}

	public void showCurrentKanjiAndShowAppropriateButtons() {
		setButtonsToRecognizeWord(repeatingWordsController.previousWordExists());
		kanjiTextArea.setText(repeatingWordsController.getCurrentKanji());
	}

	public void removeLastElementFromRow2() {
		repeatingPanel.removeLastElementFromRow(2); // TODO not remove,
		// just hide
	}

	public void updateTime(String timePassed) {
		time.setText(timeLabelText + timePassed);
	}

	public void clearKanji() {
		kanjiTextArea.setText("");
	}

	public void showWord(String word, TextAlignment alignment) {
		wordTextArea = GuiMaker.createTextPane(
				new TextPaneOptions().textAlignment(alignment).text(word).enabled(false));
	}

}
