package com.kanji.list.listRows.japanesePanelCreatingComponents;

import com.guimaker.application.DialogWindow;
import com.guimaker.enums.InputGoal;
import com.guimaker.enums.MoveDirection;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.list.myList.MyList;
import com.guimaker.listeners.SwitchBetweenInputsFailListener;
import com.guimaker.utilities.Pair;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.JapaneseWriting;

import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JapaneseWordPanelCreator
		implements SwitchBetweenInputsFailListener {

	private final JapanesePanelActionsCreator actionsCreator;
	private MyList<JapaneseWriting> lastWritingsListCreated;
	private DialogWindow parentDialog;

	private ListInputsSelectionManager listInputsSelectionManager;
	private PanelDisplayMode displayMode;
	private List<Pair<JapaneseWord, MyList<JapaneseWriting>>> writingsLists = new ArrayList<>();
	//TODO it's the second place where map did not fit due to mutable keys,
	//can we do better than list of pairs?

	public JapaneseWordPanelCreator(DialogWindow parentDialog,
			PanelDisplayMode displayMode,
			JapanesePanelActionsCreator actionsCreator,
			ListInputsSelectionManager listInputsSelectionManager) {
		//TODO parent dialog is not needed without validation i.e. in view mode
		this.displayMode = displayMode;
		this.parentDialog = parentDialog;
		this.listInputsSelectionManager = listInputsSelectionManager;
		this.actionsCreator = actionsCreator;

	}

	public ListInputsSelectionManager getListInputsSelectionManager() {
		return listInputsSelectionManager;
	}

	@Override
	public void switchBetweenInputsFailed(JTextComponent input,
			MoveDirection direction) {
		if (direction.equals(MoveDirection.BELOW) && !displayMode.equals(
				PanelDisplayMode.VIEW)) {
			MyList<JapaneseWriting> writingsListToAddWriting = findListThatFailedInSwitchingBetweenInputs(
					input);
			writingsListToAddWriting.addWord(JapaneseWriting.getInitializer()
															.initializeElement());
			writingsListToAddWriting.scrollToBottom();
		}
		if ((direction.equals(MoveDirection.LEFT) || direction.equals(
				MoveDirection.RIGHT)) && displayMode.equals(
				PanelDisplayMode.VIEW)) {
			MyList<JapaneseWriting> listThatFailed = findListThatFailedInSwitchingBetweenInputs(
					input);
			listThatFailed.getPanelWithSelectedInput()
						  .clearSelectedInput();
		}
	}

	private MyList<JapaneseWriting> findListThatFailedInSwitchingBetweenInputs(
			JTextComponent input) {
		JapaneseWord wordContainingInput = actionsCreator.getWordContainingInput(
				input);
		MyList<JapaneseWriting> writingsListToAddWriting = null;
		if (wordContainingInput != null) {
			for (Pair<JapaneseWord, MyList<JapaneseWriting>> wordWithWritings : writingsLists) {
				if (wordWithWritings.getLeft()
									.equals(wordContainingInput)
						&& wordWithWritings.getRight()
										   .getPanel()
										   .isShowing()) {
					writingsListToAddWriting = wordWithWritings.getRight();
					break;
				}
			}
		}
		else {
			writingsListToAddWriting = lastWritingsListCreated;
		}
		return writingsListToAddWriting;
	}

	public MyList<JapaneseWriting> addWritings(
			MyList<JapaneseWriting> japaneseWritingsList,
			JapaneseWord japaneseWord, InputGoal inputGoal) {
		this.lastWritingsListCreated = japaneseWritingsList;
		writingsLists.add(new Pair<>(japaneseWord, lastWritingsListCreated));

		if (!japaneseWord.isEmpty()) {
			parentDialog.getPanel()
						.addNavigableByKeyboardList(lastWritingsListCreated);
		}

		if (japaneseWord.getWritings()
						.isEmpty()) {
			japaneseWord.addWritingsForKana("", "");
		}
		Set<JapaneseWriting> writings = japaneseWord.getWritings();
		Set<JapaneseWriting> duplicatedWritings = new HashSet<>(writings);
		duplicatedWritings.forEach(
				word -> lastWritingsListCreated.addWord(word, inputGoal));
		lastWritingsListCreated.addSwitchBetweenInputsFailListener(this);
		return lastWritingsListCreated;
	}
}
