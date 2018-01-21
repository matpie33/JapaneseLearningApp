package com.kanji.webPanel;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.kanjiContext.KanjiContextOwner;
import com.kanji.strings.Prompts;
import com.kanji.utilities.FocusableComponentMaker;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class WebPagePanel {

	private JFXPanel webPage;
	private WebView webView;
	private JPanel switchingPanel;
	private MainPanel messagePanel;
	private ChangeListener connectionChange;
	private final String MESSAGE_PANEL = "MESSAGE PANEL";
	private final String WEB_PAGE_PANEL = "WEB PAGE PANEL";
	private final String CONNECTION_FAIL_PANEL = "CONNECTION FAIL PANEL";
	private JTextComponent messageComponent;
	private JPanel connectionFailPanel;
	private KanjiContextOwner kanjiContextOwner;
	private boolean shouldGrabFocusOnReload;

	public WebPagePanel (KanjiContextOwner kanjiContextOwner,
			ConnectionFailPageHandler connectionFailPageHandler){
		this.kanjiContextOwner = kanjiContextOwner;
		initiateConnectionFailListener (connectionFailPageHandler);
		initiateWebView();
		initiatePanels();
		shouldGrabFocusOnReload = true;
	}

	private void initiateConnectionFailListener(ConnectionFailPageHandler connectionFailPageHandler){
		connectionFailPanel = connectionFailPageHandler.getConnectionFailPage();
		Platform.setImplicitExit(false);
		connectionChange = new ChangeListener<Worker.State>() {
			@Override
			public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, final Worker.State newValue) {
				if (newValue == Worker.State.FAILED) {
					connectionFailPageHandler.modifyConnectionFailPage(kanjiContextOwner.getKanjiContext());
					showPanel(CONNECTION_FAIL_PANEL);
					shouldGrabFocusOnReload = true;
				}
				if (newValue == Worker.State.SUCCEEDED){
					showPanel(WEB_PAGE_PANEL);
					if (shouldGrabFocusOnReload){
						webPage.requestFocusInWindow();
					}
					shouldGrabFocusOnReload = true;
				}
				if (newValue == Worker.State.SCHEDULED){
					showPanel(MESSAGE_PANEL);
				}
			}
		};
	}

	private void showPanel (String panel){
		((CardLayout) switchingPanel.getLayout()).show(switchingPanel, panel);
	}

	private void initiatePanels (){
		messagePanel = new MainPanel(null);
		messageComponent = GuiMaker.createTextPane(new TextPaneOptions().
				text(Prompts.LOADING_PAGE).fontSize(20).textAlignment(TextAlignment.CENTERED));
		messageComponent.setText(Prompts.LOADING_PAGE);
		messagePanel.addRow(
				SimpleRowBuilder.createRow(FillType.HORIZONTAL, Anchor.CENTER, messageComponent));

		Platform.runLater(new Runnable() {
			@Override public void run() {
				StackPane pane = new StackPane(webView);
				webView.getEngine().getLoadWorker().stateProperty().addListener(connectionChange);
				webPage.setScene(new Scene(pane));
			}
		});
		webPage = new JFXPanel();
		switchingPanel = new JPanel (new CardLayout());
		switchingPanel.add(MESSAGE_PANEL, messagePanel.getPanel());
		switchingPanel.add(WEB_PAGE_PANEL, webPage);
		switchingPanel.add(CONNECTION_FAIL_PANEL, connectionFailPanel);
	}

	public JFXPanel getWebPagePanel (){
		return webPage;
	}

	private void initiateWebView(){
		Platform.runLater(new Runnable() {
			@Override public void run() {
				webView = new WebView();
			}
		});
	}

	public void showPage (String url){
		displayLoadingMessage();
		Platform.runLater(()->webView.getEngine().load(url));
	}

	public void showPageWithoutGrabbingFocus (String url){
		shouldGrabFocusOnReload = false;
		displayLoadingMessage();
		Platform.runLater(()->webView.getEngine().load(url));
	}

	private void displayLoadingMessage (){
		showPanel(MESSAGE_PANEL);

	}

	public JPanel getSwitchingPanel(){
		return switchingPanel;
	}

	public void showInTextPane (String text){
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				messageComponent.setText(text);
				showPanel(MESSAGE_PANEL);
			}
		});
	}

}
