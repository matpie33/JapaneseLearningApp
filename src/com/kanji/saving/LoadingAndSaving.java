package com.kanji.saving;

import java.io.*;

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
		oos.writeObject(savingInformation);
		fout.close();
	}

	public SavingInformation load(File file) throws Exception {
		final FileInputStream fout = new FileInputStream(file);
		final ObjectInputStream oos = new ObjectInputStream(fout);

		Object savingInformation = oos.readObject();
		oos.close();
		fout.close();

		if (savingInformation instanceof SavingInformation == false) {
			throw new Exception(
					"Incompatible information loaded compared to saving informations");
		}

		return (SavingInformation) savingInformation;
	}

}
