package com.kanji.panels;

import java.awt.Color;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.constants.Prompts;
import com.kanji.constants.SavingStatus;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.MyColors;
import com.kanji.myList.MyList;
import com.kanji.window.ElementMaker;

public class StartingPanel {
	private ElementMaker maker;
	private JScrollPane listScrollWords;
	private JScrollPane listScrollRepeated;
	private JSplitPane listsSplitPane;
	private JPanel infoPanel;
	private JPanel buttonsPanel;
	private JLabel saveInfo;
	private MainPanel panel;
	
	public StartingPanel (ElementMaker maker){
		
		panel = new MainPanel (MyColors.LIGHT_VIOLET);
		this.maker = maker;
		createUpperPanel();
		createInformationsPanel();
		createButtonsPanel(maker.getButtons());
		panel.addRow(RowMaker.createBothSidesFilledRow(listsSplitPane).fillHorizontallyEqually().fillAllVertically());
//		panel.addRow(panel.createHorizontallyFilledRow(maker.getButtons().toArray(new JComponent[]{})).fillHorizontallyEqually().fillAllVertically());
		panel.addRow(RowMaker.createHorizontallyFilledRow(buttonsPanel).fillHorizontallyEqually());
		panel.addRow(RowMaker.createHorizontallyFilledRow(infoPanel).fillHorizontallyEqually());
		
		
	}
	
	private void createUpperPanel() {
		MyList wordsList = maker.getWordsList();
		MyList repeatsList = maker.getRepeatsList();
		listScrollWords = GuiMaker.createScrollPaneForList(wordsList);
		listScrollRepeated = GuiMaker.createScrollPaneForList(repeatsList);
		listsSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScrollWords, listScrollRepeated);
		listsSplitPane.setResizeWeight(0.5);		
		panel.getPanel().repaint();
	}

	private void createInformationsPanel() {
		infoPanel = new JPanel();
		infoPanel.setBackground(Color.YELLOW);
		saveInfo = GuiMaker.createLabel("");
		changeSaveStatus(SavingStatus.NoChanges);
		infoPanel.add(saveInfo);
	}

	private void createButtonsPanel(List<JButton> list) {

		MainPanel p = new MainPanel(MyColors.DARK_BLUE);
		buttonsPanel = p.getPanel();
		p.addRow(RowMaker.createHorizontallyFilledRow(list.toArray(new JButton [] {})));

	}
	
	public void changeSaveStatus(SavingStatus savingStatus) {
		saveInfo.setText(Prompts.savingStatusPrompt + savingStatus.getText());
		infoPanel.repaint();
	}
	
	public void updateLeft() {
		listsSplitPane.setLeftComponent(GuiMaker.createScrollPaneForList(maker.getWordsList()));
		panel.getPanel().repaint();
	}
	
	public JPanel getPanel (){
		return panel.getPanel();
	}
	

}
