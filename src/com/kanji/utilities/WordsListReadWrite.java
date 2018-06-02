package com.kanji.utilities;

import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordsAndRepeatingInfo;
import javafx.util.Pair;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class WordsListReadWrite {
	private static final String KANA_SEPARATOR = ":";
	private static final String JAPANESE_WORDS_HEADER = "Japanese words";
	private static final String PROBLEMATIC_JAPANESE_WORDS_HEADER = "Problematic japanese words";
	private File readedFile;
	private final String WORD_TEXT = "Słowo to: ";
	private final String NUMBER_TEXT = "Numer id to: ";
	private final String HEADERS_REGEX =
			KANJIS_HEADER + "|" + REPEATING_DATES_HEADER + "|"
					+ PROBLEMATIC_KANJIS_HEADER + "|" + JAPANESE_WORDS_HEADER
					+ "|" + PROBLEMATIC_JAPANESE_WORDS_HEADER;
	private BufferedReader in;
	private static final String KANJIS_HEADER = "Kanji information";
	private static final String REPEATING_DATES_HEADER = "Repeating dates";
	private static final String PROBLEMATIC_KANJIS_HEADER = "Problematic kanjis";
	private static final String SEPARATOR = "#";
	private static final String KANJI_SEPARATOR = ",";
	private WordsAndRepeatingInfo<Kanji, Integer> kanjisAndRepeatingData;
	private WordsAndRepeatingInfo<JapaneseWord, String> japaneseWordsAndRepeatingData;
	private String currentHeader;

	public void readFile(File file)
			throws DuplicatedWordException, IOException {
		this.readedFile = file;
		in = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF8"));
		List<Kanji> kanjis = findKanjiWordsAndIDs();
		List<RepeatingData> repeatingKanjiData = findRepeatingDatas();
		Set<Integer> problematicKanjis = findProblematicKanjisNumbers();
		List<JapaneseWord> japaneseWords = findJapaneseWords();
		List<RepeatingData> repeatingJapaneseData = findRepeatingDatas();
		Set<String> problematicWords = findProblematicJapaneseWordsMeanings();
		kanjisAndRepeatingData = new WordsAndRepeatingInfo<>(kanjis,
				repeatingKanjiData, problematicKanjis);
		japaneseWordsAndRepeatingData = new WordsAndRepeatingInfo<>(
				japaneseWords, repeatingJapaneseData, problematicWords);
		in.close();
	}

	public WordsAndRepeatingInfo<Kanji, Integer> getKanjisAndRepeatingData() {
		return kanjisAndRepeatingData;
	}

	public WordsAndRepeatingInfo<JapaneseWord, String> getJapaneseWordsAndRepeatingData() {
		return japaneseWordsAndRepeatingData;
	}

	private Set<String> findProblematicJapaneseWordsMeanings()
			throws IOException {
		Set<String> problematicWords = new HashSet<>();
		String line;
		checkForHeader(PROBLEMATIC_JAPANESE_WORDS_HEADER);
		while ((line = in.readLine()) != null && !line.matches(HEADERS_REGEX)) {
			String[] wordMeanings = line.split(SEPARATOR);
			problematicWords.addAll(Arrays.asList(wordMeanings));
		}
		currentHeader = line;
		return problematicWords;
	}

	private List<JapaneseWord> findJapaneseWords() throws IOException {
		List<JapaneseWord> words = new ArrayList<>();
		String line;
		checkForHeader(JAPANESE_WORDS_HEADER);
		while (!(line = in.readLine()).matches(HEADERS_REGEX)) {
			JapaneseWord wordInformation = new JapaneseWord(PartOfSpeech.NOUN,
					"");
			String[] partsSeparatedByHash = separateLineByHash(line);
			String meaning = partsSeparatedByHash[0];
			String partOfSpeech = partsSeparatedByHash[1];
			wordInformation.setMeaning(meaning);
			wordInformation.setPartOfSpeech(
					PartOfSpeech.getPartOfSpeachByPolishMeaning(partOfSpeech));
			for (int i = 2; i < partsSeparatedByHash.length; i++) {
				String[] kanaAndKanjis = partsSeparatedByHash[i]
						.split(KANA_SEPARATOR);
				String kana = kanaAndKanjis[0];
				if (kanaAndKanjis.length > 1) {
					String[] kanjis = kanaAndKanjis[1].split(KANJI_SEPARATOR);
					wordInformation.addWritingsForKana(kana, kanjis);
				}
				else {
					wordInformation.addWritingsForKana(kana, "");
				}
			}
			words.add(wordInformation);
		}
		currentHeader = line;
		return words;
	}

	private String[] separateLineByHash(String line) {
		return line.split(SEPARATOR);
	}

	public void writeJapaneseListToFile(File f,
			MyList<JapaneseWord> listOfWords, MyList<RepeatingData> repeats,
			Set<JapaneseWord> problematicWords) throws IOException {
		BufferedWriter p = new BufferedWriter(
				new FileWriter(f.getPath(), true));
		List<JapaneseWord> list = listOfWords.getWords();
		p.write(JAPANESE_WORDS_HEADER);
		p.newLine();
		for (JapaneseWord word : list) {
			p.write(word.getMeaning() + SEPARATOR + word.getPartOfSpeech()
					.getPolishMeaning() + SEPARATOR);
			for (JapaneseWriting writing : word.getWritings()) {
				p.write(writing.getKanaWriting());
				if (!writing.getKanjiWritings().isEmpty()) {
					p.write(KANA_SEPARATOR);
					boolean firstKanji = true;
					for (String kanji : writing.getKanjiWritings()) {
						if (!firstKanji) {
							p.write(KANJI_SEPARATOR);
						}
						p.write(kanji);
						if (firstKanji) {
							firstKanji = false;
						}
					}
				}
				p.write(SEPARATOR);
			}
			p.newLine();
		}
		List<RepeatingData> repeatingData = repeats.getWords();
		p.write(REPEATING_DATES_HEADER);
		p.newLine();
		for (RepeatingData repeatingDate : repeatingData) {
			p.write(repeatingDate.getRepeatingRange().replace("\n", "") + SEPARATOR
					+ repeatingDate.getRepeatingDate() + SEPARATOR
					+ repeatingDate.getTimeSpentOnRepeating() + SEPARATOR);
			p.newLine();
		}
		p.write(PROBLEMATIC_JAPANESE_WORDS_HEADER);
		p.newLine();
		for (JapaneseWord word : problematicWords) {
			p.write(word.getMeaning());
			p.write(SEPARATOR);
		}
		p.close();
	}

	public void writeKanjiListToFile(File f, MyList<Kanji> listOfWords,
			MyList<RepeatingData> repeats, Set<Kanji> problematicKanjis)
			throws IOException {
		BufferedWriter p = new BufferedWriter(
				new FileWriter(f.getPath(), false));
		List<Kanji> list = listOfWords.getWords();
		p.write(KANJIS_HEADER);
		p.newLine();
		for (Kanji kanji : list) {
			p.write(kanji.getKeyword() + SEPARATOR + Integer
					.toString(kanji.getId()) + SEPARATOR);
			p.newLine();
		}
		List<RepeatingData> repeatingData = repeats.getWords();
		p.write(REPEATING_DATES_HEADER);
		p.newLine();
		for (RepeatingData repeatingDate : repeatingData) {
			p.write(repeatingDate.getRepeatingRange().replace("\n", "")
					+ SEPARATOR + repeatingDate.getRepeatingDate() + SEPARATOR
					+ repeatingDate.getTimeSpentOnRepeating() + SEPARATOR);
			p.newLine();
		}
		p.write(PROBLEMATIC_KANJIS_HEADER);
		if (!problematicKanjis.isEmpty()) {
			p.newLine();
		}
		for (Kanji i : problematicKanjis) {
			p.write(Integer.toString(i.getId()));
			p.write("#");
		}
		p.newLine();
		p.close();
	}

	private void checkForHeader(String expectedHeader) {
		if (!currentHeader.equals(expectedHeader)) {
			throw new IllegalArgumentException(
					ExceptionsMessages.ILLEGAL_LIST_FILE_FORMAT);
			//TODO be more specific with these exceptions: say what's wrong and where
		}
	}

	private List<Kanji> findKanjiWordsAndIDs()
			throws DuplicatedWordException, IOException {
		List<Kanji> kanjis = new ArrayList<>();
		String line = in.readLine();
		currentHeader = line;
		checkForHeader(KANJIS_HEADER);
		while (!(line = in.readLine()).matches(HEADERS_REGEX)) {
			String[] separatedParts = line.split(SEPARATOR);
			String kanjiKeyword = separatedParts[0];
			String wordId = separatedParts[1];
			int wordIdInt = Integer.parseInt(wordId);
			if (isIdDuplicated(wordIdInt, kanjis)) {
				openDesktopAndShowMessage(wordIdInt);
			}
			else if (isKeywordDefined(kanjiKeyword, kanjis)) {
				openDesktopAndShowMessage(kanjiKeyword, wordIdInt, kanjis);
			}
			else {
				kanjis.add(new Kanji(kanjiKeyword, Integer.valueOf(wordIdInt)));
			}
		}
		currentHeader = line;
		return kanjis;
	}

	private boolean isIdDuplicated(int id, List<Kanji> kanjis) {
		for (Kanji kanji : kanjis) {
			if (kanji.getId() == id) {
				return true;
			}
		}
		return false;
	}

	private boolean isKeywordDefined(String keyword, List<Kanji> kanjis) {
		for (Kanji kanji : kanjis) {
			if (kanji.getKeyword().equals(keyword)) {
				return true;
			}
		}
		return false;
	}

	private Set<Integer> findProblematicKanjisNumbers() throws IOException {
		Set<Integer> information = new HashSet<>();
		String line;
		checkForHeader(PROBLEMATIC_KANJIS_HEADER);

		while (!(line = in.readLine()).matches(HEADERS_REGEX)) {
			int indexOfNextSeparator = -1;

			while (indexOfNextSeparator < line.length() - 1) {
				Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line,
						indexOfNextSeparator);
				String problematicKanji = wordAndIndex.getKey();
				indexOfNextSeparator = wordAndIndex.getValue();
				information.add(Integer.parseInt(problematicKanji));
			}
		}
		currentHeader = line;
		return information;
	}

	private List<RepeatingData> findRepeatingDatas() throws IOException {
		List<RepeatingData> information = new ArrayList<>();
		String line;
		checkForHeader(REPEATING_DATES_HEADER);
		while (!(line = in.readLine()).matches(HEADERS_REGEX)) {
			String[] splittedWords = line.split(SEPARATOR);
			String ranges = splittedWords[0];
			String date = splittedWords[1];
			String timeSpent = splittedWords[2];
			RepeatingData r = new RepeatingData(ranges,
					LocalDateTime.parse(date), true, timeSpent);
			information.add(r);

		}
		currentHeader = line;
		return information;
	}

	private Pair<String, Integer> getNextSeparatedWord(String line,
			int indexOfSeparator) {
		indexOfSeparator++;
		int indexOfSearchStart = indexOfSeparator;
		indexOfSeparator = line.indexOf(SEPARATOR, indexOfSearchStart);
		return new Pair<>(line.substring(indexOfSearchStart, indexOfSeparator),
				indexOfSeparator);

	}

	private void openDesktopAndShowMessage(int wordId)
			throws DuplicatedWordException, IOException {
		Desktop.getDesktop().open(this.readedFile);
		//TODO if the file has wrong extension we get exception: no application associated for this application with this file
		throw new DuplicatedWordException(
				ExceptionsMessages.DUPLICATED_ID_EXCEPTION + " " + NUMBER_TEXT
						+ wordId);
	}

	private void openDesktopAndShowMessage(String duplicatedWord, int wordId,
			List<Kanji> kanjis) throws DuplicatedWordException, IOException {
		Desktop.getDesktop().open(this.readedFile);
		throw new DuplicatedWordException(
				ExceptionsMessages.DUPLICATED_WORD_EXCEPTION + " " + WORD_TEXT
						+ duplicatedWord + "; " + NUMBER_TEXT + wordId
						+ " oraz " + getKeywordID(duplicatedWord, kanjis));
	}

	private int getKeywordID(String keyword, List<Kanji> kanjis) {
		for (Kanji kanji : kanjis) {
			return kanji.getId();
		}
		return -1;
	}
}