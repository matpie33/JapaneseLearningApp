package com.kanji.utilities;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FocusableComponentMaker {


	public static void makeFocusable(JComponent panelToWrap){
		panelToWrap.setFocusable(true);
		addFocusListener(panelToWrap);
		addFocus(panelToWrap, panelToWrap);
	}

	private static Border createBorder (Color color){
		return BorderFactory.createLineBorder(color, 3);
	}

	private static void addFocusListener (JComponent panelToSetBackground){
		panelToSetBackground.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				panelToSetBackground.requestFocusInWindow();
				super.mouseClicked(e);
			}
		});
		panelToSetBackground.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				panelToSetBackground.setBorder(null);
			}
			@Override public void focusGained (FocusEvent e){
				panelToSetBackground.requestFocusInWindow();
				panelToSetBackground.setBorder(createBorder(BasicColors.VERY_BLUE));
			}
		});
	}

	private static void addFocus (Container container, JComponent componentToFocus){
		container.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				componentToFocus.requestFocusInWindow();
			}
		});
		for (Component component: container.getComponents()){
			if (component instanceof Container){
				addFocus((Container)component, componentToFocus);
			}
		}

	}

}