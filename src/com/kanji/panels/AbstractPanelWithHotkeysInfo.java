package com.kanji.panels;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.utilities.CommonActionsMaker;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Titles;
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
		mainPanel.setRowColor(BasicColors.VERY_LIGHT_BLUE);
		mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		createHotkeysPanel();
	}

	public void addHotkeysPanelHere() {
		hotkeysPanelIndex = mainPanel.getNumberOfRows();
	}

	private void createHotkeysPanel() {
		hotkeysPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		JLabel title = new JLabel(Titles.HOTKEYS);
		title.setForeground(BasicColors.VERY_BLUE);
		hotkeysPanel.addRow(new SimpleRow(FillType.NONE, Anchor.WEST, title));
	}

	private void addHotkeysPanel() {
		if (escapeKeyShouldClose) {
			addHotkeyInformation(HotkeysDescriptions.CLOSE_WINDOW,
					new HotkeyWrapper(KeyEvent.VK_ESCAPE));
		}
		SimpleRow row = new SimpleRow(FillType.HORIZONTAL, Anchor.SOUTH, hotkeysPanel.getPanel());
		if (hotkeysPanelIndex == 0) {
			mainPanel.addRow(row);
		}
		else {
			mainPanel.insertRow(hotkeysPanelIndex, row);
		}

	}

	public void addHotkeysInformation(KeyModifiers keyModifier, int keyEvent, JComponent component,
			String hotkeyDescription) {
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, keyEvent);
		addHotkeyInformation(hotkeyDescription, wrapper);
	}

	public void addHotkeysInformation(int keyEvent, JComponent component,
			String hotkeyDescription) {
		HotkeyWrapper wrapper = new HotkeyWrapper(KeyModifiers.NONE, keyEvent);
		addHotkeyInformation(hotkeyDescription, wrapper);
	}

	public void addHotkey(int keyEvent, AbstractAction action, JComponent component,
			String hotkeyDescription) {
		addHotkey(KeyModifiers.NONE, keyEvent, action, component, hotkeyDescription);
	}

	public void addHotkey(KeyModifiers keyModifier, int keyEvent, AbstractAction action,
			JComponent component, String hotkeyDescription) {
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, keyEvent);
		CommonActionsMaker.addHotkey(keyEvent, wrapper.getKeyMask(), action, component);
		addHotkeyInformation(hotkeyDescription, wrapper);
	}

	public AbstractButton createButtonWithHotkey(int keyEvent, AbstractAction action,
			String buttonLabel, String hotkeyDescription) {
		return createButtonWithHotkey(KeyModifiers.NONE, keyEvent, action, buttonLabel,
				hotkeyDescription);
	}

	public AbstractButton createButtonWithHotkey(KeyModifiers keyModifier, int keyEvent,
			AbstractAction action, String buttonLabel, String hotkeyDescription) {
		// TODO replace keyModifier with an enum
		AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				buttonLabel, action);
		addHotkey(keyModifier, keyEvent, action, button, hotkeyDescription);
		return button;
	}

	private void addHotkeyInformation(String hotkeyDescription, HotkeyWrapper hotkey) {
		if (hotkeyDescription.isEmpty()) {
			return;
		}
		JLabel hotkeyInfo = new JLabel(createInformationAboutHotkey(hotkey, hotkeyDescription));
		hotkeysPanel.addRow(new SimpleRow(FillType.HORIZONTAL, hotkeyInfo));
	}

	private String createInformationAboutHotkey(HotkeyWrapper hotkey, String description) {
		return (hotkey.hasKeyModifier()
				? InputEvent.getModifiersExText(hotkey.getKeyMask()) + " + " : "")
				+ KeyEvent.getKeyText(hotkey.getKeyEvent()) + " : " + description;
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

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

	public boolean isDisplayAble() {
		return parentDialog.getContainer().isDisplayable();
	}

	abstract void createElements();

	public void afterVisible() {
		// not required
	};

}
