package com.kanji.problematicWords;

import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.enums.JapanesePanelDisplayMode;
import com.kanji.constants.enums.SearchingDirection;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWordInformation;
import com.kanji.list.listElements.KanjiInformation;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelCreator.JapaneseWordPanelCreator;
import com.kanji.list.listRows.japanesePanelCreator.TextFieldSelectionHandler;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.utilities.StringUtilities;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

public class ProblematicJapaneseWordsDisplayer
		implements ProblematicWordsDisplayer<JapaneseWordInformation> {

	private MyList<JapaneseWordInformation> wordsToReviewList;
	private ProblematicJapaneseWordsPanel problematicJapaneseWordsPanel;
	private MyList<KanjiInformation> kanjiInformations;
	private TextFieldSelectionHandler selectionHandler;

	public ProblematicJapaneseWordsDisplayer(
			ApplicationWindow applicationWindow,
			ProblematicWordsController controller) {

		problematicJapaneseWordsPanel = new ProblematicJapaneseWordsPanel(
				controller, applicationWindow, this);
		JapaneseWordPanelCreator japanesePanelCreator = createJapanesePanelCreator(
				applicationWindow);
		selectionHandler = japanesePanelCreator.getSelectionHandler();
		this.wordsToReviewList = new MyList<>(applicationWindow, null,
				new RowInJapaneseWordInformations(japanesePanelCreator),
				Titles.PROBLEMATIC_KANJIS,
				new ListConfiguration().enableWordAdding(false)
						.withAdditionalNavigationButtons(
								createButtonSearchWord()),
				JapaneseWordInformation.getInitializer());
		controller.setProblematicWordsDisplayer(this);
		kanjiInformations = applicationWindow.getApplicationController()
				.getKanjiList();

	}

	private AbstractButton createButtonSearchWord() {
		return GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.SEARCH_IN_DICTIONARY, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						searchCurrentWordInDictionary();
					}
				});
	}

	private JapaneseWordPanelCreator createJapanesePanelCreator(
			ApplicationWindow applicationWindow) {
		return new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), applicationWindow,
				JapanesePanelDisplayMode.VIEW);
	}

	@Override
	public MyList<JapaneseWordInformation> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(WordRow<JapaneseWordInformation> wordRow) {
		JapaneseWordInformation japaneseWordInformation = wordRow
				.getListElement();
		KanjiCharactersReader kanjiCharactersReader = KanjiCharactersReader
				.getInstance();
		Set<String> kanjis = extractKanjis(japaneseWordInformation);
		MainPanel panel = problematicJapaneseWordsPanel
				.getKanjiInformationPanel();
		panel.clear();
		for (String kanji : kanjis) {
			KanjiInformation kanjiInformation = kanjiInformations.
					findRowBasedOnPropertyStartingFromBeginningOfList(
							new KanjiIdChecker(),
							kanjiCharactersReader.getIdOfKanji(kanji),
							SearchingDirection.FORWARD, false);
			JLabel kanjiLabel = GuiMaker
					.createLabel(new ComponentOptions().text(kanji));
			kanjiLabel.setFont(kanjiLabel.getFont().deriveFont(30f));
			//TODO set the kanjis font in one place for whole application
			String keyword = kanjiInformation != null ?
					kanjiInformation.getKanjiKeyword() :
					Prompts.NO_KANJI_INFORMATION_AVAILABLE;
			JTextComponent keywordLabel = GuiMaker.createTextArea(
					new TextAreaOptions().rowsAndColumns(2, 5).text(keyword));
			AbstractButton goToButton = GuiMaker
					.createButtonlikeComponent(ComponentType.BUTTON,
							ButtonsNames.SHOW_KANJI_STORIES,
							new AbstractAction() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (kanjiInformation == null) {
										problematicJapaneseWordsPanel
												.showKoohiPage(kanji);
									}
									else {
										problematicJapaneseWordsPanel
												.showKoohiPage(kanjiInformation
														.getKanjiID());
									}

								}
							});
			goToButton.setFocusable(false);

			panel.addElementsInColumnStartingFromColumn(SimpleRowBuilder
					.createRowStartingFromColumn(0, FillType.NONE, keywordLabel,
							goToButton)
					.fillHorizontallySomeElements(kanjiLabel));
		}
		panel.updateView();
	}

	private Set<String> extractKanjis(
			JapaneseWordInformation japaneseWordInformation) {
		Set<String> kanjis = new HashSet<>();
		Set<String> kanjiWritings = japaneseWordInformation.getKanjiWritings();
		for (String kanjiWriting : kanjiWritings) {
			for (int i = 0; i < kanjiWriting.length(); i++) {
				char nextCharacter = kanjiWriting.charAt(i);
				if (StringUtilities.characterIsKanji(nextCharacter)) {
					kanjis.add("" + nextCharacter);
				}
			}
		}
		return kanjis;
	}

	@Override
	public WordRow createWordRow(JapaneseWordInformation listElement,
			int rowNumber) {
		return new WordRow(listElement, rowNumber);
	}

	@Override
	public void initialize() {
		problematicJapaneseWordsPanel.initialize();
	}

	@Override
	public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicJapaneseWordsPanel;
	}

	public void searchCurrentWordInDictionary() {
		String currentlySelectedWord = selectionHandler
				.getCurrentlySelectedWord();
		if (!currentlySelectedWord.isEmpty()) {
			problematicJapaneseWordsPanel.searchWord(currentlySelectedWord);
		}
		else {
			//TODO add message about not selected words
		}
	}

	@Override
	public boolean isListPanelFocused() {
		return wordsToReviewList.getPanel().hasFocus();
	}

}
