package com.kanji.saving;

import com.kanji.model.saving.SavingInformation;

public interface ApplicationStateManager {
	public SavingInformation getApplicationState();

	public void restoreState(SavingInformation savingInformation);

	public boolean isClosingSafe();
}
