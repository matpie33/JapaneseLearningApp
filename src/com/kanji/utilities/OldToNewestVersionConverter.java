package com.kanji.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OldToNewestVersionConverter {

	private static Map<String, String> replacements = new HashMap<>();

	static {
		replacements.put("com.kanji.listElements.KanjiInformation",
				"com.kanji.list.listElements.KanjiInformation");
		replacements.put("com.kanji.listElements.RepeatingInformation",
				"com.kanji.list.listElements.RepeatingInformation");
		replacements.put("com.kanji.enums.ApplicationSaveableState",
				"com.kanji.constants.enums.ApplicationSaveableState");
	}

	public static void convertPreviousToNewestFile(File file) throws IOException {
		Files.copy(file.toPath(), Paths.get(file.getPath() + "1"));
		byte[] content = new byte[(int) file.length()];

		FileInputStream fileStream = new FileInputStream(file);
		fileStream.read(content);
		fileStream.close();

		Map<Integer, Map.Entry<String, String>> indexAndReplacement = new TreeMap<>();
		byte[] result = new byte[content.length + calculateBytesDifference()];

		int numberOfElementsInResult = 0;
		for (Map.Entry<String, String> replacementEntry : replacements.entrySet()) {
			byte[] toReplace = replacementEntry.getKey().getBytes();
			int index = indexOf(content, toReplace);
			if (index > -1) {
				indexAndReplacement.put(index, replacementEntry);
			}
		}

		if (indexAndReplacement.isEmpty()) {
			return;
		}

		int copyStartIndex = 0;
		for (Map.Entry<Integer, Map.Entry<String, String>> entry : indexAndReplacement.entrySet()) {
			Map.Entry<String, String> replacementEntry = entry.getValue();
			byte[] toReplace = replacementEntry.getKey().getBytes();
			String replacement = replacementEntry.getValue();
			int index = entry.getKey();
			int replacementLength = replacement.length();
			char length = (char) replacementLength;
			replacement = length + replacement;
			byte[] replacementBytes = replacement.getBytes();
			System.arraycopy(content, copyStartIndex, result, numberOfElementsInResult /*sure?*/,
					index - 1 - copyStartIndex);
			numberOfElementsInResult += index - 1 - copyStartIndex;
			copyStartIndex = index - 1 + toReplace.length + 1;
			System.arraycopy(replacementBytes, 0, result, numberOfElementsInResult,
					replacement.length());
			numberOfElementsInResult += replacement.length();
		}
		System.arraycopy(content, copyStartIndex, result, numberOfElementsInResult /*sure?*/,
				content.length - copyStartIndex);
		FileOutputStream out = new FileOutputStream(file);
		out.write(result);
		out.close();
	}

	private static int calculateBytesDifference() {
		int difference = 0;
		for (Map.Entry<String, String> entry : replacements.entrySet()) {
			difference += entry.getValue().length() - entry.getKey().length();
		}
		return difference;
	}

	private static int indexOf(byte[] outerArray, byte[] smallerArray) {
		for (int i = 0; i < outerArray.length - smallerArray.length + 1; ++i) {
			boolean found = true;
			for (int j = 0; j < smallerArray.length; ++j) {
				if (outerArray[i + j] != smallerArray[j]) {
					found = false;
					break;
				}
			}
			if (found)
				return i;
		}
		return -1;
	}

}
