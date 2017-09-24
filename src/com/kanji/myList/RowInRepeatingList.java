package com.kanji.myList;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;
import com.kanji.utilities.CommonListElements;

public class RowInRepeatingList implements ListRowMaker<RepeatingInformation> {

	private final Color labelsColor = Color.WHITE;

	@Override
	public MainPanel createListRow(RepeatingInformation rep,
			CommonListElements commonListElements) {
		String word = rep.getRepeatingRange();
		String time = rep.getTimeSpentOnRepeating();
		LocalDateTime date1 = rep.getRepeatingDate();

		JLabel repeatedWords = GuiMaker.createLabel(Prompts.REPEATING_WORDS_RANGE + word,
				labelsColor);

		DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd MMMM yyyy / HH:mm");
		JLabel date = GuiMaker.createLabel(Prompts.REPEATING_DATE + sdf.format(date1), labelsColor);
		date.setForeground(BasicColors.OCEAN_BLUE);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = GuiMaker.createLabel(Prompts.REPEATING_TIME + time, labelsColor);
		}

		JButton delete = commonListElements.getButtonDelete();

		MainPanel panel = new MainPanel(null);
		panel.addRows(
				new SimpleRow(FillType.HORIZONTAL, commonListElements.getRowNumberLabel(), date)
						.nextRow(repeatedWords).nextRow(timeSpent).nextRow(FillType.NONE, delete));
		return panel;

	}

}
