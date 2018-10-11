package com.kanji.repeating;

import com.guimaker.colors.BasicColors;
import com.guimaker.options.ComboboxOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.enums.VerbConjugationType;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.saving.RepeatingState;
import com.kanji.timer.TimeSpent;
import com.kanji.utilities.CommonListElements;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.utilities.StringUtilities;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class RepeatingJapaneseWordsDisplayer
		implements RepeatingWordsDisplayer<JapaneseWord> {

	private final Dimension wordPanelsSize = new Dimension(300, 200);
	private MainPanel fullWordInformationPanel;
	private MainPanel recognizingWordPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private JComboBox<String> partOfSpeechCombobox;
	private JComboBox<String> verbConjugationCombobox;
	private Map<Integer, Function<JapaneseWord, Set<String>>> hintTypeIntValues;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;

	public RepeatingJapaneseWordsDisplayer(
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		//TODO kanjis can be loaded just once in the "get instance" method
		fullWordInformationPanel = new MainPanel(null, true);
		fullWordInformationPanel.setRowColor(BasicColors.PURPLE_DARK_2);
		fullWordInformationPanel.setRowsBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED,
						BasicColors.BLUE_NORMAL_1, BasicColors.BLUE_NORMAL_7));
		fullWordInformationPanel.setPadding(10);
		recognizingWordPanel = new MainPanel(null, true);
		recognizingWordPanel.getPanel().setPreferredSize(wordPanelsSize);
		fullWordInformationPanel.getPanel().setPreferredSize(wordPanelsSize);
		initializeHintTypeValues();
		initializeGuiElements();
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;

	}

	private void initializeHintTypeValues() {
		hintTypeIntValues = new HashMap<>();
		hintTypeIntValues.put(1, japaneseWordInformation -> new HashSet<>(
				Arrays.asList(japaneseWordInformation.getMeaning())));
		hintTypeIntValues.put(2, JapaneseWord::getKanaWritings);
		hintTypeIntValues.put(3, JapaneseWord::getKanjiWritings);

	}

	private void initializeGuiElements() {
		partOfSpeechCombobox = GuiElementsCreator
				.createCombobox(new ComboboxOptions());
		partOfSpeechCombobox.setFocusable(false);
		Arrays.stream(PartOfSpeech.values()).forEach(
				partOfSpeech -> partOfSpeechCombobox
						.addItem(partOfSpeech.getPolishMeaning()));
		verbConjugationCombobox = new JComboBox<>();
		verbConjugationCombobox.setFocusable(false);
		Arrays.stream(VerbConjugationType.values()).forEach(
				verbConjugationType -> verbConjugationCombobox
						.addItem(verbConjugationType.getDisplayedText()));

	}

	@Override
	public void showWordAssessmentPanel(JapaneseWord japaneseWord) {
		fullWordInformationPanel.clear();
		japaneseWordPanelCreator
				.addJapanesePanelToExistingPanel(fullWordInformationPanel,
						japaneseWord, InputGoal.NO_INPUT,
						CommonListElements.forSingleRowOnly(Color.WHITE), true);
	}

	@Override
	public JPanel getWordAssessmentPanel() {
		return fullWordInformationPanel.getRootPanel();
	}

	@Override
	public JPanel getWordGuessingPanel() {
		return recognizingWordPanel.getPanel();
	}

	@Override
	public void showWordGuessingPanel() {
		partOfSpeechCombobox.setSelectedIndex(0);
	}


	@Override
	public String getWordHint(JapaneseWord kanjiInformation) {
		int possibilitiesAmount;
		if (kanjiInformation.hasKanjiWriting()) {
			possibilitiesAmount = 3;
		}
		else {
			possibilitiesAmount = 2;
		}
		Random random = new Random();
		int randomNumber = random.nextInt(possibilitiesAmount) + 1;
		Function<JapaneseWord, Set<String>> hintGetter = hintTypeIntValues
				.get(randomNumber);
		return StringUtilities.concatenateStrings(
				new ArrayList<>((hintGetter.apply(kanjiInformation))));
	}



}
