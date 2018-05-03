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

	private JComponent focusedPanel;

	public void makeFocusable(JComponent panelToWrap) {
		panelToWrap.setFocusable(true);
		addFocusListener(panelToWrap);
		addFocus(panelToWrap, panelToWrap);
	}

	private static Border createBorder(Color color) {
		return BorderFactory.createLineBorder(color, 3);
	}

	private void clearFocusedPanel() {
		focusedPanel.setBorder(null);
		focusedPanel = null;
	}

	private void setFocusedPanel(JComponent panel) {
		focusedPanel = panel;
		focusedPanel.setBorder(createBorder(BasicColors.VERY_LIGHT_BLUE));
		focusedPanel.requestFocusInWindow();
	}

	private void addFocusListener(JComponent panelToSetBackground) {
		panelToSetBackground.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearFocusedPanel();
				setFocusedPanel(panelToSetBackground);
				super.mouseClicked(e);
			}
		});
	}

	private void addFocus(Container container, JComponent componentToFocus) {
		container.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				setFocusedPanel(componentToFocus);
			}
		});
		for (Component component : container.getComponents()) {
			if (component instanceof Container) {
				addFocus((Container) component, componentToFocus);
			}
		}

	}

}

