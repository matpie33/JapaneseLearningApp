package com.kanji.webPageEnhancer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kanji.constants.enums.EnglishTranslationDirection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class EnglishDictionaryCaller {

	//TODO move to guimaker

	private final static String DICTIONARY_API_TEMPLATE = "https://glosbe"
			+ ".com/gapi/translate?from=%s&dest=%s&format=json&phrase"
			+ "=%s&pretty=true";
	private static final String ROOT_NODE = "tuc";
	private static final String MEANING_ROOT_NODE = "phrase";
	private static final String MEANING_CHILD_NODE = "text";

	public List<String> callEnglishDictionary(String wordToCheck,
			EnglishTranslationDirection englishTranslationDirection)
			throws IOException {
		URLConnection request = makeApiCallToDictionary(wordToCheck,
				englishTranslationDirection);
		List<String> meanings = getWordMeaningsFromJSON(request);
		return meanings;
	}

	private List<String> getWordMeaningsFromJSON(URLConnection request)
			throws IOException {
		JsonParser parser = new JsonParser();
		JsonElement root = parser.parse(
				new InputStreamReader((InputStream) request.getContent()));
		JsonObject rootObject = root.getAsJsonObject();
		JsonArray nodesList = rootObject.getAsJsonArray(ROOT_NODE);

		List<String> meanings = new ArrayList<>();
		for (JsonElement jsonElement : nodesList) {
			JsonObject meaningNode = jsonElement.getAsJsonObject();
			if (meaningNode.has(MEANING_ROOT_NODE)) {
				JsonObject phrase = meaningNode.getAsJsonObject(
						MEANING_ROOT_NODE);
				String text = phrase.getAsJsonPrimitive(MEANING_CHILD_NODE)
									.getAsString();
				meanings.add(text);
			}
		}
		return meanings;
	}

	private URLConnection makeApiCallToDictionary(String wordToCheck,
			EnglishTranslationDirection englishTranslationDirection)
			throws IOException {
		URL url = new URL(String.format(DICTIONARY_API_TEMPLATE,
				englishTranslationDirection.getSourceLanguageAbbreviation(),
				englishTranslationDirection.getDestionationLanguageAbbreviation(),
				wordToCheck));
		URLConnection request = url.openConnection();
		request.connect();
		return request;
	}

}
