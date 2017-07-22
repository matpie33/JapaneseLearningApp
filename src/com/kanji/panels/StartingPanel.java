package com.kanji.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.myList.MyList;
import com.kanji.utilities.ElementMaker;
import com.kanji.windows.ApplicationWindow;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private JScrollPane listScrollWords;
	private JScrollPane listScrollRepeated;
	private ElementMaker maker;
	private final Dimension scrollPanesSize = new Dimension(300, 300);
	private final Dimension minimumListSize = new Dimension(200, 100);
	private JSplitPane listsSplitPane;
	private MainPanel buttonsPanel;
	private MainPanel infoPanel;
	private JButton showProblematicKanjis;
	private JLabel saveInfo;
	private ApplicationWindow applicationWindow;
	private int infoPanelComponentsRow = 0;

	public StartingPanel(ApplicationWindow a, ElementMaker maker) {
		applicationWindow = a;
		this.maker = maker;
	}

	@Override
	void createElements() {
		// TODO add line separator between panels
		// TODO info panel should have some kind of border or other background
		// color
		createUpperPanel();
		createInformationsPanel();
		createButtonsPanel(maker.getButtons());
		mainPanel.addRow(RowMaker.createBothSidesFilledRow(listsSplitPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(buttonsPanel.getPanel()));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(infoPanel.getPanel()));
	}

	@SuppressWarnings("serial")
	public void addHotkeys() {
		AbstractAction searchWord = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.showSearchWordDialog(maker.getWordsList());
			}
		};
		AbstractAction startLearningDialog = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.showLearningStartDialog(maker.getRepeatsList(),
						((KanjiWords) maker.getWordsList().getWords()).getNumberOfKanjis());
			}
		};
		AbstractAction loadKanjiDialog = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				maker.openKanjiFile();
			}
		};
		JRootPane rootPane = mainPanel.getPanel().getRootPane();
		addHotkey(KeyEvent.VK_CONTROL, KeyEvent.VK_Q, loadKanjiDialog, rootPane,
				HotkeysDescriptions.OPEN_LOAD_KANJI_DIALOG);
		addHotkey(KeyEvent.VK_CONTROL, KeyEvent.VK_R, startLearningDialog, rootPane,
				HotkeysDescriptions.OPEN_START_LEARNING_DIALOG);
		addHotkey(KeyEvent.VK_CONTROL, KeyEvent.VK_F, searchWord, rootPane,
				HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG);
	}

	@SuppressWarnings("rawtypes")
	private void createUpperPanel() {
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();
		listScrollWords = createScrollPaneForList(wordsList);
		listScrollRepeated = createScrollPaneForList(repeatsList);
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollWords,
				listScrollRepeated);
	}

	private void createInformationsPanel() {
		infoPanel = new MainPanel(null);
		saveInfo = new JLabel();
		showProblematicKanjis = maker.getProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		infoPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.WEST, saveInfo));
	}

	private void createButtonsPanel(List<JButton> list) {
		buttonsPanel = new MainPanel(null);
		buttonsPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.WEST,
				list.toArray(new JButton[] {})));
	}

	@SuppressWarnings("rawtypes")
	private JScrollPane createScrollPaneForList(MyList list) {
		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE, 6);
		JScrollPane listScrollWords = createScrollPane(BasicColors.OCEAN_BLUE, raisedBevel, list);
		list.setScrollPane(listScrollWords);
		listScrollWords.setMinimumSize(minimumListSize);
		return listScrollWords;
	}

	private JScrollPane createScrollPane(Color bgColor, Border border, Component component) {

		JScrollPane scroll = new JScrollPane(component);
		scroll.getViewport().setBackground(bgColor);
		scroll.setBorder(border);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.setPreferredSize(scrollPanesSize);
		return scroll;

	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.savingStatusPrompt + savingStatus.getStatus() + "; "
				+ Prompts.problematicKanjiPrompt + applicationWindow.getProblematicKanjis().size());

	}

	public void addButtonIcon() {
		infoPanel.addElementsToRow(infoPanelComponentsRow, showProblematicKanjis);
	}

	public void removeButtonProblematicsKanji() {
		if (infoPanel.rowContainsComponent(infoPanelComponentsRow, showProblematicKanjis)) {
			infoPanel.removeLastElementFromRow(infoPanelComponentsRow);
		}
	}

}
