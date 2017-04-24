package com.kanji.panels;

import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.KeyBindingsMaker;
import com.kanji.graphicInterface.MyColors;
import com.kanji.listenersAndAdapters.ActionMaker;
import com.kanji.myList.MyList;
import com.kanji.window.BaseWindow;

public class InsertWordPanel {

	private com.guimaker.panels.MainPanel panel;
	private BaseWindow parentDialog;
	private MyList list;
	private JTextField insertWord;
	private JTextField insertNumber;

	public InsertWordPanel(BaseWindow parent) {
		panel = new MainPanel(MyColors.DARK_GREEN);
		parentDialog = parent;
	}

	public JPanel createPanel(MyList list) {
		this.list = list;
		JLabel insertWordLabel = GuiMaker.createLabel(Prompts.wordAddDialogPrompt);
		insertWord = GuiMaker.createTextField(100);
		
		JLabel insertNumberLabel = GuiMaker.createLabel(Prompts.wordAddNumberPrompt);
		insertNumber = GuiMaker.createTextField(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT);
	

		System.out.println("PR: "+parentDialog);
		JButton cancel = GuiMaker.createButton(ButtonsNames.buttonCancelText, 
				ActionMaker.createDisposingAction(parentDialog.getNewDialog()));
		AbstractAction approveAction = ActionMaker.createValidatingAction(insertWord, insertNumber, 
				list, this);
		JButton approve = GuiMaker.createButton(ButtonsNames.buttonApproveText,
				approveAction);
		KeyBindingsMaker.makeBindings(approve, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				approveAction);
		panel.addRow(RowMaker.createHorizontallyFilledRow(insertWordLabel,insertWord).
				fillHorizontallySomeElements(insertWord));
		panel.addRow(RowMaker.createHorizontallyFilledRow(insertNumberLabel,insertNumber).
				fillHorizontallySomeElements(insertNumber));
		panel.addRow(RowMaker.createUnfilledRow(GridBagConstraints.EAST, cancel,approve));
		return panel.getPanel();
	}


	

	
	public void updateGUI(String word, int number){
		System.out.println("adding: ");
		addWordToList(word, number);
		parentDialog.save();
		insertWord.selectAll();
		insertWord.requestFocusInWindow();
	}
	
	private void addWordToList(String word, int number) {
		((KanjiWords) list.getContentManager()).addNewRow(word, number);
		list.scrollToBottom();
	}	

}
