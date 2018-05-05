package com.kanji.utilities;

import com.guimaker.colors.BasicColors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FocusableComponentCreator {

	public static final Color UNSELECTED_PANEL_COLOR = Color.WHITE;
	public static final Color SELECTED_PANEL_COLOR = BasicColors.VERY_LIGHT_BLUE;
	private JComponent focusedPanel;

	public void makeFocusable(JComponent panelToWrap) {
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

