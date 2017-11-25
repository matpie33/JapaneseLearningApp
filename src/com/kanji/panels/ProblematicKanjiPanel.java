package com.kanji.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import com.kanji.strings.*;
import javafx.beans.value.ChangeListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
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

	private final String OFFLINE_KANJI_PANEL = "Offline kanji";
	private final String ONLINE_KANJI_PANEL = "Online kanji";

	public ProblematicKanjiPanel(Font kanjiFont, MyList <KanjiInformation> kanjiList,
			ApplicationWindow parentDialog, ProblematicKanjisController controller) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		kanjiOnlineDisplayingPanel = new JFXPanel();
		this.kanjiFont = kanjiFont;
		kanjiDisplayingPanel = new JPanel();
		kanjiOfflineDisplayingPanel = new MainPanel(BasicColors.VERY_BLUE);
		messageFont = new JLabel().getFont().deriveFont(15f);
	}

	public void showKanjiKoohiLoginPage (){
		System.out.println("show kanji koohi");
		controller.showKanjiKoohiLoginPage();
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

		kanjiDisplayingPanel.setLayout(new CardLayout());
		kanjiDisplayingPanel.add(kanjiOfflineDisplayingPanel.getPanel(), OFFLINE_KANJI_PANEL);
		kanjiDisplayingPanel.add(kanjiOnlineDisplayingPanel, ONLINE_KANJI_PANEL);

		AbstractButton buttonClose = createButtonClose();
		AbstractButton maximize = createButtonWithHotkey(KeyModifiers.ALT,
				KeyEvent.VK_ENTER, controller.createMaximizeAction(),
				ButtonsNames.MAXIMIZE, HotkeysDescriptions.MAXIMIZE_WINDOW);
		//TODO make the dialog full screen immediately without the need for button
		kanjiOnlineDisplayingPanel.setPreferredSize(new Dimension(400,600));
		kanjiOnlineDisplayingPanel.setBorder(getDefaultBorder());
		kanjiOnlineDisplayingPanel.setBackground(Color.white);

		kanjiOfflineDisplayingPanel
				.addRow(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));

		wordsList = new MainPanel(null);
		wordsList.setBorder(getDefaultBorder());
		wordsList.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, maximize)
				.nextRow(FillType.BOTH, controller.getKanjiRepeatingList().getPanel())
						.setNotOpaque());

		mainPanel.addElementsInColumnStartingFromColumn(kanjiDisplayingPanel,
				0, wordsList.getPanel(), kanjiDisplayingPanel);
		setNavigationButtons(Anchor.CENTER, buttonClose);

	}

	public void renderPage (ChangeListener connectionFailListener, String url){
		Platform.setImplicitExit(false);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebView webView = new WebView();
				WebEngine engine = webView.getEngine();

				StackPane pane = new StackPane(webView);
				kanjiOnlineDisplayingPanel.setScene(new Scene(pane));
				engine.load(url);
				engine.getLoadWorker().stateProperty().addListener(connectionFailListener);
				showPanel(ONLINE_KANJI_PANEL);
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
