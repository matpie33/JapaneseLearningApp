package com.kanji.webPanel;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.ElementCopier;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Prompts;
import com.kanji.context.ContextOwner;
import com.kanji.context.KanjiContext;
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
import java.awt.event.ActionEvent;

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
	private ContextOwner<KanjiContext> contextOwner;
	private boolean shouldGrabFocusOnReload;
	private AbstractButton reloadButton;
	private String currentlyLoadingPage;

	//TODO it's too coupled to kanji context, should be more generic
	public WebPagePanel(ContextOwner<KanjiContext> contextOwner,
			ConnectionFailPageHandler connectionFailPageHandler) {
		this.contextOwner = contextOwner;
		createButtonReload();
		if (connectionFailPageHandler == null) {
			connectionFailPageHandler = new ConnectionFailMessagePage(
					ElementCopier.copyButton(reloadButton));
		}
		initiateConnectionFailListener(connectionFailPageHandler);
		initiatePanels();
		shouldGrabFocusOnReload = true;
	}

	private void initiateConnectionFailListener(
			ConnectionFailPageHandler connectionFailPageHandler) {
		connectionFailPanel = connectionFailPageHandler.getConnectionFailPage();
		Platform.setImplicitExit(false);
		connectionChange = new ChangeListener<Worker.State>() {
			@Override
			public void changed(
					ObservableValue<? extends Worker.State> observable,
					Worker.State oldValue, final Worker.State newValue) {
				if (newValue == Worker.State.FAILED) {
					connectionFailPageHandler.modifyConnectionFailPage(
							contextOwner.getContext());
					showPanel(CONNECTION_FAIL_PANEL);
					shouldGrabFocusOnReload = true;
				}
				if (newValue == Worker.State.SUCCEEDED) {
					showPanel(WEB_PAGE_PANEL);
					if (shouldGrabFocusOnReload) {
						webPage.requestFocusInWindow();
					}
					shouldGrabFocusOnReload = true;
				}
				if (newValue == Worker.State.SCHEDULED) {
					showPanel(MESSAGE_PANEL);
				}
			}
		};
	}

	private void showPanel(String panel) {
		((CardLayout) switchingPanel.getLayout()).show(switchingPanel, panel);
	}

	private void initiatePanels() {
		messagePanel = new MainPanel(null);
		messageComponent = GuiElementsCreator.createTextPane(new TextPaneOptions().
				text(Prompts.LOADING_PAGE).fontSize(20)
				.textAlignment(TextAlignment.CENTERED).editable(false));

		messagePanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, messageComponent));
		messagePanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, reloadButton));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webView = new WebView();
				StackPane pane = new StackPane(webView);
				webView.getEngine().getLoadWorker().stateProperty()
						.addListener(connectionChange);
				webPage = new JFXPanel();
				webPage.setScene(new Scene(pane));

				switchingPanel = new JPanel(new CardLayout());
				switchingPanel.add(MESSAGE_PANEL, messagePanel.getPanel());
				switchingPanel.add(WEB_PAGE_PANEL, webPage);
				switchingPanel.add(CONNECTION_FAIL_PANEL, connectionFailPanel);
			}
		});

	}

	private void createButtonReload() {
		reloadButton = GuiElementsCreator.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.RELOAD_PAGE, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showPage(currentlyLoadingPage);
					}
				});

	}

	public void showPage(String url) {
		currentlyLoadingPage = url;
		Platform.runLater(() -> webView.getEngine().load(url));
	}

	public JFXPanel getWebPanel() {
		return webPage;
	}

	public JPanel getSwitchingPanel() {
		return switchingPanel;
	}

	public void showPageWithoutGrabbingFocus(String url) {
		shouldGrabFocusOnReload = false;
		showPage(url);
	}

}
