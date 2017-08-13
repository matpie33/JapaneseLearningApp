package com.kanji.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.constants.ButtonsNames;
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
		try {
			createButtonsPanel(addListeners());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mainPanel.addRow(RowMaker.createBothSidesFilledRow(listsSplitPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(buttonsPanel.getPanel()));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(infoPanel.getPanel()));
	}

	private List<JButton> addListeners() throws Exception {
		List<JButton> buttons = new ArrayList<>();
		for (String name : ButtonsNames.mainPageButtonNames) {
			int keyEvent;
			AbstractAction action;
			String hotkeyDescription;

			switch (name) {
			case ButtonsNames.buttonOpenText:
				hotkeyDescription = HotkeysDescriptions.OPEN_LOAD_KANJI_DIALOG;
				keyEvent = KeyEvent.VK_Q;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						maker.openKanjiFile();
					}
				};
				break;
			case ButtonsNames.buttonAddText:
				hotkeyDescription = HotkeysDescriptions.ADD_WORD;
				keyEvent = KeyEvent.VK_I;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						maker.addWord();
					}
				};
				break;
			case ButtonsNames.buttonSearchText:
				hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
				keyEvent = KeyEvent.VK_F;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						maker.searchWord();
					}
				};
				break;
			case ButtonsNames.buttonStartText:
				hotkeyDescription = HotkeysDescriptions.OPEN_START_LEARNING_DIALOG;
				keyEvent = KeyEvent.VK_R;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						maker.startLearning();
					}
				};
				break;
			case ButtonsNames.buttonSaveText:
				hotkeyDescription = HotkeysDescriptions.SAVE_PROJECT;
				keyEvent = KeyEvent.VK_S;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						maker.showSaveDialog();
					}
				};
				break;
			case ButtonsNames.buttonSaveListText:
				hotkeyDescription = HotkeysDescriptions.EXPORT_LIST;
				keyEvent = KeyEvent.VK_T;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						maker.exportList();
					}
				};
				break;
			default:
				throw new Exception("Unsupported button name");
			}
			buttons.add(createButtonWithHotkey(KeyEvent.VK_CONTROL, keyEvent, action, name,
					hotkeyDescription));
		}
		return buttons;
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
		infoPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		saveInfo = new JLabel();
		showProblematicKanjis = createShowProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		infoPanel.addRow(RowMaker.createUnfilledRow(Anchor.WEST, saveInfo));
	}

	private JButton createShowProblematicKanjiButton() {
		JButton problematicKanjiButton = new JButton(ButtonsNames.buttonShowProblematicKanji);
		problematicKanjiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.showProblematicKanjiDialog();
			}
		});
		return problematicKanjiButton;
	}

	private void createButtonsPanel(List<JButton> list) {
		buttonsPanel = new MainPanel(null);
		buttonsPanel
				.addRow(RowMaker.createUnfilledRow(Anchor.WEST, list.toArray(new JButton[] {})));
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
				+ Prompts.problematicKanjiPrompt
				+ applicationWindow.getStartingController().getProblematicKanjis().size());

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
