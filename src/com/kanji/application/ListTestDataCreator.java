package com.kanji.application;

import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.WordDictionaryData;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.constants.strings.Urls;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.listRows.RowInRepeatingList;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import javax.swing.*;
import java.time.LocalDateTime;

public class ListTestDataCreator {

	private ApplicationController applicationController;

	public ListTestDataCreator(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	private void createKanjiTestList() {
		MyList<Kanji> kanjiList = applicationController.getKanjiList();

		for (int i = 1; i <= 510; i++) {
			kanjiList.addWord(new Kanji("Word no. " + i, i));
		}
		kanjiList.addWord(new Kanji(
				"Firstly a trivial correction: the integer ALIGN_JUSTIF"
						+ " should read ALIGN_JUSTIFIED Secondly, I have tried several variations of getting "
						+ "justified text in JTextPane including the solution given above and using a menuitem "
						+ "with alignment action such as: menu.add(new , i, i);",
				11));
	}

	public MyList<Kanji> initializeKanjiList() {
		return new MyList<>(new ListConfiguration<>(Prompts.KANJI,
				new RowInKanjiInformations(applicationController,
						PanelDisplayMode.EDIT), Kanji.getInitializer(),
				Titles.KANJI_LIST, applicationController.getApplicationWindow(),
				applicationController));
	}

	private void createKanjiRepeatingDatesTestList() {
		MyList<RepeatingData> kanjiRepeatingDates = applicationController.getKanjiRepeatingDates();
		kanjiRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(1993, 11, 13, 13, 25),
						true, "3 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2005, 1, 1, 11, 11),
						true, "4 minuty"));
		kanjiRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2000, 12, 31, 10, 0),
						true, "5 minut"));
	}

	public MyList<RepeatingData> initializeKanjiRepeatingList() {
		return new MyList<>(
				new ListConfiguration<>(Prompts.REPEATING_DATE_DELETE,
						new RowInRepeatingList(),
						RepeatingData.getInitializer(),
						Titles.KANJI_REPEATING_LIST,
						applicationController.getApplicationWindow(),
						applicationController).showButtonsLoadNextPreviousWords(
						false)
											  .enableWordAdding(false));
	}

	private void createJapaneseWordsRepeatingDatesTestList() {
		MyList<RepeatingData> japaneseWordsRepeatingDates = applicationController.getJapaneseWordsRepeatingDates();
		japaneseWordsRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(1993, 11, 13, 13, 25),
						true, "3 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2005, 1, 1, 11, 11),
						true, "4 minuty"));
		japaneseWordsRepeatingDates.addWord(
				new RepeatingData("abc", LocalDateTime.of(2000, 12, 31, 10, 0),
						true, "5 minut"));
	}

	public MyList<RepeatingData> initializeJapaneseWordsRepeatingData() {
		return new MyList<>(
				new ListConfiguration<>(Prompts.REPEATING_DATE_DELETE,
						new RowInRepeatingList(),
						RepeatingData.getInitializer(),
						Titles.JAPANESE_REPEATING_LIST,
						applicationController.getApplicationWindow(),
						applicationController).enableWordAdding(false)
											  .showButtonsLoadNextPreviousWords(
													  false));
	}

	private void createJapaneseWordsTestList() {
		MyList<JapaneseWord> japaneseWords = applicationController.getJapaneseWords();

		JapaneseWord cat = new JapaneseWord(PartOfSpeech.NOUN, "kot");
		cat.addWritingsForKana("ねこ", "頭骨");
		cat.addParticleData(new WordParticlesData(JapaneseParticle.DE));
		cat.setPartOfSpeech(PartOfSpeech.EXPRESSION);
		japaneseWords.addWord(cat);
		JapaneseWord dog2 = new JapaneseWord(PartOfSpeech.NOUN, "pies");
		dog2.addWritingsForKana("いぬ");
		japaneseWords.addWord(dog2);
		JapaneseWord verb = new JapaneseWord(PartOfSpeech.VERB, "otwierać");
		verb.addWritingsForKana("あける", "開ける", "空ける", "明ける");
		verb.addWritingsForKana("ひらける", "開ける", "空ける", "明ける");
		JapaneseWord japaneseWord = new JapaneseWord(PartOfSpeech.NOUN, "Test");
		japaneseWord.addWritingsForKana("らけあ", "務");
		japaneseWords.addWord(japaneseWord);
		JapaneseWord japaneseWord2 = new JapaneseWord(PartOfSpeech.NOUN,
				"trykot");
		japaneseWord2.addWritingsForKana("らけな", "務");
		japaneseWords.addWord(japaneseWord2);
		JapaneseWord japaneseWord3 = new JapaneseWord(PartOfSpeech.NOUN,
				"splot");
		japaneseWord3.addWritingsForKana("らけば", "務");
		japaneseWords.addWord(japaneseWord3);
		JapaneseWord japaneseWord4 = new JapaneseWord(PartOfSpeech.NOUN,
				"przykazanie");
		japaneseWord4.addWritingsForKana("らけだ", "務");
		japaneseWords.addWord(japaneseWord4);
		JapaneseWord japaneseWord5 = new JapaneseWord(PartOfSpeech.NOUN,
				"opowieść, historia, legenda");
		japaneseWord5.addWritingsForKana("らけけ", "務");
		japaneseWords.addWord(japaneseWord5);
		JapaneseWord japaneseWord6 = new JapaneseWord(PartOfSpeech.NOUN,
				"pies z kotem");
		japaneseWord6.addWritingsForKana("らけか", "務");
		japaneseWords.addWord(japaneseWord6);
		japaneseWords.addWord(verb);
	}

	public MyList<JapaneseWord> initializeJapaneseWordsList() {
		JapaneseWordPanelCreator japaneseWordPanelCreator = new JapaneseWordPanelCreator(
				applicationController,
				applicationController.getApplicationWindow(),
				PanelDisplayMode.EDIT);
		RowInJapaneseWordInformations rowInJapaneseWordInformations = new RowInJapaneseWordInformations(
				japaneseWordPanelCreator);
		MyList<JapaneseWord> japaneseWords = new MyList<>(
				new ListConfiguration<>(Prompts.JAPANESE_WORD_DELETE,
						rowInJapaneseWordInformations,
						JapaneseWord.getInitializer(),
						Titles.JAPANESE_WORDS_LIST,
						applicationController.getApplicationWindow(),
						applicationController).dictionaryData(
						new WordDictionaryData(applicationController
								.getStartingPanel()
								.getJapaneseWordsListsSplitPane(),
								Urls.JISHO_SEARCH_PATTERN)));
		japaneseWordPanelCreator.setWordsList(japaneseWords);
		return japaneseWords;
	}

	public void initializeTestData() {
		createJapaneseWordsRepeatingDatesTestList();
		createJapaneseWordsTestList();
		createKanjiRepeatingDatesTestList();
		createKanjiTestList();
	}

}
