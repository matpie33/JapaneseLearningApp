package com.kanji.problematicWords;

import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.enums.PanelDisplayMode;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.TextAreaOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.enums.MovingDirection;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.constants.strings.Titles;
import com.kanji.list.listElementPropertyManagers.KanjiIdChecker;
import com.kanji.list.listElements.JapaneseWord;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listRows.RowInJapaneseWordInformations;
import com.kanji.list.listRows.japanesePanelCreatingComponents.JapaneseWordPanelCreator;
import com.kanji.list.myList.ListConfiguration;
import com.kanji.list.myList.MyList;
import com.kanji.model.WordRow;
import com.kanji.panelsAndControllers.controllers.ProblematicWordsController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ProblematicJapaneseWordsPanel;
import com.kanji.utilities.JapaneseWritingUtilities;
import com.kanji.utilities.KanjiCharactersReader;
import com.kanji.windows.ApplicationWindow;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class ProblematicJapaneseWordsDisplayer
		implements ProblematicWordsDisplayer<JapaneseWord> {

	private MyList<JapaneseWord> wordsToReviewList;
	private ProblematicJapaneseWordsPanel problematicJapaneseWordsPanel;
	private MyList<Kanji> kanjiInformations;
	private JapaneseWordPanelCreator japaneseWordPanelCreator;

	public ProblematicJapaneseWordsDisplayer(
			ApplicationWindow applicationWindow,
			ProblematicWordsController controller) {

		problematicJapaneseWordsPanel = new ProblematicJapaneseWordsPanel(
				controller, applicationWindow, this);
		japaneseWordPanelCreator = createJapanesePanelCreator(
				applicationWindow);
		this.wordsToReviewList = new MyList<>(applicationWindow, null,
				new RowInJapaneseWordInformations(japaneseWordPanelCreator),
				Titles.PROBLEMATIC_KANJIS,
				new ListConfiguration().enableWordAdding(false)
						.withAdditionalNavigationButtons(
								createButtonSearchWord()),
				JapaneseWord.getInitializer());
		controller.setProblematicWordsDisplayer(this);
		kanjiInformations = applicationWindow.getApplicationController()
				.getKanjiList();

	}

	private AbstractButton createButtonSearchWord() {
		return problematicJapaneseWordsPanel
				.createButtonWithHotkey(KeyModifiers.ALT, KeyEvent.VK_C,
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								searchCurrentWordInDictionary();
							}
						}, ButtonsNames.SEARCH_IN_DICTIONARY,
						HotkeysDescriptions.SEARCH_IN_DICTIONARY);

	}

	private JapaneseWordPanelCreator createJapanesePanelCreator(
			ApplicationWindow applicationWindow) {
		return new JapaneseWordPanelCreator(
				applicationWindow.getApplicationController(), applicationWindow,
				PanelDisplayMode.VIEW);
	}

	@Override
	public MyList<JapaneseWord> getWordsToReviewList() {
		return wordsToReviewList;
	}

	@Override
	public void browseWord(WordRow<JapaneseWord> wordRow) {
		JapaneseWord japaneseWord = wordRow.getListElement();
		KanjiCharactersReader kanjiCharactersReader = KanjiCharactersReader
				.getInstance();
		Set<String> kanjis = extractKanjis(japaneseWord);
		MainPanel panel = problematicJapaneseWordsPanel
				.getKanjiInformationPanel();
		panel.clear();
		panel.addElementsInColumn(SimpleRowBuilder.createRow(FillType.NONE,
				GuiElementsCreator.createLabel(new ComponentOptions()
						.text(Prompts.KANJI_INFORMATIONS)))
				.setColumnToPutRowInto(2));
		//TODO refactor main panel to respect anchors when mixing elements in column and elements one by one

		for (String kanji : kanjis) {
			Kanji kanjiInformation = kanjiInformations.
					findRowBasedOnPropertyStartingFromBeginningOfList(
							new KanjiIdChecker(),
							kanjiCharactersReader.getIdOfKanji(kanji),
							MovingDirection.FORWARD, false);
			JLabel kanjiLabel = GuiElementsCreator.createLabel(
					new ComponentOptions().text(kanji)
							.font(ApplicationWindow.getKanjiFont()));
			kanjiLabel.setFont(kanjiLabel.getFont().deriveFont(30f));
			//TODO set the kanjis font in one place for whole application
			String keyword = kanjiInformation != null ?
					kanjiInformation.getKeyword() :
					Prompts.NO_KANJI_INFORMATION_AVAILABLE;
			JTextComponent keywordLabel = GuiElementsCreator.createTextArea(
					new TextAreaOptions().editable(false).rowsAndColumns(2, 5)
							.text(keyword));
			AbstractButton goToButton = GuiElementsCreator
					.createButtonlikeComponent(
							new ButtonOptions(ButtonType.BUTTON)
									.text(ButtonsNames.SEARCH),
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
														.getId());
									}

								}
							});
			goToButton.setFocusable(false);

			panel.addElementsInColumn(SimpleRowBuilder
					.createRowStartingFromColumn(0, FillType.NONE, kanjiLabel,
							keywordLabel, goToButton));
		}
		panel.updateView();
	}

	private Set<String> extractKanjis(JapaneseWord japaneseWord) {
		Set<String> kanjis = new HashSet<>();
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
	public void initialize() {
		problematicJapaneseWordsPanel.initialize();
	}

	@Override
	public AbstractPanelWithHotkeysInfo getPanel() {
		return problematicJapaneseWordsPanel;
	}

	public void searchCurrentWordInDictionary() {
		String currentlySelectedWord = japaneseWordPanelCreator
				.getListInputsSelectionManager().getSelectedInput().getText();

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
