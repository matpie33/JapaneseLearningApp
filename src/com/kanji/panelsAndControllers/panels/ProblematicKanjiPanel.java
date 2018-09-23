package com.kanji.panelsAndControllers.panels;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.options.TextPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.guimaker.webPanel.ContextOwner;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.constants.enums.SplitPaneOrientation;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Urls;
import com.kanji.list.myList.MyList;
import com.kanji.panelSwitching.FocusableComponentsManager;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.webPanel.ConnectionFailKanjiOfflinePage;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;
import javafx.application.Platform;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicWordsController controller;
	private JTextComponent kanjiTextPane;
	private MainPanel kanjiOfflineDisplayingPanel;
	private Font messageFont;

	private WebPagePanel dictionaryWebPanel;
	private WebPagePanel kanjiWebPanel;
	private MyList wordsToReviewList;

	private FocusableComponentsManager focusableComponentsManager;

	public ProblematicKanjiPanel(Font kanjiFont, ApplicationWindow parentDialog,
			ProblematicWordsController controller,
			ContextOwner kanjiContextContextOwner) {
		this.parentDialog = parentDialog;
		this.controller = controller;
		kanjiOfflineDisplayingPanel = new MainPanel(BasicColors.BLUE_DARK_1);
		messageFont = new JLabel().getFont().deriveFont(15f);
		dictionaryWebPanel = new WebPagePanel(kanjiContextContextOwner, null);
		kanjiWebPanel = new WebPagePanel(kanjiContextContextOwner,
				new ConnectionFailKanjiOfflinePage(kanjiFont));
		focusableComponentsManager = new FocusableComponentsManager(
				mainPanel.getPanel());
	}

	public void initialize(String pageToRender) {
		dictionaryWebPanel
				.showPageWithoutGrabbingFocus(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		wordsToReviewList = controller.getWordsToReviewList();
		showPageInKoohi(pageToRender);
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

		kanjiOfflineDisplayingPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, kanjiTextPane));

		focusableComponentsManager.makeFocusable(wordsToReviewList.getPanel());
		focusableComponentsManager
				.makeFocusable(dictionaryWebPanel.getWebPanel());
		focusableComponentsManager.makeFocusable(kanjiWebPanel.getWebPanel());

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

		AbstractButton buttonReturn = createButtonReturn();

		setNavigationButtons(Anchor.WEST, buttonReturn);
	}

	private AbstractButton createButtonReturn() {
		return createButtonWithHotkey(KeyModifiers.CONTROL, KeyEvent.VK_E,
				controller.closeDialogAndManageState(), ButtonsNames.GO_BACK,
				HotkeysDescriptions.RETURN_FROM_LEARNING);

	}

	public void showPageInKoohi(String url) {
		kanjiWebPanel.showPageWithoutGrabbingFocus(url);
	}

	@Override
	public DialogWindow getDialog() {
		return parentDialog;
	}

	public boolean isListPanelFocused() {
		return focusableComponentsManager.getFocusedComponent().equals(wordsToReviewList.getPanel());
	}

	public void focusPreviouslyFocusedElement() {
		focusableComponentsManager.focusPreviouslyFocusedElement();
	}
}
