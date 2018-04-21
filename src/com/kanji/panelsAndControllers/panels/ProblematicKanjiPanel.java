package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Urls;
import com.kanji.context.ContextOwner;
import com.kanji.context.KanjiContext;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.utilities.FocusableComponentCreator;
import com.kanji.webPanel.ConnectionFailKanjiOfflinePage;
import com.kanji.webPanel.WebPagePanel;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicWordsController controller;
	private JFXPanel kanjiOnlineDisplayingPanel;
	private JTextComponent kanjiTextPane;
	private MainPanel kanjiOfflineDisplayingPanel;
	private Font messageFont;

	private WebPagePanel dictionaryWebPanel;
	private WebPagePanel kanjiWebPanel;
	private MyList wordsToReviewList;

	public ProblematicKanjiPanel(Font kanjiFont, ApplicationWindow parentDialog,
			ProblematicWordsController controller,
			ContextOwner<KanjiContext> kanjiContextContextOwner) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		kanjiOnlineDisplayingPanel = new JFXPanel();
		kanjiOfflineDisplayingPanel = new MainPanel(BasicColors.VERY_BLUE);
		messageFont = new JLabel().getFont().deriveFont(15f);
		dictionaryWebPanel = new WebPagePanel(kanjiContextContextOwner, null);
		kanjiWebPanel = new WebPagePanel(kanjiContextContextOwner,
				new ConnectionFailKanjiOfflinePage(kanjiFont));
	}

	public void initialize() {
		dictionaryWebPanel
				.showPageWithoutGrabbingFocus(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		wordsToReviewList = controller.getWordsToReviewList();
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
	}

	@Override
	public void createElements() {

		kanjiTextPane = GuiElementsCreator.createTextPane(
				new TextPaneOptions().border(null).editable(false)
						.textAlignment(TextAlignment.CENTERED).text("")
						.border(getDefaultBorder()));
		kanjiTextPane.setText(Prompts.NO_KANJI_TO_DISPLAY);
		kanjiTextPane.setFont(messageFont);

		AbstractButton buttonClose = createButtonClose();
		kanjiOnlineDisplayingPanel.setBorder(getDefaultBorder());
		kanjiOnlineDisplayingPanel.setBackground(Color.white);

		kanjiOfflineDisplayingPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));

		FocusableComponentCreator.makeFocusable(wordsToReviewList.getPanel());
		FocusableComponentCreator.makeFocusable(dictionaryWebPanel.getWebPanel());
		FocusableComponentCreator.makeFocusable(kanjiWebPanel.getWebPanel());

		JSplitPane wordsAndDictionaryPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						dictionaryWebPanel.getSwitchingPanel(),
						wordsToReviewList.getPanel(), 0.7);

		JSplitPane mainSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						wordsAndDictionaryPane,
						kanjiWebPanel.getSwitchingPanel(), 0.2);

		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, mainSplitPane));

		setNavigationButtons(Anchor.WEST, buttonClose);
	}

	public void showPageInKoohi(String url) {
		kanjiWebPanel.showPageWithoutGrabbingFocus(url);
	}

	@Override
	public DialogWindow getDialog() {
		return parentDialog;
	}

	public boolean isListPanelFocused() {
		return wordsToReviewList.getPanel().hasFocus();
	}

}
