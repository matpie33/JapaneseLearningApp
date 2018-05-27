package com.kanji.panelSwitching;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.MoveDirection;
import com.guimaker.utilities.CommonActionsCreator;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.SplitPaneOrientation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

public class FocusableComponentCreator {

	private static final Color UNSELECTED_PANEL_COLOR = Color.WHITE;
	private static final Color SELECTED_PANEL_COLOR = BasicColors.VERY_LIGHT_BLUE;
	private JComponent focusedPanel;
	private JPanel rootPanel;
	private boolean switchingModeEnabled;
	private Color SWITCHABLE_PANEL_COLOR = BasicColors.DARK_GREEN;
	private PanelSwitchingHandler panelSwitchingHandler;

	public FocusableComponentCreator(JPanel rootPanel) {
		this.rootPanel = rootPanel;
		initializeHotkeysForSwitchingBetweenPanels();
		panelSwitchingHandler = new PanelSwitchingHandler();
	}

	private void initializeHotkeysForSwitchingBetweenPanels() {
		CommonActionsCreator
				.addHotkey(new HotkeyWrapper(KeyEvent.VK_BACK_QUOTE), //actually tilde
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								switchingModeEnabled = !switchingModeEnabled;
								markPanelsAsSwitchable();
								KeyboardFocusManager
										.getCurrentKeyboardFocusManager()
										.clearGlobalFocusOwner();
								if (!switchingModeEnabled) {
									focusedPanel.requestFocusInWindow();
								}
							}
						}, rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_W),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectNextPanelInDirectionIfInSwitchingMode(MoveDirection.ABOVE);
					}
				}, rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_S),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectNextPanelInDirectionIfInSwitchingMode(MoveDirection.BELOW);
					}
				}, rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_A),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectNextPanelInDirectionIfInSwitchingMode(MoveDirection.LEFT);
					}
				}, rootPanel);
		CommonActionsCreator.addHotkey(new HotkeyWrapper(KeyEvent.VK_D),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						selectNextPanelInDirectionIfInSwitchingMode(MoveDirection.RIGHT);
					}
				}, rootPanel);
	}

	private void selectNextPanelInDirectionIfInSwitchingMode(MoveDirection above) {
		if (switchingModeEnabled) {
			JComponent closestPanelBasedOnDirection = panelSwitchingHandler
					.findClosestPanelBasedOnDirection(focusedPanel, above);
			if (closestPanelBasedOnDirection != null) {
				markPanelAsSelected(closestPanelBasedOnDirection);
			}
		}
	}

	private void markPanelAsSelected(JComponent panelToSelect) {
		if (focusedPanel!=null){
			focusedPanel.setBorder(createBorder(SWITCHABLE_PANEL_COLOR));
		}
		panelToSelect.setBorder(createBorder(SELECTED_PANEL_COLOR));
		focusedPanel = panelToSelect;
	}

	private void markPanelsAsSwitchable() {
		for (JComponent panel : panelSwitchingHandler.getPanels()) {
			if (panel == focusedPanel) {
				continue;
			}
			panel.setBorder(createBorder(switchingModeEnabled ?
					SWITCHABLE_PANEL_COLOR :
					UNSELECTED_PANEL_COLOR));
		}
	}

	public void makeFocusable(JComponent panelToWrap) {
		panelSwitchingHandler.addPanel(panelToWrap);
		panelToWrap.setFocusable(true);
		panelToWrap.setBorder(createBorder(UNSELECTED_PANEL_COLOR));
		addFocusListener(panelToWrap);
		addFocus(panelToWrap, panelToWrap);
	}

	private static Border createBorder(Color color) {
		return BorderFactory.createLineBorder(color, 5);
	}

	private void clearFocusedPanel() {
		if (focusedPanel != null) {
			focusedPanel.setBorder(createBorder(UNSELECTED_PANEL_COLOR));
			focusedPanel = null;
		}
	}

	private void setFocusedPanel(JComponent panel) {
		focusedPanel = panel;
		focusedPanel.setBorder(createBorder(SELECTED_PANEL_COLOR));
		focusedPanel.requestFocusInWindow();
	}

	private void addFocusListener(JComponent panelToSetBackground) {
		panelToSetBackground.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				manageFocus(panelToSetBackground);
				super.mouseClicked(e);
			}
		});
		panelToSetBackground.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				manageFocus(panelToSetBackground);
			}
		});
	}

	private void manageFocus(JComponent panelToSetBackground) {
		clearFocusedPanel();
		setFocusedPanel(panelToSetBackground);
	}

	private void addFocus(Container container, JComponent componentToFocus) {
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
		for (Component component : container.getComponents()) {
			if (component instanceof Container) {
				addFocus((Container) component, componentToFocus);
			}
		}

	}

}

