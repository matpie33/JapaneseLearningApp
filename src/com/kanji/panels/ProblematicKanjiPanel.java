package com.kanji.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import com.kanji.enums.SplitPaneOrientation;
import com.kanji.strings.*;
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
import com.kanji.listElements.KanjiInformation;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.myList.MyList;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;
import javafx.embed.swing.JFXPanel;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicKanjisController controller;
	private MainPanel wordsList;
	private JFXPanel kanjiOnlineDisplayingPanel;
	private JTextComponent kanjiTextPane;
	private Font kanjiFont;
	private MainPanel kanjiOfflineDisplayingPanel;
	private Font messageFont;
	private final String DICTIONARY_PL_EN_MAIN_PAGE = "https://pl.bab.la/slownik/polski-angielski/";
	private WebPagePanel dictionaryWebPanel;
	private WebPagePanel kanjiWebPanel;

	public ProblematicKanjiPanel(Font kanjiFont, MyList <KanjiInformation> kanjiList,
			ApplicationWindow parentDialog, ProblematicKanjisController controller) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		kanjiOnlineDisplayingPanel = new JFXPanel();
		this.kanjiFont = kanjiFont;
		kanjiOfflineDisplayingPanel = new MainPanel(BasicColors.VERY_BLUE);
		messageFont = new JLabel().getFont().deriveFont(15f);
		dictionaryWebPanel = new WebPagePanel(controller, new ConnectionFailMessagePage());
		kanjiWebPanel = new WebPagePanel(controller, new ConnectionFailKanjiOfflinePage(kanjiFont));
	}

	public void loadDictionaryAndKoohiPage(){
		showPageInKoohi(controller.getKanjiKoohiLoginPageUrl());
		dictionaryWebPanel.showPageWithoutGrabbingFocus(DICTIONARY_PL_EN_MAIN_PAGE);
	}

	@Override
	public void afterVisible (){
		SwingUtilities.invokeLater(() -> wordsList.getPanel().requestFocusInWindow());

	}

	public void restoreState (ProblematicKanjisState problematicKanjisState){
		controller.createProblematicKanjisList(problematicKanjisState.getReviewedKanjis(),
				problematicKanjisState.getNotReviewKanjis());
		controller.highlightReviewedWords(problematicKanjisState.getReviewedKanjis().size());
	}

	public ProblematicKanjisController getController() {
		return controller;
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
		configureParentDialog();
	}

	@Override
	void createElements() {

		kanjiTextPane = GuiMaker.createTextPane(new TextPaneOptions().border(null).editable(false)
				.textAlignment(TextAlignment.CENTERED).text("").border(getDefaultBorder()));
		kanjiTextPane.setText(Prompts.NO_KANJI_TO_DISPLAY);
		kanjiTextPane.setFont(messageFont);

		AbstractButton buttonClose = createButtonClose();
		kanjiOnlineDisplayingPanel.setBorder(getDefaultBorder());
		kanjiOnlineDisplayingPanel.setBackground(Color.white);

		kanjiOfflineDisplayingPanel
				.addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));

		wordsList = new MainPanel(BasicColors.OCEAN_BLUE);
		wordsList.setBorder(getDefaultBorder());
		wordsList.addRows(SimpleRowBuilder.createRow(FillType.BOTH,
				controller.getKanjiRepeatingList().getPanel()));

		FocusableComponentMaker.makeFocusable(wordsList.getPanel());
		FocusableComponentMaker.makeFocusable
				(dictionaryWebPanel.getWebPagePanel());
		FocusableComponentMaker.makeFocusable
				(kanjiWebPanel.getWebPagePanel());

		JSplitPane wordsAndDictionaryPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL);
		wordsAndDictionaryPane.setLeftComponent(dictionaryWebPanel.getSwitchingPanel());
		wordsAndDictionaryPane.setRightComponent(wordsList.getPanel());
		wordsAndDictionaryPane.setResizeWeight(0.7);

		JSplitPane mainSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL);
		mainSplitPane.setLeftComponent(wordsAndDictionaryPane);
		mainSplitPane.setRightComponent(kanjiWebPanel.getSwitchingPanel());
		mainSplitPane.setResizeWeight(0.3);

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH,
				mainSplitPane));

		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

	@Override
	protected MainPanel parentPanelForHotkeys (){
		return wordsList;
	}

	private void configureParentDialog() {

		addHotkey(KeyEvent.VK_SPACE, controller.createActionShowNextKanjiOrCloseDialog(),
				((JDialog) parentDialog.getContainer()).getRootPane(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);

		parentDialog.getContainer().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				controller.closeDialogAndManageState(parentDialog);
			}
		});
		parentDialog.maximize();
	}

	public boolean isListPanelFocused(){
		return wordsList.getPanel().hasFocus();
	}

	public void showPageInKoohi (String url){
		kanjiWebPanel.showPageWithoutGrabbingFocus(url);
	}

	public void highlightRow(int rowNumber) {
		controller.getKanjiRepeatingList().highlightRow(rowNumber);
		//TODO move it to controller or keep kanji repeating list as member variable
	}

	@Override
	public DialogWindow getDialog() {
		return parentDialog;
	}

	public void addProblematicKanjis (Set<Integer> problematicKanjis){
		controller.addProblematicKanjis(problematicKanjis);
	}

}
