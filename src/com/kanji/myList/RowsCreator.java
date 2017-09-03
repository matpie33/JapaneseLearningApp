package com.kanji.myList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;

public class RowsCreator<Row> {

	protected ListWordsController<Row> kanjiWords;
	protected MyList<Row> list;
	protected MainPanel wrappingPanel;
	protected MainPanel rowsPanel;
	private int highlightedRowNumber;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(350, 300);
	private JLabel titleLabel;
	private ListRow<Row> listRow;

	public RowsCreator(ListRow<Row> listRow) {
		kanjiWords = new ListWordsController<>();
		highlightedRowNumber = -1;
		wrappingPanel = new MainPanel(BasicColors.VERY_BLUE, true);
		rowsPanel = new MainPanel(null, true);
		titleLabel = new JLabel();
		titleLabel.setForeground(Color.WHITE);
		createDefaultScrollPane();
		wrappingPanel.addRows(new SimpleRow(FillType.NONE, Anchor.CENTER, titleLabel)
				.nextRow(FillType.BOTH, parentScrollPane));
		this.listRow = listRow;

	}

	public void addRow(Row row) {
		JPanel wrappingPanel = this.rowsPanel.addRow(
				new SimpleRow(FillType.HORIZONTAL, Anchor.NORTH, listRow.listRow(row).getPanel()));
		wrappingPanel
				.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BasicColors.LIGHT_BLUE));
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}

	public ListWordsController<Row> getController() {
		return kanjiWords;
	}

	public void setList(MyList<Row> list) {
		this.list = list;
	}

	public JPanel getPanel() {
		return wrappingPanel.getPanel();
	}

	private void createDefaultScrollPane() {
		this.parentScrollPane = new JScrollPane();
		Border raisedBevel = BorderFactory.createMatteBorder(3, 3, 0, 0, BasicColors.LIGHT_BLUE);
		parentScrollPane = GuiMaker.createScrollPane(BasicColors.VERY_BLUE, raisedBevel,
				rowsPanel.getPanel(), scrollPanesSize);
	}

	public void highlightRowAndScroll(int rowNumber, boolean clearLastHighlightedWord) {
		if (highlightedRowNumber >= 0 && clearLastHighlightedWord) {
			rowsPanel.clearPanelColor(highlightedRowNumber);
		}
		changePanelColor(rowNumber, Color.red);
		highlightedRowNumber = rowNumber;
		scrollTo(rowsPanel.getRows().get(rowNumber));
		// scrollTo(panel);
		this.wrappingPanel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		rowsPanel.setPanelColor(rowNumber, color);
	}

	public void scrollTo(JPanel panel) {
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

}
