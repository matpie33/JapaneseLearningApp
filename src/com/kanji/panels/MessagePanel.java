package com.kanji.panels;

import java.awt.Dimension;
import javax.swing.AbstractButton;
import javax.swing.JScrollPane;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRowBuilder;

public class MessagePanel extends AbstractPanelWithHotkeysInfo {

	private String message;

	public MessagePanel(String message) {
		this.message = message;
	}

	@Override
	void createElements() {
		AbstractButton buttonClose = createButtonClose();
		JScrollPane scrollPane = GuiMaker.createTextPaneWrappedInScrollPane(
				// TODO add vertical alignment
				new TextPaneOptions().textAlignment(TextAlignment.CENTERED).text(message)
						.opaque(false).preferredSize(new Dimension(200, 100)).enabled(false));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, scrollPane));
		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

}
