package com.kanji.webPanel;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.model.WebContext;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.webPanel.ConnectionFailPageHandler;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class ConnectionFailKanjiOfflinePage
		implements ConnectionFailPageHandler {

	private MainPanel mainPanel;
	private JTextComponent kanjiTextPane;
	private final float kanjiFontSize = 200f;
	private final float messageFontSize = 30f;

	public ConnectionFailKanjiOfflinePage(Font kanjiFont) {
		kanjiTextPane = GuiElementsCreator.createTextPane(
				new TextPaneOptions().border(null).editable(false)
						.textAlignment(TextAlignment.CENTERED).text(""));
		kanjiTextPane.setFont(kanjiFont.deriveFont(kanjiFontSize));
		mainPanel = new MainPanel();
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));
	}

	@Override
	public JPanel getConnectionFailPage() {
		return mainPanel.getPanel();
	}

	@Override
	public void modifyConnectionFailPage(WebContext context) {
		if (context.isEmpty()) {
			kanjiTextPane.setText(context.getNoContentMessage());
			setFontSize(messageFontSize);
		}
		else {
			kanjiTextPane.setText(context.getContent());
			setFontSize(kanjiFontSize);
		}

		mainPanel.updateView();
	}

	private void setFontSize(float fontSize) {
		kanjiTextPane.setFont(kanjiTextPane.getFont().deriveFont(fontSize));
	}

}
