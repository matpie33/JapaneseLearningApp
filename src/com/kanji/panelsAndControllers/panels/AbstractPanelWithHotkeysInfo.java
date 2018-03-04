package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.ExpandablePanel;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.CommonActionsMaker;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Titles;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPanelWithHotkeysInfo {
	protected MainPanel mainPanel;
	private ExpandablePanel hotkeysPanel;
	DialogWindow parentDialog;
	private int hotkeysPanelIndex;
	private AbstractButton[] navigationButtons;
	private Anchor buttonsAnchor = Anchor.EAST;
	private Border defaultBorder = BorderFactory
			.createBevelBorder(BevelBorder.LOWERED);
	private Map<HotkeyWrapper, AbstractAction> hotkeysMapping = new HashMap<>();
	private boolean isMaximized;

	public AbstractPanelWithHotkeysInfo() {
		mainPanel = new MainPanel(BasicColors.OCEAN_BLUE);
		mainPanel.setRowColor(BasicColors.VERY_LIGHT_BLUE);
		mainPanel.setBorder(defaultBorder);
		createHotkeysPanel();
		isMaximized = false;
	}

	public void setMaximize(boolean maximized) {
		isMaximized = maximized;
	}

	public boolean isMaximized() {
		return isMaximized;
	}

	protected Border getDefaultBorder() {
		return defaultBorder;
	}

	void addHotkeysPanelHere() {
		hotkeysPanelIndex = mainPanel.getNumberOfRows();
	}

	public void setNavigationButtons(AbstractButton... buttons) {
		navigationButtons = buttons;
	}

	void setNavigationButtons(Anchor anchor, AbstractButton... buttons) {
		navigationButtons = buttons;
		buttonsAnchor = anchor;
	}

	private void createHotkeysPanel() {
		hotkeysPanel = new ExpandablePanel(BasicColors.VERY_LIGHT_BLUE,
				Titles.HOTKEYS);
		hotkeysPanelIndex = -1;
	}

	private void addHotkeysPanel() {
		if (hotkeysMapping.isEmpty()) {
			return;
		}
		SimpleRow row = SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, Anchor.SOUTH,
						hotkeysPanel.getPanel());
		MainPanel panelForHotkeys = parentPanelForHotkeys();

		if (hotkeysPanelIndex == -1) {
			panelForHotkeys.addRows(row);
		}
		else if (hotkeysPanelIndex > 0) {
			panelForHotkeys.insertRow(hotkeysPanelIndex, row);
		}
		if (navigationButtons != null)
			panelForHotkeys
					.addRows( // TODO fix in gui maker: if putting rows as
							// highest
							// as
							// possible, then west
							// should be as highest as possible, but now I need
							// to
							// use northwest
							SimpleRowBuilder
									.createRow(FillType.NONE, buttonsAnchor,
											navigationButtons).disableBorder()
									.setNotOpaque());

	}

	protected MainPanel parentPanelForHotkeys() {
		return mainPanel;
	}

	public void addHotkeysInformation(int keyEvent, String hotkeyDescription) {
		HotkeyWrapper wrapper = new HotkeyWrapper(KeyModifiers.NONE, keyEvent);
		addHotkeyInformation(hotkeyDescription, wrapper);
	}

	public void addHotkey(int keyEvent, AbstractAction action,
			JComponent component, String hotkeyDescription) {
		addHotkey(KeyModifiers.NONE, keyEvent, action, component,
				hotkeyDescription);
	}

	public void addHotkey(KeyModifiers keyModifier, int keyEvent,
			AbstractAction action, JComponent component,
			String hotkeyDescription) {
		HotkeyWrapper wrapper = new HotkeyWrapper(keyModifier, keyEvent);
		if (hotkeysMapping.containsKey(wrapper)) {
			throw new IllegalArgumentException(
					"Multiple actions binded to the same key: " + KeyEvent
							.getKeyText(wrapper.getKeyEvent())
							+ " in the class: " + this);
		}
		hotkeysMapping.put(wrapper, action);
		CommonActionsMaker
				.addHotkey(keyEvent, wrapper.getKeyMask(), action, component);
		addHotkeyInformation(hotkeyDescription, wrapper);
	}

	public AbstractButton createButtonWithHotkey(int keyEvent,
			AbstractAction action, String buttonLabel,
			String hotkeyDescription) {
		return createButtonWithHotkey(KeyModifiers.NONE, keyEvent, action,
				buttonLabel, hotkeyDescription);
	}

	public AbstractButton createButtonWithHotkey(KeyModifiers keyModifier,
			int keyEvent, AbstractAction action, String buttonLabel,
			String hotkeyDescription) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON, buttonLabel,
						action);
		addHotkey(keyModifier, keyEvent, action, button, hotkeyDescription);
		button.setFocusable(false);
		return button;
	}

	private void addHotkeyInformation(String hotkeyDescription,
			HotkeyWrapper hotkey) {
		if (hotkeyDescription.isEmpty()) {
			return;
		}
		JLabel hotkeyInfo = new JLabel(
				createInformationAboutHotkey(hotkey, hotkeyDescription));
		hotkeysPanel.createRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, hotkeyInfo));
	}

	private String createInformationAboutHotkey(HotkeyWrapper hotkey,
			String description) {
		return (hotkey.hasKeyModifier() ?
				InputEvent.getModifiersExText(hotkey.getKeyMask()) + " + " :
				"") + translateKeyText(
				KeyEvent.getKeyText(hotkey.getKeyEvent())) + " : "
				+ description;
	}

	private String translateKeyText(String text) {
		if (text.equalsIgnoreCase("period")) {
			return "kropka";
		}
		else {
			return text;
		}
	}

	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	public JPanel createPanel() {
		createElements();
		addHotkeysPanel();
		return mainPanel.getPanel();
	}

	public DialogWindow getDialog() {
		return parentDialog;
	}

	public boolean isDisplayable() {
		return parentDialog.getContainer().isDisplayable();
	}

	public abstract void createElements();

	public void afterVisible() {
		// not required
	}

	AbstractButton createButtonClose() {
		AbstractAction dispose = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ESCAPE, dispose,
				ButtonsNames.CLOSE_WINDOW, HotkeysDescriptions.CLOSE_WINDOW);
	}

	public JPanel getPanel() {
		return mainPanel.getPanel();
	}

}
