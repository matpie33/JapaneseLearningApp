package com.kanji.constants;

public enum SavingStatus {
	SAVING("Zapisywanie"), SAVED("Zapisano"), NO_CHANGES("Brak zmian");

	private String status;

	SavingStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
