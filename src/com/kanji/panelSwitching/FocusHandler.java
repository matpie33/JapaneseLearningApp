package com.kanji.panelSwitching;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.MoveDirection;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class FocusHandler {

	private static final Color UNFOCUSED_PANEL_COLOR = Color.WHITE;
	private static final Color FOCUSED_PANEL_COLOR = Color.BLACK;
	private static final Color SELECTABLE_PANEL_COLOR = BasicColors.DARK_GREEN;
	private static final Color SELECTED_PANEL_COLOR = Color.ORANGE;
	private boolean switchingModeEnabled;
	private JComponent focusedPanel;
	private PanelSwitchingHandler panelSwitchingHandler = new PanelSwitchingHandler();

	public void toggleSwitchMode() {
		switchingModeEnabled = !switchingModeEnabled;
		markPanelsAsSwitchable();
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.clearGlobalFocusOwner();
		if (!switchingModeEnabled) {
			focusedPanel.requestFocusInWindow();
		}
	}

	private void markPanelAsSelected(JComponent panelToSelect) {
		if (focusedPanel != null) {
			focusedPanel.setBorder(createBorder(SELECTABLE_PANEL_COLOR));
		}
		panelToSelect.setBorder(createBorder(SELECTED_PANEL_COLOR));
		focusedPanel = panelToSelect;
	}

	private void markPanelsAsSwitchable() {
		for (JComponent panel : panelSwitchingHandler.getPanels()) {
			if (panel == focusedPanel) {
				panel.setBorder(createBorder(SELECTED_PANEL_COLOR));
				continue;
			}
			panel.setBorder(createBorder(switchingModeEnabled ?
					SELECTABLE_PANEL_COLOR :
					UNFOCUSED_PANEL_COLOR));
		}
	}

	private static Border createBorder(Color color) {
		return BorderFactory.createLineBorder(color, 5);
	}

	public void markAsFocusable(JComponent panel) {
		panel.setFocusable(true);
		panel.setBorder(createBorder(UNFOCUSED_PANEL_COLOR));

	}

	public void clearFocusedPanel() {
		if (focusedPanel != null) {
			focusedPanel.setBorder(createBorder(UNFOCUSED_PANEL_COLOR));
			focusedPanel = null;
		}
	}

	public void focusPanel(JComponent panel) {
		focusedPanel = panel;
		focusedPanel.setBorder(createBorder(FOCUSED_PANEL_COLOR));
		focusedPanel.requestFocusInWindow();
	}

	public void selectNextPanelInDirectionIfInSwitchingMode(
			MoveDirection above) {
		if (switchingModeEnabled) {
			JComponent closestPanelBasedOnDirection = panelSwitchingHandler
					.findClosestPanelBasedOnDirection(focusedPanel, above);
			if (closestPanelBasedOnDirection != null) {
				markPanelAsSelected(closestPanelBasedOnDirection);
			}
		}
	}

	public void registerPanel(JComponent panel) {
		panelSwitchingHandler.registerPanel(panel);
	}
}
