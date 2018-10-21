package com.kanji.application;

import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.MyList;
import com.kanji.constants.enums.JapaneseParticle;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.listElements.WordParticlesData;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.RowInKanjiInformations;
import com.kanji.list.listRows.RowInRepeatingList;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.panelsAndControllers.controllers.ApplicationController;

import java.time.LocalDateTime;

public class ListTestDataCreator {

	private ApplicationController applicationController;

	public ListTestDataCreator(ApplicationController applicationController) {
		this.applicationController = applicationController;
	}

	private void createKanjiTestList() {
		MyList<Kanji> kanjiList = applicationController.getKanjiList();

		for (int i = 1; i <= 510; i++) {
			System.out.println("create : " + i);
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
		return new MyList<>(applicationController.getApplicationWindow(),
				applicationController,
				new RowInKanjiInformations(applicationController,
						PanelDisplayMode.EDIT), Titles.KANJI_LIST,
				new ListConfiguration(Prompts.KANJI), Kanji.getInitializer());
	}

	private void createKanjiRepeatingDatesTestList() {
		MyList<RepeatingData> kanjiRepeatingDates = applicationController
				.getKanjiRepeatingDates();
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
		return new MyList<>(applicationController.getApplicationWindow(),
				applicationController, new RowInRepeatingList(),
				Titles.KANJI_REPEATING_LIST,
				new ListConfiguration(Prompts.REPEATING_DATE_DELETE)
						.showButtonsLoadNextPreviousWords(false)
						.enableWordAdding(false),
				RepeatingData.getInitializer());
	}

	private void createJapaneseWordsRepeatingDatesTestList() {
		MyList<RepeatingData> japaneseWordsRepeatingDates = applicationController
				.getJapaneseWordsRepeatingDates();
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
		return new MyList<>(applicationController.getApplicationWindow(),
				applicationController, new RowInRepeatingList(),
				Titles.JAPANESE_REPEATING_LIST,
				new ListConfiguration(Prompts.REPEATING_DATE_DELETE)
						.enableWordAdding(false)
						.showButtonsLoadNextPreviousWords(false),
				RepeatingData.getInitializer());
	}

	private void createJapaneseWordsTestList() {
		MyList<JapaneseWord> japaneseWords = applicationController
				.getJapaneseWords();

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
		japaneseWord.addWritingsForKana("らけ1", "務");
		japaneseWords.addWord(japaneseWord);
		JapaneseWord japaneseWord2 = new JapaneseWord(PartOfSpeech.NOUN,
				"trykot");
		japaneseWord2.addWritingsForKana("らけ2", "務");
		japaneseWords.addWord(japaneseWord2);
		JapaneseWord japaneseWord3 = new JapaneseWord(PartOfSpeech.NOUN,
				"splot");
		japaneseWord3.addWritingsForKana("らけ3", "務");
		japaneseWords.addWord(japaneseWord3);
		JapaneseWord japaneseWord4 = new JapaneseWord(PartOfSpeech.NOUN,
				"przykazanie");
		japaneseWord4.addWritingsForKana("らけ4", "務");
		japaneseWords.addWord(japaneseWord4);
		JapaneseWord japaneseWord5 = new JapaneseWord(PartOfSpeech.NOUN,
				"opowieść, historia, legenda");
		japaneseWord5.addWritingsForKana("らけ5", "務");
		japaneseWords.addWord(japaneseWord5);
		JapaneseWord japaneseWord6 = new JapaneseWord(PartOfSpeech.NOUN,
				"pies z kotem");
		japaneseWord6.addWritingsForKana("らけ6", "務");
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
				applicationController.getApplicationWindow(),
				applicationController, rowInJapaneseWordInformations,
				Titles.JAPANESE_WORDS_LIST,
				new ListConfiguration(Prompts.JAPANESE_WORD_DELETE),
				JapaneseWord.getInitializer());
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