package com.kanji.utilities;

import com.kanji.constants.enums.AdditionalInformationTag;
import com.kanji.constants.enums.PartOfSpeech;
import com.kanji.constants.strings.Labels;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.model.AdditionalInformation;

import java.util.List;

public class JapaneseWordsAdjuster {

	public void replaceSuruInMeaningToAdditionalInformation(
			List<JapaneseWord> japaneseWords) {
		for (JapaneseWord word : japaneseWords) {
			String meaning = word.getMeaning();
			if (meaning.contains("suru") || meaning.contains("する")) {
				String suru;
				if (meaning.contains("suru")) {
					suru = "suru";
				}
				else {
					suru = "する";
				}

				meaning = meaning.replace("(+" + suru + "）", "");
				meaning = meaning.replace("(+ " + suru + "）", "");
				meaning = meaning.replace("(+" + suru + ")", "");
				meaning = meaning.replace("(+ " + suru + ")", "");
				meaning = meaning.replace("+" + suru, "");
				meaning = meaning.replace("+ " + suru, "");
				meaning = meaning.replace(" +" + suru, "");
				meaning = meaning.replace(" + " + suru, "");
				meaning = meaning.replace(" +　" + suru, "");
				meaning = meaning.replace("+　を" + suru, "");
				meaning = meaning.replace(suru, "");
				meaning = meaning.trim();
				if (meaning.indexOf(",") == meaning.length() - 1) {
					meaning = meaning.substring(0, meaning.length() - 1);
				}
				if (meaning.indexOf("、") == meaning.length() - 1) {
					meaning = meaning.substring(0, meaning.length() - 1);
				}
				meaning = meaning.trim();
				word.setPartOfSpeech(PartOfSpeech.NOUN);
				word.setAdditionalInformation(new AdditionalInformation(
						AdditionalInformationTag.TAKING_SURU, Labels.YES));
				word.setMeaning(meaning);
			}
		}
	}

}
