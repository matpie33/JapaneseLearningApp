package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.actions.TextAlignment;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Titles;
import com.kanji.controllers.RepeatingWordsController;
import com.kanji.windows.ApplicationWindow;

public class RepeatingWordsPanel extends AbstractPanelWithHotkeysInfo {

	private JButton recognizedWord;
	private JButton notRecognizedWord;
	private MainPanel repeatingPanel;
	private final Color repeatingBackgroundColor = Color.white;
	private JButton showPreviousWord;

	private JLabel time;
	private String timeLabelText = "Czas: ";
	private MainPanel centerPanel;
	private JLabel remainingLabel;

	private JTextPane kanjiTextArea;
	private JTextPane wordTextArea;
	private JButton pauseOrResume;
	private JButton showWord;
	private RepeatingWordsController repeatingWordsController;

	public RepeatingWordsPanel(ApplicationWindow applicationWindow) {
		this.repeatingWordsController = new RepeatingWordsController(applicationWindow, this);
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
	}

	// TODO maybe create an abstract class panel that return controller and some
	// more common things;
	public RepeatingWordsController getController() {
		return repeatingWordsController;
	}

	@Override
	void createElements() {
		JLabel titleLabel = new JLabel(Titles.repeatingWords);
		time = new JLabel(this.timeLabelText);
		remainingLabel = new JLabel(repeatingWordsController.createRemainingKanjisPrompt());
		JButton returnButton = createReturnButton();

		createElementsForRepeatingPanel();
		setButtonsToLearningAndAddThem();

		centerPanel.addRow(RowMaker.createUnfilledRow(Anchor.NORTH, titleLabel, time));
		centerPanel.addRow(RowMaker.createBothSidesFilledRow(repeatingPanel.getPanel()));
		centerPanel.addRow(RowMaker.createUnfilledRow(Anchor.SOUTH, remainingLabel, returnButton));
		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, centerPanel.getPanel()));
	}

	public void setButtonsToLearningAndAddThem() {
		addElementsToRepeatingPanel(showWordButtons(repeatingWordsController.previousWordExists()));
	}

	private JButton[] showWordButtons(boolean withPreviousWordButton) {
		if (withPreviousWordButton) {
			return new JButton[] { this.pauseOrResume, this.showWord, this.showPreviousWord };
		}
		else {
			return new JButton[] { this.pauseOrResume, this.showWord };
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

		showPreviousWord = createButtonWithHotkey(KeyEvent.VK_G, action,
				ButtonsNames.buttonShowPreviousWord, HotkeysDescriptions.SHOW_PREVIOUS_KANJI);
		showPreviousWord.setFocusable(false);

	}

	public void showKanji(String kanji) {
		kanjiTextArea.setText(repeatingWordsController.getCurrentKanji());
	}

	private void createWordDescriptionTextArea() {
		this.wordTextArea = new JTextPane();
	}

	private void createKanjiTextArea() {
		Font f = repeatingWordsController.getKanjiFont();
		kanjiTextArea = GuiElementsMaker.createTextPane("", TextAlignment.JUSTIFIED);
		kanjiTextArea.setFont(f);
		kanjiTextArea.setOpaque(false);
	}

	public void setButtonsToRecognizeWord(boolean withShowPreviousWordButton) {
		addElementsToRepeatingPanel(recognizeWordButtons(withShowPreviousWordButton));
	}

	private JButton[] recognizeWordButtons(boolean withShowPreviousWordButton) {
		if (withShowPreviousWordButton) {
			return new JButton[] { this.pauseOrResume, this.recognizedWord, this.notRecognizedWord,
					showPreviousWord };
		}
		else {
			return new JButton[] { this.pauseOrResume, this.recognizedWord,
					this.notRecognizedWord };
		}

	}

	private void createPauseOrResumeButton() {

		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedButtonPause();
			}
		};
		pauseOrResume = createButtonWithHotkey(KeyEvent.VK_P, a, ButtonsNames.buttonPause,
				HotkeysDescriptions.PAUSE);
	}

	private void createRecognizedWordButton() {
		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedRecognizedWordButton();
			}
		};
		recognizedWord = createButtonWithHotkey(KeyEvent.VK_SPACE, a,
				ButtonsNames.buttonRecognizedWordText,
				HotkeysDescriptions.SHOW_KANJI_OR_SET_KANJI_AS_KNOWN_KANJI);
	}

	private void createNotRecognizedWordButton() {

		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedNotRecognizedWordButton();
			}
		};
		notRecognizedWord = createButtonWithHotkey(KeyEvent.VK_A, a,
				ButtonsNames.buttonNotRecognizedText, HotkeysDescriptions.SET_KANJI_AS_PROBLEMATIC);
	}

	public void goToNextWord() {
		setButtonsToLearningAndAddThem();
		remainingLabel.setText(repeatingWordsController.createRemainingKanjisPrompt());
	}

	private void addElementsToRepeatingPanel(JButton[] buttons) {
		repeatingPanel.clear();
		repeatingPanel.addRow(RowMaker.createBothSidesFilledRow(wordTextArea));
		repeatingPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, kanjiTextArea));

		repeatingPanel
				.addRow(RowMaker.createHorizontallyFilledRow(buttons).fillHorizontallyEqually());

		mainPanel.getPanel().repaint();

	}

	private JButton createReturnButton() {
		JButton returnButton = new JButton(ButtonsNames.buttonGoBackText);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.pressedButtonReturn();
			}
		});
		returnButton.setFocusable(false);
		return returnButton;

	}

	private void createShowWordButton() {
		this.showWord = new JButton(ButtonsNames.buttonShowKanjiText);
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repeatingWordsController.presedButtonShowWord();
			}
		};
		showWord = createButtonWithHotkey(KeyEvent.VK_SPACE, action,
				ButtonsNames.buttonShowKanjiText, "");
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
		wordTextArea = GuiElementsMaker.createTextPane(word, alignment);
	}

}
