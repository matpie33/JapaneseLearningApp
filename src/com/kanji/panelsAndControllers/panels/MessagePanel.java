package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRowBuilder;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends AbstractPanelWithHotkeysInfo {

	private String message;
	protected AbstractButton buttonClose;

	public MessagePanel(String message) {
		this.message = message;
	}

	@Override
	public void createElements() {
		buttonClose = createButtonClose();
		JScrollPane scrollPane = GuiMaker.createTextPaneWrappedInScrollPane(
				// TODO add vertical alignment
				new TextPaneOptions().backgroundColor(Color.WHITE)
						.textAlignment(TextAlignment.JUSTIFIED).text(message)
						.opaque(true).preferredSize(new Dimension(200, 100))
						.enabled(false));
		mainPanel
				.addRows(SimpleRowBuilder.createRow(FillType.BOTH, scrollPane));
		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

}
