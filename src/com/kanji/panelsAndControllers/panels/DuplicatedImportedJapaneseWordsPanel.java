package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.FillType;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listRows.RowInDuplicatedImportedWordsList;
import com.kanji.list.myList.MyList;
import com.kanji.model.DuplicatedJapaneseWordInformation;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

public class DuplicatedImportedJapaneseWordsPanel
		extends AbstractPanelWithHotkeysInfo {

	private int nextRowNotHighlighted = 0;
	private ApplicationWindow applicationWindow;
	private List<DuplicatedJapaneseWordInformation> duplicatedImportedJapaneseWordsPanelList;

	public DuplicatedImportedJapaneseWordsPanel(
			ApplicationWindow applicationWindow,
			List<DuplicatedJapaneseWordInformation> duplicatedImportedJapaneseWordsPanelList) {
		this.applicationWindow = applicationWindow;
		this.duplicatedImportedJapaneseWordsPanelList = duplicatedImportedJapaneseWordsPanelList;
	}

	@Override
	public void createElements() {
		MyList<DuplicatedJapaneseWordInformation> dup = new MyList<>(
				getDialog(), applicationWindow.getApplicationController(),
				new RowInDuplicatedImportedWordsList(applicationWindow,
						getDialog(),
						applicationWindow.getApplicationController()
								.getJapaneseWords()),
				Titles.DUPLICATED_WORDS_PANEL, false, null, null);
		for (DuplicatedJapaneseWordInformation word : duplicatedImportedJapaneseWordsPanelList) {
			dup.addWord(word);
			System.out.println("adding: " + word);
		}
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, dup.getPanel()));
		addHotkey(KeyEvent.VK_SPACE, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dup.highlightRow(nextRowNotHighlighted);
				applicationWindow.getApplicationController().getJapaneseWords().
						highlightRow(duplicatedImportedJapaneseWordsPanelList
								.get(nextRowNotHighlighted)
								.getDuplicatedWordRowNumber());
				nextRowNotHighlighted++;
			}
		}, mainPanel.getPanel(), "desc");

	}

	private void createButtonGoToNextRow() {

	}

}
