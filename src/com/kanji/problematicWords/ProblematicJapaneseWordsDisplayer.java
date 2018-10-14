package com.kanji.problematicWords;

import com.guimaker.enums.MoveDirection;
import com.kanji.constants.enums.TypeOfWordForRepeating;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Urls;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanjiData;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panelUpdaters.ProblematicJapaneseWordsPanelUpdater;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.webPanel.KanjiKoohiWebPageHandler;
import com.kanji.windows.ApplicationWindow;

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

	public ProblematicJapaneseWordsDisplayer(
			ApplicationWindow applicationWindow,
			ProblematicWordsController<JapaneseWord> controller) {

		this.applicationWindow = applicationWindow;
		problematicJapaneseWordsPanel = new ProblematicJapaneseWordsPanel(
				applicationWindow, this, controller);
		kanjiList = applicationWindow.getApplicationController().getKanjiList();
		problematicJapaneseWordsPanelUpdater = new ProblematicJapaneseWordsPanelUpdater(
				problematicJapaneseWordsPanel);
		controller.setProblematicWordsDisplayer(this, TypeOfWordForRepeating
				.JAPANESE_WORDS);
		kanjiKoohiWebPageHandler = KanjiKoohiWebPageHandler.getInstance();

	}

	@Override
	public MyList<JapaneseWord> getWordsToReviewList() {
		return problematicJapaneseWordsPanel.getWordsList();
	}

	@Override
	public void browseWord(JapaneseWord japaneseWord) {
		KanjiCharactersReader kanjiCharactersReader = KanjiCharactersReader
				.getInstance();
		Set<String> kanjis = extractKanjis(japaneseWord);
		List<KanjiData> kanjiDataList = new ArrayList<>();
		for (String kanji : kanjis) {
			Kanji kanjiInformation = kanjiList.
					findRowBasedOnPropertyStartingFromBeginningOfList(
							new KanjiIdChecker(),
							kanjiCharactersReader.getIdOfKanji(kanji),
							MoveDirection.BELOW, false);
			kanjiDataList.add(new KanjiData(kanji, kanjiInformation));
		}

		problematicJapaneseWordsPanelUpdater
				.addInformationAboutKanjisForGivenWord(kanjiDataList);
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
				JTextComponent selectedInput = problematicJapaneseWordsPanel
						.getJapanesePanelCreator()
						.getListInputsSelectionManager().getSelectedInput();

				if (selectedInput != null) {
					problematicJapaneseWordsPanel
							.getJapaneseEnglishDictionaryPanel()
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
		return Urls.TANGORIN_URL + "/general/" + currentlySelectedWord;
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
