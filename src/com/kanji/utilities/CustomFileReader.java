package com.kanji.utilities;

import java.awt.Desktop;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listElements.RepeatingInformation;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.exception.DuplicatedWordException;
import com.kanji.model.KanjisAndRepeatingInfo;
import com.kanji.list.myList.MyList;
import javafx.util.Pair;

public class CustomFileReader {
	private File readedFile;
	private final String WORD_TEXT = "Słowo to: ";
	private final String NUMBER_TEXT = "Numer id to: ";
	private final String HEADERS_REGEX = KANJIS_HEADER + "|"+ REPEATING_DATES_HEADER +"|"+ PROBLEMATIC_KANJIS_HEADER;
	private BufferedReader in;
	private static final String KANJIS_HEADER = "Kanji information";
	private static final String REPEATING_DATES_HEADER = "Repeating dates";
	private static final String PROBLEMATIC_KANJIS_HEADER = "Problematic kanjis";
	private static final String SEPARATOR = "#";

	public KanjisAndRepeatingInfo readFile(File file) throws DuplicatedWordException, IOException {
		this.readedFile = file;
		in = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF8"));
		List <KanjiInformation> kanjiInformations = findWordsAndIDs(file);
		List <RepeatingInformation> repeatingInformations = findRepeatingInformations(file);
		Set <Integer> problematicKanjis = findProblematicKanjisNumbers();
		KanjisAndRepeatingInfo kanjisAndRepeatingInfo = new KanjisAndRepeatingInfo(kanjiInformations, repeatingInformations, problematicKanjis);
		in.close();
		return kanjisAndRepeatingInfo;
	}

	public void writeToFile(File f, MyList<KanjiInformation> listOfWords,
			MyList <RepeatingInformation> repeats, Set <Integer> problematicKanjis)
			throws IOException {
		BufferedWriter p = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(f), "UTF8"));
		List<KanjiInformation> list = listOfWords.getWords();
		p.write(KANJIS_HEADER);
		p.newLine();
		for (KanjiInformation kanji : list) {
			p.write(kanji.getKanjiKeyword() +SEPARATOR + kanji.getKanjiID() + SEPARATOR);
			p.newLine();
		}
		List<RepeatingInformation> repeatingInformations = repeats.getWords();
		p.write(REPEATING_DATES_HEADER);
		p.newLine();
		for (RepeatingInformation repeatingDate : repeatingInformations) {
			p.write(repeatingDate.getRepeatingRange()+SEPARATOR +
					repeatingDate.getRepeatingDate()+ SEPARATOR +
					repeatingDate.getTimeSpentOnRepeating()+ SEPARATOR);
			p.newLine();
		}
		p.write(PROBLEMATIC_KANJIS_HEADER);
		p.newLine();
		for (Integer i: problematicKanjis){
			p.write(i.toString());
			p.write("#");
		}
		p.close();
	}

	private List <KanjiInformation> findWordsAndIDs(File file)
			throws DuplicatedWordException, IOException {
		List <KanjiInformation> kanjiInformations = new ArrayList<>();
		String line = in.readLine();
		if (!line.equals(KANJIS_HEADER)){
			throw new IllegalArgumentException(ExceptionsMessages.ILLEGAL_LIST_FILE_FORMAT);
			//TODO be more specific with these exceptions: say what's wrong and where
		}
		while (!(line = in.readLine()).matches( HEADERS_REGEX)) {
			int indexOfNextSeparator = -1;
			Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String kanjiKeyword = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String wordId = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			indexOfNextSeparator++;
			if (line.indexOf(SEPARATOR, indexOfNextSeparator)>0){
				throw new IllegalArgumentException(ExceptionsMessages.ILLEGAL_LIST_FILE_FORMAT);
			}
			int wordIdInt = Integer.parseInt(wordId);
			if (isIdDuplicated(wordIdInt, kanjiInformations)){
				openDesktopAndShowMessage(wordIdInt);
			}
			else if (isKeywordDefined(kanjiKeyword, kanjiInformations)){
				openDesktopAndShowMessage(kanjiKeyword, wordIdInt, kanjiInformations);
			}
			else {
				kanjiInformations.add(new KanjiInformation(kanjiKeyword, Integer.valueOf(wordIdInt)));
			}
		}

		return kanjiInformations;
	}

	private boolean isIdDuplicated (int id, List <KanjiInformation> kanjiInformations){
		for (KanjiInformation kanjiInformation: kanjiInformations){
			if (kanjiInformation.getKanjiID() == id){
				return true;
			}
		}
		return false;
	}

	private boolean isKeywordDefined (String keyword, List <KanjiInformation> kanjiInformations){
		for (KanjiInformation kanjiInformation: kanjiInformations){
			if (kanjiInformation.getKanjiKeyword().equals(keyword)){
				return true;
			}
		}
		return false;
	}

	private Set <Integer> findProblematicKanjisNumbers () throws IOException {
		Set <Integer> information = new HashSet<>();
		String line;
		while ((line = in.readLine()) != null) {
			int indexOfNextSeparator = -1;

			while(indexOfNextSeparator<line.length()-1){
				Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
				String problematicKanji = wordAndIndex.getKey();
				indexOfNextSeparator = wordAndIndex.getValue();
				information.add(Integer.parseInt(problematicKanji));
			}
		}
		return information;
	}

	private List <RepeatingInformation> findRepeatingInformations (File file)
			throws DuplicatedWordException, IOException {
		List <RepeatingInformation> information = new ArrayList<>();
		String line;

		while (!(line = in.readLine()).matches( HEADERS_REGEX)) {
			int indexOfNextSeparator = -1;
			Pair<String, Integer> wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String ranges = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String date = wordAndIndex.getKey();
			indexOfNextSeparator = wordAndIndex.getValue();
			wordAndIndex = getNextSeparatedWord(line, indexOfNextSeparator);
			String timeSpent = wordAndIndex.getKey();
			RepeatingInformation r = new RepeatingInformation(ranges, LocalDateTime.parse(date), true, timeSpent);
			information.add(r);

		}
		return information;
	}

	private Pair<String, Integer> getNextSeparatedWord (String line, int indexOfSeparator){
		indexOfSeparator++;
		int indexOfSearchStart = indexOfSeparator;
		indexOfSeparator = line.indexOf(SEPARATOR,
				indexOfSearchStart);
		return new Pair <> (line.substring(indexOfSearchStart, indexOfSeparator), indexOfSeparator);

	}

	private void openDesktopAndShowMessage(int wordId) throws DuplicatedWordException, IOException {
		Desktop.getDesktop().open(this.readedFile);
		//TODO if the file has wrong extension we get exception: no application associated for this application with this file
		throw new DuplicatedWordException(ExceptionsMessages.DUPLICATED_ID_EXCEPTION + " " + NUMBER_TEXT + wordId);
	}

	private void openDesktopAndShowMessage(String duplicatedWord, int wordId,
			List <KanjiInformation> kanjiInformations) throws DuplicatedWordException, IOException {
		Desktop.getDesktop().open(this.readedFile);
		throw new DuplicatedWordException(
				ExceptionsMessages.DUPLICATED_WORD_EXCEPTION + " " + WORD_TEXT + duplicatedWord + "; "
						+ NUMBER_TEXT + wordId + " oraz " + getKeywordID(duplicatedWord, kanjiInformations));
	}

	private int getKeywordID(String keyword, List <KanjiInformation> kanjiInformations) {
		for (KanjiInformation kanjiInformation: kanjiInformations) {
			return kanjiInformation.getKanjiID();
		}
		return -1;
	}
}