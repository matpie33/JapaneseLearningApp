package com.kanji.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.Titles;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiRepeatingList;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicKanjisController controller;
	private Dimension preferredSize = new Dimension(600, 600);
	private int maximumNumberOfRows = 5;
	private MyList<KanjiInformation> kanjiRepeatingList;
	private RowInKanjiRepeatingList rowInKanjiRepeatingList;

	public ProblematicKanjiPanel(MyList<KanjiInformation> kanjiList,
			Set<Integer> problematicKanji) {
		super(true);
		controller = new ProblematicKanjisController(this, problematicKanji, kanjiList);

		rowInKanjiRepeatingList = new RowInKanjiRepeatingList(controller);
		kanjiRepeatingList = new MyList<KanjiInformation>(parentDialog, null,
				rowInKanjiRepeatingList, "Kanji do powtorki");
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
		JRadioButton withInternet = createRadioButtonForLearningWithInternet();
		JRadioButton withoutInternet = createRadioButtonForLearningWithoutInternet();
		ButtonGroup group = new ButtonGroup();
		group.add(withInternet);
		group.add(withoutInternet);
		JButton button = GuiElementsMaker.createButton(ButtonsNames.buttonApproveText,
				CommonActionsMaker.createDisposeAction(parentDialog));
		buildRows();

		MainPanel radioButtonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		radioButtonsPanel.addRow(RowMaker.createHorizontallyFilledRow(
				new JLabel(Titles.optionsForShowingProblematicKanjis)));
		radioButtonsPanel
				.addRow(RowMaker.createHorizontallyFilledRow(withInternet, withoutInternet));

		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER,
				new JLabel(Titles.currentProblematicWords)));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(radioButtonsPanel.getPanel()));
		mainPanel.addRow(RowMaker.createBothSidesFilledRow(kanjiRepeatingList.getPanel()));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, button));
	}

	private JRadioButton createRadioButtonForLearningWithInternet() {
		JRadioButton withInternet = GuiElementsMaker
				.createRadioButton(Labels.repeatingWithInternet);
		withInternet.setFocusable(false);
		withInternet.setSelected(true);
		AbstractAction useInternetAction = createActionListenerForUsingInternet(withInternet, true);
		withInternet.addActionListener(useInternetAction);
		addHotkey(KeyEvent.VK_I, useInternetAction, withInternet,
				HotkeysDescriptions.SHOW_KANJI_WITH_INTERNET);
		return withInternet;
	}

	private AbstractAction createActionListenerForUsingInternet(JRadioButton radioButton,
			boolean useInternet) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUseInternet(useInternet);
				radioButton.setSelected(true);
			}
		};
		return action;
	}

	private JRadioButton createRadioButtonForLearningWithoutInternet() {
		JRadioButton withoutInternet = GuiElementsMaker
				.createRadioButton(Labels.repeatingWithoutInternet);
		withoutInternet.setFocusable(false);
		AbstractAction dontUseInternetAction = createActionListenerForUsingInternet(withoutInternet,
				false);
		addHotkey(KeyEvent.VK_N, dontUseInternetAction, withoutInternet,
				HotkeysDescriptions.SHOW_KANJI_WITHOUT_INTERNET);
		withoutInternet.addActionListener(dontUseInternetAction);
		return withoutInternet;
	}

	private void configureParentDialog() {
		AbstractAction goToNextResource = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.goToNextResource();
			}
		};

		addHotkey(KeyEvent.VK_SPACE, goToNextResource,
				((JDialog) parentDialog.getContainer()).getRootPane(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);

		controller.limitSizeIfTooManyRows(maximumNumberOfRows);
		parentDialog.getContainer().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				controller.hideProblematicsPanel(parentDialog);
			}
		});
	}

	public boolean allProblematicKanjisRepeated() {
		return controller.allProblematicKanjisRepeated();
	}

	public void clear() {
		mainPanel.clear();
	}

	public void showKanjiOffline(String kanji) {
		parentDialog.showKanjiDialog(kanji, this);
	}

	public void showMessage(String message) {
		parentDialog.showMessageDialog(message);
	}

	public void buildRows() {
		for (KanjiInformation kanji : controller.getKanjis()) {
			kanjiRepeatingList.addWord(kanji);
		}
	}

	public void limitSize() {
		parentDialog.getContainer().setPreferredSize(preferredSize);
	}

	public void highlightRow(int rowNumber) {
		rowInKanjiRepeatingList.highlightRowAndScroll(rowNumber, false);
	}

}
