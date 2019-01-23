package com.kanji.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class KanjiCharactersReader {

	private List<String> words;
	private static KanjiCharactersReader kanjiCharsReader;

	private KanjiCharactersReader() {
		words = new ArrayList<>();
	}

	public static KanjiCharactersReader getInstance() {
		if (kanjiCharsReader == null) {
			kanjiCharsReader = new KanjiCharactersReader();
		}
		return kanjiCharsReader;
	}

	public void loadKanjisIfNeeded() {
		if (!words.isEmpty()) {
			return;
		}
		words.add("placeholder");

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getClass().getClassLoader()
							  .getResourceAsStream("kanjis.txt"), "Utf-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				words.add(line);
			}
			String first = words.get(0)
								.replace("\uFEFF", "");
			words.remove(0);
			words.add(0, first);
			br.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getKanjiById(int id) {
		return words.get(id);
	}

	public int getIdOfKanji(String kanji) {
		return words.indexOf(kanji);
	}

}
