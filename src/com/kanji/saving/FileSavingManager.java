package com.kanji.saving;

import com.kanji.model.saving.SavingInformation;

import java.io.File;
import java.io.IOException;

public class FileSavingManager {

	private static final String BACKUP_DIRECTORY_NAME = "backups";
	private static final int NUMBER_OF_BACKUP_FILES = 10;
	private static final String BACKUP_FILENAME = "backup";
	private final LoadingAndSaving loadingAndSaving;
	private int lastBackupFileNumber;
	private File originalFile;

	public FileSavingManager() {
		loadingAndSaving = new LoadingAndSaving();
	}

	public void doBackupFile(File originalFile,
			SavingInformation savingInformation) throws IOException {
		String pathToBackupDirectory = createDirectoryForBackupIfNotExists(
				originalFile);
		lastBackupFileNumber = increaseBackupFileNumber(savingInformation);
		saveFile(new File(constructBackupFilename(lastBackupFileNumber,
				pathToBackupDirectory)), savingInformation);
		savingInformation.setLastBackupFileNumber(lastBackupFileNumber);
		saveFile(originalFile, savingInformation);
		loadingAndSaving.setFileToSave(originalFile);
	}

	public void saveFile(File file, SavingInformation savingInformation)
			throws IOException {
		System.out.println("saving: " + file.getAbsolutePath());
		loadingAndSaving.setFileToSave(file);
		loadingAndSaving.save(savingInformation);
	}

	public void saveFile(SavingInformation savingInformation)
			throws IOException {
		saveFile(originalFile, savingInformation);
	}

	private int increaseBackupFileNumber(SavingInformation savingInformation) {
		return (savingInformation.getLastBackupFileNumber() + 1)
				% NUMBER_OF_BACKUP_FILES;
	}

	private String constructBackupFilename(int backupFileNumber,
			String pathToBackupDirectory) {
		return pathToBackupDirectory + "/" + BACKUP_FILENAME + backupFileNumber;
	}

	private String createDirectoryForBackupIfNotExists(File originalFile) {
		File file = new File(
				originalFile.getParent() + "/" + BACKUP_DIRECTORY_NAME);
		file.mkdirs();
		return file.getAbsolutePath();
	}

	public int getLastBackupFileNumber() {
		return lastBackupFileNumber;
	}

	public SavingInformation load(File file) throws Exception {
		originalFile = file;
		return loadingAndSaving.load(file);
	}

	public boolean hasFileToSave() {
		return originalFile != null;
	}

	public void setFileToSave(File fileToSave) {
		this.originalFile = fileToSave;
		loadingAndSaving.setFileToSave(fileToSave);
	}

	public String getFileDirectory(){
		return originalFile.getParent();
	}

}
