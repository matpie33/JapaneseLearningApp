package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.RepeatingData;
import com.kanji.list.myList.ListRowData;
import com.kanji.list.myList.ListRowMaker;
import com.kanji.utilities.CommonListElements;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RowInRepeatingList implements ListRowMaker<RepeatingData> {

	private final Color labelsColor = Color.WHITE;

	@Override
	public MainPanel createListRow(RepeatingData repeatingData,
			CommonListElements commonListElements, boolean forSearchPanel) {
		String word = repeatingData.getRepeatingRange();
		String time = repeatingData.getTimeSpentOnRepeating();
		LocalDateTime date1 = repeatingData.getRepeatingDate();

		JLabel repeatedWords = GuiMaker.createLabel(new ComponentOptions()
				.text(Prompts.REPEATING_WORDS_RANGE + word)
				.foregroundColor(labelsColor));

		DateTimeFormatter sdf = DateTimeFormatter
				.ofPattern("dd MMMM yyyy / HH:mm");
		JLabel date = GuiMaker.createLabel(new ComponentOptions()
				.text(Prompts.REPEATING_DATE + sdf.format(date1))
				.foregroundColor(labelsColor));

		date.setForeground(BasicColors.OCEAN_BLUE);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = GuiMaker.createLabel(
					new ComponentOptions().text(Prompts.REPEATING_TIME + time)
							.foregroundColor(labelsColor));
		}

		AbstractButton delete = commonListElements.getButtonDelete();

		MainPanel panel = new MainPanel(null);
		panel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				commonListElements.getRowNumberLabel(), date)
				.nextRow(repeatedWords).nextRow(timeSpent)
				.nextRow(FillType.NONE, delete));


		if (forSearchPanel){
			//TODO implement it for searching repeating list to work
		}

		return panel;

	}

	@Override
	public ListRowData getRowData() {
		return null;
	}
}
