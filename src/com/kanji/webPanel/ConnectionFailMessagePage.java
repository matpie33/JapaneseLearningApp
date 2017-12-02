package com.kanji.webPanel;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.kanjiContext.KanjiContext;
import com.kanji.strings.Prompts;
import sun.applet.Main;

import javax.swing.*;
import javax.swing.text.JTextComponent;

public class ConnectionFailMessagePage implements ConnectionFailPageHandler {

	private MainPanel messagePanel;
	private JTextComponent messageComponent;

	public ConnectionFailMessagePage (){
		messagePanel = new MainPanel(null);
		messageComponent = GuiMaker.createTextPane(new TextPaneOptions().
				text(Prompts.LOADING_PAGE).fontSize(20).textAlignment(TextAlignment.CENTERED));
		messagePanel.addRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.CENTER, messageComponent));
		messageComponent.setText(Prompts.CONNECTION_ERROR);
	}

	@Override public JPanel getConnectionFailPage() {
		return messagePanel.getPanel();
	}

	@Override public void modifyConnectionFailPage(KanjiContext context) {
		return;
	}
}
