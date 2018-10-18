package com.kanji.application;

import com.kanji.windows.ApplicationWindow;

public interface ApplicationChangesManager {

	public boolean isClosingSafe();
	public void save();
	public ApplicationWindow getApplicationWindow();

}
