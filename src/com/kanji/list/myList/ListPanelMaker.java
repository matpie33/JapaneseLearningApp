package com.kanji.list.myList;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.ListRow;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.range.Range;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ListPanelMaker<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private ListWordsController<Word> listWordsController;
	private MainPanel rowsPanel;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(350, 200);
	private JLabel titleLabel;
	private ListRowMaker<Word> listRow;
	private Border rowBorder = BorderFactory
			.createMatteBorder(0, 0, 2, 0, BasicColors.LIGHT_BLUE);
	private ApplicationController applicationController;
	private MainPanel listPanel;
	private boolean enableWordAdding;
	private AbstractButton buttonLoadNextWords;
	private AbstractButton buttonLoadPreviousWords;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;

	public ListPanelMaker(boolean enableWordAdding,
			ApplicationController applicationController,
			ListRowMaker<Word> listRow, ListWordsController<Word> controller) {
		this.applicationController = applicationController;
		listWordsController = controller;
		this.enableWordAdding = enableWordAdding;

		rowsPanel = new MainPanel(null, true);
		listPanel = new MainPanel(null);
		titleLabel = new JLabel();
		createDefaultScrollPane();

		rowsPanel.setBorder(rowBorder);
		this.listRow = listRow;
		createAndAddButtonsShowNextAndPrevious();
	}

	public LoadNextWordsHandler getLoadNextWordsHandler() {
		return loadNextWordsHandler;
	}

	public LoadPreviousWordsHandler getLoadPreviousWordsHandler() {
		return loadPreviousWordsHandler;
	}

	private void createAndAddButtonsShowNextAndPrevious() {
		buttonLoadNextWords = createAndAddButtonLoadWords(
				ButtonsNames.SHOW_NEXT_WORDS_ON_LIST);
		buttonLoadPreviousWords = createAndAddButtonLoadWords(
				ButtonsNames.SHOW_PREVIOUS_WORDS_ON_LIST);
		loadNextWordsHandler = new LoadNextWordsHandler(listWordsController,
				rowsPanel);
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, rowsPanel);
		buttonLoadNextWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadNextWordsHandler));
		buttonLoadPreviousWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadPreviousWordsHandler));
		rowsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, buttonLoadPreviousWords));
		rowsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.HORIZONTAL, buttonLoadNextWords));
	}

	private AbstractButton createAndAddButtonLoadWords(String buttonName) {
		AbstractButton button = GuiMaker
				.createButtonlikeComponent(ComponentType.BUTTON, buttonName,
						null);
		button.setEnabled(false);
		return button;
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, LoadWordsHandler loadWordsHandler) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowNumber));
		JButton remove = new JButton(ButtonsNames.REMOVE_ROW);
		remove.addActionListener(
				listWordsController.createDeleteRowAction(word));
		CommonListElements commonListElements = new CommonListElements(remove,
				rowNumberLabel);
		rowNumberLabel.setForeground(BasicColors.OCEAN_BLUE);
		JComponent row = null;
		if (shouldShowWord) {
			SimpleRow simpleRow = SimpleRowBuilder
					.createRow(FillType.HORIZONTAL, Anchor.NORTH,
							listRow.createListRow(word, commonListElements)
									.getPanel());
			row = loadWordsHandler.showWord(simpleRow);
		}
		else if (!buttonLoadNextWords.isEnabled()) {
			buttonLoadNextWords.setEnabled(true);
		}
		rowsPanel.updateView();
		return new ListRow<>(word, row, rowNumberLabel);
	}

	private AbstractAction createButtonShowNextOrPreviousWords(
			LoadWordsHandler loadWordsHandler) {

		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int numberOfAddedWords = listWordsController
						.addNextHalfOfMaximumWords(loadWordsHandler);
				if (numberOfAddedWords > 0) {
					removeWordsFromRangeInclusive(loadWordsHandler
							.getRangeOfWordsToRemove(numberOfAddedWords));
				}
				boolean hasMoreWordsToShow = numberOfAddedWords
						== listWordsController.getMaximumWordsToShow() / 2;
				loadWordsHandler
						.enableOrDisableLoadWordsButtons(buttonLoadNextWords,
								buttonLoadPreviousWords, hasMoreWordsToShow);
				rowsPanel.updateView();
			}
		};

	}

	public String createTextForRowNumber(int rowNumber) {
		return "" + rowNumber + ". ";
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	@Override
	public void createElements() {
		listPanel.addRows(SimpleRowBuilder
				.createRow(FillType.NONE, Anchor.CENTER, titleLabel)
				.nextRow(FillType.BOTH, parentScrollPane));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, listPanel.getPanel()));
		setNavigationButtons(enableWordAdding ?
				new AbstractButton[] { createButtonAddWord(),
						createButtonFindWord() } :
				new AbstractButton[] { createButtonFindWord() });
	}

	private void createDefaultScrollPane() {
		Border raisedBevel = BorderFactory
				.createMatteBorder(3, 3, 0, 0, BasicColors.LIGHT_BLUE);
		parentScrollPane = GuiMaker.createScrollPane(
				new ScrollPaneOptions().componentToWrap(rowsPanel.getPanel())
						.backgroundColor(BasicColors.VERY_BLUE)
						.border(raisedBevel).preferredSize(scrollPanesSize));

	}

	public void removeWordsFromRangeInclusive(Range range) {
		rowsPanel.removeRowsInclusive(range.getRangeStart(),
				range.getRangeEnd());
	}

	private AbstractButton createButtonAddWord() {
		String name = ButtonsNames.ADD;
		String hotkeyDescription = HotkeysDescriptions.ADD_WORD;
		int keyEvent = KeyEvent.VK_I;
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.showInsertWordDialog();
			}
		};
		//TODO add in my list a parameter with hotkeys mapping for add/search panels
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action,
				name, hotkeyDescription);

	}

	private AbstractButton createButtonFindWord() {
		String name = ButtonsNames.SEARCH;
		//TODO differentiate between my list - kanji vs repeating list vs problematic kanjis?
		String hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
		int keyEvent = KeyEvent.VK_F;
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.showSearchWordDialog();
			}
		};
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action,
				name, hotkeyDescription);
	}

	public void clearHighlightedRow(JComponent row) {
		rowsPanel.clearPanelColor(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = rowsPanel.getIndexOfPanel(row);
		changePanelColor(rowNumber, Color.red);
		scrollTo(rowsPanel.getRows().get(rowNumber));
		this.rowsPanel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			this.parentScrollPane.getViewport()
					.setViewPosition(new Point(0, r));
		});
	}

	public void scrollToBottom() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				JScrollBar scrollBar = parentScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});

	}

	public int removeRow(JComponent panel) {
		int rowNumber = rowsPanel.getIndexOfPanel(panel);
		rowsPanel.removeRow(rowNumber);
		return rowNumber;
	}

	public void clear() {
		rowsPanel.clear();
		createAndAddButtonsShowNextAndPrevious();
	}

	public void scrollToTop() {
		SwingUtilities.invokeLater(
				() -> parentScrollPane.getVerticalScrollBar().setValue(0));
	}

	public int getNumberOfListRows() {
		return rowsPanel.getNumberOfRows() - 2;
	}

	public void enableButtonShowPreviousWords() {
		buttonLoadPreviousWords.setEnabled(true);
	}
}
