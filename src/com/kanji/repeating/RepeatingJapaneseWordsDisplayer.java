package com.kanji.repeating;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.model.CommonListElements;
import com.guimaker.panels.mainPanel.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.StringUtilities;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanel;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.utilities.KanjiCharactersReader;

import java.awt.*;
import java.util.*;
import java.util.function.Function;

public class RepeatingJapaneseWordsDisplayer
		implements RepeatingWordsDisplayer<JapaneseWord> {

	private final JapaneseWordPanel japaneseWordPanel;
	private KanjiCharactersReader kanjiCharactersReader;
	private Map<Integer, Function<JapaneseWord, Set<String>>> hintTypeIntValues;
	private String UNIQUE_NAME = "Repeating japanese words";
	private ApplicationController applicationController;

	public RepeatingJapaneseWordsDisplayer(
			ApplicationController applicationController) {
		japaneseWordPanel = new JapaneseWordPanel(
				applicationController.getApplicationWindow(),
				PanelDisplayMode.VIEW, applicationController);
		kanjiCharactersReader = KanjiCharactersReader.getInstance();
		kanjiCharactersReader.loadKanjisIfNeeded();
		//TODO kanjis can be loaded just once in the "get instance" method
		initializeHintTypeValues();
		this.applicationController = applicationController;
	}

	private void initializeHintTypeValues() {
		hintTypeIntValues = new HashMap<>();
		hintTypeIntValues.put(1, japaneseWordInformation -> new HashSet<>(
				Collections.singletonList(
						japaneseWordInformation.getMeaning())));
		hintTypeIntValues.put(2, JapaneseWord::getKanaWritings);
		hintTypeIntValues.put(3, JapaneseWord::getKanjiWritings);

	}

	@Override
	public void showFullWordDetailsPanel(JapaneseWord japaneseWord,
			MainPanel wordAssessmentPanel) {
		wordAssessmentPanel.clear();
		MainPanel rowPanel = japaneseWordPanel.createElements(japaneseWord,
				InputGoal.NO_INPUT,
				CommonListElements.forSingleRowOnly(Color.WHITE,
						applicationController.getJapaneseWords()))
											  .getRowPanel();
		wordAssessmentPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.NORTH,
						rowPanel.getPanel()));
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}

	@Override
	public String getWordHint(JapaneseWord japaneseWord) {
		int possibilitiesAmount;
		if (japaneseWord.hasKanjiWriting()) {
			possibilitiesAmount = 3;
		}
		else {
			possibilitiesAmount = 2;
		}
		Random random = new Random();
		int randomNumber = random.nextInt(possibilitiesAmount) + 1;
		Function<JapaneseWord, Set<String>> hintGetter = hintTypeIntValues.get(
				randomNumber);
		return StringUtilities.concatenateStrings(
				new ArrayList<>((hintGetter.apply(japaneseWord))));
	}

}
