package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.actions.TextAlignment;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Titles;

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
	private RepeatingWordsController controller;

	public RepeatingWordsPanel(RepeatingWordsController controller) {
		this.controller = controller;
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		this.repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
		createGraphicElementsAndAddThem();

	}

	@Override
	void createElements() {
	}

	private void createGraphicElementsAndAddThem() {
		JLabel titleLabel = new JLabel(Titles.repeatingWords);
		this.time = new JLabel(this.timeLabelText);
		centerPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.NORTH, titleLabel, time));
		initiateRepeatingPanel();
		this.remainingLabel = new JLabel(controller.createRemainingKanjisPrompt());
		JButton returnButton = createReturnButton();
		centerPanel.addRow(
				RowMaker.createUnfilledRow(GridBagConstraints.SOUTH, remainingLabel, returnButton));
		mainPanel.addRow(
				RowMaker.createUnfilledRow(GridBagConstraints.CENTER, centerPanel.getPanel()));
	}

	private void initiateRepeatingPanel() {
		createElementsForRepeatingPanel();
		setButtonsToLearningAndAddThem();
		centerPanel.addRow(RowMaker.createBothSidesFilledRow(repeatingPanel.getPanel()));
	}

	public void setButtonsToLearningAndAddThem() {
		addElementsToRepeatingPanel(showWordButtons(controller.previousWordExists()));
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
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.goToPreviousWord();
				setButtonsToRecognizeWord(controller.previousWordExists());
				kanjiTextArea.setText(controller.getCurrentKanji());
			}
		};

		showPreviousWord = createButtonWithHotkey(KeyEvent.VK_G, action,
				ButtonsNames.buttonShowPreviousWord, HotkeysDescriptions.SHOW_PREVIOUS_KANJI);

	}

	private void createWordLabel() {
		this.wordTextArea = new JTextPane();
	}

	private void createWordArea() {
		Font f = controller.getKanjiFont();
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
				controller.pressedButtonPause();
			}
		};
		pauseOrResume = createButtonWithHotkey(KeyEvent.VK_P, a, ButtonsNames.buttonPause,
				HotkeysDescriptions.PAUSE);
		pauseOrResume.setFocusable(false);
	}

	private void createRecognizedWordButton() {
		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				controller.pressedRecognizedWordButton();
			}
		};
		recognizedWord = createButtonWithHotkey(KeyEvent.VK_SPACE, a,
				ButtonsNames.buttonRecognizedWordText,
				HotkeysDescriptions.SHOW_KANJI_OR_SET_KANJI_AS_KNOWN_KANJI);
	}

	private void createNotRecognizedWordButton() {

		AbstractAction a = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				controller.pressedNotRecognizedWordButton();
			}
		};
		notRecognizedWord = createButtonWithHotkey(KeyEvent.VK_A, a,
				ButtonsNames.buttonNotRecognizedText, HotkeysDescriptions.SET_KANJI_AS_PROBLEMATIC);
	}

	public void goToNextWord() {
		setButtonsToLearningAndAddThem();
		remainingLabel.setText(controller.createRemainingKanjisPrompt());
		showWord.requestFocusInWindow();
	}

	private void addElementsToRepeatingPanel(JButton[] buttons) {
		repeatingPanel.clear();
		repeatingPanel.addRow(RowMaker.createBothSidesFilledRow(wordTextArea));
		repeatingPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, kanjiTextArea));

		repeatingPanel
				.addRow(RowMaker.createHorizontallyFilledRow(buttons).fillHorizontallyEqually());
		// TODO need refactor : dont remove and add each row each time; instead
		// try "hide and show"

		mainPanel.getPanel().repaint();

	}

	private JButton createReturnButton() {
		JButton returnButton = new JButton(ButtonsNames.buttonGoBackText);
		returnButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.pressedButtonReturn();
			}
		});
		returnButton.setFocusable(false);
		return returnButton;

	}

	private void createShowWordButton() {
		this.showWord = new JButton(ButtonsNames.buttonShowKanjiText);
		this.showWord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.presedButtonShowWord();
			}
		});
	}

	public void showCurrentKanji() {
		setButtonsToRecognizeWord(controller.previousWordExists());
		kanjiTextArea.setText(controller.getCurrentKanji());
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

	public void requestFocusForShowWord() {
		showWord.requestFocusInWindow();
	}

}
