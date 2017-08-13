package com.kanji.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingList;

public class LoadingAndSaving {

	private File fileToSave;

	public boolean hasFileToSave() {
		return fileToSave != null;
	}

	public void setFileToSave(File file) {
		fileToSave = file;
	}

	public void save(SavingInformation savingInformation) throws IOException {
		FileOutputStream fout = new FileOutputStream(this.fileToSave);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(savingInformation.getKanjiWords());
		oos.writeObject(savingInformation.getRepeatingList());
		oos.writeObject(savingInformation.getProblematicKanjis());
		fout.close();
	}

	public SavingInformation load() throws Exception {
		final FileInputStream fout = new FileInputStream(fileToSave);
		final ObjectInputStream oos = new ObjectInputStream(fout);

		Object kanjiWords = oos.readObject();
		Object repeatingList = oos.readObject();
		Object problematicKanjis = oos.readObject();

		if (kanjiWords instanceof KanjiWords == false
				|| repeatingList instanceof RepeatingList == false
				|| problematicKanjis instanceof Set == false) {
			throw new Exception("Incompatible information loaded compared to saving informations");
		}

		// TODO it's unsafe operation, need refactor
		return new SavingInformation((KanjiWords) kanjiWords, (RepeatingList) repeatingList,
				(Set) problematicKanjis);
	}

}
