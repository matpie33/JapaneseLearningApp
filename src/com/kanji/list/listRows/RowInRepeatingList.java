package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.listeners.InputValidationListener;
import com.guimaker.model.PanelConfiguration;
import com.guimaker.options.ComponentOptions;
import com.guimaker.panels.GuiElementsCreator;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.guimaker.utilities.CommonListElements;
import com.kanji.constants.strings.Prompts;
import com.kanji.list.listElements.Kanji;
import com.kanji.list.listElements.RepeatingData;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RowInRepeatingList implements ListRowCreator<RepeatingData> {

	private final Color labelsColor = Color.WHITE;

	@Override
	public ListRowData createListRow(RepeatingData repeatingData,
			CommonListElements commonListElements, InputGoal inputGoal) {
		String word = repeatingData.getRepeatingRange();
		String time = repeatingData.getTimeSpentOnRepeating();
		LocalDateTime date1 = repeatingData.getRepeatingDate();

		JLabel repeatedWords = GuiElementsCreator.createLabel(
				new ComponentOptions()
						.text(Prompts.REPEATING_WORDS_RANGE + word)
						.foregroundColor(labelsColor));

		DateTimeFormatter sdf = DateTimeFormatter
				.ofPattern("dd MMMM yyyy / HH:mm");
		JLabel date = GuiElementsCreator.createLabel(new ComponentOptions()
				.text(Prompts.REPEATING_DATE + sdf.format(date1))
				.foregroundColor(labelsColor));

		date.setForeground(BasicColors.BLUE_NORMAL_2);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = GuiElementsCreator.createLabel(new ComponentOptions()
					.text(String.format(Prompts.REPEATING_TIME, time))
					.foregroundColor(labelsColor));
		}

		AbstractButton delete = commonListElements.getButtonDelete();

		MainPanel panel = new MainPanel();
		panel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				commonListElements.getRowNumberLabel(), date)
				.nextRow(repeatedWords).nextRow(timeSpent)
				.nextRow(FillType.NONE, delete));

		if (!inputGoal.equals(InputGoal.EDIT)) {
			//TODO implement it for searching repeating list to work
		}

		ListRowDataCreator<Kanji> rowDataCreator = new ListRowDataCreator<>(
				panel);
		return rowDataCreator.getListRowData();

	}

	@Override
	public void addValidationListener(
			InputValidationListener<RepeatingData> inputValidationListener) {

	}
}
