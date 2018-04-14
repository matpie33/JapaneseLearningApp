package com.kanji.utilities;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanjisAndRepeatingInfo;
import javafx.util.Pair;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KanjiListFileReader {
	private File readedFile;
	private final String WORD_TEXT = "Słowo to: ";
	private final String NUMBER_TEXT = "Numer id to: ";
	private final String HEADERS_REGEX =
			KANJIS_HEADER + "|" + REPEATING_DATES_HEADER + "|"
					+ PROBLEMATIC_KANJIS_HEADER;
	private BufferedReader in;
	private static final String KANJIS_HEADER = "Kanji information";
	private static final String REPEATING_DATES_HEADER = "Repeating dates";
	private static final String PROBLEMATIC_KANJIS_HEADER = "Problematic kanjis";
	private static final String SEPARATOR = "#";

	public KanjisAndRepeatingInfo readFile(File file)
			throws DuplicatedWordException, IOException {
		this.readedFile = file;
		in = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF8"));
		List<Kanji> kanjis = findWordsAndIDs(file);
		List<RepeatingData> repeatingData = findRepeatingInformations(
				file);
		Set<Integer> problematicKanjis = findProblematicKanjisNumbers();
		KanjisAndRepeatingInfo kanjisAndRepeatingInfo = new KanjisAndRepeatingInfo(
				kanjis, repeatingData, problematicKanjis);
		in.close();
		return kanjisAndRepeatingInfo;
	}

	public void writeToFile(File f, MyList<Kanji> listOfWords,
			MyList<RepeatingData> repeats,
			Set<Kanji> problematicKanjis) throws IOException {
		BufferedWriter p = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
		List<Kanji> list = listOfWords.getWords();
		p.write(KANJIS_HEADER);
		p.newLine();
		for (Kanji kanji : list) {
			p.write(kanji.getKeyword() + SEPARATOR + kanji.getId()
					+ SEPARATOR);
			p.newLine();
		}
		List<RepeatingData> repeatingData = repeats.getWords();
		p.write(REPEATING_DATES_HEADER);
		p.newLine();
		for (RepeatingData repeatingDate : repeatingData) {
			p.write(repeatingDate.getRepeatingRange() + SEPARATOR
					+ repeatingDate.getRepeatingDate() + SEPARATOR
					+ repeatingDate.getTimeSpentOnRepeating() + SEPARATOR);
			p.newLine();
		}
		p.write(PROBLEMATIC_KANJIS_HEADER);
		p.newLine();
		for (Kanji i : problematicKanjis) {
			p.write(i.getId());
			p.write("#");
		}
		p.close();
	}

	private List<Kanji> findWordsAndIDs(File file)
			throws DuplicatedWordException, IOException {
		List<Kanji> kanjis = new ArrayList<>();
		String line = in.readLine();
		if (!line.equals(KANJIS_HEADER)) {
			throw new IllegalArgumentException(
					ExceptionsMessages.ILLEGAL_LIST_FILE_FORMAT);
			//TODO be more specific with these exceptions: say what's wrong and where
		}
		while (!(line = in.readLine()).matches(HEADERS_REGEX)) {
			int indexOfNextSeparator = -1;
			Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line,
					indexOfNextSeparator);
			String kanjiKeyword = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String wordId = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			indexOfNextSeparator++;
			if (line.indexOf(SEPARATOR, indexOfNextSeparator) > 0) {
				throw new IllegalArgumentException(
						ExceptionsMessages.ILLEGAL_LIST_FILE_FORMAT);
			}
			int wordIdInt = Integer.parseInt(wordId);
			if (isIdDuplicated(wordIdInt, kanjis)) {
				openDesktopAndShowMessage(wordIdInt);
			}
			else if (isKeywordDefined(kanjiKeyword, kanjis)) {
				openDesktopAndShowMessage(kanjiKeyword, wordIdInt, kanjis);
			}
			else {
				kanjis.add(new Kanji(kanjiKeyword,
						Integer.valueOf(wordIdInt)));
			}
		}

		return kanjis;
	}

	private boolean isIdDuplicated(int id,
			List<Kanji> kanjis) {
		for (Kanji kanji : kanjis) {
			if (kanji.getId() == id) {
				return true;
			}
		}
		return false;
	}

	private boolean isKeywordDefined(String keyword,
			List<Kanji> kanjis) {
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
		while ((line = in.readLine()) != null) {
			int indexOfNextSeparator = -1;

			while (indexOfNextSeparator < line.length() - 1) {
				Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line,
						indexOfNextSeparator);
				String problematicKanji = wordAndIndex.getKey();
				indexOfNextSeparator = wordAndIndex.getValue();
				information.add(Integer.parseInt(problematicKanji));
			}
		}
		return information;
	}

	private List<RepeatingData> findRepeatingInformations(File file)
			throws DuplicatedWordException, IOException {
		List<RepeatingData> information = new ArrayList<>();
		String line;

		while (!(line = in.readLine()).matches(HEADERS_REGEX)) {
			int indexOfNextSeparator = -1;
			Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line,
					indexOfNextSeparator);
			String ranges = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String date = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String timeSpent = wordAndIndex.getKey();
			RepeatingData r = new RepeatingData(ranges,
					LocalDateTime.parse(date), true, timeSpent);
			information.add(r);

		}
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
			List<Kanji> kanjis)
			throws DuplicatedWordException, IOException {
		Desktop.getDesktop().open(this.readedFile);
		throw new DuplicatedWordException(
				ExceptionsMessages.DUPLICATED_WORD_EXCEPTION + " " + WORD_TEXT
						+ duplicatedWord + "; " + NUMBER_TEXT + wordId
						+ " oraz " + getKeywordID(duplicatedWord, kanjis));
	}

	private int getKeywordID(String keyword,
			List<Kanji> kanjis) {
		for (Kanji kanji : kanjis) {
			return kanji.getId();
		}
		return -1;
	}
}