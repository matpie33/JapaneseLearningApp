package com.kanji.panelsAndControllers.panels;

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
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.enums.SavingStatus;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.windows.ApplicationWindow;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private JTabbedPane tabs;
	private WordsAndRepeatingInformationsPanel kanjiRepeatingPanel;
	private WordsAndRepeatingInformationsPanel japaneseWordsRepeatingPanel;
	private MainPanel bottomPanel;
	private JLabel problematicKanjis;
	private JLabel saveInfo;
	private AbstractButton showProblematicKanjis;
	private ApplicationWindow applicationWindow;
	private ApplicationController applicationController;


	public StartingPanel (ApplicationWindow a){
		applicationController = a.getApplicationController();
		kanjiRepeatingPanel = new WordsAndRepeatingInformationsPanel(applicationController.getKanjiList(),
				applicationController.getKanjiRepeatingDates());
		japaneseWordsRepeatingPanel = new WordsAndRepeatingInformationsPanel(
				applicationController.getJapaneseWords(),
				applicationController.getJapaneseWordsRepeatingDates());
		applicationWindow = a;
		tabs = new JTabbedPane();
	}

	@Override
	public void createElements() {
		createInformationsPanel();

		tabs.addTab("Powtórki kanji", kanjiRepeatingPanel.createPanel());
		tabs.addTab("Powtórki słówek", japaneseWordsRepeatingPanel.createPanel());

		List<AbstractButton> buttons = createButtons();
		bottomPanel = new MainPanel(null);
		bottomPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, buttons.toArray(new JButton[] {}))
				.setNotOpaque().disableBorder().nextRow(saveInfo, problematicKanjis));

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, tabs));
		addHotkeysPanelHere();
		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, bottomPanel.getPanel()));
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
	}

	public void updateProblematicKanjisAmount(int problematicKanjisNumber) {
		problematicKanjis.setText(Prompts.PROBLEMATIC_KANJI + problematicKanjisNumber);
	}

	private List<AbstractButton> createButtons() {
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
				hotkeyDescription = HotkeysDescriptions.REVIEW_PROBLEMATIC_KANJIS;
				keyEvent = KeyEvent.VK_P;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationWindow.showProblematicKanjiDialog();
					}
				};
				break;
			default:
				throw new RuntimeException("Unsupported button name: " + name);
			}
			AbstractButton button = createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action, name,
					hotkeyDescription);
			if (name.equals(ButtonsNames.SHOW_PROBLEMATIC_KANJIS)){
				showProblematicKanjis = button;
				showProblematicKanjis.setEnabled(false);
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

	public void addProblematicKanjisButton() {
		showProblematicKanjis.setEnabled(true);
	}

}

