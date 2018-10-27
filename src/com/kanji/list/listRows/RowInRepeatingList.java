package com.kanji.list.listRows;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.FillType;
import com.guimaker.enums.InputGoal;
import com.guimaker.list.ListRowData;
import com.guimaker.list.myList.ListRowCreator;
import com.guimaker.list.myList.ListRowDataCreator;
import com.guimaker.listeners.InputValidationListener;
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

	public static final String DATE_FORMAT = "dd MMMM yyyy / HH:mm";
	private final Color labelsColor = Color.WHITE;

	@Override
	public ListRowData createListRow(RepeatingData repeatingData,
			CommonListElements commonListElements, InputGoal inputGoal) {
		String repeatingRange = repeatingData.getRepeatingRange();
		String timeSpent = repeatingData.getTimeSpentOnRepeating();
		LocalDateTime date = repeatingData.getRepeatingDate();

		JLabel rangeOfRepeatedWordsLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(
						Prompts.REPEATING_WORDS_RANGE + repeatingRange)
									  .foregroundColor(labelsColor));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		JLabel dateLabel = GuiElementsCreator.createLabel(
				new ComponentOptions().text(
						Prompts.REPEATING_DATE + formatter.format(date))
									  .foregroundColor(BasicColors.BLUE_NORMAL_2));

		JLabel timeSpentLabel = null;

		if (timeSpent != null) {
			timeSpentLabel = GuiElementsCreator.createLabel(
					new ComponentOptions().text(
							String.format(Prompts.REPEATING_TIME, timeSpent))
										  .foregroundColor(labelsColor));
		}

		AbstractButton deleteButton = commonListElements.getButtonDelete();

		MainPanel panel = new MainPanel();
		panel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				commonListElements.getRowNumberLabel(), dateLabel)
									  .nextRow(rangeOfRepeatedWordsLabel)
									  .nextRow(timeSpentLabel)
									  .nextRow(FillType.NONE, deleteButton));

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
