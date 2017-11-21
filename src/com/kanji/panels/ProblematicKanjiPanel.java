package com.kanji.panels;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Set;

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
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.strings.Labels;
import com.kanji.strings.Titles;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.saving.ProblematicKanjisState;
import com.kanji.myList.MyList;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicKanjisController controller;
	private Dimension preferredSize = new Dimension(600, 600);
	private int maximumNumberOfRows = 5;
	private MainPanel wordsList;
	private JFXPanel webPanel;
	private JTextComponent kanjiPane;
	private Font kanjiFont;

	public ProblematicKanjiPanel(Font kanjiFont, MyList <KanjiInformation> kanjiList,
			ApplicationWindow parentDialog, ProblematicKanjisController controller) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		webPanel = new JFXPanel();
		this.kanjiFont = kanjiFont;
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

		kanjiPane = GuiMaker.createTextPane(new TextPaneOptions().border(null).editable(false)
				.textAlignment(TextAlignment.CENTERED).text(""));
		kanjiPane.setFont(kanjiFont);

		AbstractButton withInternet = createRadioButtonForLearningWithInternet();
		AbstractButton withoutInternet = createRadioButtonForLearningWithoutInternet();
		ButtonGroup group = new ButtonGroup();
		group.add(withInternet);
		group.add(withoutInternet);
		AbstractButton buttonClose = createButtonClose();
		MainPanel radioButtonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		radioButtonsPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				new JLabel(Titles.OPTIONS_FOR_SHOWING_PROBLEMATIC_KANJIS)).nextRow(withInternet,
						withoutInternet));
		AbstractButton maximize = createButtonWithHotkey(KeyModifiers.ALT,
				KeyEvent.VK_ENTER, controller.createMaximizeAction(),
				ButtonsNames.MAXIMIZE, HotkeysDescriptions.MAXIMIZE_WINDOW);
		//TODO make the dialog full screen immediately without the need for button
		webPanel.setPreferredSize(new Dimension(400,600));
		webPanel.setBorder(getDefaultBorder());
		webPanel.setBackground(Color.white);

		wordsList = new MainPanel(null);
		wordsList.setBorder(getDefaultBorder());
		wordsList.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, maximize)
				.nextRow(FillType.HORIZONTAL,radioButtonsPanel.getPanel())
				.nextRow(FillType.BOTH, controller.getKanjiRepeatingList().getPanel())
						.setNotOpaque());
		mainPanel.addElementsInColumnStartingFromColumn(webPanel,
				0, wordsList.getPanel(), webPanel);
		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

	public void renderPage (String url){
		Platform.setImplicitExit(false);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				WebView webView = new WebView();
				WebEngine engine = webView.getEngine();
				engine.load(url);
				VBox pane = new VBox(webView);
				webPanel.setScene(new Scene(pane));

			}
		});

	}

	@Override
	protected MainPanel parentPanelForHotkeys (){
		return wordsList;
	}

	private AbstractButton createRadioButtonForLearningWithInternet() {
		AbstractButton withInternet = GuiMaker.createButtonlikeComponent(ComponentType.RADIOBUTTON,
				Labels.REPEATING_WITH_INTERNET, controller.createActionForShowingKanjiUsingInternet(true),
				KeyEvent.VK_I);
		withInternet.setFocusable(false);
		withInternet.setSelected(true);
		addHotkeysInformation(KeyEvent.VK_I,
                HotkeysDescriptions.SHOW_KANJI_WITH_INTERNET);
		return withInternet;
	}

	private AbstractButton createRadioButtonForLearningWithoutInternet() {
		AbstractButton withoutInternet = GuiMaker.createButtonlikeComponent(
				ComponentType.RADIOBUTTON, Labels.REPEATING_WITHOUT_INTERNET,
				controller.createActionForShowingKanjiUsingInternet(false), KeyEvent.VK_N);
		withoutInternet.setFocusable(false);
		addHotkeysInformation(KeyEvent.VK_N,
                HotkeysDescriptions.SHOW_KANJI_WITHOUT_INTERNET);
		return withoutInternet;
	}

	private void configureParentDialog() {

		addHotkey(KeyEvent.VK_SPACE, controller.createActionShowNextKanjiOrCloseDialog(),
				((JDialog) parentDialog.getContainer()).getRootPane(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);

		controller.limitSizeIfTooManyRows(maximumNumberOfRows);
		parentDialog.getContainer().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				controller.closeDialogAndManageState(parentDialog);
			}
		});
	}

	public void showKanjiDialog(String kanji) {
		kanjiPane.setText(kanji);
		SwingNode swingNode = new SwingNode();

		JPanel p = new JPanel();
		p.add(kanjiPane, BorderLayout.CENTER);
		swingNode.setContent(p);
		StackPane pane = new StackPane();
		pane.getChildren().add(swingNode);
		webPanel.setScene(new Scene(pane));

	}

	public void showMessage(String message) {
		parentDialog.showMessageDialog(message);
	}

	public void limitSize() {
		parentDialog.getContainer().setPreferredSize(preferredSize);
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
