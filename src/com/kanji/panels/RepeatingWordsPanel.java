package com.kanji.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.Titles;
import com.kanji.controllers.RepeatingWordsController;
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

	public RepeatingWordsPanel(ApplicationWindow applicationWindow) {
		this.repeatingWordsController = new RepeatingWordsController(applicationWindow, this);
		kanjiFont = applicationWindow.getKanjiFont();
		centerPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		repeatingPanel = new MainPanel(this.repeatingBackgroundColor);
		repeatingPanel.setBorder(getDefaultBorder());
	}

	public RepeatingWordsController getController() {
		return repeatingWordsController;
	}

	@Override
	void createElements() {
		JLabel titleLabel = new JLabel(Titles.REPEATING_WORDS_DIALOG);
		time = new JLabel();
		remainingLabel = new JLabel(repeatingWordsController.createRemainingKanjisPrompt());
		AbstractButton returnButton = createReturnButton();
		createRepeatingPanel();
		setButtonsToLearning();
		mainPanel.getPanel().repaint();
		centerPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH, titleLabel, time)
				.nextRow(FillType.BOTH, repeatingPanel.getPanel()).setBorder(getDefaultBorder())
				.nextRow(FillType.NONE, Anchor.CENTER, remainingLabel, returnButton));
		//TODO in gui maker enable me to put some element in some anchor so that remaining label can be positioned vertically center
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, centerPanel.getPanel())
				.useAllExtraVerticalSpace());
	}

	private void setButtonsToLearning() {
		notRecognizedWord.setEnabled(false);
		showPreviousWord.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void createRepeatingPanel() {
		createWordDescriptionTextArea();
		createKanjiTextArea();
		createPauseOrResumeButton();
		createRecognizedWordButton();
		createNotRecognizedWordButton();
		createGoToPreviousWordButton();
		addElementsToRepeatingPanel();
	}

	private void addElementsToRepeatingPanel(){
		AbstractButton [] navigationButtons = new AbstractButton[] { this.pauseOrResume,
				showKanjiOrRecognizeWord, notRecognizedWord, this.showPreviousWord };
		repeatingPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, wordTextArea).setColor(BasicColors.GREY)
				.nextRow(FillType.NONE, Anchor.CENTER, kanjiTextArea).disableBorder()
				.nextRow(FillType.HORIZONTAL, navigationButtons).fillHorizontallyEqually().disableBorder());
	}

	private void createGoToPreviousWordButton() {
		showPreviousWord = createButtonWithHotkey(KeyEvent.VK_G,
				repeatingWordsController.createActionGoToPreviousWord(),
				ButtonsNames.PREVIOUS_WORD,	HotkeysDescriptions.SHOW_PREVIOUS_KANJI);
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

	private void setButtonsToRecognizing() {
		showKanjiOrRecognizeWord.setText(ButtonsNames.RECOGNIZED_WORD);
		notRecognizedWord.setEnabled(true);
		showPreviousWord.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void createPauseOrResumeButton() {
		pauseOrResume = createButtonWithHotkey(KeyEvent.VK_P,
				repeatingWordsController.createButtonPause(), ButtonsNames.PAUSE,
				HotkeysDescriptions.PAUSE);
	}

	private void createRecognizedWordButton() {
		showKanjiOrRecognizeWord = createButtonWithHotkey(KeyEvent.VK_SPACE,
				repeatingWordsController.createRecognizedWordAction(),
				ButtonsNames.SHOW_KANJI, HotkeysDescriptions.SHOW_KANJI_OR_SET_KANJI_AS_KNOWN_KANJI);
	}

	private void createNotRecognizedWordButton() {
		notRecognizedWord = createButtonWithHotkey(KeyEvent.VK_A,
				repeatingWordsController.createNotRecognizedWordAction(),
				ButtonsNames.NOT_RECOGNIZED, HotkeysDescriptions.SET_KANJI_AS_PROBLEMATIC);
	}

	public void setElementsToLearningState() {
		showKanjiOrRecognizeWord.setText(ButtonsNames.SHOW_KANJI);
		setButtonsToLearning();
	}

	public void updateRemainingKanjis (String remainingKanjisPrompt){
		remainingLabel.setText(remainingKanjisPrompt);
	}

	private AbstractButton createReturnButton() {
		return createButtonWithHotkey(KeyModifiers.ALT, KeyEvent.VK_H,
				repeatingWordsController.createActionExit(),
				ButtonsNames.GO_BACK, HotkeysDescriptions.RETURN_FROM_LEARNING);
	}

	public void showCurrentKanjiAndSetButtons(String currentKanji) {
		setButtonsToRecognizing();
		kanjiTextArea.setText(currentKanji);
	}

	public void updateTime(String timePassed) {
		time.setText(Labels.TIME_LABEL + timePassed);
	}

	public void clearKanji() {
		kanjiTextArea.setText("");
	}

	public void showWord(String word) {
		wordTextArea.setText(word);
	}

	public void toggleGoToPreviousWordButton(){
		showPreviousWord.setEnabled(!showPreviousWord.isEnabled());
	}

}
