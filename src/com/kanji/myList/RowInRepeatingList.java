package com.kanji.myList;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;

public class RowInRepeatingList implements ListRow<RepeatingInformation> {

	private MyList<RepeatingInformation> list;

	@Override
	public MainPanel listRow(RepeatingInformation rep, JLabel rowNumberLabel) {
		String word = rep.getRepeatingRange();
		String time = rep.getTimeSpentOnRepeating();
		LocalDateTime date1 = rep.getRepeatingDate();

		JLabel repeatedWords = createLabel(Prompts.REPEATING_WORDS_RANGE + word);

		DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd MMMM yyyy / HH:mm");
		JLabel date = createLabel(Prompts.REPEATING_DATE + sdf.format(date1));
		date.setForeground(BasicColors.OCEAN_BLUE);
		rowNumberLabel.setForeground(BasicColors.OCEAN_BLUE);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = createLabel(Prompts.REPEATING_TIME + time);
		}

		JButton delete = list.createButtonRemove(rep);

		MainPanel panel = new MainPanel(null);
		panel.addRows(new SimpleRow(FillType.HORIZONTAL, rowNumberLabel, date)
				.nextRow(repeatedWords).nextRow(timeSpent).nextRow(FillType.NONE, delete));
		// addActionListener(delete, wrappingPanel, rep);
		return panel;

	}

	private JLabel createLabel(String word) {
		JLabel l1 = new JLabel(word);
		l1.setForeground(Color.WHITE);
		return l1;
	}

	public void setList(MyList<RepeatingInformation> list) {
		this.list = list;
	}

	// private void removeRow(JPanel row) {
	// rowsPanel.removeRow(row);
	// }

}
