package com.kanji.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRowBuilder;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.Titles;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.myList.MyList;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicKanjisController controller;
	private Dimension preferredSize = new Dimension(600, 600);
	private int maximumNumberOfRows = 5;

	public ProblematicKanjiPanel(Font kanjiFont, ApplicationWindow applicationWindow,
			MyList<KanjiInformation> kanjiList, Set<Integer> problematicKanji) {
		parentDialog = applicationWindow;
		controller = new ProblematicKanjisController(applicationWindow, kanjiFont, this, problematicKanji, kanjiList);
	}

	public ProblematicKanjisController getController() {
		return controller;
	}

	@Override
	public void setParentDialog(DialogWindow dialog) {
		super.setParentDialog(dialog);
		configureParentDialog();
	}

	@Override
	void createElements() {
		AbstractButton withInternet = createRadioButtonForLearningWithInternet();
		AbstractButton withoutInternet = createRadioButtonForLearningWithoutInternet();
		ButtonGroup group = new ButtonGroup();
		group.add(withInternet);
		group.add(withoutInternet);
		AbstractButton buttonClose = createButtonClose();

		MainPanel radioButtonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		radioButtonsPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL,
				new JLabel(Titles.OPTIONS_FOR_SHOWING_PROBLEMATIC_KANJIS)).nextRow(withInternet,
						withoutInternet));

		mainPanel.addRows(SimpleRowBuilder.createRow(FillType.HORIZONTAL, radioButtonsPanel.getPanel())
				.nextRow(FillType.BOTH, controller.getKanjiRepeatingList().getPanel()).setNotOpaque());
		setNavigationButtons(Anchor.CENTER, buttonClose);
	}

	private AbstractButton createRadioButtonForLearningWithInternet() {
		AbstractButton withInternet = GuiMaker.createButtonlikeComponent(ComponentType.RADIOBUTTON,
				Labels.REPEATING_WITH_INTERNET, controller.createActionForShowingKanjiUsingInternet(true),
				KeyEvent.VK_I);
		withInternet.setFocusable(false);
		withInternet.setSelected(true);
		addHotkeysInformation(KeyEvent.VK_I,
                HotkeysDescriptions.SHOW_KANJI_WITH_INTERNET);
		return withInternet;
	}

	private AbstractButton createRadioButtonForLearningWithoutInternet() {
		AbstractButton withoutInternet = GuiMaker.createButtonlikeComponent(
				ComponentType.RADIOBUTTON, Labels.REPEATING_WITHOUT_INTERNET,
				controller.createActionForShowingKanjiUsingInternet(false), KeyEvent.VK_N);
		withoutInternet.setFocusable(false);
		addHotkeysInformation(KeyEvent.VK_N,
                HotkeysDescriptions.SHOW_KANJI_WITHOUT_INTERNET);
		return withoutInternet;
	}

	private void configureParentDialog() {

		addHotkey(KeyEvent.VK_SPACE, controller.createActionShowNextKanjiOrCloseDialog(),
				((JDialog) parentDialog.getContainer()).getRootPane(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);

		controller.limitSizeIfTooManyRows(maximumNumberOfRows);
		parentDialog.getContainer().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				controller.addButtonForShowingProblematicKanjis(parentDialog);
			}
		});
	}

	public boolean allProblematicKanjisRepeated() {
		return controller.allProblematicKanjisRepeated();
	}

	public void showKanjiDialog(KanjiPanel kanjiPanel) {
		parentDialog.showKanjiDialog(kanjiPanel);
	}

	public void showMessage(String message) {
		parentDialog.showMessageDialog(message);
	}

	public void limitSize() {
		parentDialog.getContainer().setPreferredSize(preferredSize);
	}

	public void highlightRow(int rowNumber) {
		controller.getKanjiRepeatingList().highlightRow(rowNumber);
	}

	@Override
	public DialogWindow getDialog() {
		return parentDialog;
	}

	public void addProblematicKanjis (Set<Integer> problematicKanjis){
		controller.addProblematicKanjis(problematicKanjis);
	}

}
