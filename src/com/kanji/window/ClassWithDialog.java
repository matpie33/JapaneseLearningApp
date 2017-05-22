package com.kanji.window;

import java.awt.Point;
import java.util.Set;

import javax.swing.JFrame;

import com.kanji.Row.KanjiWords;
import com.kanji.dialogs.LoadingPanel;
import com.kanji.dialogs.MyDialog;
import com.kanji.myList.MyList;

@SuppressWarnings("serial")
public abstract class ClassWithDialog extends JFrame {
	private MyDialog dialog;
	private boolean isExcelReaderLoaded;

	// TODO remove this class, add dialog property to base window, add method
	// "openNewWindow" in new dialog
	// TODO which will check whether dialog is opened or not, and will take as
	// parameter panel to be painted

	public void showDialogToSearch(MyList list) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this); // TODO moze skrocic?
			dialog.showSearchWordDialog(list);
			dialog.setLocationAtLeftUpperCornerOfParent(this);
		}

	}

	private boolean notOpenedYet() {
		return (dialog == null || !dialog.isDisplayable()); // TODO
															// try
															// to
															// avoid
		// nulls
	}

	public void showDialogToAddWord(MyList list) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showInsertDialog(list);
			// dialog.setLocationAtLeftUpperCornerOfParent(this);
			dialog.setLocation(getRightComponentOfSplitPanePosition());
		}
	}

	public LoadingPanel showProgressDialog() {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			LoadingPanel p = dialog.showProgressDialog();
			dialog.setLocationAtCenterOfParent(this);
			return p;
		}
		return null;
	}

	public void showMessageDialog(String message, boolean modal) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showMsgDialog(message, modal);
			dialog.setLocationAtCenterOfParent(this);
		}
	}

	public void showLearnStartDialog(MyList list, int maximumNumber) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showLearningStartDialog(list, maximumNumber);
			dialog.setLocationRelativeTo(this);
		}
	}

	public void showProblematicKanjiDialog(KanjiWords kanjiWords, Set<Integer> problematicKanjis) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);
			dialog.showProblematicKanjiDialog(kanjiWords, problematicKanjis);
			dialog.setLocationRelativeTo(this);
		}
	}

	public boolean showConfirmDialog(String prompt) {
		if (notOpenedYet()) {
			dialog = new MyDialog(this);

			dialog.showConfirmDialog(prompt);

			// dialog.setLocationAtCenterOfParent(this);
			// dialog.setLocationRelativeTo(null);

		}
		return dialog.isAccepted();
	}

	public boolean isDialogOpened() {
		return dialog.isDisplayable();
	}

	public void closeDialog() {
		dialog.dispose();
	}

	public abstract Point getRightComponentOfSplitPanePosition();

}
