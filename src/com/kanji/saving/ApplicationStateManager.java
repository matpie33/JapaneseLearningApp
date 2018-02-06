package com.kanji.saving;

public interface ApplicationStateManager {
	public SavingInformation getApplicationState();

	public void restoreState(SavingInformation savingInformation);
}
