package com.kanji.myList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;

public abstract class RowsCreator<Row> {

	protected ListWordsController<Row> kanjiWords;
	protected MyList<Row> list;
	protected MainPanel panel;
	private int highlightedRowNumber;
	private JScrollPane parentScrollPane;
	private final Dimension scrollPanesSize = new Dimension(350, 300);

	public RowsCreator() {
		kanjiWords = new ListWordsController<>();
		highlightedRowNumber = -1;
		panel = new MainPanel(BasicColors.VERY_BLUE, true);
		createDefaultScrollPane();
	}

	public abstract JPanel createRow(Row row);

	public ListWordsController<Row> getController() {
		return kanjiWords;
	}

	public void setList(MyList<Row> list) {
		this.list = list;
	}

	public JPanel getPanel() {
		return panel.getPanel();
	}

	private void createDefaultScrollPane() {
		this.parentScrollPane = new JScrollPane();
		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE, 6);
		parentScrollPane = GuiMaker.createScrollPane(BasicColors.OCEAN_BLUE, raisedBevel,
				panel.getPanel(), scrollPanesSize);
	}

	public void highlightRowAndScroll(int rowNumber) {
		// TODO mess with indexes
		if (highlightedRowNumber >= 0) {
			changePanelColor(highlightedRowNumber, null);
		}
		changePanelColor(rowNumber, Color.red);
		highlightedRowNumber = rowNumber;
		scrollTo(panel.getRows().get(rowNumber));
		// scrollTo(panel);
		this.panel.getPanel().repaint();
	}

	private void changePanelColor(int rowNumber, Color color) {
		panel.setPanelColor(rowNumber + 1, color);
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
				panel.getPanel().revalidate();
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
