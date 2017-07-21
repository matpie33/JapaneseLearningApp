package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
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

	public AbstractPanelWithHotkeysInfo(boolean isEscapeClosingWindow) {
		this();
		this.escapeKeyShouldClose = isEscapeClosingWindow;
	}

	public AbstractPanelWithHotkeysInfo() {
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		createHotkeysPanel();
	}

	private void createHotkeysPanel() {
		hotkeysPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		JLabel title = new JLabel(Titles.hotkeysTitle);
		hotkeysPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, title));
	}

	private void addHotkeysPanel() {
		if (escapeKeyShouldClose) {
			addHotkeyInformation(HotkeysDescriptions.CLOSE_WINDOW,
					new HotkeyWrapper(KeyEvent.VK_ESCAPE));
		}
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(hotkeysPanel.getPanel()));
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
		return (hotkey.hasProperKeyModifier()
				? "Kombinacja " + KeyEvent.getKeyText(hotkey.getKeyModifier()) + " +" : "Klawisz")
				+ " " + KeyEvent.getKeyText(hotkey.getKeyEvent()) + " : " + description;
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
