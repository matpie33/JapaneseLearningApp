package com.kanji.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.guimaker.colors.BasicColors;
import com.guimaker.enums.Anchor;
import com.guimaker.enums.ComponentType;
import com.guimaker.enums.FillType;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.SimpleRow;
import com.guimaker.utilities.CommonActionsMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.Titles;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.myList.MyList;
import com.kanji.myList.RowInKanjiRepeatingList;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private ProblematicKanjisController controller;
	private Dimension preferredSize = new Dimension(600, 600);
	private int maximumNumberOfRows = 5;
	private MyList<KanjiInformation> kanjiRepeatingList;
	private RowInKanjiRepeatingList rowInKanjiRepeatingList;

	public ProblematicKanjiPanel(Font kanjiFont, ApplicationWindow applicationWindow,
			MyList<KanjiInformation> kanjiList, Set<Integer> problematicKanji) {
		super(true);
		parentDialog = applicationWindow;
		controller = new ProblematicKanjisController(kanjiFont, this, problematicKanji, kanjiList);

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
		AbstractButton withInternet = createRadioButtonForLearningWithInternet();
		AbstractButton withoutInternet = createRadioButtonForLearningWithoutInternet();
		ButtonGroup group = new ButtonGroup();
		group.add(withInternet);
		group.add(withoutInternet);
		AbstractButton button = GuiMaker.createButtonlikeComponent(ComponentType.BUTTON,
				ButtonsNames.APPROVE,
				CommonActionsMaker.createDisposeAction(parentDialog.getContainer()));
		buildRows();

		MainPanel radioButtonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		radioButtonsPanel.addRows(new SimpleRow(FillType.HORIZONTAL,
				new JLabel(Titles.OPTIONS_FOR_SHOWING_PROBLEMATIC_KANJIS)).nextRow(withInternet,
						withoutInternet));

		mainPanel.addRows(new SimpleRow(FillType.NONE, Anchor.CENTER,
				new JLabel(Titles.CURRENT_PROBLEMATIC_WORDS))
						.nextRow(FillType.HORIZONTAL, radioButtonsPanel.getPanel())
						.nextRow(FillType.BOTH, kanjiRepeatingList.getPanel()));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.CENTER, button));
	}

	private AbstractButton createRadioButtonForLearningWithInternet() {
		AbstractButton withInternet = GuiMaker.createButtonlikeComponent(ComponentType.RADIOBUTTON,
				Labels.REPEATING_WITH_INTERNET, createActionListenerForUsingInternet(true),
				KeyEvent.VK_I);
		withInternet.setFocusable(false);
		withInternet.setSelected(true);
		addHotkeysInformation(KeyEvent.VK_I, withInternet,
				HotkeysDescriptions.SHOW_KANJI_WITH_INTERNET);
		return withInternet;
	}

	private AbstractAction createActionListenerForUsingInternet(boolean useInternet) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUseInternet(useInternet);
				JRadioButton source = (JRadioButton) e.getSource();
				source.setSelected(true);
			}
		};
		return action;
	}

	private AbstractButton createRadioButtonForLearningWithoutInternet() {
		AbstractButton withoutInternet = GuiMaker.createButtonlikeComponent(
				ComponentType.RADIOBUTTON, Labels.REPEATING_WITHOUT_INTERNET,
				createActionListenerForUsingInternet(false), KeyEvent.VK_N);
		withoutInternet.setFocusable(false);
		addHotkeysInformation(KeyEvent.VK_N, withoutInternet,
				HotkeysDescriptions.SHOW_KANJI_WITHOUT_INTERNET);
		return withoutInternet;
	}

	private void configureParentDialog() {
		AbstractAction goToNextResource = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showNextKanjiOrCloseChildDialog();
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

	public void showKanjiDialog(KanjiPanel kanjiPanel) {
		parentDialog.showKanjiDialog(kanjiPanel);
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
		// rowInKanjiRepeatingList.highlightRowAndScroll(rowNumber, false);
	}

	@Override
	public DialogWindow getDialog() {
		return parentDialog;
	}

}
