package com.kanji.myList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.KeyModifiers;
import com.kanji.controllers.ApplicationController;
import com.kanji.model.ListRow;
import com.kanji.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.strings.ButtonsNames;
import com.kanji.strings.HotkeysDescriptions;
import com.kanji.utilities.CommonListElements;

public class ListPanelMaker<Word> extends AbstractPanelWithHotkeysInfo {

	private ListWordsController<Word> listWordsController;
	private MainPanel rowsPanel;
	private int highlightedRowNumber;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(350, 200);
	private JLabel titleLabel;
	private ListRowMaker<Word> listRow;
	private Border rowBorder = BorderFactory.createMatteBorder(0, 0, 2, 0, BasicColors.LIGHT_BLUE);
	private JPanel parentPanel;
	private ApplicationController applicationController;
	private MainPanel listPanel;
	private boolean enableWordAdding;

	public ListPanelMaker(boolean enableWordAdding, ApplicationController applicationController, ListRowMaker<Word> listRow,
			JPanel parentPanel, ListWordsController<Word> controller) {
		this.applicationController = applicationController;
		listWordsController = controller;
		highlightedRowNumber = -1;
		this. enableWordAdding = enableWordAdding;

		rowsPanel = new MainPanel(null, true);
		listPanel = new MainPanel(null);
		titleLabel = new JLabel();
		createDefaultScrollPane();

		rowsPanel.setBorder(rowBorder);
		this.listRow = listRow;
		this.parentPanel = parentPanel;
	}

	public ListRow<Word> addRow(Word word) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowsPanel.getNumberOfRows()+1));
		JButton remove = new JButton("-");
		remove.addActionListener(listWordsController.createDeleteRowAction(word));
		CommonListElements commonListElements = new CommonListElements(remove, rowNumberLabel);
		rowNumberLabel.setForeground(BasicColors.OCEAN_BLUE);
		JComponent row = rowsPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				Anchor.NORTH, listRow.createListRow(word, commonListElements).getPanel()));
		return new ListRow<Word>(word, row, rowNumberLabel);
	}

	public String createTextForRowNumber(int rowNumber) {
		return "" + rowNumber + ".";
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	public ListWordsController<Word> getController() {
		return listWordsController;
	}

	@Override
	public void createElements() {
		listPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, titleLabel)
				.nextRow(FillType.BOTH, parentScrollPane));
		mainPanel.addRow(SimpleRowBuilder.createRow(FillType.BOTH, listPanel.getPanel()));
		setNavigationButtons(enableWordAdding?
				new AbstractButton[] {createButtonAddWord(), createButtonFindWord()}:
				new AbstractButton[] {createButtonFindWord()});
	}

	private void createDefaultScrollPane() {
		Border raisedBevel = BorderFactory.createMatteBorder(3, 3, 0, 0, BasicColors.LIGHT_BLUE);
		parentScrollPane = GuiMaker.createScrollPane(new ScrollPaneOptions()
				.componentToWrap(rowsPanel.getPanel()).backgroundColor(BasicColors.VERY_BLUE)
				.border(raisedBevel).preferredSize(scrollPanesSize));

	}

	private AbstractButton createButtonAddWord (){
		String name = ButtonsNames.ADD;
		String hotkeyDescription = HotkeysDescriptions.ADD_WORD;
		int keyEvent = KeyEvent.VK_I;
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.showInsertWordDialog();
			}
		};
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action, name,
				hotkeyDescription);

	}

	private AbstractButton createButtonFindWord () {
		String name = ButtonsNames.SEARCH;
		//TODO differentiate between my list - kanji vs repeating list vs problematic kanjis?
		String hotkeyDescription = HotkeysDescriptions.OPEN_SEARCH_WORD_DIALOG;
		int keyEvent = KeyEvent.VK_F;
		AbstractAction action = new AbstractAction() {
			@Override public void actionPerformed(ActionEvent e) {
				applicationController.showSearchWordDialog();
			}
		};
		return createButtonWithHotkey(KeyModifiers.CONTROL, keyEvent, action, name,
				hotkeyDescription);
	}

	public void highlightRowAndScroll(int rowNumber, boolean clearLastHighlightedWord) {
		if (highlightedRowNumber >= 0 && clearLastHighlightedWord) {
			rowsPanel.clearPanelColor(highlightedRowNumber);
		}
		changePanelColor(rowNumber, Color.red);
		highlightedRowNumber = rowNumber;
		scrollTo(rowsPanel.getRows().get(rowNumber));
		this.rowsPanel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JComponent panel) {
		int r = panel.getY();
		this.parentScrollPane.getViewport().setViewPosition(new Point(0, r));
	}

	public int getHighlightedRowNumber() {
		return highlightedRowNumber;
	}

	public void scrollToBottom() {
		parentPanel.revalidate();

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
	}

}
