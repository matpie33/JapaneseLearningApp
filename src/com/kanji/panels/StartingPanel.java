package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.strings.Prompts;
import com.kanji.enums.SavingStatus;
import com.kanji.controllers.ApplicationController;
import com.kanji.windows.ApplicationWindow;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private ApplicationController applicationController;
	private MainPanel bottomPanel;
	private JSplitPane listsSplitPane;
	private AbstractButton showProblematicKanjis;
	private JLabel saveInfo;
	private ApplicationWindow applicationWindow;
	private JLabel problematicKanjis;
	private boolean problematicKanjiButtonIsVisible;

	public StartingPanel(ApplicationWindow a, ApplicationController maker) {
		applicationWindow = a;
		this.applicationController = maker;
	}

	@Override
	void createElements() {
		createSplitPane();
		createInformationsPanel();
		List<AbstractButton> buttons = addListeners();
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH, listsSplitPane));
		addHotkeysPanelHere();
		bottomPanel = new MainPanel(null);
		bottomPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, buttons.toArray(new JButton[] {}))
				.setNotOpaque().disableBorder().nextRow(saveInfo, problematicKanjis));
		showProblematicKanjis.setEnabled(false);
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, bottomPanel.getPanel()));
	}

	private void createSplitPane (){
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				applicationController.getWordsList().getPanel(),
				applicationController.getRepeatsList().getPanel());
		listsSplitPane.setOneTouchExpandable(true);
		listsSplitPane.setContinuousLayout(true);
		listsSplitPane.setResizeWeight(0.5);
	}

	private List<AbstractButton> addListeners() {
		List<AbstractButton> buttons = new ArrayList<>();
		for (String name : ButtonsNames.BUTTONS_ON_MAIN_PAGE) {
			int keyEvent;
			AbstractAction action;
			String hotkeyDescription;

			switch (name) {
			case ButtonsNames.LOAD_LIST:
				keyEvent = KeyEvent.VK_D;
				hotkeyDescription = HotkeysDescriptions.LOAD_KANJI_LIST;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.loadKanjiList();
					}
				};
				break;
			case ButtonsNames.LOAD_PROJECT:
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
						applicationController.showInsertWordDialog();
					}
				};
				break;
			case ButtonsNames.SEARCH:
				hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
				keyEvent = KeyEvent.VK_F;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.showSearchWordDialog();
					}
				};
				break;
			case ButtonsNames.START:
				hotkeyDescription = HotkeysDescriptions.OPEN_START_LEARNING_DIALOG;
				keyEvent = KeyEvent.VK_R;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.showLearningStartDialog();
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
						applicationController.saveList();
					}
				};
				break;
			case ButtonsNames.SHOW_PROBLEMATIC_KANJIS:
				hotkeyDescription = HotkeysDescriptions.EXPORT_LIST;
				keyEvent = KeyEvent.VK_P;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationWindow.showProblematicKanjiDialog();
					}
				};
				break;
			default:
				throw new RuntimeException("Unsupported button name");
			}
			AbstractButton button = createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action, name,
					hotkeyDescription);
			if (name.equals(ButtonsNames.SHOW_PROBLEMATIC_KANJIS)){
				showProblematicKanjis = button;
			}
			buttons.add(button);
		}
		return buttons;
	}

	private void createInformationsPanel() {
		saveInfo = new JLabel();
		problematicKanjis = new JLabel();
		showProblematicKanjis = createShowProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		updateProblematicKanjisAmount(applicationController.getProblematicKanjis().size());
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

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
	}

	public void updateProblematicKanjisAmount(int problematicKanjisNumber) {
		problematicKanjis.setText(Prompts.PROBLEMATIC_KANJI + problematicKanjisNumber);
	}

	public void addProblematicKanjisButton() {
		problematicKanjiButtonIsVisible = true;
		showProblematicKanjis.setEnabled(true);
	}

	public void removeButtonProblematicsKanji() {
		if (problematicKanjiButtonIsVisible) {
			bottomPanel.removeElementsFromRow(0, showProblematicKanjis);
			problematicKanjiButtonIsVisible = false;
		}
	}

	public JPanel getPanel (){
		return mainPanel.getPanel();
	}

}
