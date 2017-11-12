package com.kanji.saving;

import com.kanji.utilities.SavingInformation;

public interface ApplicationStateManager {

	public SavingInformation getApplicationState();
	public void stop();
	public void resume();
}
