package com.kanji.webPanel;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Prompts;
import com.kanji.context.KanjiContext;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ConnectionFailMessagePage implements ConnectionFailPageHandler {

	private MainPanel messagePanel;
	private JTextComponent messageComponent;
	private AbstractButton buttonReload;

	public ConnectionFailMessagePage(AbstractButton buttonReload) {
		this.buttonReload = buttonReload;
		messagePanel = new MainPanel(null);
		messageComponent = GuiElementsCreator.createTextPane(new TextPaneOptions().
				text(Prompts.CONNECTION_ERROR).fontSize(20)
				.textAlignment(TextAlignment.CENTERED).editable(false));
		messagePanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, messageComponent));
		messagePanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, buttonReload));

		System.out.println("button rel: " + buttonReload);
	}

	@Override
	public JPanel getConnectionFailPage() {
		return messagePanel.getPanel();

	}

	@Override
	public void modifyConnectionFailPage(KanjiContext context) {
		//TODO bad idea to require kanji context
		return;
	}
}
