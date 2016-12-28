package com.kanji.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.kanji.constants.ButtonsNames;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.graphicInterface.ActionMaker;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.MainPanel;
import com.kanji.graphicInterface.MyColors;
import com.kanji.listenersAndAdapters.AdaptersMaker;
import com.kanji.myList.MyList;
import com.kanji.panelsLogic.LearningStartLogic;
import com.kanji.window.BaseWindow;

public class LearningStartPanel {

	private JScrollPane scrollPane;
	private JTextField sumRangeField;
	private JCheckBox problematicCheckbox;
	
	private BaseWindow parentFrame;
	private MainPanel panel;
	private LearningStartLogic logic;
	private MainPanel rangesPanel;
	
	public LearningStartPanel(JPanel panel, BaseWindow parentOfParent, int numberOfWords, MyList list) {
		this.panel = new MainPanel (MyColors.DARK_GREEN);
		this.parentFrame = parentOfParent;
		logic = new LearningStartLogic(this, numberOfWords, list);
	}

	public JPanel createPanel() { // TODO add focus to textfield from
		if (!excelReaderIsLoaded())
			loadExcel();
		JTextArea labelStartLearning = GuiMaker.createTextArea(false);
		labelStartLearning.setOpaque(false);
		labelStartLearning.setText(Prompts.learnStartPrompt);
		panel.createRow(labelStartLearning);

		problematicCheckbox = GuiMaker.createCheckBox(Options.problematicKanjiOption, 
				ActionMaker.createActionAddProblematicKanjis(logic));
		panel.createRow(problematicCheckbox);

		rangesPanel = new MainPanel (MyColors.LIGHT_VIOLET);
		Border b= BorderFactory.createLineBorder(Color.red);
		scrollPane = GuiMaker.createScrollPane(MyColors.DARK_BLUE,b, rangesPanel.getPanel(), 
				new Dimension (300,200));
		addRowToPanel();
				
		this.panel.createRow(2,scrollPane);

		JTextField problematicKanjis = GuiMaker.createTextField(5, 
				"Problematyczne: "+parentFrame.getProblematicKanjis().size());
		this.panel.createRow(problematicKanjis);

		JButton newRow = GuiMaker.createButton(ButtonsNames.buttonAddRowText, ActionMaker.addNewRow(this));
		sumRangeField = GuiMaker.createTextField(5, Prompts.sumRangePrompt);
		this.panel.createRow(newRow, sumRangeField);

		JButton cancel = GuiMaker.createButton(ButtonsNames.buttonCancelText,
				ActionMaker.createDisposingAction(parentFrame.getWindow()));
		JButton approve = GuiMaker.createButton(ButtonsNames.buttonApproveText, 
				ActionMaker.startLearning(logic));
		this.panel.createRow( cancel, approve );
		return this.panel.getPanel();
	}


	private void loadExcel() {
		if (parentFrame instanceof BaseWindow) {
			BaseWindow p = (BaseWindow) parentFrame;
			p.startLoadingKanji();
		}
	}

	public void addRowToPanel() {
		
		JLabel labelFrom = GuiMaker.createLabel("Od: ");
		JTextField textFieldFrom = GuiMaker.createTextField(5);
		JLabel labelTo = GuiMaker.createLabel(" do: ");
		JTextField textFieldTo = GuiMaker.createTextField(5);				

		JButton delete = new JButton ();
		JPanel container = rangesPanel.createRow(GridBagConstraints.NORTHWEST,1,
				labelFrom, textFieldFrom, labelTo, textFieldTo);
		if (rangesPanel.getNumberOfRows()>1){
			delete = GuiMaker.createButton(ButtonsNames.buttonRemoveRowText, 
					ActionMaker.createDeletingRowAction(rangesPanel, container, logic));
			rangesPanel.addElementsToRow(container, delete);
		}
		if (rangesPanel.getNumberOfRows()==2){
			JPanel firstRow = rangesPanel.getRows().get(0);
			delete = GuiMaker.createButton(ButtonsNames.buttonRemoveRowText, 
					ActionMaker.createDeletingRowAction(rangesPanel, firstRow, logic));
			rangesPanel.addElementsToRow(firstRow, delete);
		}
		
		KeyAdapter checkTextFields = AdaptersMaker.create(textFieldFrom, textFieldTo, this, container);
		textFieldFrom.addKeyListener(checkTextFields);
		textFieldTo.addKeyListener(checkTextFields);
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		
	}	

	public void updateSumOfWords(int newSum) {
		sumRangeField.setText(Prompts.sumRangePrompt + newSum);
	}
	

	public boolean excelReaderIsLoaded() {
		if (parentFrame instanceof BaseWindow) {
			BaseWindow parent = (BaseWindow) parentFrame;
			return parent.isExcelLoaded();
		} else
			return false; // TODO or throw exception
	}	
	public JCheckBox getProblematicCheckBox(){
		return problematicCheckbox;
	}
	
	public BaseWindow getParentFrame(){
		return parentFrame;
	}
	
	public LearningStartLogic getLogic (){
		return logic;
	}
	
	public MainPanel getRangesPanel(){
		return rangesPanel;
	}


}
