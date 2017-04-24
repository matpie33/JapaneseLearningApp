package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.ExceptionsMessages;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Options;
import com.kanji.constants.Prompts;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.MyColors;
import com.kanji.graphicInterface.SimpleWindow;
import com.kanji.listenersAndAdapters.ActionMaker;
import com.kanji.myList.MyList;
import com.kanji.myList.SearchOptions;

public class SearchWordPanel {

	private GridBagConstraints layoutConstraints;
	private JTextField textField;
	private SimpleWindow window;
	private JRadioButton fullWordsSearchOption;
	private JRadioButton perfectMatchSearchOption;
	private SearchOptions options;	
	private MyList list;
	private MainPanel panel;
	
	public SearchWordPanel (SimpleWindow window){
		panel = new MainPanel(MyColors.DARK_GREEN);
		this.window=window;
		layoutConstraints = new GridBagConstraints();			
		options = new SearchOptions();
	}
	
	public void setLayoutConstraints (GridBagConstraints c){
		layoutConstraints=c;
	}
	
	public JPanel createPanel(MyList list){
		this.list=list;
		JLabel prompt = GuiMaker.createLabel(Prompts.wordSearchDialogPrompt);		
		textField = GuiMaker.createTextField(20);	
		
		
		JRadioButton defaultSearchOption = GuiMaker.createRadioButton(Options.wordSearchDefaultOption, 
				ActionMaker.createDefaultSearchOption(options));
		
		
		fullWordsSearchOption = GuiMaker.createRadioButton(Options.wordSearchOnlyFullWordsOption,
				ActionMaker.createFullWordsSearchOption(options));		
		
		perfectMatchSearchOption = GuiMaker.createRadioButton(Options.wordSearchPerfectMatchOption,
				ActionMaker.createPerfectMatchSearchOption(options));
		
		
		addRadioButtonsToGroup (defaultSearchOption, fullWordsSearchOption,	perfectMatchSearchOption);
		
		defaultSearchOption.setSelected(true);
		
		
		JButton previous = createButtonPrevious(ButtonsNames.buttonPreviousText);
		JButton next = createButtonNext(ButtonsNames.buttonNextText);
		panel.addRow(RowMaker.createHorizontallyFilledRow(prompt,textField).
				fillHorizontallySomeElements(textField));
		panel.addRow(RowMaker.createHorizontallyFilledRow(defaultSearchOption));
		panel.addRow(RowMaker.createHorizontallyFilledRow(fullWordsSearchOption));
		panel.addRow(RowMaker.createHorizontallyFilledRow(perfectMatchSearchOption));
		panel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.EAST, previous,next));
				
		return panel.getPanel();
	}
	
	private void addRadioButtonsToGroup (JRadioButton ... buttons){
		ButtonGroup group = new ButtonGroup();
		for (JRadioButton button: buttons)
			group.add(button);
	}		
	

	private JButton createButtonPrevious (String text){
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				tryToFindNextOccurence(NumberValues.BACKWARD_DIRECTION);			
			}
		});
		
		return button;
	}
	
	private JButton createButtonNext (String text){
		JButton button = new JButton(text);
		button.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed (ActionEvent e){
				tryToFindNextOccurence(NumberValues.FORWARD_DIRECTION);			
			}
		});
		return button;		
	}	
	
	
	private void tryToFindNextOccurence(int direction){
		System.out.println(options.isMatchByExpressionEnabled());
		System.out.println(options.isMatchByWordEnabled());
		try {
			boolean found = list.findAndHighlightNextOccurence(textField.getText(), direction, options);
			if (!found)
				window.showMessageDialog(ExceptionsMessages.wordNotFoundMessage, true);
		} 
		catch (Exception e) {
			e.printStackTrace();
			window.showMessageDialog(e.getMessage(),true); 
		}
	}

	
}
