package com.guimaker.panelSwitching;

import com.guimaker.enums.MoveDirection;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.HotkeyWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FocusableComponentsManager {

	private JPanel rootPanel;
	private FocusHandler focusHandler = new FocusHandler();

	public FocusableComponentsManager(JPanel rootPanel) {
		this.rootPanel = rootPanel;
		initializeHotkeysForSwitchingBetweenPanels();

	}

	private void initializeHotkeysForSwitchingBetweenPanels() {
		CommonActionsCreator
				.addHotkey(new HotkeyWrapper(KeyEvent.VK_BACK_QUOTE),
						wrapAsAction(focusHandler::toggleSwitchMode),
						rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_W),
				wrapAsAction(() -> focusHandler
						.selectNextPanelInDirectionIfInSwitchingMode(
								MoveDirection.ABOVE)), rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_S),
				wrapAsAction(() -> focusHandler
						.selectNextPanelInDirectionIfInSwitchingMode(
								MoveDirection.BELOW)), rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_A),
				wrapAsAction(() -> focusHandler
						.selectNextPanelInDirectionIfInSwitchingMode(
								MoveDirection.LEFT)), rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_D),
				wrapAsAction(() -> focusHandler
						.selectNextPanelInDirectionIfInSwitchingMode(
								MoveDirection.RIGHT)), rootPanel);
	}

	private AbstractAction wrapAsAction(Runnable r) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				r.run();
			}
		};
	}

	public void makeFocusable(JComponent panelToWrap) {
		focusHandler.registerPanel(panelToWrap);
		focusHandler.markAsFocusable(panelToWrap);
		addFocusAndMouseListener(panelToWrap, panelToWrap);
		addFocus(panelToWrap, panelToWrap);
	}

	public void makeFocusable (JComponent... panels){
		for (JComponent panel : panels) {
			makeFocusable(panel);
		}
	}

	private void addFocusAndMouseListener(Component container,
			JComponent componentToFocus) {
		container.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				manageFocus(componentToFocus);
			}
		});
		container.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				manageFocus(componentToFocus);
			}
		});
	}

	private void manageFocus(JComponent panelToSetBackground) {
		focusHandler.clearFocusedPanel();
		focusHandler.focusPanel(panelToSetBackground);
	}

	private void addFocus(Container container, JComponent componentToFocus) {
		addFocusAndMouseListener(container, componentToFocus);
		for (Component component : container.getComponents()) {
			if (component instanceof Container) {
				addFocus((Container) component, componentToFocus);
			}
		}

	}

	public JComponent getFocusedComponent() {
		return focusHandler.getFocusedElement();
	}

	public void focusPreviouslyFocusedElement() {
		focusHandler.focusPreviouslyFocusedElement();
	}
}

