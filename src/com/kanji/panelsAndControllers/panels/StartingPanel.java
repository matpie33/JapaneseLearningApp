package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.SavingStatus;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.context.WordTypeContext;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.StartingController;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StartingPanel extends AbstractPanelWithHotkeysInfo {

	private static final String UNIQUE_NAME = "Starting panel";
	private JTabbedPane tabs;
	private WordsAndRepeatingInformationsPanel kanjiRepeatingPanel;
	private WordsAndRepeatingInformationsPanel japaneseWordsRepeatingPanel;
	private MainPanel bottomPanel;
	private JLabel problematicKanjis;
	private JLabel saveInfo;
	private AbstractButton showProblematicWordsButton;
	private ApplicationController applicationController;
	private Map<String, WordsAndRepeatingInformationsPanel> listToTabLabel = new LinkedHashMap<>();
	private WordTypeContext wordTypeContext;
	private StartingController startingController;
	private ApplicationWindow applicationWindow;

	public StartingPanel() {
		tabs = new JTabbedPane();
		wordTypeContext = new WordTypeContext();
		startingController = new StartingController(this);
	}

	public void setApplicationWindow(
			ApplicationController applicationController) {
		//TODO add it in constructor
		this.applicationWindow = applicationController.getApplicationWindow();
		this.applicationController = applicationController;
	}

	public void createListPanels() {
		kanjiRepeatingPanel = new WordsAndRepeatingInformationsPanel(
				applicationController.getKanjiList(),
				applicationController.getKanjiRepeatingDates(),
				TypeOfWordForRepeating.KANJIS);
		japaneseWordsRepeatingPanel = new WordsAndRepeatingInformationsPanel(
				applicationController.getJapaneseWords(),
				applicationController.getJapaneseWordsRepeatingDates(),
				TypeOfWordForRepeating.JAPANESE_WORDS);
	}

	public JSplitPane getSplitPaneFor(Class listClass) {
		if (listClass.equals(Kanji.class)) {
			return kanjiRepeatingPanel.getListsSplitPane();
		}
		else {
			return japaneseWordsRepeatingPanel.getListsSplitPane();
		}
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);

	}

	@Override
	public void createElements() {

		listToTabLabel.put("Powtórki kanji", kanjiRepeatingPanel);
		listToTabLabel.put("Powtórki słówek", japaneseWordsRepeatingPanel);

		for (Map.Entry<String, WordsAndRepeatingInformationsPanel> listAndTabLabel : listToTabLabel
				.entrySet()) {
			tabs.addTab(listAndTabLabel.getKey(),
					listAndTabLabel.getValue().createPanel());
		}
		for (int i = 0; i < tabs.getTabCount(); i++) {
			tabs.setBackgroundAt(i, BasicColors.BLUE_NORMAL_1);
		}

		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				applicationController.updateProblematicWordsAmount();
			}
		});

		createInformationsPanel();

		addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_W, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabs.getSelectedIndex() == tabs.getTabCount() - 1) {
					tabs.setSelectedIndex(0);
				}
				else {
					tabs.setSelectedIndex(tabs.getSelectedIndex() + 1);
				}

			}
		}, getPanel(), HotkeysDescriptions.SWITCH_WORD_TAB);

		tabs.setSelectedIndex(0);
		tabs.addChangeListener(startingController.createTabChangeListener());
		wordTypeContext.setWordTypeForRepeating(TypeOfWordForRepeating.KANJIS);

		List<AbstractButton> buttons = createButtons();
		bottomPanel = new MainPanel(null);
		bottomPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				buttons.toArray(new JButton[] {})).setNotOpaque()
				.disableBorder().nextRow(saveInfo, problematicKanjis));

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, tabs));
		addHotkeysPanelHere();
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, bottomPanel.getPanel()));
	}

	public void switchToList(TypeOfWordForRepeating wordType) {
		if (wordType.equals(TypeOfWordForRepeating.KANJIS)) {
			tabs.setSelectedIndex(0);
		}
		else if (wordType.equals(TypeOfWordForRepeating.JAPANESE_WORDS)) {
			tabs.setSelectedIndex(1);
			//TODO use enum instead of class checking, and tab index to enum and use it instead of
			// listToLabel map
		}
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
	}

	public void updateProblematicWordsAmount(int problematicKanjisNumber) {
		String wordType = JapaneseWritingUtilities
				.getTextForTypeOfWordForRepeating(
						applicationController.getActiveWordsListType());
		problematicKanjis.setText(
				String.format(Prompts.PROBLEMATIC_WORDS_AMOUNT, wordType,
						problematicKanjisNumber));
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
				hotkeyDescription = HotkeysDescriptions.LOAD_LISTS_FROM_TEXT_FILE;
				action = new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applicationController.loadWordsFromTextFiles();
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
						applicationController
								.showProblematicWordsDialogForCurrentList();
					}
				};
				break;
			default:
				throw new RuntimeException("Unsupported button name: " + name);
			}
			AbstractButton button = createButtonWithHotkey(KeyModifiers.CONTROL,
					keyEvent, action, name, hotkeyDescription);
			if (name.equals(ButtonsNames.SHOW_PROBLEMATIC_KANJIS)) {
				showProblematicWordsButton = button;
				showProblematicWordsButton.setEnabled(false);
			}
			buttons.add(button);
		}
		return buttons;
	}

	private void createInformationsPanel() {
		saveInfo = GuiElementsCreator.createLabel(new ComponentOptions());
		problematicKanjis = GuiElementsCreator
				.createLabel(new ComponentOptions());
		showProblematicWordsButton = createShowProblematicWordsButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		updateProblematicWordsAmount(
				applicationController.getProblematicKanjis().size());
	}

	private AbstractButton createShowProblematicWordsButton() {
		AbstractButton problematicKanjiButton = GuiElementsCreator
				.createButtonLikeComponent(new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.SHOW_PROBLEMATIC_KANJIS));
		return problematicKanjiButton;
	}

	public void enableShowProblematicWordsButton() {
		showProblematicWordsButton.setEnabled(true);
	}

	public MyList getActiveWordsList() {
		return listToTabLabel.get(tabs.getTitleAt(tabs.getSelectedIndex()))
				.getWordsList();
	}

	public MyList<RepeatingData> getActiveRepeatingList() {
		return listToTabLabel.get(tabs.getTitleAt(tabs.getSelectedIndex()))
				.getRepeatingList();
	}

	public void updateWordTypeContext(String newTabName) {
		WordsAndRepeatingInformationsPanel panel = listToTabLabel
				.get(newTabName);
		if (panel != null) {
			wordTypeContext
					.setWordTypeForRepeating(panel.getTypeOfWordForRepeating());
		}
	}

	public void refreshAllTabs() {
		tabs.repaint();
		tabs.revalidate();
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}
}

