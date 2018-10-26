package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.MoveDirection;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ProblematicWordsPanelCommonPart {

	private AbstractPanelWithHotkeysInfo problematicWordsPanel;
	private ProblematicWordsController problematicWordsController;

	public ProblematicWordsPanelCommonPart(
			AbstractPanelWithHotkeysInfo problematicWordsPanel,
			ProblematicWordsController problematicWordsController) {
		this.problematicWordsPanel = problematicWordsPanel;
		this.problematicWordsController = problematicWordsController;
	}

	public void addCommonPartToPanel() {
		problematicWordsPanel.setNavigationButtons(Anchor.WEST,
				createButtonReturn());
		initializeActionBrowseNextWord();
		initializeActionBrowsePreviousWord();
	}

	private AbstractButton createButtonReturn() {
		return problematicWordsPanel.createButtonWithHotkey(
				KeyModifiers.CONTROL, KeyEvent.VK_E,
				problematicWordsController.exitProblematicWordsPanel(),
				JapaneseApplicationButtonsNames.GO_BACK,
				HotkeysDescriptions.RETURN_FROM_LEARNING);
	}

	private void initializeActionBrowseNextWord() {
		initializeAction(KeyEvent.VK_SPACE,
				problematicWordsController.createActionShowNextWord(
						MoveDirection.BELOW),
				HotkeysDescriptions.SHOW_NEXT_PROBLEMATIC_WORD);

	}

	private void initializeActionBrowsePreviousWord() {
		initializeAction(KeyEvent.VK_BACK_SPACE,
				problematicWordsController.createActionShowNextWord(
						MoveDirection.ABOVE),
				HotkeysDescriptions.SHOW_PREVIOUS_PROBLEMATIC_WORD);
	}

	private void initializeAction(int hotkey, AbstractAction action,
			String actionDescription) {
		problematicWordsPanel.addHotkey(hotkey, action,
				problematicWordsPanel.getPanel(), actionDescription);
	}

}
