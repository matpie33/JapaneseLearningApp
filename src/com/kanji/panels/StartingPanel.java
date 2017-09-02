package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
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
	private JLabel problematicKanjis;

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
		mainPanel.addRow(new SimpleRow(FillType.BOTH, listsSplitPane));
		addHotkeysPanelHere();
		mainPanel.addRows(new SimpleRow(FillType.HORIZONTAL, buttonsPanel.getPanel())
				.nextRow(infoPanel.getPanel()));
	}

	private List<AbstractButton> addListeners() throws Exception {
		List<AbstractButton> buttons = new ArrayList<>();
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
		infoPanel.setGapsRightSideBetweenColumnsTo(20);
		infoPanel.setRightBorder();
		saveInfo = new JLabel();
		problematicKanjis = new JLabel();
		showProblematicKanjis = createShowProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		updateProblematicKanjisAmount(applicationController.getProblematicKanjis().size());
		infoPanel.addRow(new SimpleRow(FillType.NONE, Anchor.WEST, saveInfo, problematicKanjis));
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

	private void createButtonsPanel(List<AbstractButton> list) {
		buttonsPanel = new MainPanel(null);
		buttonsPanel
				.addRow(new SimpleRow(FillType.NONE, Anchor.WEST, list.toArray(new JButton[] {})));
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
	}

	public void updateProblematicKanjisAmount(int problematicKanjisNumber) {
		problematicKanjis.setText(Prompts.PROBLEMATIC_KANJI + problematicKanjisNumber);
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
