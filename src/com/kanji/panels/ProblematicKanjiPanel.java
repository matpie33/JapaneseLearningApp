package com.kanji.panels;

import java.awt.Dimension;
import java.awt.Font;
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
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiInformation;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.actions.GuiElementsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;
import com.kanji.constants.Labels;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.controllers.ProblematicKanjisController;
import com.kanji.myList.MyList;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjiPanel extends AbstractPanelWithHotkeysInfo {

	private JScrollPane scrollPane;
	private MainPanel panelInScrollPane;
	private ProblematicKanjisController controller;
	private Dimension preferredSize = new Dimension(600, 600);
	private int maximumNumberOfRows = 5;

	public ProblematicKanjiPanel(MyList<KanjiInformation> kanjiSearcher,
			Set<Integer> problematicKanji) {
		super(true);
		controller = new ProblematicKanjisController(this, problematicKanji, kanjiSearcher);
		panelInScrollPane = new MainPanel(BasicColors.LIGHT_BLUE, true);
		panelInScrollPane.setGapsBetweenRowsTo(5);
	}

	public Font getKanjiFont() {
		return controller.getKanjisReader().getFont();
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
		controller.buildRowsForProblematicKanjis();
		scrollPane = new JScrollPane(panelInScrollPane.getPanel());

		MainPanel radioButtonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		radioButtonsPanel.addRow(RowMaker.createHorizontallyFilledRow(
				new JLabel(Titles.optionsForShowingProblematicKanjis)));
		radioButtonsPanel
				.addRow(RowMaker.createHorizontallyFilledRow(withInternet, withoutInternet));

		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER,
				new JLabel(Titles.currentProblematicWords)));
		mainPanel.addRow(RowMaker.createHorizontallyFilledRow(radioButtonsPanel.getPanel()));
		mainPanel.addRow(RowMaker.createBothSidesFilledRow(scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(RowMaker.createUnfilledRow(Anchor.CENTER, button));
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

		controller.limitSizeIfTooManyRows(maximumNumberOfRows);
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
			parentDialog.showMessageDialog(Prompts.noMoreKanjis);
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

	public void showMessage(String message) {
		parentDialog.showMessageDialog(message);
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
