package com.kanji.panelsAndControllers.panels;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.model.WebContext;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.options.TextAreaOptions;
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
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanjiData;
import com.kanji.panelSwitching.FocusableComponentsManager;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.problematicWords.ProblematicJapaneseWordsDisplayer;
import com.kanji.utilities.CommonGuiElementsCreator;
import com.kanji.windows.ApplicationWindow;

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
		kanjiInformationPanel = new MainPanel(null, true);
		englishPolishDictionaryPanel = new WebPagePanel(this, null);
		japaneseEnglishDictionaryPanel = new WebPagePanel(this, null);
		kanjiKoohiWebPanel = new WebPagePanel(this, null);
		createProblematicWordsList();
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
		kanjiInformationPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, GuiElementsCreator
						.createLabel(new ComponentOptions()
								.text(Prompts.KANJI_INFORMATIONS))));
		for (KanjiData kanjiData : kanjiDataList) {
			JLabel kanjiLabel = GuiElementsCreator.createLabel(
					new ComponentOptions().text(kanjiData.getKanjiCharacter())
							.font(ApplicationWindow.getKanjiFont()));
			kanjiLabel.setFont(kanjiLabel.getFont().deriveFont(30f));
			//TODO set the kanjis font in one place for whole application
			JTextComponent keywordLabel = GuiElementsCreator.createTextArea(
					new TextAreaOptions().editable(false).rowsAndColumns(2, 5)
							.text(kanjiData.getKanji() == null ?
									Prompts.NO_KANJI_INFORMATION_AVAILABLE :
									kanjiData.getKanji().getKeyword()));
			AbstractButton goToKanjiStoryButton = createGoToKanjiStoryButton(
					kanjiData);
			goToKanjiStoryButton.setFocusable(false);
			kanjiInformationPanel.addElementsInColumn(SimpleRowBuilder
					.createRowStartingFromColumn(0, FillType.NONE, kanjiLabel,
							keywordLabel, goToKanjiStoryButton));
		}
		kanjiInformationPanel.updateView();
	}

	private AbstractButton createGoToKanjiStoryButton(KanjiData kanjiData) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.SHOW_KANJI_STORIES),
				problematicJapaneseWordsDisplayer
						.createActionShowKanjiDetailsInKoohiPage(
								kanjiData.getKanji(),
								kanjiData.getKanjiCharacter()));
	}

	private JapaneseWordPanelCreator createJapanesePanelCreator(
			ApplicationWindow applicationWindow) {
		return new JapaneseWordPanelCreator(applicationController,
				applicationWindow, PanelDisplayMode.VIEW);
	}

	private AbstractButton createButtonSearchWord() {
		return createButtonWithHotkey(KeyModifiers.ALT, KeyEvent.VK_C,
				problematicJapaneseWordsDisplayer
						.createActionSearchCurrentWordInDictionary(),
				ButtonsNames.SEARCH_IN_DICTIONARY,
				HotkeysDescriptions.SEARCH_IN_DICTIONARY);

	}

	public JapaneseWordPanelCreator getJapanesePanelCreator() {
		return japanesePanelCreator;
	}

	@Override
	public void createElements() {

		focusableComponentsManager
				.makeFocusable(problematicWordsList.getPanel(),
						japaneseEnglishDictionaryPanel.getWebPanel(),
						englishPolishDictionaryPanel.getWebPanel(),
						kanjiInformationPanel.getPanel(),
						kanjiKoohiWebPanel.getWebPanel());

		JScrollPane scrollPaneForKanjiInformation = GuiElementsCreator
				.createScrollPane(new ScrollPaneOptions()
						.componentToWrap(kanjiInformationPanel.getPanel()));

		JSplitPane wordsListAndKanjiInformationSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						scrollPaneForKanjiInformation,
						problematicWordsList.getPanel(), 0.2);
		JSplitPane dictionariesSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.VERTICAL,
						japaneseEnglishDictionaryPanel.getSwitchingPanel(),
						englishPolishDictionaryPanel.getSwitchingPanel(), 0.5);

		JSplitPane dictionariesWithProblematicWordsSplitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						dictionariesSplitPane,
						wordsListAndKanjiInformationSplitPane, 0.3);

		JSplitPane splitPane = CommonGuiElementsCreator
				.createSplitPane(SplitPaneOrientation.HORIZONTAL,
						dictionariesWithProblematicWordsSplitPane,
						kanjiKoohiWebPanel.getSwitchingPanel(), 0.7);

		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, splitPane));
		new ProblematicWordsPanelCommonPart(this, problematicWordsController)
				.addCommonPartToPanel();

	}

	private void createProblematicWordsList() {
		japanesePanelCreator = createJapanesePanelCreator(
				(ApplicationWindow) parentDialog);
		this.problematicWordsList = new MyList<>(parentDialog,
				applicationController,
				new RowInJapaneseWordInformations(japanesePanelCreator),
				Titles.PROBLEMATIC_KANJIS,
				new ListConfiguration().enableWordAdding(false)
						.showButtonsLoadNextPreviousWords(false)
						.withAdditionalNavigationButtons(
								createButtonSearchWord()),
				JapaneseWord.getInitializer());
		japanesePanelCreator.setWordsList(problematicWordsList);
		problematicWordsList.addListObserver(applicationController);
	}

	@Override
	public WebContext getContext() {
		return null; //TODO implement it to return kanji tried to retrieve
	}

	public void setList(MyList<JapaneseWord> list) {
		this.problematicWordsList = list;
	}

	public MyList<JapaneseWord> getWordsList() {
		return problematicWordsList;
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}
}
