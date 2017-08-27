package com.kanji.windows;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.guimaker.colors.BasicColors;
import com.kanji.Row.KanjiInformation;
import com.kanji.constants.ApplicationPanels;
import com.kanji.constants.MenuTexts;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.constants.Titles;
import com.kanji.fileReading.KanjiCharactersReader;
import com.kanji.myList.MyList;
import com.kanji.panels.InsertWordPanel;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panels.LoadingPanel;
import com.kanji.panels.ProblematicKanjiPanel;
import com.kanji.panels.RepeatingWordsPanel;
import com.kanji.panels.SearchWordPanel;
import com.kanji.panels.StartingPanel;
import com.kanji.utilities.ApplicationController;

@SuppressWarnings("serial")
public class ApplicationWindow extends DialogWindow {

	private JPanel mainApplicationPanel;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private StartingPanel startingPanel;
	private JFrame container;
	private ApplicationController applicationController; // TODO we
															// should
	// create it in
	// starting
	// panel instead

	// TODO handle the situation in gui applicationControllerController when the
	// panel has just 1 row so
	// we
	// don't have to use row (0) but somehow easier

	public KanjiCharactersReader excel;

	public ApplicationWindow() {
		super(null);
		container = new JFrame();

		mainApplicationPanel = new JPanel(new CardLayout());

		RepeatingWordsPanel repeatingWordsPanel = new RepeatingWordsPanel(this);
		applicationController = new ApplicationController(this,
				repeatingWordsPanel.getController());

		startingPanel = new StartingPanel(this, applicationController);

		mainApplicationPanel.add(startingPanel.createPanel(),
				ApplicationPanels.STARTING_PANEL.getPanelName());
		mainApplicationPanel.add(repeatingWordsPanel.createPanel(),
				ApplicationPanels.REPEATING_PANEL.getPanelName());

		setWindowProperties();
	}

	public ApplicationController getApplicationController() {
		return applicationController; // TODO remove this method later
	}

	private void setWindowProperties() {
		container = new JFrame();
		container.setJMenuBar(createMenuBar());
		container.setContentPane(mainApplicationPanel);
		container.pack();
		container.setMinimumSize(container.getSize());
		container.setTitle(Titles.app);
		container.setLocationRelativeTo(null);
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		container.setVisible(true);
	}

	public void showPanel(ApplicationPanels panel) {
		((CardLayout) mainApplicationPanel.getLayout()).show(mainApplicationPanel,
				panel.getPanelName());
	}

	public void save() {
		this.applicationController.save();
	}

	public void changeSaveStatus(SavingStatus savingStatus) {
		startingPanel.changeSaveStatus(savingStatus);
		container.repaint();
	}

	public void updateTitle(String update) {
		container.setTitle(Titles.app + "   " + update);
	}

	public void scrollToBottom() {
		applicationController.getRepeatsList().scrollToBottom();
	}

	public void addButtonIcon() {
		startingPanel.addButtonIcon();
	}

	public void removeButtonProblematicsKanji() {
		startingPanel.removeButtonProblematicsKanji();
	}

	public void showLearningStartDialog(MyList list, int maximumNumber) {
		showPanel(new LearningStartPanel(this, maximumNumber, list), Titles.learnStartDialog, false,
				Position.CENTER);

	}

	// TODO dialogs should either be jframe or modal in order for alt tab to
	// switch focus to the right window
	public void showInsertDialog(MyList list) {
		showPanel(new InsertWordPanel(list), Titles.insertWordDialog, false, Position.LEFT_CORNER);
	}

	public void showSearchWordDialog(MyList<KanjiInformation> list) {
		showPanel(new SearchWordPanel(list), Titles.wordSearchDialog, false, Position.LEFT_CORNER);
	}

	public void showProblematicKanjiDialog(MyList<KanjiInformation> kanjiSearcher,
			Set<Integer> problematicKanjis) {
		problematicKanjiPanel = new ProblematicKanjiPanel(kanjiSearcher, problematicKanjis);
		showPanel(problematicKanjiPanel, Titles.insertWordDialog, true, Position.CENTER);
	}

	public void showProblematicKanjiDialog() {
		showReadyPanel(problematicKanjiPanel.getDialog());
	}

	public LoadingPanel showProgressDialog() {
		LoadingPanel dialog = new LoadingPanel(Prompts.kanjiLoadingPrompt);
		showPanel(dialog, Titles.messageDialog, false, Position.CENTER);
		return dialog;
	}

	public void closeDialog() {
		childWindow.getContainer().dispose();
	}

	public JFrame getContainer() {
		return container;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(BasicColors.OCEAN_BLUE);
		JMenu menu = new JMenu(MenuTexts.menuBarFile);
		menuBar.add(menu);
		JMenuItem item = new JMenuItem(MenuTexts.menuOpen);

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applicationController.openKanjiProject();
			}
		});

		menu.add(item);
		return menuBar;
	}

}
