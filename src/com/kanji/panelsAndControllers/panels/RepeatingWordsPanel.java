package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.Colors;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Labels;
import com.kanji.constants.strings.Titles;
import com.kanji.panelsAndControllers.controllers.RepeatingWordsController;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

public class RepeatingWordsPanel extends AbstractPanelWithHotkeysInfo {

	private AbstractButton showKanjiOrRecognizeWordButton;
	private AbstractButton notRecognizedWordButton;
	private MainPanel repeatingDataPanel;
	private AbstractButton showPreviousWordButton;
	private JLabel timeElapsedLabel;
	private MainPanel rootPanel;
	private JLabel remainingLabel;
	private JTextComponent wordHintTextPane;
	private AbstractButton pauseButton;
	private RepeatingWordsController repeatingWordsController;
	private JPanel wordDataPanel;
	private final static String RECOGNIZING_WORD_PANEL_NAME = "Recognizing word";
	private final static String WORD_FULL_INFORMATION_PANEL_NAME = "Word full information";
	private JLabel titleLabel;
	private AbstractButton returnButton;

	public RepeatingWordsPanel(RepeatingWordsController controller) {
		rootPanel = new MainPanel(null);
		repeatingDataPanel = new MainPanel(Colors.BACKGROUND_PANEL_COLOR);
		repeatingDataPanel.setRowsBorder(getDefaultBorder());
		this.repeatingWordsController = controller;
		wordDataPanel = new JPanel(new CardLayout());
		wordDataPanel.setBackground(null);
		addWordDataPanelCards(new JPanel(), new JPanel());
	}

	public void addWordDataPanelCards(JPanel panelForRecognizingWord,
			JPanel wordFullInformationPanel) {
		wordDataPanel.removeAll();
		wordDataPanel
				.add(RECOGNIZING_WORD_PANEL_NAME, panelForRecognizingWord);
		wordDataPanel.add(WORD_FULL_INFORMATION_PANEL_NAME,
				wordFullInformationPanel);
	}

	@Override
	public void createElements() {
		titleLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(Titles.REPEATING_WORDS_DIALOG));
		timeElapsedLabel = GuiElementsCreator
				.createLabel(new ComponentOptions());
		remainingLabel = GuiElementsCreator.createLabel(new ComponentOptions()
				.text(repeatingWordsController.createRemainingWordsPrompt()));
		returnButton = createReturnButton();
		createRepeatingPanelElements();

		addElementsToPanels();
		setButtonsToRecognizingState();
		mainPanel.getPanel().repaint();
	}

	private void setButtonsToRecognizingState() {
		notRecognizedWordButton.setEnabled(false);
		showPreviousWordButton
				.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void createRepeatingPanelElements() {
		createWordHintTextPane();
		createPauseButton();
		createShowKanjiOrRecognizeWordButton();
		createNotRecognizedWordButton();
		createShowPreviousWordButton();
	}

	private void addElementsToPanels() {
		repeatingDataPanel.addRows(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, wordHintTextPane)
				.nextRow(FillType.BOTH, Anchor.CENTER, wordDataPanel)
				.nextRow(FillType.NONE, this.pauseButton,
						showKanjiOrRecognizeWordButton, notRecognizedWordButton,
						this.showPreviousWordButton).fillHorizontallyEqually()
				.disableBorder());

		rootPanel.addRows(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.NORTH, titleLabel,
						timeElapsedLabel)
				.nextRow(FillType.BOTH, repeatingDataPanel.getPanel())
				.setBorder(getDefaultBorder())
				.nextRow(FillType.NONE, Anchor.CENTER, remainingLabel,
						returnButton));

		//TODO in gui maker enable me to put some element in some anchor so that remaining label can be positioned vertically center
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, Anchor.CENTER,
						rootPanel.getPanel()).useAllExtraVerticalSpace());
	}

	private void createShowPreviousWordButton() {
		showPreviousWordButton = createButtonWithHotkey(KeyEvent.VK_G,
				repeatingWordsController.createActionGoToPreviousWord(),
				ButtonsNames.PREVIOUS_WORD,
				HotkeysDescriptions.SHOW_PREVIOUS_KANJI);
		showPreviousWordButton.setFocusable(false);
	}

	private void createWordHintTextPane() {
		wordHintTextPane = GuiElementsCreator.createTextPane(
				new TextPaneOptions().textAlignment(TextAlignment.CENTERED)
						.text("").editable(false).fontSize(30f)
						.backgroundColor(Colors.CONTENT_PANEL_COLOR)
						.font(ApplicationWindow.getKanjiFont()));
	}

	public void setButtonsToRecognizing() {
		showKanjiOrRecognizeWordButton.setText(ButtonsNames.RECOGNIZED_WORD);
		notRecognizedWordButton.setEnabled(true);
		showPreviousWordButton
				.setEnabled(repeatingWordsController.previousWordExists());
	}

	private void createPauseButton() {
		pauseButton = createButtonWithHotkey(KeyEvent.VK_P,
				repeatingWordsController.createActionPause(),
				ButtonsNames.PAUSE, HotkeysDescriptions.PAUSE);
	}

	private void createShowKanjiOrRecognizeWordButton() {
		showKanjiOrRecognizeWordButton = createButtonWithHotkey(KeyEvent.VK_SPACE,
				repeatingWordsController
						.createShowFullInformationOrMarkWordAsRecognizedAction(),
				ButtonsNames.SHOW_KANJI,
				HotkeysDescriptions.SHOW_KANJI_OR_SET_KANJI_AS_KNOWN_KANJI);
	}

	private void createNotRecognizedWordButton() {
		notRecognizedWordButton = createButtonWithHotkey(KeyEvent.VK_A,
				repeatingWordsController.createNotRecognizedWordAction(),
				ButtonsNames.NOT_RECOGNIZED,
				HotkeysDescriptions.SET_KANJI_AS_PROBLEMATIC);
	}

	public void setElementsToRecognizingState() {
		showKanjiOrRecognizeWordButton.setText(ButtonsNames.SHOW_KANJI);
		setButtonsToRecognizingState();
	}

	public void updateRemainingWordsText(String remainingKanjisPrompt) {
		remainingLabel.setText(remainingKanjisPrompt);
	}

	private AbstractButton createReturnButton() {
		return createButtonWithHotkey(KeyModifiers.CONTROL, KeyEvent.VK_E,
				repeatingWordsController.createActionExit(),
				ButtonsNames.GO_BACK, HotkeysDescriptions.RETURN_FROM_LEARNING);
	}

	public void updateTime(String timePassed) {
		timeElapsedLabel.setText(Labels.TIME_LABEL + timePassed);
	}

	public void showWord(String word) {
		wordHintTextPane.setText(word);
	}

	public void showCardForRecognizingWord() {
		showPanel(RECOGNIZING_WORD_PANEL_NAME);
	}

	public void showCardWithFullInformationAboutWord() {
		showPanel(WORD_FULL_INFORMATION_PANEL_NAME);
	}

	private void showPanel(String name) {
		((CardLayout) wordDataPanel.getLayout())
				.show(wordDataPanel, name);
	}

	public void toggleGoToPreviousWordButton() {
		showPreviousWordButton.setEnabled(!showPreviousWordButton.isEnabled());
	}

}
