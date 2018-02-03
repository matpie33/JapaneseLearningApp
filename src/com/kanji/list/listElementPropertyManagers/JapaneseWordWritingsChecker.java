package com.kanji.list.listElementPropertyManagers;

import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.model.KanaAndKanjiTextFields;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<KanaAndKanjiTextFields, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private RowInJapaneseWordInformations rowInJapaneseWordInformations;
	public JapaneseWordWritingsChecker (RowInJapaneseWordInformations row){
		rowInJapaneseWordInformations = row;
	}

	private List <String> convertKanjiTextFieldsToTheirStringValues (
			List <JTextComponent> kanjiTextFields){
		List<String> kanjiWritings = kanjiTextFields.stream()
				.map(JTextComponent::getText).collect(Collectors.toList());
		return kanjiWritings;
	}

	@Override public String getInvalidPropertyReason() {
		return "TODO validacja";
	}

	@Override
	public boolean isPropertyFound(KanaAndKanjiTextFields kanaAndKanjiTextFields,
			JapaneseWordInformation wordInformation) {
		List <String> kanjiWritings = convertKanjiTextFieldsToTheirStringValues(
				kanaAndKanjiTextFields.getKanjiTextFields());
		String kanaWriting = kanaAndKanjiTextFields.getKanaTextField().getText();
		List <String> kanjiWritingsInJapaneseWordForGivenKana =
				wordInformation.getKanaToKanjiWritingsMap().get(kanaWriting);
		if (kanjiWritingsInJapaneseWordForGivenKana != null){
			return kanjiWritings.equals(kanjiWritingsInJapaneseWordForGivenKana);
		}
		return false;
	}

	@Override
	public KanaAndKanjiTextFields convertTextInputToProperty(JTextComponent valueToConvert) {
		for (Map.Entry<JTextComponent, List <JTextComponent>> kanaToKanjiWritingsTextFields:
				rowInJapaneseWordInformations.getKanaToKanjiWritingsTextComponents()
						.entrySet()){
			if (kanaToKanjiWritingsTextFields.getKey().equals(valueToConvert)){
				return new KanaAndKanjiTextFields(kanaToKanjiWritingsTextFields.getKey(),
						kanaToKanjiWritingsTextFields.getValue());
			}
		}
		return null;
	}
	//TODO add validation

	@Override
	public void setProperty(JapaneseWordInformation japaneseWordInformation,
			KanaAndKanjiTextFields kanaAndKanjiTextFields) {
		List <String> kanjiWritings = convertKanjiTextFieldsToTheirStringValues(
				kanaAndKanjiTextFields.getKanjiTextFields());
		String kanaWriting = kanaAndKanjiTextFields.getKanaTextField().getText();
		japaneseWordInformation.addWritings(kanaWriting, kanjiWritings.toArray(
				new String [] {}));
	}

}
