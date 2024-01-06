package com.kanji.list.listElementPropertyManagers.japaneseWordWritings;

import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListElementPropertyManager;
import com.kanji.constants.enums.TypeOfJapaneseWriting;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElementPropertyManagers.WordSearchOptionsHolder;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;
import com.kanji.utilities.JapaneseWritingUtilities;

import javax.swing.text.JTextComponent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JapaneseWordWritingsChecker  {

	public static boolean areWritingsEqual(JapaneseWriting searchedWriting,
			JapaneseWriting writing, InputGoal inputGoal) {
		Set<String> searchedKanjiWritings = searchedWriting.getKanjiWritings();
		String searchedKanaWriting = searchedWriting.getKanaWriting();
		return kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
				searchedKanaWriting, writing.getKanaWriting(),
				searchedKanjiWritings, writing.getKanjiWritings(), inputGoal);

	}

	private static boolean kanaWritingsAreEqualAndKanjiWritingsContainAllOtherKanjiWritings(
			String searchedKana, String existingWordKana,
			Set<String> searchedKanji, Set<String> existingKanjiWritings,
			InputGoal inputGoal) {

		if (JapaneseWritingUtilities.isKanaEmpty(searchedKana)
				&& JapaneseWritingUtilities.isKanaEmpty(existingWordKana)) {
			return true;
		}

		if (JapaneseWritingUtilities.isInputEmpty(searchedKana,
				TypeOfJapaneseWriting.KANA)) {
			return areKanjisSame(searchedKanji, existingKanjiWritings,
					inputGoal);
		}
		else {
			if (searchedKana.equals(existingWordKana)) {
				return areKanjisSame(searchedKanji, existingKanjiWritings,
						inputGoal);
			}
			else {
				return false;
			}
		}

	}

	private static boolean areKanjisSame(Set<String> searchedKanji,
			Set<String> existingKanjiWritings, InputGoal inputGoal) {

		if (inputGoal.equals(InputGoal.SEARCH)) {
			if (JapaneseWritingUtilities.areKanjiWritingsEmpty(searchedKanji)) {
				return true;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji);
			}
		}
		else {
			if (JapaneseWritingUtilities.areKanjiWritingsEmpty(
					existingKanjiWritings)
					&& JapaneseWritingUtilities.areKanjiWritingsEmpty(
					searchedKanji)) {
				return true;
			}
			else if (JapaneseWritingUtilities.areKanjiWritingsEmpty(
					existingKanjiWritings)
					!= JapaneseWritingUtilities.areKanjiWritingsEmpty(
					searchedKanji)) {
				return false;
			}
			else {
				return existingKanjiWritings.containsAll(searchedKanji)
						|| searchedKanji.containsAll(existingKanjiWritings);
			}
		}
	}

}
