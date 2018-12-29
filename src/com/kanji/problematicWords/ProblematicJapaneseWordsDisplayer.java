package com.kanji.problematicWords;

import com.guimaker.application.ApplicationWindow;
import com.guimaker.enums.MoveDirection;
import com.guimaker.list.myList.MyList;
import com.guimaker.model.WebContext;
import com.guimaker.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Urls;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.model.KanjiData;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panelUpdaters.ProblematicJapaneseWordsPanelUpdater;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.webPanel.KanjiKoohiWebPageHandler;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ProblematicJapaneseWordsDisplayer
		implements ProblematicWordsDisplayer<JapaneseWord> {

	private ProblematicJapaneseWordsPanel problematicJapaneseWordsPanel;
	private MyList<Kanji> kanjiList;
	private ApplicationWindow applicationWindow;
	private ProblematicJapaneseWordsPanelUpdater problematicJapaneseWordsPanelUpdater;
	private KanjiKoohiWebPageHandler kanjiKoohiWebPageHandler;
	private ProblematicWordsController<JapaneseWord> controller;
	private MyList<JapaneseWord> wordsToReviewList;

	public ProblematicJapaneseWordsDisplayer(
			ApplicationController applicationController) {

		this.applicationWindow = applicationController.getApplicationWindow();
		controller = new ProblematicWordsController<>(applicationController,
				this);
		problematicJapaneseWordsPanel = new ProblematicJapaneseWordsPanel(
				applicationController, this, controller);
		problematicJapaneseWordsPanel.setParentDialog(applicationWindow);
		kanjiList = applicationController.getKanjiList();
		problematicJapaneseWordsPanelUpdater = new ProblematicJapaneseWordsPanelUpdater(
				problematicJapaneseWordsPanel);
		kanjiKoohiWebPageHandler = KanjiKoohiWebPageHandler.getInstance();
		wordsToReviewList = problematicJapaneseWordsPanel.getWordsList();

	}

	public ProblematicWordsController<JapaneseWord> getController() {
		return controller;
	}

	@Override
	public WebContext getContext() {
		return null; //TODO implement it
	}

	@Override
	public MyList<JapaneseWord> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(JapaneseWord japaneseWord) {
		KanjiCharactersReader kanjiCharactersReader = KanjiCharactersReader.getInstance();
		Set<String> kanjis = extractKanjis(japaneseWord);
		List<KanjiData> kanjiDataList = new ArrayList<>();
		for (String kanji : kanjis) {
			Kanji kanjiInformation = kanjiList.findRowBasedOnPropertyStartingFromBeginningOfList(
					new KanjiIdChecker(),
					kanjiCharactersReader.getIdOfKanji(kanji),
					MoveDirection.BELOW, false);
			kanjiDataList.add(new KanjiData(kanji, kanjiInformation));
		}

		problematicJapaneseWordsPanelUpdater.addInformationAboutKanjisForGivenWord(
				kanjiDataList);
	}

	@Override
	public String getKanjiKoohiLoginCookieHeader() {
		return kanjiKoohiWebPageHandler.getKanjiKoohiLoginCookieHeader();
	}

	public AbstractAction createActionShowKanjiDetailsInKoohiPage(
			Kanji kanjiInformation, String kanjiCharacter) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String kanjiCharacterOrId = kanjiInformation == null ?
						kanjiCharacter :
						"" + kanjiInformation.getId();
				showKoohiPage(kanjiCharacterOrId);

			}
		};
	}

	private void showKoohiPage(String kanjiData) {
		String uriText = Urls.KANJI_KOOHI_REVIEW_BASE_PAGE;
		uriText += kanjiData;
		problematicJapaneseWordsPanel.getKanjiKoohiWebPanel()
									 .showPageWithoutGrabbingFocus(uriText);
	}

	private Set<String> extractKanjis(JapaneseWord japaneseWord) {
		Set<String> kanjis = new LinkedHashSet<>();
		Set<String> kanjiWritings = japaneseWord.getKanjiWritings();
		for (String kanjiWriting : kanjiWritings) {
			for (int i = 0; i < kanjiWriting.length(); i++) {
				char nextCharacter = kanjiWriting.charAt(i);
				if (JapaneseWritingUtilities.characterIsKanji(nextCharacter)) {
					kanjis.add("" + nextCharacter);
				}
			}
		}
		return kanjis;
	}

	@Override
	public void initializeWebPages() {
		problematicJapaneseWordsPanel.getJapaneseEnglishDictionaryPanel()
									 .showPage(Urls.TANGORIN_URL);
		problematicJapaneseWordsPanel.getEnglishPolishDictionaryPanel()
									 .showPage(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		problematicJapaneseWordsPanel.getKanjiKoohiWebPanel()
									 .showPageWithoutGrabbingFocus(
											 kanjiKoohiWebPageHandler.getInitialPage());
	}

	@Override
	public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicJapaneseWordsPanel;
	}

	public AbstractAction createActionSearchCurrentWordInDictionary() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextComponent selectedInput = problematicJapaneseWordsPanel.getJapanesePanelCreator()
																			.getListInputsSelectionManager()
																			.getSelectedInput();

				if (selectedInput != null) {
					problematicJapaneseWordsPanel.getJapaneseEnglishDictionaryPanel()
												 .showPageWithoutGrabbingFocus(
														 createUrlForWordInJapaneseEnglishDictionary(
																 selectedInput.getText()));
				}
				else {
					applicationWindow.showMessageDialog(
							ExceptionsMessages.NO_SELECTED_WORD_TO_SEARCH_IN_DICTIONARY);
				}
			}
		};

	}

	private String createUrlForWordInJapaneseEnglishDictionary(
			String currentlySelectedWord) {
		return String.format(Urls.TANGORIN_SEARCH_PATTERN,
				currentlySelectedWord);
	}

	@Override
	public boolean isListPanelFocused() {
		return problematicJapaneseWordsPanel.getFocusableComponentsManager()
											.getFocusedComponent()
											.equals(problematicJapaneseWordsPanel.getWordsList()
																				 .getPanel());
	}

	@Override
	public void focusPreviouslyFocusedElement() {
		problematicJapaneseWordsPanel.getFocusableComponentsManager()
									 .focusPreviouslyFocusedElement();
	}
}
