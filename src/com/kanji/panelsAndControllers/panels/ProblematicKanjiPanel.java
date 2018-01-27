package com.kanji.panelsAndControllers.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.*;
import com.kanji.context.ContextOwner;
import com.kanji.context.KanjiContext;
import com.kanji.list.myList.MyList;
import com.kanji.utilities.CommonGuiElementsMaker;
import com.kanji.utilities.FocusableComponentMaker;
import com.kanji.webPanel.ConnectionFailKanjiOfflinePage;
import com.kanji.webPanel.ConnectionFailMessagePage;
import com.kanji.webPanel.WebPagePanel;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;
import javafx.embed.swing.JFXPanel;

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
			ContextOwner <KanjiContext> kanjiContextContextOwner) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		kanjiOnlineDisplayingPanel = new JFXPanel();
		kanjiOfflineDisplayingPanel = new MainPanel(BasicColors.VERY_BLUE);
		messageFont = new JLabel().getFont().deriveFont(15f);
		dictionaryWebPanel = new WebPagePanel(kanjiContextContextOwner, new ConnectionFailMessagePage());
		kanjiWebPanel = new WebPagePanel(kanjiContextContextOwner, new ConnectionFailKanjiOfflinePage(kanjiFont));
	}

	public void initialize(){
		dictionaryWebPanel.showPageWithoutGrabbingFocus(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		wordsToReviewList = controller.getWordsToReviewList();
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
		configureParentDialog();
	}

	@Override
	public void createElements() {

		kanjiTextPane = GuiMaker.createTextPane(new TextPaneOptions().border(null).editable(false)
				.textAlignment(TextAlignment.CENTERED).text("").border(getDefaultBorder()));
		kanjiTextPane.setText(Prompts.NO_KANJI_TO_DISPLAY);
		kanjiTextPane.setFont(messageFont);

		AbstractButton buttonClose = createButtonClose();
		kanjiOnlineDisplayingPanel.setBorder(getDefaultBorder());
		kanjiOnlineDisplayingPanel.setBackground(Color.white);

		kanjiOfflineDisplayingPanel
				.addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));

		FocusableComponentMaker.makeFocusable(wordsToReviewList.getPanel());
		FocusableComponentMaker.makeFocusable(dictionaryWebPanel.getWebPanel());
		FocusableComponentMaker.makeFocusable(kanjiWebPanel.getWebPanel());


		JSplitPane wordsAndDictionaryPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL, dictionaryWebPanel.getSwitchingPanel(),
				wordsToReviewList.getPanel(), 0.7);

		JSplitPane mainSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL, wordsAndDictionaryPane,
				kanjiWebPanel.getSwitchingPanel(), 0.3);

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH,
				mainSplitPane));

		setNavigationButtons(Anchor.WEST, buttonClose);
	}

//	@Override
//	protected MainPanel parentPanelForHotkeys (){
//		return wordsToReviewList.getpa;
//	}

	private void configureParentDialog() {


	}

	public void showPageInKoohi (String url){
		kanjiWebPanel.showPageWithoutGrabbingFocus(url);
	}

	@Override
	public DialogWindow getDialog() {
		return parentDialog;
	}

	public boolean isListPanelFocused(){
		return wordsToReviewList.getPanel().hasFocus();
	}

}
