package com.kanji.fileReading;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class KanjiCharactersReader {

	private Font font;
	private List<String> words;
	private static KanjiCharactersReader kanjiCharsReader;

	private KanjiCharactersReader() {
		font = new Font("MS PMincho", Font.BOLD, 100);
		words = new ArrayList<String>();
	}

	public static KanjiCharactersReader getInstance() {
		if (kanjiCharsReader == null) {
			kanjiCharsReader = new KanjiCharactersReader();
		}
		return kanjiCharsReader;
	}

	public void loadKanjisIfNeeded() {
		if (!words.isEmpty()) {
			System.out.println("!@#$% no need");
			return;
		}

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream("kanjis.txt"), "Utf-8"));
			String line = "";
			while ((line = br.readLine()) != null) {
				words.add(line);
			}
			String first = words.get(0).replace("\uFEFF", "");
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

	public Font getFont() {
		return font;
	}

}
