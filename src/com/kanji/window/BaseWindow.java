package com.kanji.window;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.dialogs.RepeatingWordsPanel;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;
import com.kanji.range.SetOfRanges;

@SuppressWarnings("serial")
public class BaseWindow extends ClassWithDialog {

	private Insets insets = new Insets(20, 20, 20, 20);
	private ElementMaker maker;
	private JScrollPane listScrollWords;
	private JScrollPane listScrollRepeated;
	private final Dimension scrollPanesSize = new Dimension(300, 300);
	private final Dimension minimumListSize = new Dimension(200, 100);
	private JPanel mainPanel;
	private RepeatingWordsPanel repeatingWordsPanel;
	private boolean isExcelReaderLoaded;
	private Set<Integer> problematicKanjis;
	private JSplitPane listsSplitPane;
	private GridBagLayout g;
	private JPanel infoPanel;
	private JPanel buttonsPanel;
	private JButton showProblematicKanjis;

	private JLabel saveInfo;

	public ExcelReader excel;

	public static final String LIST_PANEL = "Panel with lists and buttons";
	public static final String LEARNING_PANEL = "Panel for repeating words";

	public BaseWindow() {

		problematicKanjis = new HashSet<Integer>();
		isExcelReaderLoaded = false;
		maker = new ElementMaker(this);
		mainPanel = new JPanel(new CardLayout());
		setContentPane(mainPanel);

		createUpperPanel();
		createInformationsPanel();
		createButtonsPanel(maker.getButtons());

		JPanel listsPanel = putPanelsTogetherAndSetContentPane();
		mainPanel.add(listsPanel, LIST_PANEL);

		repeatingWordsPanel = new RepeatingWordsPanel(this);
		mainPanel.add(repeatingWordsPanel, LEARNING_PANEL);
		setJMenuBar(maker.getMenu());

		setWindowProperties();

	}

	private void createUpperPanel() {
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();
		listScrollWords = createScrollPaneForList(wordsList);
		listScrollRepeated = createScrollPaneForList(repeatsList);
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollWords,
				listScrollRepeated);
		repaint();
	}

	private void createInformationsPanel() {
		infoPanel = new JPanel();
		infoPanel.setBackground(Color.YELLOW);
		saveInfo = new JLabel();
		changeSaveStatus(SavingStatus.NO_CHANGES);
		infoPanel.add(saveInfo);
	}

	private JScrollPane createScrollPaneForList(MyList list) {

		Border raisedBevel = BorderFactory.createLineBorder(Color.BLUE, 6);

		JScrollPane listScrollWords = createScrollPane(Color.GREEN, raisedBevel, list);
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

		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new GridBagLayout());
		buttonsPanel.setBackground(Color.RED);

		GridBagConstraints c = new GridBagConstraints();

		c.insets = insets;
		c.gridx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;

		for (int i = 0; i < list.size(); i++) {
			if (indexIsHigherThanHalfOfSize(i, list.size()))
				c.anchor = GridBagConstraints.EAST;
			buttonsPanel.add(list.get(i), c);
			c.gridx++;
		}

	}

	private boolean indexIsHigherThanHalfOfSize(int i, int size) {
		return i > (size - 1) / 2;
	}

	private JPanel putPanelsTogetherAndSetContentPane() {

		JPanel main = new JPanel();
		main.setLayout(new GridBagLayout());
		main.setBackground(Color.RED);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		main.add(listsSplitPane, c);

		c.weighty = 0;
		c.gridy++;
		main.add(buttonsPanel, c);

		c.gridy++;
		main.add(infoPanel, c);

		return main;

	}

	private void setWindowProperties() {
		pack();
		setMinimumSize(getSize());
		setTitle(Titles.appTitle);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

	public void loadExcelReader() {

		excel = new ExcelReader();
		excel.load();
		isExcelReaderLoaded = true;
		repeatingWordsPanel.setExcelReader(excel);

	}

	public boolean isExcelLoaded() {
		return isExcelReaderLoaded;
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
		repaint();
	}

	public void save() {
		this.maker.save();
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.savingStatusPrompt + savingStatus.getStatus() + "; "
				+ Prompts.problematicKanjiPrompt + problematicKanjis.size());
		repaint();
	}

	public void updateTitle(String update) {
		setTitle(Titles.appTitle + "   " + update);
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
		if (showProblematicKanjis == null) {
			showProblematicKanjis = new JButton("Pokaż problematyczne");
		}
		showProblematicKanjis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showProblematicKanjiDialog(null, null);
			}
		});
		for (Component c : infoPanel.getComponents()) {
			if (c == showProblematicKanjis) {
				return;
			}
		}

		System.out.println("not found");
		infoPanel.add(showProblematicKanjis);
		infoPanel.revalidate();
		infoPanel.repaint();
	}

	public void removeButtonProblematicsKanji() {
		infoPanel.remove(showProblematicKanjis);
		System.out.println("after: " + infoPanel.getComponentCount());
		infoPanel.revalidate();
		infoPanel.repaint();
	}

}
