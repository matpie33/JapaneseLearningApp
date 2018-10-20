package com.kanji.panelsAndControllers.panels;

import com.guimaker.application.ApplicationConfiguration;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.MoveDirection;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.ExpandablePanel;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;
import com.kanji.constants.strings.Titles;
import com.kanji.list.myList.MyList;
import com.kanji.windows.DialogWindow;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
	private List<MyList> navigableByKeyboardLists = new ArrayList<>();
	private Map<MoveDirection, HotkeyWrapper> hotkeysForMovingBetweenInputs = new HashMap<>();
	private boolean navigateBetweenInputsByHotkeys;
	private boolean isReady;
	private Color contentColor;

	public AbstractPanelWithHotkeysInfo() {
		mainPanel = new MainPanel(null);
		mainPanel.setRowsBorder(defaultBorder);
		createHotkeysPanel();
		isMaximized = false;
	}

	public Color getContentColor() {
		return contentColor;
	}

	public boolean isReady() {
		return isReady;
	}

	private void addHotkeys(JComponent rootPanel) {
		for (Map.Entry<MoveDirection, HotkeyWrapper> hotkey : hotkeysForMovingBetweenInputs
				.entrySet()) {
			HotkeyWrapper hotkeyWrapper = hotkey.getValue();
			KeyModifiers keyModifier = KeyModifiers
					.of(hotkeyWrapper.getKeyModifier());
			switch (hotkey.getKey()) {
			case RIGHT:
				addHotkey(keyModifier, hotkeyWrapper.getKeyEvent(),
						wrapToAction(MyList::selectNextInputInSameRow),
						rootPanel,
						HotkeysDescriptions.SELECT_NEXT_INPUT_IN_SAME_ROW);
				break;
			case LEFT:
				addHotkey(keyModifier, hotkeyWrapper.getKeyEvent(),
						wrapToAction(MyList::selectPreviousInputInSameRow),
						rootPanel,
						HotkeysDescriptions.SELECT_PREVIOUS_INPUT_IN_SAME_ROW);
				break;
			case BELOW:
				addHotkey(keyModifier, hotkeyWrapper.getKeyEvent(),
						wrapToAction(MyList::selectInputBelowCurrent),
						rootPanel,
						HotkeysDescriptions.SELECT_INPUT_BELOW_CURRENT);
				break;
			case ABOVE:
				addHotkey(keyModifier, hotkeyWrapper.getKeyEvent(),
						wrapToAction(MyList::selectInputAboveCurrent),
						rootPanel,
						HotkeysDescriptions.SELECT_INPUT_ABOVE_CURRENT);
				break;
			}

		}

	}

	private void initializeHotkeysForMovingBetweeenInputs() {
		hotkeysForMovingBetweenInputs.put(MoveDirection.ABOVE,
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_W));
		hotkeysForMovingBetweenInputs.put(MoveDirection.BELOW,
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_S));
		hotkeysForMovingBetweenInputs.put(MoveDirection.RIGHT,
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_D));
		hotkeysForMovingBetweenInputs.put(MoveDirection.LEFT,
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_A));
	}

	public void addNavigableByKeyboardList(MyList navigableList) {
		navigableByKeyboardLists.add(navigableList);
		if (!navigateBetweenInputsByHotkeys) {
			navigateBetweenInputsByHotkeys = true;
			initializeHotkeysForMovingBetweeenInputs();
			addHotkeys(getPanel());
		}

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
		hotkeysPanel = new ExpandablePanel(null, Titles.HOTKEYS);
		hotkeysPanelIndex = -1;
	}

	private void addHotkeysPanel() {
		if (hotkeysMapping.isEmpty()) {
			return;
		}
		AbstractSimpleRow row = SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, Anchor.SOUTH,
						hotkeysPanel.getPanel());
		MainPanel panelForHotkeys = parentPanelForHotkeys();

		if (hotkeysPanelIndex == -1) {
			panelForHotkeys.addRow(row);
		}
		else if (hotkeysPanelIndex > 0) {
			panelForHotkeys.insertRow(hotkeysPanelIndex, row);
		}
		if (navigationButtons != null)
			panelForHotkeys.addRow(SimpleRowBuilder
					.createRow(FillType.NONE, buttonsAnchor, navigationButtons)
					.disableBorder().setNotOpaque());

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
					"Multiple actions binded to the same key: " + KeyModifiers
							.of(wrapper.getKeyModifier()) + "+" + KeyEvent
							.getKeyText(wrapper.getKeyEvent())
							+ " in the class: " + this);
		}
		hotkeysMapping.put(wrapper, action);
		CommonActionsCreator.addHotkey(wrapper, action, component);
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
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonLabel), action);
		addHotkey(keyModifier, keyEvent, action, button, hotkeyDescription);
		button.setFocusable(false);
		return button;
	}

	private void addHotkeyInformation(String hotkeyDescription,
			HotkeyWrapper hotkey) {
		if (hotkeyDescription.isEmpty()) {
			return;
		}
		JLabel hotkeyInfo = GuiElementsCreator.createLabel(
				new ComponentOptions().text(createInformationAboutHotkey(hotkey,
						hotkeyDescription)));
		hotkeysPanel.createRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, hotkeyInfo));
	}

	private String createInformationAboutHotkey(HotkeyWrapper hotkey,
			String description) {
		return (hotkey.hasKeyModifier() ?
				InputEvent.getModifiersExText(hotkey.getKeyModifier()) + " + " :
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
		//TODO this should be set in constructor
		parentDialog = parent;
		ApplicationConfiguration parentConfiguration = parent
				.getParentConfiguration();
		hotkeysPanel
				.setBackground(parentConfiguration.getPanelBackgroundColor());
		mainPanel.setBackground(parentConfiguration.getPanelBackgroundColor());
		mainPanel.setRowColor(parentConfiguration.getContentPanelColor());

		contentColor = parentConfiguration.getContentPanelColor();
	}

	public void clear() {
		isReady = false;
		mainPanel.clear();
	}

	public JPanel createPanel() {
		if (!isReady) {
			createElements();
			addHotkeysPanel();
			isReady = true;
		}
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
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ESCAPE, dispose,
				JapaneseApplicationButtonsNames.CLOSE_WINDOW,
				HotkeysDescriptions.CLOSE_WINDOW);
	}

	public JPanel getPanel() {
		return mainPanel.getPanel();
	}

	private AbstractAction wrapToAction(Consumer<MyList> actionOnInput) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyList selectedList = getListWithSelectedInput();
				if (selectedList == null ||
						selectedList.getPanel().getVisibleRect().getSize()
								.getHeight() != selectedList.getPanel()
								.getSize().getHeight()) {
					selectedList = findFirstVisibleList();
				}
				actionOnInput.accept(selectedList);

			}
		};
	}

	private MyList findFirstVisibleList() {
		for (MyList navigableList : navigableByKeyboardLists) {
			if (navigableList.getPanel().getVisibleRect().getSize().getHeight()
					== navigableList.getPanel().getSize().getHeight()) {
				return navigableList;
			}
		}
		return null;
	}

	private MyList getListWithSelectedInput() {
		for (MyList navigableList : navigableByKeyboardLists) {
			if (navigableList.hasSelectedInput()) {
				return navigableList;
			}
		}
		return null;
	}

	public abstract String getUniqueName();

}
