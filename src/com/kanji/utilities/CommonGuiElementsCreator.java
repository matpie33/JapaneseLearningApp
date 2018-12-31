package com.kanji.utilities;

import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.kanji.constants.enums.SplitPaneOrientation;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class CommonGuiElementsCreator {

	public static JTextComponent createShortInput(String defaultContent,
			PanelDisplayMode displayMode) {
		TextComponentOptions options = new TextComponentOptions().editable(
				displayMode.equals(PanelDisplayMode.EDIT))
																 .text(defaultContent);
		if (displayMode.equals(PanelDisplayMode.EDIT)) {
			options.rowsAndColumns(1, 35);
		}
		return GuiElementsCreator.createTextField(options);
	}

	public static JLabel createErrorLabel(String message) {
		return GuiElementsCreator.createLabel(
				new ComponentOptions().text(message)
									  .foregroundColor(Color.RED));
	}

	public static JSplitPane createSplitPane(
			SplitPaneOrientation splitPaneOrientation,
			JComponent leftOrUpperComponent, JComponent rightOrDownComponent,
			double splittingWeight) {
		JSplitPane splitPane = new JSplitPane(splitPaneOrientation.getValue());
		splitPane.setContinuousLayout(true);
		splitPane.setLeftComponent(leftOrUpperComponent);
		splitPane.setRightComponent(rightOrDownComponent);
		splitPane.setResizeWeight(splittingWeight);
		splitPane.setBorder(null);
		splitPane.setUI(createSplitPaneDivider(splitPaneOrientation));
		splitPane.setBorder(null);
		return splitPane;
	}

	private static BasicSplitPaneUI createSplitPaneDivider(
			SplitPaneOrientation splitPaneOrientation) {
		return new BasicSplitPaneUI() {
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider(this) {
					public void setBorder(Border b) {
						int hor = splitPaneOrientation.equals(
								SplitPaneOrientation.HORIZONTAL) ? 1 : 0;
						int ver = splitPaneOrientation.equals(
								SplitPaneOrientation.VERTICAL) ? 1 : 0;
						super.setBorder(
								BorderFactory.createMatteBorder(ver, hor, ver,
										hor,
										Color.BLACK));
					}

					@Override
					public void paint(Graphics g) {
						g.setColor(Color.LIGHT_GRAY);
						g.fillRect(0, 0, getSize().width, getSize().height);
						super.paint(g);
					}
				};
			}
		};
	}

}
