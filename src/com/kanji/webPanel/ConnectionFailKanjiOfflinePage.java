package com.kanji.webPanel;

import com.guimaker.colors.BasicColors;
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
		mainPanel = new MainPanel(BasicColors.VERY_BLUE);
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));
	}

	@Override
	public JPanel getConnectionFailPage() {
		return mainPanel.getPanel();
	}

	@Override
	public void modifyConnectionFailPage(KanjiContext context) {
		if (context.isEmpty()) {
			kanjiTextPane.setText(Prompts.NO_KANJI_TO_DISPLAY);
			setFontSize(messageFontSize);
		}
		else {
			kanjiTextPane.setText(context.getKanjiCharacter());
			setFontSize(kanjiFontSize);
		}

		mainPanel.updateView();
	}

	private void setFontSize(float fontSize) {
		kanjiTextPane.setFont(kanjiTextPane.getFont().deriveFont(fontSize));
	}

}
