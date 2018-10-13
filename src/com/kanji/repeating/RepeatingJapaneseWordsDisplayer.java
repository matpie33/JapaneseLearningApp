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

	private KanjiCharactersReader kanjiCharactersReader;
	private Map<Integer, Function<JapaneseWord, Set<String>>> hintTypeIntValues;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;

	public RepeatingJapaneseWordsDisplayer(
			JapaneseWordPanelCreator japaneseWordPanelCreator) {
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		//TODO kanjis can be loaded just once in the "get instance" method
		initializeHintTypeValues();
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	private void initializeHintTypeValues() {
		hintTypeIntValues = new HashMap<>();
		hintTypeIntValues.put(1, japaneseWordInformation -> new HashSet<>(
				Collections.singletonList(japaneseWordInformation.getMeaning())));
		hintTypeIntValues.put(2, JapaneseWord::getKanaWritings);
		hintTypeIntValues.put(3, JapaneseWord::getKanjiWritings);

	}

	@Override
	public void showFullWordDetailsPanel(JapaneseWord japaneseWord, MainPanel
			wordAssessmentPanel) {
		wordAssessmentPanel.clear();
		japaneseWordPanelCreator
				.addJapanesePanelToExistingPanel(wordAssessmentPanel,
						japaneseWord, InputGoal.NO_INPUT,
						CommonListElements.forSingleRowOnly(Color.WHITE), true);
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
