package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.utilities.ApplicationController;
import com.kanji.windows.ApplicationWindow;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private ApplicationController applicationController;

	private JSplitPane listsSplitPane;
	private MainPanel buttonsPanel;
	private MainPanel infoPanel;
	private JButton showProblematicKanjis;
	private JLabel saveInfo;
	private ApplicationWindow applicationWindow;
	private int infoPanelComponentsRow = 0;

	public StartingPanel(ApplicationWindow a, ApplicationController maker) {
		applicationWindow = a;
		this.applicationController = maker;
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
		for (String name : ButtonsNames.BUTTONS_ON_MAIN_PAGE) {
			int keyEvent;
			AbstractAction action;
			String hotkeyDescription;

			switch (name) {
			case ButtonsNames.OPEN:
				hotkeyDescription = HotkeysDescriptions.OPEN_LOAD_KANJI_DIALOG;
				keyEvent = KeyEvent.VK_Q;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.openKanjiProject();
					}
				};
				break;
			case ButtonsNames.ADD:
				hotkeyDescription = HotkeysDescriptions.ADD_WORD;
				keyEvent = KeyEvent.VK_I;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.addWord();
					}
				};
				break;
			case ButtonsNames.SEARCH:
				hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
				keyEvent = KeyEvent.VK_F;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.searchWord();
					}
				};
				break;
			case ButtonsNames.START:
				hotkeyDescription = HotkeysDescriptions.OPEN_START_LEARNING_DIALOG;
				keyEvent = KeyEvent.VK_R;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.startLearning();
					}
				};
				break;
			case ButtonsNames.SAVE:
				hotkeyDescription = HotkeysDescriptions.SAVE_PROJECT;
				keyEvent = KeyEvent.VK_S;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.showSaveDialog();
					}
				};
				break;
			case ButtonsNames.SAVE_LIST:
				hotkeyDescription = HotkeysDescriptions.EXPORT_LIST;
				keyEvent = KeyEvent.VK_T;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.exportList();
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
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				applicationController.getWordsList().getPanel(),
				applicationController.getRepeatsList().getPanel());
	}

	private void createInformationsPanel() {
		infoPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		saveInfo = new JLabel();
		showProblematicKanjis = createShowProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		infoPanel.addRow(RowMaker.createUnfilledRow(Anchor.WEST, saveInfo));
	}

	private JButton createShowProblematicKanjiButton() {
		JButton problematicKanjiButton = new JButton(ButtonsNames.SHOW_PROBLEMATIC_KANJIS);
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

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus() + "; "
				+ Prompts.PROBLEMATIC_KANJI
				+ applicationWindow.getApplicationController().getProblematicKanjis().size());

		// TODO separate the save info to 2 different labels, and in this method
		// only change 1 of them

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
