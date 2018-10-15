package com.kanji.list.myList;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ButtonType;
import com.guimaker.enums.FillType;
import com.guimaker.inputSelection.ListInputsSelectionManager;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ButtonOptions;
import com.guimaker.options.ComponentOptions;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.AbstractSimpleRow;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.ColorChanger;
import com.guimaker.utilities.HotkeyWrapper;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.constants.Colors;
import com.kanji.constants.enums.InputGoal;
import com.kanji.constants.strings.ButtonsNames;
import com.kanji.constants.strings.HotkeysDescriptions;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.ListElement;
import com.kanji.model.ListRow;
import com.kanji.panelsAndControllers.controllers.ApplicationController;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.range.Range;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ListPanelCreator<Word extends ListElement>
		extends AbstractPanelWithHotkeysInfo {

	private ListInputsSelectionManager listInputsSelectionManager;
	private ListWordsController<Word> listWordsController;
	private MainPanel rowsPanel;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(550, 100);
	private JLabel titleLabel;
	private ListRowCreator<Word> listRow;
	private ApplicationController applicationController;
	private MainPanel rootPanel;
	private boolean enableWordAdding;
	private AbstractButton buttonLoadNextWords;
	private AbstractButton buttonLoadPreviousWords;
	private LoadNextWordsHandler loadNextWordsHandler;
	private LoadPreviousWordsHandler loadPreviousWordsHandler;
	private List<AbstractButton> navigationButtons;
	private JComponent listElementsPanel;
	private boolean isScrollBarInherited;
	private boolean enableWordSearching;
	private boolean showButtonsNextAndPrevious;
	private boolean isSkipTitle;
	private Color labelsColor = Color.WHITE;
	private boolean scrollBarSizeFittingContent;
	private InputGoal inputGoal;
	private boolean hasParentList;
	private ListSearchPanelCreator<Word> listSearchPanelCreator;
	private final static String UNIQUE_NAME = "list panel creator";

	public ListPanelCreator(ListConfiguration listConfiguration,
			ApplicationController applicationController,
			ListRowCreator<Word> listRow,
			ListWordsController<Word> controller) {
		listSearchPanelCreator = new ListSearchPanelCreator<>();
		this.applicationController = applicationController;
		listWordsController = controller;
		isSkipTitle = listConfiguration.isSkipTitle();
		hasParentList =
				listConfiguration.getParentListAndWordContainingThisList()
						!= null;

		rowsPanel = new MainPanel(null, true, true,
				new PanelConfiguration(listConfiguration.getDisplayMode()));
		rootPanel = new MainPanel(null);
		if (hasParentList) {
			mainPanel.setRowColor(ColorChanger.makeLighter(getContentColor()));
			rootPanel.setRowColor(ColorChanger.makeLighter(getContentColor()));
		}

		titleLabel = GuiElementsCreator.createLabel(new ComponentOptions());
		loadNextWordsHandler = new LoadNextWordsHandler(listWordsController,
				rowsPanel);
		loadPreviousWordsHandler = new LoadPreviousWordsHandler(
				listWordsController, rowsPanel);
		this.listRow = listRow;

		navigationButtons = new ArrayList<>();
		unwrapConfiguration(listConfiguration);
		listInputsSelectionManager = listConfiguration
				.getAllInputsSelectionManager();

	}

	public void addElementsForEmptyList() {
		rowsPanel.insertRow(1, SimpleRowBuilder.createRow(FillType.NONE,
				GuiElementsCreator.createLabel(
						new ComponentOptions().text(Prompts.EMPTY_LIST)),
				createButtonAddRow(InputGoal.EDIT)));
	}

	private void unwrapConfiguration(ListConfiguration listConfiguration) {
		this.enableWordAdding = listConfiguration.isWordAddingEnabled();
		this.isScrollBarInherited = listConfiguration.isScrollBarInherited();
		this.enableWordSearching = listConfiguration.isWordSearchingEnabled();
		showButtonsNextAndPrevious = listConfiguration
				.isShowButtonsLoadNextPreviousWords();
		scrollBarSizeFittingContent = listConfiguration
				.isScrollBarSizeFittingContent();
		addNavigationButtons(
				listConfiguration.getAdditionalNavigationButtons());
		//TODO redundant code - keep reference to list configuration instead of keep all the params in this class
	}

	public void inheritScrollPane() {
		isScrollBarInherited = false;
	}

	private void addNavigationButtons(AbstractButton... buttons) {
		for (AbstractButton button : buttons) {
			navigationButtons.add(button);
		}
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
		buttonLoadNextWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadNextWordsHandler));
		buttonLoadPreviousWords.addActionListener(
				createButtonShowNextOrPreviousWords(loadPreviousWordsHandler));
		rowsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, buttonLoadPreviousWords));
		rowsPanel.addRow(SimpleRowBuilder
				.createRow(FillType.NONE, buttonLoadNextWords));
		if (!showButtonsNextAndPrevious) {
			buttonLoadPreviousWords.setVisible(false);
			buttonLoadNextWords.setVisible(false);
		}
	}

	private AbstractButton createAndAddButtonLoadWords(String buttonName) {
		AbstractButton button = GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(buttonName), null);
		button.setEnabled(false);
		return button;
	}

	public ListRow<Word> addRow(Word word, int rowNumber,
			boolean shouldShowWord, LoadWordsHandler loadWordsHandler,
			InputGoal inputGoal) {
		this.inputGoal = inputGoal;
		CommonListElements commonListElements = createCommonListElements(word,
				inputGoal, rowNumber);
		MainPanel rowPanel = null;
		if (shouldShowWord) {
			ListRowData<Word> listRow = this.listRow
					.createListRow(word, commonListElements, inputGoal);
			rowPanel = listRow.getRowPanel();
			AbstractSimpleRow abstractSimpleRow = SimpleRowBuilder
					.createRow(FillType.HORIZONTAL, Anchor.NORTH,
							rowPanel.getPanel());
			if (hasParentList) {
				rowPanel.setBackground(
						ColorChanger.makeLighter(getContentColor()));
			}
			loadWordsHandler.showWord(abstractSimpleRow);
		}
		else if (!buttonLoadNextWords.isEnabled()) {
			buttonLoadNextWords.setEnabled(true);
		}
		if (rowPanel != null && listInputsSelectionManager != null) {
			rowPanel.addManager(listInputsSelectionManager);
		}
		rowsPanel.updateView();
		return new ListRow<>(word, rowPanel,
				commonListElements.getRowNumberLabel(), rowNumber);
	}

	private CommonListElements createCommonListElements(Word word,
			InputGoal inputGoal, int rowNumber) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowNumber));
		rowNumberLabel.setForeground(labelsColor);
		AbstractButton remove = createButtonRemoveWord(word);
		AbstractButton addNewWord = createButtonAddRow(inputGoal);
		AbstractButton editWord = createButtonEditWord(word);
		AbstractButton finishEditing = createButtonFinishEditing(word);
		return new CommonListElements(remove, rowNumberLabel, addNewWord,
				labelsColor, editWord, finishEditing, false);

	}

	private AbstractButton createButtonRemoveWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.REMOVE_ROW),
				listWordsController.createDeleteRowAction(word));
	}

	private AbstractButton createButtonAddRow(InputGoal inputGoal) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.ADD_ROW),
				new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						listWordsController.addNewWord(inputGoal);
					}
				});
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
				boolean hasMoreWordsToShow = numberOfAddedWords == Math
						.ceil((double) listWordsController
								.getMaximumWordsToShow() / 2);
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
		createRootPanel();
		createAndAddButtonsShowNextAndPrevious();
		addElementsForEmptyList();
		if (enableWordAdding) {
			navigationButtons.add(createButtonAddWord());
		}
		if (enableWordSearching) {
			ListRowData<Word> listRow = this.listRow.createListRow(
					listWordsController.getWordInitializer()
							.initializeElement(),
					CommonListElements.forSingleRowOnly(Color.WHITE),
					InputGoal.SEARCH);
			if (!listRow.isEmpty()) {
				mainPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
						listSearchPanelCreator.createPanel(listRow,
								createButtonFilter(listSearchPanelCreator)))
						.setColor(BasicColors.GREEN_BRIGHT_1));
				addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_SPACE,
						listSearchPanelCreator
								.createActionSwitchComboboxValue(),
						mainPanel.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
				addHotkey(KeyModifiers.CONTROL, KeyEvent.VK_F,
						new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								listSearchPanelCreator.getFilteringInput()
										.requestFocusInWindow();
							}
						}, mainPanel.getPanel(),
						HotkeysDescriptions.SWITCH_SEARCH_CRITERIA);
			}
		}

		if (!isSkipTitle) {
			rootPanel.addRow(SimpleRowBuilder
					.createRow(FillType.NONE, Anchor.CENTER, titleLabel));
		}
		rootPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, listElementsPanel));
		mainPanel.addRow(SimpleRowBuilder
				.createRow(FillType.BOTH, rootPanel.getPanel()));

		if (!enableWordSearching && !enableWordAdding) {
			mainPanel.getPanel().setOpaque(false);
		}

		setNavigationButtons(
				navigationButtons.toArray(new AbstractButton[] {}));
	}

	private AbstractButton createButtonFilter(
			ListSearchPanelCreator<Word> listSearchPanelCreator) {
		AbstractButton filterButton = GuiElementsCreator
				.createButtonLikeComponent(new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.FILTER));
		AbstractAction action = listWordsController
				.createFilterAction(listSearchPanelCreator, filterButton);
		addHotkey(KeyEvent.VK_ENTER, action, getPanel(),
				HotkeysDescriptions.FILTER_WORDS);
		filterButton.addActionListener(action);
		return filterButton;
	}

	private void createRootPanel() {
		if (!isScrollBarInherited) {
			parentScrollPane = GuiElementsCreator.createScrollPane(
					new ScrollPaneOptions().opaque(false)
							.componentToWrap(rowsPanel.getPanel()));
			if (!scrollBarSizeFittingContent) {
				parentScrollPane.setPreferredSize(scrollPanesSize);
			}
			listElementsPanel = parentScrollPane;
		}
		else {
			listElementsPanel = rowsPanel.getPanel();
		}

	}

	public void removeWordsFromRangeInclusive(Range range) {
		rowsPanel.removeRowsInclusive(range.getRangeStart(),
				range.getRangeEnd());
	}

	private AbstractButton createButtonFinishEditing(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON)
						.text(ButtonsNames.FINISH_EDITING),
				listWordsController.createFinishEditAction(word),
				new HotkeyWrapper(KeyEvent.VK_ENTER));
	}

	private AbstractButton createButtonEditWord(Word word) {
		return GuiElementsCreator.createButtonlikeComponent(
				new ButtonOptions(ButtonType.BUTTON).text(ButtonsNames.EDIT),
				listWordsController.createEditWordAction(word));
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

	public void clearHighlightedRow(JComponent row) {
		rowsPanel.clearPanelColor(row);
	}

	public void highlightRowAndScroll(JComponent row) {
		int rowNumber = rowsPanel.getIndexOfPanel(row);
		changePanelColor(rowNumber, Colors.LIST_ROW_HIGHLIGHT_COLOR);
		scrollTo(rowsPanel.getRows().get(rowNumber));
		this.rowsPanel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		if (isScrollBarInherited) {
			//TODO keep reference to the inherited scrollbar and use it to scroll
			return;
		}
		SwingUtilities.invokeLater(() -> {
			int r = panel.getY();
			this.parentScrollPane.getViewport()
					.setViewPosition(new Point(0, r));
		});
	}

	public void scrollToBottom() {
		if (isScrollBarInherited) {
			return;
		}

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

	public MainPanel getRowsPanel() {
		return rowsPanel;
	}

	public void toggleEnabledState() {
		rowsPanel.toggleEnabledState();
	}

	public MainPanel repaintWord(Word word, int rowNumber, JComponent oldPanel,
			InputGoal customInputGoal, boolean highlighted) {
		CommonListElements commonListElements = createCommonListElements(word,
				this.inputGoal, rowNumber);
		MainPanel newPanel = listRow.createListRow(word, commonListElements,
				customInputGoal == null ? this.inputGoal : customInputGoal)
				.getRowPanel();
		if (highlighted) {
			newPanel.setBackground(Colors.LIST_ROW_HIGHLIGHT_COLOR);
		}
		if (customInputGoal != null && customInputGoal
				.equals(InputGoal.EDIT_TEMPORARILY)) {
			newPanel.setBackground(Colors.LIST_ROW_EDIT_TEMPORARILY_COLOR);
			newPanel.updateView();
		}
		rowsPanel.replacePanel(oldPanel, newPanel.getPanel());
		rowsPanel.updateView();
		return newPanel;
	}

	public boolean isFilterInputFocused() {
		return listSearchPanelCreator.getFilteringInput().hasFocus();
	}

	@Override
	public String getUniqueName() {
		return UNIQUE_NAME;
	}
}
