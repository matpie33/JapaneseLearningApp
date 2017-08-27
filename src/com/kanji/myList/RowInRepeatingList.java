package com.kanji.myList;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;

public class RowInRepeatingList extends RowsCreator<RepeatingInformation> {

	private Color defaultColor = Color.RED;
	private int rowsCounter;

	public RowInRepeatingList(MyList<RepeatingInformation> list) {
		this.list = list;
		rowsCounter = 1;
	}

	@Override
	public JPanel createRow(RepeatingInformation rep) {
		String word = rep.getRepeatingRange();
		String time = rep.getTimeSpentOnRepeating();
		Date date1 = rep.getRepeatingDate();

		String rowNumber = "" + rowsCounter++ + ".";
		JLabel repeatedWords = createLabel(Prompts.repeatingWordsRangePrompt + word);

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		JLabel date = createLabel(
				rowNumber + " " + Prompts.repeatingDatePrompt + sdf.format(date1) + ".");
		date.setForeground(BasicColors.OCEAN_BLUE);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = createLabel(Prompts.repeatingTimePrompt + time);
		}

		JButton delete = createButtonRemove();

		MainPanel panel = new MainPanel(null);
		panel.addRow(RowMaker.createHorizontallyFilledRow(date));
		// TODO add in main panel method for creating a series of rows in one
		// line
		panel.addRow(RowMaker.createHorizontallyFilledRow(repeatedWords));
		if (timeSpent != null) {
			panel.addRow(RowMaker.createHorizontallyFilledRow(timeSpent));
		}

		panel.addRow(RowMaker.createUnfilledRow(Anchor.WEST, delete));
		JPanel wrappingPanel = this.rowsPanel
				.addRow(RowMaker.createHorizontallyFilledRow(panel.getPanel()));
		wrappingPanel
				.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BasicColors.LIGHT_BLUE));
		addActionListener(delete, wrappingPanel, rep);
		return this.rowsPanel.getPanel();

	}

	private JLabel createLabel(String word) {
		JLabel l1 = new JLabel(word);
		l1.setForeground(Color.WHITE);
		return l1;
	}

	private JButton createButtonRemove() {
		JButton remove = new JButton("-");
		return remove;
	}

	private void addActionListener(JButton button, JPanel panel, final RepeatingInformation kanji) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!list.showMessage(String.format(Prompts.deleteElementPrompt,
						Prompts.repeatingElementPrompt))) {
					return;
				}

				removeRow(panel);
				list.getWords().remove(kanji);
				list.save();
			}
		});
	}

	@Override
	public void setList(MyList<RepeatingInformation> list) {
		this.list = list;
	}

	private void removeRow(JPanel row) {
		rowsPanel.removeRow(row);
	}

}
