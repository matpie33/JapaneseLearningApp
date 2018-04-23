package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
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
import com.kanji.context.ContextOwner;
import com.kanji.context.WordTypeContext;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.StartingController;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;

public class StartingPanel extends AbstractPanelWithHotkeysInfo
		implements ContextOwner<WordTypeContext> {

	private JTabbedPane tabs;
	private WordsAndRepeatingInformationsPanel kanjiRepeatingPanel;
	private WordsAndRepeatingInformationsPanel japaneseWordsRepeatingPanel;
	private MainPanel bottomPanel;
	private JLabel problematicKanjis;
	private JLabel saveInfo;
	private AbstractButton showProblematicKanjis;
	private ApplicationController applicationController;
	private Map<String, WordsAndRepeatingInformationsPanel> listToTabLabel = new LinkedHashMap<>();
	private WordTypeContext wordTypeContext;
	private StartingController startingController;
	private ApplicationWindow applicationWindow;
	private JPanel mainApplicationPanel;

	public StartingPanel(ApplicationWindow a, JPanel mainApplicationPanel) {
		applicationWindow = a;
		this.mainApplicationPanel = mainApplicationPanel;
		applicationController = a.getApplicationController();
		kanjiRepeatingPanel = new WordsAndRepeatingInformationsPanel(
				applicationController.getKanjiList(),
				applicationController.getKanjiRepeatingDates(),
				TypeOfWordForRepeating.KANJIS);
		japaneseWordsRepeatingPanel = new WordsAndRepeatingInformationsPanel(
				applicationController.getJapaneseWords(),
				applicationController.getJapaneseWordsRepeatingDates(),
				TypeOfWordForRepeating.JAPANESE_WORDS);
		tabs = new JTabbedPane();
		wordTypeContext = new WordTypeContext();
		startingController = new StartingController(this);
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
		createInformationsPanel();

		listToTabLabel.put("Powtórki kanji", kanjiRepeatingPanel);
		listToTabLabel.put("Powtórki słówek", japaneseWordsRepeatingPanel);

		for (Map.Entry<String, WordsAndRepeatingInformationsPanel> listAndTabLabel : listToTabLabel
				.entrySet()) {
			tabs.addTab(listAndTabLabel.getKey(),
					listAndTabLabel.getValue().createPanel());
		}
		for (int i = 0; i < tabs.getTabCount(); i++) {
			tabs.setBackgroundAt(i, BasicColors.LIGHT_BLUE);
		}

		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				applicationWindow.updateProblematicWordsAmount();
			}
		});

		addHotkey(KeyModifiers.SHIFT, KeyEvent.VK_PERIOD, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabs.getSelectedIndex() == tabs.getTabCount() - 1) {
					tabs.setSelectedIndex(0);
				}
				else {
					tabs.setSelectedIndex(tabs.getSelectedIndex() + 1);
				}

			}
		}, mainApplicationPanel, HotkeysDescriptions.SWITCH_WORD_TAB);

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

	public void switchToList(Class listType) {
		if (listType.equals(Kanji.class)) {
			tabs.setSelectedIndex(0);
		}
		else if (listType.equals(JapaneseWord.class)) {
			tabs.setSelectedIndex(1);
			//TODO use enum instead of class checking, and tab index to enum and use it instead of
			// listToLabel map
		}
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.SAVING_STATUS + savingStatus.getStatus());
	}

	public void updateProblematicWordsAmount(int problematicKanjisNumber,
			Class activeWordsClass) {
		String prefix;
		if (activeWordsClass.equals(Kanji.class)) {
			prefix = Prompts.PROBLEMATIC_KANJI;
		}
		else if (activeWordsClass.equals(JapaneseWord.class)) {
			prefix = Prompts.PROBLEMATIC_WORDS;
		}
		else {
			throw new IllegalArgumentException(
					"Unknown active words class name: " + activeWordsClass);
		}
		problematicKanjis.setText(prefix + problematicKanjisNumber);
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
						applicationWindow
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
				showProblematicKanjis = button;
				showProblematicKanjis.setEnabled(false);
			}
			buttons.add(button);
		}
		return buttons;
	}

	private void createInformationsPanel() {
		saveInfo = GuiElementsCreator.createLabel(new ComponentOptions());
		problematicKanjis = GuiElementsCreator
				.createLabel(new ComponentOptions());
		showProblematicKanjis = createShowProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		updateProblematicWordsAmount(
				applicationController.getProblematicKanjis().size(),
				Kanji.class);
	}

	private JButton createShowProblematicKanjiButton() {
		JButton problematicKanjiButton = new JButton(
				ButtonsNames.SHOW_PROBLEMATIC_KANJIS);
		problematicKanjiButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationWindow.showProblematicWordsDialog(new HashSet<>());
			}
		});
		return problematicKanjiButton;
	}

	public void addProblematicKanjisButton() {
		showProblematicKanjis.setEnabled(true);
	}

	public MyList getActiveWordsList() {
		return listToTabLabel.get(tabs.getTitleAt(tabs.getSelectedIndex()))
				.getWordsList();
	}

	public MyList getActiveRepeatingList() {
		return listToTabLabel.get(tabs.getTitleAt(tabs.getSelectedIndex()))
				.getRepeatingList();
	}

	@Override
	public WordTypeContext getContext() {
		return wordTypeContext;
	}

	public void updateWordTypeContext(String newTabName) {
		WordsAndRepeatingInformationsPanel panel = listToTabLabel
				.get(newTabName);
		if (panel != null) {
			wordTypeContext
					.setWordTypeForRepeating(panel.getTypeOfWordForRepeating());
		}
	}

}

