package com.kanji.myList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.options.ScrollPaneOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.model.ListRow;
import com.kanji.utilities.CommonListElements;

public class ListPanelMaker<Word> {

	private ListWordsController<Word> listWordsController;
	private MainPanel wrappingPanel;
	private MainPanel rowsPanel;
	private int highlightedRowNumber;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(350, 300);
	private JLabel titleLabel;
	private ListRowMaker<Word> listRow;

	public ListPanelMaker(ListRowMaker<Word> listRow, ListWordsController<Word> controller) {
		listWordsController = controller;
		highlightedRowNumber = -1;
		wrappingPanel = new MainPanel(BasicColors.NAVY_BLUE, true);
		rowsPanel = new MainPanel(null, true, false);
		titleLabel = new JLabel();
		titleLabel.setForeground(Color.WHITE);
		createDefaultScrollPane();
		wrappingPanel.addRows(SimpleRowBuilder.createRow(FillType.NONE, Anchor.CENTER, titleLabel)
				.nextRow(FillType.BOTH, parentScrollPane));
		this.listRow = listRow;
	}

	public ListRow<Word> addRow(Word word) {
		JLabel rowNumberLabel = new JLabel(createTextForRowNumber(rowsPanel.getNumberOfRows() + 1));
		JButton remove = new JButton("-");
		remove.addActionListener(listWordsController.createDeleteRowAction(word));
		CommonListElements commonListElements = new CommonListElements(remove, rowNumberLabel);
		rowNumberLabel.setForeground(BasicColors.OCEAN_BLUE);
		JComponent wrappingPanel = this.rowsPanel.addRow(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				Anchor.NORTH, listRow.createListRow(word, commonListElements).getPanel()));
		wrappingPanel
				.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BasicColors.LIGHT_BLUE));
		return new ListRow<Word>(word, wrappingPanel, rowNumberLabel);
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

	public JPanel getPanel() {
		return wrappingPanel.getPanel();
	}

	private void createDefaultScrollPane() {
		Border raisedBevel = BorderFactory.createMatteBorder(3, 3, 0, 0, BasicColors.LIGHT_BLUE);
		parentScrollPane = GuiMaker.createScrollPane(new ScrollPaneOptions()
				.componentToWrap(rowsPanel.getPanel()).backgroundColor(BasicColors.VERY_BLUE)
				.border(raisedBevel).preferredSize(scrollPanesSize));

	}

	public void highlightRowAndScroll(int rowNumber, boolean clearLastHighlightedWord) {
		if (highlightedRowNumber >= 0 && clearLastHighlightedWord) {
			rowsPanel.clearPanelColor(highlightedRowNumber);
		}
		changePanelColor(rowNumber, Color.red);
		highlightedRowNumber = rowNumber;
		scrollTo(rowsPanel.getRows().get(rowNumber));
		this.wrappingPanel.getPanel().repaint();
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
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// TODO swing utilities
				wrappingPanel.getPanel().revalidate();
				parentScrollPane.revalidate();
				JScrollBar scrollBar = parentScrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});

	}

	public JScrollPane returnMe(JScrollPane scrollPane) {
		this.parentScrollPane = scrollPane;
		return this.parentScrollPane;
	}

	public JScrollPane getScrollPane() {
		return parentScrollPane;
	}

	public void setScrollPane(JScrollPane scr) {
		this.parentScrollPane = scr;
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
