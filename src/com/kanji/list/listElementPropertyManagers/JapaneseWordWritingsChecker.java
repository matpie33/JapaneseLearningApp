package com.kanji.list.listElementPropertyManagers;

import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.panelCreators.JapaneseWordPanelCreator;
import com.kanji.model.KanaAndKanjiTextFields;
import com.kanji.utilities.StringUtilities;
import com.kanji.utilities.WordSearching;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JapaneseWordWritingsChecker extends WordSearchOptionsHolder implements
		ListElementPropertyManager<KanaAndKanjiTextFields, JapaneseWordInformation> {
	//TODO I hate to create a class which is veeery similar to each other for every word element
	private JapaneseWordPanelCreator japaneseWordPanelCreator;
	private String errorDetails = "";

	public JapaneseWordWritingsChecker (JapaneseWordPanelCreator
			japaneseWordPanelCreator){
		this.japaneseWordPanelCreator = japaneseWordPanelCreator;
	}

	private List <String> convertKanjiTextFieldsToTheirStringValues (
			List <JTextComponent> kanjiTextFields){
		List<String> kanjiWritings = kanjiTextFields.stream()
				.map(JTextComponent::getText).filter(s-> !s.isEmpty()).collect(Collectors.toList());
		return kanjiWritings;
	}

	@Override public String getInvalidPropertyReason() {

				return errorDetails;
	}

	@Override
	public boolean isPropertyFound(KanaAndKanjiTextFields kanaAndKanjiTextFields,
			JapaneseWordInformation wordInformation) {
		if (kanaAndKanjiTextFields.getModifiedTextFieldValue().isEmpty()){
			return false;
		}
		List <String> kanjiWritings = convertKanjiTextFieldsToTheirStringValues(
				kanaAndKanjiTextFields.getKanjiTextFields());
		String kanaWriting = kanaAndKanjiTextFields.getKanaTextField().getText();

		for (Map.Entry<String, List<String>> kanaToKanjis:
				wordInformation.getKanaToKanjiWritingsMap().entrySet()){
			if (kanaToKanjis.getKey().equals(kanaWriting) && !
					kanjiWritings.isEmpty() &&
					kanaToKanjis.getValue().containsAll(kanjiWritings)){
				return true;
			}
		}
		return false;
	}

	@Override
	public KanaAndKanjiTextFields convertTextInputToProperty(JTextComponent valueToConvert) {
		errorDetails = "";
		for (Map.Entry<JTextComponent, List <JTextComponent>>
				kanaToKanjiWritingsTextFields: japaneseWordPanelCreator
				.getKanaToKanjiWritingsTextComponents().entrySet()){
			boolean kanaModified = false;
			boolean foundTextField = false;
			boolean invalidInput = false;
			if (kanaToKanjiWritingsTextFields.getKey().equals(valueToConvert)){
				kanaModified = true;
				foundTextField = true;
				String kanaText = valueToConvert.getText();
				if (!StringUtilities.wordIsInKana(kanaText)){
					errorDetails +=
							String.format(ExceptionsMessages.KANA_WRITING_INCORRECT,
									kanaText);
					invalidInput = true;
				}
			}
			else if (kanaToKanjiWritingsTextFields.getValue().contains
					(valueToConvert)){
				kanaModified = false;
				foundTextField = true;
				String kanjiText = valueToConvert.getText();
				if (!kanjiText.isEmpty() &&
						!StringUtilities.wordIsInKanji(kanjiText)){
					errorDetails +=
							String.format(
									ExceptionsMessages.KANJI_WRITING_INCORRECT,
									kanjiText);
					invalidInput = true;
				}
			}
			if (!foundTextField){
				continue;
			}


			if (invalidInput){
				return null;
			}
			if (foundTextField){
				return new KanaAndKanjiTextFields(kanaToKanjiWritingsTextFields.getKey(),
						kanaToKanjiWritingsTextFields.getValue(),
						valueToConvert.getText(), kanaModified);
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

	@Override
	public void replaceProperty (JapaneseWordInformation propertyHolder,
			KanaAndKanjiTextFields oldValue, KanaAndKanjiTextFields newValue){
				if (oldValue.getModifiedTextFieldValue().equals(
						newValue.getModifiedTextFieldValue())){
					return;
				}
				List <String> newKanjiWritings = convertKanjiTextFieldsToTheirStringValues
						(newValue.getKanjiTextFields());
				if (newValue.isKanaModified()){
					propertyHolder.getKanaToKanjiWritingsMap().remove(
							oldValue.getModifiedTextFieldValue()
					);
				}
				propertyHolder.getKanaToKanjiWritingsMap().put(newValue
					.getKanaTextField().getText(), newKanjiWritings);
	}

}
