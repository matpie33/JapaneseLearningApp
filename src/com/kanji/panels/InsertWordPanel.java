package com.kanji.panels;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;

import com.kanji.Row.KanjiWords;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.NumberValues;
import com.kanji.constants.Prompts;
import com.kanji.graphicInterface.ActionMaker;
import com.kanji.graphicInterface.GuiMaker;
import com.kanji.graphicInterface.KeyBindingsMaker;
import com.kanji.graphicInterface.MainPanel;
import com.kanji.graphicInterface.MyColors;
import com.kanji.myList.MyList;
import com.kanji.window.BaseWindow;
import com.kanji.window.LimitDocumentFilter;

public class InsertWordPanel {

	private MainPanel panel;
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
		insertWord = GuiMaker.createTextField(20);
		panel.createRow(insertWordLabel, insertWord);
		JLabel insertNumberLabel = GuiMaker.createLabel(Prompts.wordAddNumberPrompt);
		insertNumber = GuiMaker.createTextField(20);
		panel.createRow(insertNumberLabel, insertNumber);
		limitCharactersInTextField(insertNumber);

		JButton cancel = GuiMaker.createButton(ButtonsNames.buttonCancelText, 
				ActionMaker.createDisposingAction(parentDialog.getWindow()));
		AbstractAction approveAction = ActionMaker.createValidatingAction(insertWord, insertNumber, 
				list, this);
		JButton approve = GuiMaker.createButton(ButtonsNames.buttonApproveText,
				approveAction);
		KeyBindingsMaker.makeBindings(approve, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				approveAction);
		panel.createRow(cancel, approve);
		return panel.getPanel();
	}


	private void limitCharactersInTextField(JTextField textField) {
		((AbstractDocument) textField.getDocument())
				.setDocumentFilter(new LimitDocumentFilter(NumberValues.INTEGER_MAX_VALUE_DIGITS_AMOUNT));
	}

	
	public void updateGUI(String word, int number){
		System.out.println("adding: ");
		addWordToList(word, number);
		parentDialog.save();
		insertWord.selectAll();
		insertWord.requestFocusInWindow();
	}
	
	private void addWordToList(String word, int number) {
		((KanjiWords) list.getWords()).addNewRow(word, number);
		list.scrollToBottom();
	}	

}
