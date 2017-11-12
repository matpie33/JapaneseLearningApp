package com.kanji.saving;

public interface ApplicationStateManager {

	public SavingInformation getApplicationState();
	public void stop();
	public void resume();
}
