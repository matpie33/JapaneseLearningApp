package com.kanji.window;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.Border;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.dialogs.DialogWindow;
import com.kanji.dialogs.InsertWordPanel;
import com.kanji.dialogs.LearningStartPanel;
import com.kanji.dialogs.LoadingPanel;
import com.kanji.dialogs.ProblematicKanjiPanel;
import com.kanji.dialogs.RepeatingWordsPanel;
import com.kanji.dialogs.SearchWordPanel;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.SetOfRanges;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private Insets insets = new Insets(20, 20, 20, 20);
	private ElementMaker maker;
	private JScrollPane listScrollWords;
	private JScrollPane listScrollRepeated;
	private final Dimension scrollPanesSize = new Dimension(300, 300);
	private final Dimension minimumListSize = new Dimension(200, 100);
	private JPanel mainPanel;
	private RepeatingWordsPanel repeatingWordsPanel;
	private Set<Integer> problematicKanjis;
	private JSplitPane listsSplitPane;
	private GridBagLayout g;
	private MainPanel infoPanel;
	private MainPanel buttonsPanel;
	private JButton showProblematicKanjis;
	private MainPanel main;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private JFrame container;
	private int infoPanelComponentsRow = 0;
	// TODO handle the situation in gui maker when the panel has just 1 row so
	// we
	// don't have to use row (0) but somehow easier

	private JLabel saveInfo;

	public ExcelReader excel;

	public static final String LIST_PANEL = "Panel with lists and buttons";
	public static final String LEARNING_PANEL = "Panel for repeating words";

	public ApplicationWindow() {
		// TODO separate panel from this class

		super(null);

		container = new JFrame();
		main = new MainPanel(BasicColors.LIGHT_BLUE);
		// TODO searching is case sensitive, should not be
		problematicKanjis = new HashSet<Integer>();
		maker = new ElementMaker(this);
		mainPanel = new JPanel(new CardLayout());
		container.setContentPane(mainPanel);
		createUpperPanel();
		createInformationsPanel();
		createButtonsPanel(maker.getButtons());

		JPanel listsPanel = putPanelsTogetherAndSetContentPane();
		mainPanel.add(listsPanel, LIST_PANEL);

		repeatingWordsPanel = new RepeatingWordsPanel(this);
		mainPanel.add(repeatingWordsPanel.getPanel(), LEARNING_PANEL);
		container.setJMenuBar(maker.getMenu());

		setWindowProperties();

	}

	private void createUpperPanel() {
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();
		listScrollWords = createScrollPaneForList(wordsList);
		listScrollRepeated = createScrollPaneForList(repeatsList);
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollWords,
				listScrollRepeated);
		container.repaint();
	}

	private void createInformationsPanel() {
		infoPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		saveInfo = new JLabel();
		showProblematicKanjis = maker.getProblematicKanjiButton();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		infoPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.WEST, saveInfo));
	}

	private JScrollPane createScrollPaneForList(MyList list) {

		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE, 6);

		JScrollPane listScrollWords = createScrollPane(BasicColors.OCEAN_BLUE, raisedBevel, list);
		list.setScrollPane(listScrollWords);
		listScrollWords.setMinimumSize(minimumListSize);

		return listScrollWords;

	}

	private JScrollPane createScrollPane(Color bgColor, Border border, Component component) {

		JScrollPane scroll = new JScrollPane(component);
		scroll.getViewport().setBackground(bgColor);
		scroll.setBorder(border);
		scroll.getVerticalScrollBar().setUnitIncrement(20);
		scroll.setPreferredSize(scrollPanesSize);
		return scroll;

	}

	private void createButtonsPanel(List<JButton> list) {

		buttonsPanel = new MainPanel(BasicColors.VERY_LIGHT_BLUE);
		buttonsPanel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.WEST,
				list.toArray(new JButton[] {})));

	}

	private boolean indexIsHigherThanHalfOfSize(int i, int size) {
		return i > (size - 1) / 2;
	}

	private JPanel putPanelsTogetherAndSetContentPane() {
		MainPanel main = new MainPanel(BasicColors.LIGHT_BLUE);
		main.addRow(RowMaker.createBothSidesFilledRow(listsSplitPane));
		main.addRow(RowMaker.createHorizontallyFilledRow(buttonsPanel.getPanel()));
		main.addRow(RowMaker.createHorizontallyFilledRow(infoPanel.getPanel()));
		return main.getPanel();
	}

	private void setWindowProperties() {
		container.setContentPane(mainPanel);
		container.pack();
		container.setMinimumSize(container.getSize());
		container.setTitle(Titles.appTitle);
		container.setLocationRelativeTo(null);
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public void showCardPanel(String cardName) {
		((CardLayout) mainPanel.getLayout()).show(mainPanel, cardName);
	}

	public void setWordsRangeToRepeat(SetOfRanges ranges, boolean withProblematic) {

		repeatingWordsPanel.setRepeatingWords(maker.getWordsList());
		repeatingWordsPanel.setRangesToRepeat(ranges);
		repeatingWordsPanel.reset();
		System.out.println("setting: " + problematicKanjis);
		if (withProblematic)
			repeatingWordsPanel.setProblematicKanjis(problematicKanjis);

		repeatingWordsPanel.startRepeating();
	}

	public void setRepeatingInformation(RepeatingInformation info) {
		repeatingWordsPanel.setRepeatingInformation(info);
	}

	public void addProblematicKanjis(Set<Integer> problematicKanjiList) {
		this.problematicKanjis.addAll(problematicKanjiList);

		System.out.println(this.problematicKanjis);
	}

	public Set<Integer> getProblematicKanjis() {
		return problematicKanjis;
	}

	public void setProblematicKanjis(Set<Integer> problematicKanjis) {
		this.problematicKanjis.addAll(problematicKanjis);
	}

	public void updateLeft() {
		listsSplitPane.setLeftComponent(createScrollPaneForList(maker.getWordsList()));
		container.repaint();
	}

	public void save() {
		this.maker.save();
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.savingStatusPrompt + savingStatus.getStatus() + "; "
				+ Prompts.problematicKanjiPrompt + problematicKanjis.size());
		container.repaint();
	}

	public void updateTitle(String update) {
		container.setTitle(Titles.appTitle + "   " + update);
	}

	public void addToRepeatsList(RepeatingInformation info) {
		maker.getRepeatsList().getWords().add(info);
	}

	public Point getRightComponentOfSplitPanePosition() {
		return listsSplitPane.getRightComponent().getLocation();
	}

	public void scrollToBottom() {
		maker.getRepeatsList().scrollToBottom();
	}

	public void addButtonIcon() {
		if (problematicKanjiPanel.allProblematicKanjisRepeated()) {
			return;
		}
		infoPanel.addElementsToRow(infoPanelComponentsRow, showProblematicKanjis);
	}

	public void removeButtonProblematicsKanji() {
		if (infoPanel.rowContainsComponent(infoPanelComponentsRow, showProblematicKanjis)) {
			infoPanel.removeLastElementFromRow(infoPanelComponentsRow);
		}
	}

	public void showLearningStartDialog(MyList list, int maximumNumber) {
		showPanel(new LearningStartPanel(this, maximumNumber, list), Titles.learnStartDialogTitle,
				false, Position.CENTER);

	}

	// TODO dialogs should either be jframe or modal in order for alt tab to
	// switch focus to the right window
	public void showInsertDialog(MyList list) {
		showPanel(new InsertWordPanel(list), Titles.insertWordDialogTitle, false,
				Position.LEFT_CORNER);
	}

	public void showSearchWordDialog(MyList list) {
		showPanel(new SearchWordPanel(list), Titles.insertWordDialogTitle, false,
				Position.LEFT_CORNER);
	}

	public void showProblematicKanjiDialog(KanjiWords kanjiWords, Set<Integer> problematicKanjis) {
		problematicKanjiPanel = new ProblematicKanjiPanel(kanjiWords, problematicKanjis);
		showPanel(problematicKanjiPanel, Titles.insertWordDialogTitle, true, Position.CENTER);
	}

	public void showProblematicKanjiDialog() {
		childWindow.getContainer().setVisible(true);
	}

	public LoadingPanel showProgressDialog() { // TODO progress dialog doesn't
												// scroll anymore
		LoadingPanel dialog = new LoadingPanel(Prompts.kanjiLoadingPrompt);
		showPanel(dialog, Titles.messageDialogTitle, false, Position.CENTER);
		return dialog;
	}

	public void closeDialog() {
		childWindow.getContainer().dispose();
	}

	public void setVisible(boolean vis) {
		container.setVisible(vis);
	}

	public JFrame getContainer() {
		return container;
	}

}
