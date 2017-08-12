package com.kanji.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.GuiMaker;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.fileReading.KanjiCharactersReader;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private JScrollPane scrollPane;
	private MainPanel panelInScrollPane;
	private ProblematicKanjisController controller;
	private Dimension preferredSize = new Dimension(600, 600);
	private int maximumNumberOfRows = 5;

	// TODO donot give me this set of problematic kanjis, directly give it
	// controller
	public ProblematicKanjiPanel(KanjiWords kanjis, Set<Integer> problematicKanji) {
		super(true);
		controller = new ProblematicKanjisController(this, problematicKanji, kanjis);
		panelInScrollPane = new MainPanel(BasicColors.LIGHT_BLUE, true);

	}

	public KanjiCharactersReader getKanjisReader() {
		return controller.getKanjisReader();
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
		createPanelWithProblematicKanjis();
		scrollPane = new JScrollPane(panelInScrollPane.getPanel());

		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER,
				new JLabel(Titles.currentProblematicWords)));
		MainPanel radioButtonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		radioButtonsPanel.addRow(RowMaker.createHorizontallyFilledRow(
				new JLabel(Titles.optionsForShowingProblematicKanjis)));
		radioButtonsPanel
				.addRow(RowMaker.createHorizontallyFilledRow(withInternet, withoutInternet));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(radioButtonsPanel.getPanel()));

		mainPanel.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, button));
	}

	private void createPanelWithProblematicKanjis() {

		panelInScrollPane.setGapsBetweenRowsTo(5);
		controller.buildRowsForProblematicKanjis();
	}

	private JRadioButton createRadioButtonForLearningWithInternet() {
		JRadioButton withInternet = GuiElementsMaker
				.createRadioButton(Labels.repeatingWithInternet);
		withInternet.setFocusable(false);
		withInternet.setSelected(true);
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setUseInternet(true);
				withInternet.setSelected(true);
			}
		};
		withInternet.addActionListener(action);
		addHotkey(KeyEvent.VK_I, action, withInternet,
				HotkeysDescriptions.SHOW_KANJI_WITH_INTERNET);
		return withInternet;
	}

	private JRadioButton createRadioButtonForLearningWithoutInternet() {
		JRadioButton withoutInternet = GuiElementsMaker
				.createRadioButton(Labels.repeatingWithoutInternet);
		withoutInternet.setFocusable(false);
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				withoutInternet.setSelected(true);
				controller.setUseInternet(false);
			}
		};
		addHotkey(KeyEvent.VK_N, action, withoutInternet,
				HotkeysDescriptions.SHOW_KANJI_WITHOUT_INTERNET);
		withoutInternet.addActionListener(action);
		return withoutInternet;
	}

	private JButton createButtonGoToSource(MainPanel panel, int kanjiId) {
		// TODO use 1 parameter that can uniquely identify the row
		JButton button = new JButton(ButtonsNames.buttonGoToSource);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.goToSpecifiedResource(panel, kanjiId);
			}
		});
		button.setFocusable(false);
		return button;
	}

	private void configureParentDialog() {
		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showNextKanji();
			}
		};

		addHotkey(KeyEvent.VK_SPACE, a, ((JDialog) parentDialog.getContainer()).getRootPane(),
				HotkeysDescriptions.SHOW_NEXT_KANJI);

		controller.checkForTooManyRowsToDisplaAll(maximumNumberOfRows);
		parentDialog.getContainer().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				controller.hideProblematicsPanel(parentDialog);
			}
		});
	}

	public void showNextKanjiOrClose() {
		if (controller.hasMoreKanji())
			controller.goToNextResource();
		else {
			parentDialog.closeChild();
			parentDialog.showMsgDialog(Prompts.noMoreKanjis);
		}
	}

	public void highlightRow(JPanel panelWithKanji) {
		scrollPane.getVerticalScrollBar()
				.setValue((int) Math.floor(panelWithKanji.getParent().getBounds().getY()));
		panelWithKanji.setBackground(BasicColors.OCEAN_BLUE);
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

	public void showMsg(String message) {
		parentDialog.showMsgDialog(message);
	}

	public void buildRow(String kanji, int kanjiId) {
		final MainPanel panel = new MainPanel(BasicColors.DARK_BLUE);
		final JLabel id = new JLabel("" + kanjiId);
		JTextArea kanjiTextArea = GuiMaker.createTextArea(false, false);
		kanjiTextArea.setText(kanji);
		controller.addKanjiRow(panel, kanjiId);
		JButton buttonGoToSource = createButtonGoToSource(panel, kanjiId);
		panel.addRow(RowMaker.createHorizontallyFilledRow(kanjiTextArea, id, buttonGoToSource)
				.fillHorizontallySomeElements(kanjiTextArea));
		panelInScrollPane.addRow(RowMaker.createHorizontallyFilledRow(panel.getPanel()));
	}

	public void limitSize() {
		parentDialog.getContainer().setPreferredSize(preferredSize);
	}

}
