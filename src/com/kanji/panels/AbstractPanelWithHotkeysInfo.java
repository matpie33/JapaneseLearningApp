package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.guimaker.row.SimpleRow;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Titles;
import com.kanji.keyEvents.HotkeyWrapper;
import com.kanji.windows.DialogWindow;

public abstract class AbstractPanelWithHotkeysInfo {
	protected MainPanel mainPanel;
	private MainPanel hotkeysPanel;
	protected DialogWindow parentDialog;
	private boolean escapeKeyShouldClose;
	private int hotkeysPanelIndex;

	public AbstractPanelWithHotkeysInfo(boolean isEscapeClosingWindow) {
		this();
		this.escapeKeyShouldClose = isEscapeClosingWindow;
	}

	public AbstractPanelWithHotkeysInfo() {
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		createHotkeysPanel();
	}

	public void addHotkeysPanelHere() {
		hotkeysPanelIndex = mainPanel.getNumberOfRows();
	}

	private void createHotkeysPanel() {
		hotkeysPanel = new MainPanel(null);
		JLabel title = new JLabel(Titles.hotkeysTitle);
		title.setForeground(BasicColors.NAVY_BLUE);
		hotkeysPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.WEST, title));
	}

	private void addHotkeysPanel() {
		if (escapeKeyShouldClose) {
			addHotkeyInformation(HotkeysDescriptions.CLOSE_WINDOW,
					new HotkeyWrapper(KeyEvent.VK_ESCAPE));
		}
		SimpleRow row = RowMaker.createHorizontallyFilledRow(hotkeysPanel.getPanel());
		if (hotkeysPanelIndex == 0) {
			mainPanel.addRow(row);
		}
		else {
			mainPanel.insertRow(hotkeysPanelIndex, row);
		}

	}

	public void addHotkey(int keyEvent, AbstractAction action, JComponent component,
			String hotkeyDescription) {
		addHotkey(0, keyEvent, action, component, hotkeyDescription);
	}

	public void addHotkey(int keyModifier, int keyEvent, AbstractAction action,
			JComponent component, String hotkeyDescription) {
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, keyEvent);
		CommonActionsMaker.addHotkey(keyEvent, wrapper.getKeyMask(), action, component);
		addHotkeyInformation(hotkeyDescription, wrapper);
	}

	public JButton createButtonWithHotkey(int keyEvent, AbstractAction action, String buttonLabel,
			String hotkeyDescription) {
		JButton button = GuiElementsMaker.createButton(buttonLabel, action);
		addHotkey(keyEvent, action, button, hotkeyDescription);
		return button;
	}

	private void addHotkeyInformation(String hotkeyDescription, HotkeyWrapper hotkey) {
		JLabel hotkeyInfo = new JLabel(createInformationAboutHotkey(hotkey, hotkeyDescription));
		hotkeysPanel.addRow(RowMaker.createHorizontallyFilledRow(hotkeyInfo));
	}

	private String createInformationAboutHotkey(HotkeyWrapper hotkey, String description) {
		return (hotkey.hasProperKeyModifier() ? KeyEvent.getKeyText(hotkey.getKeyModifier()) + " + "
				: "") + KeyEvent.getKeyText(hotkey.getKeyEvent()) + " : " + description;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	abstract void createElements();

	public JPanel createPanel() {
		createElements();
		addHotkeysPanel();
		return mainPanel.getPanel();
	}

	public boolean isEscapeOnClose() {
		return escapeKeyShouldClose;
	}

	public DialogWindow getDialog() {
		return parentDialog;
	}

}
