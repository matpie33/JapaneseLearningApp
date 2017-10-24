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
import javax.swing.text.JTextComponent;

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
import com.kanji.constants.Labels;
import com.kanji.constants.Titles;
import com.kanji.controllers.RepeatingWordsController;
import com.kanji.utilities.RepeatingState;
import com.kanji.windows.ApplicationWindow;

public class RepeatingWordsPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton showKanjiOrRecognizeWord;
	private AbstractButton notRecognizedWord;
	private MainPanel repeatingPanel;
	private final Color repeatingBackgroundColor = Color.white;
	private AbstractButton showPreviousWord;

	private JLabel time;
	private MainPanel centerPanel;
	private JLabel remainingLabel;

	private JTextComponent kanjiTextArea;
	private JTextComponent wordTextArea;
	private AbstractButton pauseOrResume;
	private RepeatingWordsController repeatingWordsController;
	private Font kanjiFont;
	private RepeatingState repeatingState;

	public RepeatingWordsPanel(ApplicationWindow applicationWindow) {
		this.repeatingWordsController = new RepeatingWordsController(applicationWindow, this);
		kanjiFont = applicationWindow.getKanjiFont();
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
		repeatingPanel.setBorder(getDefaultBorder());
		repeatingState = RepeatingState.WORD_NOT_SHOWING;
	}

	public RepeatingWordsController getController() {
		return repeatingWordsController;
	}

	@Override
	void createElements() {
		JLabel titleLabel = new JLabel(Titles.REPEATING_WORDS_DIALOG);
		time = new JLabel();
		remainingLabel = new JLabel(repeatingWordsController.createRemainingKanjisPrompt());
		JButton returnButton = createReturnButton();

		createElementsForRepeatingPanel();
		setButtonsToLearningAndAddThem();
		AbstractButton [] navigationButtons = new AbstractButton[] { this.pauseOrResume,
				showKanjiOrRecognizeWord, notRecognizedWord, this.showPreviousWord };
		repeatingPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, wordTextArea).setColor(BasicColors.GREY)
				.nextRow(FillType.NONE, Anchor.CENTER, kanjiTextArea).disableBorder()
				.nextRow(FillType.HORIZONTAL, navigationButtons).fillHorizontallyEqually().disableBorder());
		mainPanel.getPanel().repaint();

		centerPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH, titleLabel, time)
				.nextRow(FillType.BOTH, repeatingPanel.getPanel()).setBorder(getDefaultBorder())
				.nextRow(FillType.NONE, Anchor.CENTER, remainingLabel, returnButton));
		//TODO in gui maker enable me to put some element in some anchor so that remaining label can be positioned vertically center
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, centerPanel.getPanel())
				.useAllExtraVerticalSpace());
	}

	public void setButtonsToLearningAndAddThem() {
		showKanjiOrRecognizeWord.setText(ButtonsNames.SHOW_KANJI);
		setVisibilityOfRecognizingButtons(false);
		showPreviousWord.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void setVisibilityOfRecognizingButtons (boolean visibility){
		notRecognizedWord.setEnabled(visibility);
	}

	private void createElementsForRepeatingPanel() {
		createWordDescriptionTextArea();
		createKanjiTextArea();
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
				showPreviousWord.setEnabled(false);
				repeatingState = RepeatingState.WORD_IS_SHOWING;
			}
		};

		showPreviousWord = createButtonWithHotkey(KeyEvent.VK_G, action, ButtonsNames.PREVIOUS_WORD,
				HotkeysDescriptions.SHOW_PREVIOUS_KANJI);
		showPreviousWord.setFocusable(false);

	}

	private void createWordDescriptionTextArea() {
		wordTextArea = GuiMaker.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.CENTERED).text("").enabled(false));
	}

	private void createKanjiTextArea() {
		kanjiTextArea = GuiMaker.createTextPane(new TextPaneOptions()
				.textAlignment(TextAlignment.JUSTIFIED).text("").enabled(false));
		kanjiTextArea.setFont(kanjiFont);
		kanjiTextArea.setOpaque(false);
	}

	public void setButtonsToRecognizeWord() {
		showKanjiOrRecognizeWord.setText(ButtonsNames.RECOGNIZED_WORD);
		setVisibilityOfRecognizingButtons(true);
		showPreviousWord.setEnabled(repeatingWordsController.previousWordExists());
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
				if (repeatingState == RepeatingState.WORD_IS_SHOWING){
					repeatingWordsController.pressedRecognizedWordButton();
				}
				else{
					repeatingWordsController.presedButtonShowWord();
					repeatingState = RepeatingState.WORD_IS_SHOWING;
					showKanjiOrRecognizeWord.setText(ButtonsNames.RECOGNIZED_WORD);
				}
			}
		};
		showKanjiOrRecognizeWord = createButtonWithHotkey(KeyEvent.VK_SPACE, a, ButtonsNames.SHOW_KANJI,
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
		repeatingState = RepeatingState.WORD_NOT_SHOWING;
		showKanjiOrRecognizeWord.setText(ButtonsNames.SHOW_KANJI);
		showPreviousWord.setEnabled(true);
		setButtonsToLearningAndAddThem();
		remainingLabel.setText(repeatingWordsController.createRemainingKanjisPrompt());
		wordTextArea.requestFocusInWindow();
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


	public void showCurrentKanjiAndShowAppropriateButtons() {
		setButtonsToRecognizeWord();

		kanjiTextArea.setText(repeatingWordsController.getCurrentKanji());
	}

	public void updateTime(String timePassed) {
		time.setText(Labels.TIME_LABEL + timePassed);
	}

	public void clearKanji() {
		kanjiTextArea.setText("");
	}

	public void showWord(String word, TextAlignment alignment) {
			wordTextArea.setText(word);
	}

}
