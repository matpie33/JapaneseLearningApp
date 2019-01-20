package com.kanji.panelsAndControllers.panels;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.*;
import com.guimaker.list.myList.ListConfiguration;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.HotkeyWrapper;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.model.WebContext;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.panelSwitching.FocusableComponentsManager;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.webPanel.ContextOwner;
import com.guimaker.webPanel.WebPagePanel;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.JapaneseApplicationButtonsNames;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.model.KanjiData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.webPageEnhancer.WebPageActions;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.util.List;

public class ProblematicJapaneseWordsPanel extends AbstractPanelWithHotkeysInfo
		implements ContextOwner {

	private static final String UNIQUE_NAME = "Problematic japanese words panel";
	private MyList<JapaneseWord> problematicWordsList;
	private MainPanel kanjiInformationPanel;
	private WebPagePanel englishPolishDictionaryPanel;
	private WebPagePanel japaneseEnglishDictionaryPanel;
	private WebPagePanel kanjiKoohiWebPanel;
	private ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer;
	private JapaneseWordPanelCreator japanesePanelCreator;
	private FocusableComponentsManager focusableComponentsManager;
	private ProblematicWordsController<JapaneseWord> problematicWordsController;
	private ApplicationController applicationController;

	public ProblematicJapaneseWordsPanel(
			ApplicationController applicationController,
			ProblematicJapaneseWordsDisplayer problematicJapaneseWordsDisplayer,
			ProblematicWordsController<JapaneseWord> problematicWordsController) {
		this.applicationController = applicationController;
		focusableComponentsManager = new FocusableComponentsManager(getPanel());
		this.problematicWordsController = problematicWordsController;
		this.parentDialog = applicationController.getApplicationWindow();
		this.problematicJapaneseWordsDisplayer = problematicJapaneseWordsDisplayer;
		kanjiInformationPanel = new MainPanel(
				new PanelConfiguration().putRowsAsHighestAsPossible());
		englishPolishDictionaryPanel = new WebPagePanel(this, null,
				applicationController.getApplicationWindow());
		createJapaneseEnglishDictionary(applicationController);
		createKanjiKoohiWebPanel(applicationController);
		createProblematicWordsList();
	}

	private void createJapaneseEnglishDictionary(
			ApplicationController applicationController) {
		japaneseEnglishDictionaryPanel = new WebPagePanel(this, null,
				applicationController.getApplicationWindow());
		WebPageActions webPageActions = new WebPageActions(japaneseEnglishDictionaryPanel,
				applicationController);
		japaneseEnglishDictionaryPanel.addHotkey(new HotkeyWrapper(KeyModifiers.ALT,
						KeyEvent.VK_R),
				webPageActions.createActionCallEnglishDictionary());
	}

	private void createKanjiKoohiWebPanel(
			ApplicationController applicationController) {
		kanjiKoohiWebPanel = new WebPagePanel(this, null,
				applicationController.getApplicationWindow());
		WebPageActions webPageActions = new WebPageActions(kanjiKoohiWebPanel,
				applicationController);
		kanjiKoohiWebPanel.addHotkey(
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_Q),
				webPageActions.createActionFindKanjiPolishKeyword());
		kanjiKoohiWebPanel.addHotkey(
				new HotkeyWrapper(KeyModifiers.ALT, KeyEvent.VK_T),
				webPageActions.createActionCallEnglishDictionary());
	}

	public FocusableComponentsManager getFocusableComponentsManager() {
		return focusableComponentsManager;
	}

	public WebPagePanel getKanjiKoohiWebPanel() {
		return kanjiKoohiWebPanel;
	}

	public WebPagePanel getEnglishPolishDictionaryPanel() {
		return englishPolishDictionaryPanel;
	}

	public WebPagePanel getJapaneseEnglishDictionaryPanel() {
		return japaneseEnglishDictionaryPanel;
	}

	public void createKanjiInformationPanelForKanjiData(
			List<KanjiData> kanjiDataList) {
		kanjiInformationPanel.clear();
		kanjiInformationPanel.addRow(
				SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER,
						GuiElementsCreator.createLabel(
								new ComponentOptions().text(
										Prompts.KANJI_INFORMATIONS))));
		for (KanjiData kanjiData : kanjiDataList) {
			JLabel kanjiLabel = GuiElementsCreator.createLabel(
					new ComponentOptions().text(kanjiData.getKanjiCharacter())
										  .font(ApplicationWindow.getKanjiFont()));
			kanjiLabel.setFont(kanjiLabel.getFont()
										 .deriveFont(30f));
			//TODO set the kanjis font in one place for whole application
			JTextComponent keywordLabel = GuiElementsCreator.createTextArea(
					new TextAreaOptions().editable(false)
										 .rowsAndColumns(2, 5)
										 .text(kanjiData.getKanji() == null ?
												 Prompts.NO_KANJI_INFORMATION_AVAILABLE :
												 kanjiData.getKanji()
														  .getKeyword()));
			AbstractButton goToKanjiStoryButton = createGoToKanjiStoryButton(
					kanjiData);
			goToKanjiStoryButton.setFocusable(false);
			kanjiInformationPanel.addElementsInColumn(
					SimpleRowBuilder.createRowStartingFromColumn(0,
							FillType.NONE, kanjiLabel, keywordLabel,
							goToKanjiStoryButton));
		}
		kanjiInformationPanel.updateView();
	}

	private AbstractButton createGoToKanjiStoryButton(KanjiData kanjiData) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(
						JapaneseApplicationButtonsNames.SHOW_KANJI_STORIES),
				problematicJapaneseWordsDisplayer.createActionShowKanjiDetailsInKoohiPage(
						kanjiData.getKanji(), kanjiData.getKanjiCharacter()));
	}

	private JapaneseWordPanelCreator createJapanesePanelCreator(
			ApplicationWindow applicationWindow) {
		return new JapaneseWordPanelCreator(applicationController,
				applicationWindow, PanelDisplayMode.VIEW);
	}

	private AbstractButton createButtonSearchWord() {
		return createButtonWithHotkey(KeyModifiers.ALT, KeyEvent.VK_C,
				problematicJapaneseWordsDisplayer.createActionSearchCurrentWordInDictionary(),
				JapaneseApplicationButtonsNames.SEARCH_IN_DICTIONARY,
				HotkeysDescriptions.SEARCH_IN_DICTIONARY);

	}

	public JapaneseWordPanelCreator getJapanesePanelCreator() {
		return japanesePanelCreator;
	}

	@Override
	public void createElements() {

		focusableComponentsManager.makeFocusable(
				problematicWordsList.getPanel(),
				japaneseEnglishDictionaryPanel.getWebPanel(),
				englishPolishDictionaryPanel.getWebPanel(),
				kanjiInformationPanel.getPanel(),
				kanjiKoohiWebPanel.getWebPanel());

		JScrollPane scrollPaneForKanjiInformation = GuiElementsCreator.createScrollPane(
				new ScrollPaneOptions().componentToWrap(
						kanjiInformationPanel.getPanel()));

		JSplitPane wordsListAndKanjiInformationSplitPane = GuiElementsCreator.createSplitPane(
				SplitPaneOrientation.VERTICAL, scrollPaneForKanjiInformation,
				problematicWordsList.getPanel(), 0.2);
		JSplitPane dictionariesSplitPane = GuiElementsCreator.createSplitPane(
				SplitPaneOrientation.VERTICAL,
				japaneseEnglishDictionaryPanel.getSwitchingPanel(),
				englishPolishDictionaryPanel.getSwitchingPanel(), 0.5);

		JSplitPane dictionariesWithProblematicWordsSplitPane = GuiElementsCreator.createSplitPane(
				SplitPaneOrientation.HORIZONTAL, dictionariesSplitPane,
				wordsListAndKanjiInformationSplitPane, 0.3);

		JSplitPane splitPane = GuiElementsCreator.createSplitPane(
				SplitPaneOrientation.HORIZONTAL,
				dictionariesWithProblematicWordsSplitPane,
				kanjiKoohiWebPanel.getSwitchingPanel(), 0.7);

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, splitPane));
		new ProblematicWordsPanelCommonPart(this,
				problematicWordsController).addCommonPartToPanel();

	}

	private void createProblematicWordsList() {
		japanesePanelCreator = createJapanesePanelCreator(
				(ApplicationWindow) parentDialog);
		this.problematicWordsList = new MyList<>(
				new ListConfiguration<>(Prompts.JAPANESE_WORD_DELETE,
						new RowInJapaneseWordInformations(japanesePanelCreator),
						JapaneseWord.getInitializer(),
						Titles.PROBLEMATIC_JAPANESE_WORDS, parentDialog,
						applicationController).enableWordAdding(false)
											  .showButtonsLoadNextPreviousWords(
													  false)
											  .withAdditionalNavigationButtons(
													  createButtonSearchWord()));

		japanesePanelCreator.setWordsList(problematicWordsList);
		problematicWordsList.addListObserver(applicationController);
	}

	@Override
	public WebContext getContext() {
		return null; //TODO implement it to return kanji tried to retrieve
	}

	public MyList<JapaneseWord> getWordsList() {
		return problematicWordsList;
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}
}
