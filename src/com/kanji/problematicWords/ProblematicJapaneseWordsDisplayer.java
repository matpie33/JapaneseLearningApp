package com.kanji.problematicWords;

import com.guimaker.enums.MoveDirection;
import com.kanji.constants.strings.ExceptionsMessages;
import com.kanji.constants.strings.Urls;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.myList.MyList;
import com.kanji.model.KanjiData;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panelUpdaters.ProblematicJapaneseWordsPanelUpdater;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
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

	public ProblematicJapaneseWordsDisplayer(
			ApplicationWindow applicationWindow,
			ProblematicWordsController<JapaneseWord> controller) {

		this.applicationWindow = applicationWindow;
		problematicJapaneseWordsPanel = new ProblematicJapaneseWordsPanel(
				controller, applicationWindow, this);
		controller.setProblematicWordsDisplayer(this);
		kanjiList = applicationWindow.getApplicationController().getKanjiList();
		problematicJapaneseWordsPanelUpdater = new ProblematicJapaneseWordsPanelUpdater(
				problematicJapaneseWordsPanel);

	}

	@Override
	public MyList<JapaneseWord> getWordsToReviewList() {
		return problematicJapaneseWordsPanel.getWordsList();
	}

	@Override
	public void browseWord(WordRow<JapaneseWord> wordRow) {
		JapaneseWord japaneseWord = wordRow.getListElement();
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

	public void showKoohiPage(String kanjiData) {
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
	public WordRow createWordRow(JapaneseWord listElement, int rowNumber) {
		return new WordRow(listElement, rowNumber);
	}

	@Override
	public void initializeWebPages() {
		problematicJapaneseWordsPanel.getJapaneseEnglishDictionaryPanel()
				.showPage(Urls.TANGORIN_URL);
		problematicJapaneseWordsPanel.getEnglishPolishDictionaryPanel()
				.showPage(Urls.DICTIONARY_PL_EN_MAIN_PAGE);
		String pageToRender = isLoginDataRemembered() ?
				Urls.KANJI_KOOHI_MAIN_PAGE :
				Urls.KANJI_KOOHI_LOGIN_PAGE;
		problematicJapaneseWordsPanel.getKanjiKoohiWebPanel()
				.showPageWithoutGrabbingFocus(pageToRender);
	}

	private boolean isLoginDataRemembered() {
		//TODO duplicated code from problematic kanji displayer
		CookieManager cookieManager = (CookieManager) CookieHandler
				.getDefault();
		for (HttpCookie cookies : cookieManager.getCookieStore().getCookies()) {
			if (cookies.getName().equals("koohii")) {
				return true;
			}
		}
		return false;
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
