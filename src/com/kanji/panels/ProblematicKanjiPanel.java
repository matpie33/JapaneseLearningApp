package com.kanji.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Set;

import com.kanji.enums.SplitPaneOrientation;
import com.kanji.strings.*;
import com.kanji.utilities.CommonGuiElementsMaker;
import javafx.beans.value.ChangeListener;

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
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicKanjisController controller;
	private MainPanel wordsList;
	private JFXPanel kanjiOnlineDisplayingPanel;
	private JTextComponent kanjiTextPane;
	private Font kanjiFont;
	private MainPanel kanjiOfflineDisplayingPanel;
	private JPanel kanjiDisplayingPanel;
	private Font messageFont;
	private WebView webView;
	private JFXPanel dictionaryPanel;
	private WebView dictWebView;
	private final String DICTIONARY_PL_EN_MAIN_PAGE = "https://pl.bab.la/slownik/polski-angielski/";

	private final String OFFLINE_KANJI_PANEL = "Offline kanji";
	private final String ONLINE_KANJI_PANEL = "Online kanji";

	public ProblematicKanjiPanel(Font kanjiFont, MyList <KanjiInformation> kanjiList,
			ApplicationWindow parentDialog, ProblematicKanjisController controller) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		kanjiOnlineDisplayingPanel = new JFXPanel();
		this.kanjiFont = kanjiFont;
		kanjiDisplayingPanel = new JPanel(new CardLayout());
		kanjiOfflineDisplayingPanel = new MainPanel(BasicColors.VERY_BLUE);
		messageFont = new JLabel().getFont().deriveFont(15f);
		initiateWebView();
	}

	private void initiateWebView (){
		Platform.runLater(new Runnable() {
			@Override public void run() {
				webView = new WebView();
				dictWebView = new WebView();
				//TODO rewrite it using lambdas and method references
			}
		});
	}

	public void showKanjiKoohiLoginPage (){
		controller.showKanjiKoohiLoginPage();
		createDictionaryPanel();
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

		kanjiDisplayingPanel.add(kanjiOfflineDisplayingPanel.getPanel(), OFFLINE_KANJI_PANEL);
		kanjiDisplayingPanel.add(kanjiOnlineDisplayingPanel, ONLINE_KANJI_PANEL);

		AbstractButton buttonClose = createButtonClose();
		kanjiOnlineDisplayingPanel.setBorder(getDefaultBorder());
		kanjiOnlineDisplayingPanel.setBackground(Color.white);

		kanjiOfflineDisplayingPanel
				.addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));

		wordsList = new MainPanel(BasicColors.OCEAN_BLUE);
		wordsList.setBorder(getDefaultBorder());
		wordsList.addRows(SimpleRowBuilder.createRow(FillType.BOTH,
				controller.getKanjiRepeatingList().getPanel()));

		JSplitPane wordsAndDictionaryPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.VERTICAL);
		wordsAndDictionaryPane.setLeftComponent(dictionaryPanel);
		wordsAndDictionaryPane.setRightComponent(wordsList.getPanel());
		wordsAndDictionaryPane.setResizeWeight(0.7);

		JSplitPane mainSplitPane = CommonGuiElementsMaker.createSplitPane(
				SplitPaneOrientation.HORIZONTAL);
		mainSplitPane.setLeftComponent(wordsAndDictionaryPane);
		mainSplitPane.setRightComponent(kanjiDisplayingPanel);
		mainSplitPane.setResizeWeight(0.3);

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.BOTH,
				mainSplitPane));

		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

	private void createDictionaryPanel (){
		WebEngine engine = dictWebView.getEngine();
		StackPane pane = new StackPane(dictWebView);

		dictionaryPanel = new JFXPanel();
		dictionaryPanel.setScene(new Scene(pane));
		Platform.runLater(new Runnable() {
			@Override public void run() {
				engine.load(DICTIONARY_PL_EN_MAIN_PAGE);
				//TODO throw exception in main dictionaryPanel when using add row method for adding multiple rows
			}
		});

	}

	public void renderPage (ChangeListener connectionFailListener, String url){
		Platform.setImplicitExit(false);
		//TODO try to rewrite to with implicit exit as true to spare memory
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebEngine engine = webView.getEngine();
				StackPane pane = new StackPane(webView);
				kanjiOnlineDisplayingPanel.setScene(new Scene(pane));
				engine.load(url);
				engine.getLoadWorker().stateProperty().addListener(connectionFailListener);
				SwingUtilities.invokeLater(new Runnable() {
					@Override public void run() {
						showPanel(ONLINE_KANJI_PANEL);
					}
				});
			}
		});

	}

	public void displayConnectionErrorMessage (){
		showMessageInKanjiPanel(Prompts.CONNECTION_ERROR);
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

	public void showKanji(String kanji) {
		if (!kanjiTextPane.getFont().equals(kanjiFont)){
			kanjiTextPane.setFont(kanjiFont);
		}
		kanjiTextPane.setText(kanji);
		showPanel(OFFLINE_KANJI_PANEL);
	}

	public void showMessageInKanjiPanel (String message){
		kanjiTextPane.setText(message);
		kanjiTextPane.setFont(messageFont);
		showPanel(OFFLINE_KANJI_PANEL);
	}

	private void showPanel (String panelLabel){
		((CardLayout) kanjiDisplayingPanel.getLayout()).show(kanjiDisplayingPanel, panelLabel);
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
