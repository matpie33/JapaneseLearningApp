package com.kanji.window;

import javax.swing.JFrame;

import com.kanji.dialogs.MyDialog;

@SuppressWarnings("serial")
public abstract class ClassWithDialog extends JFrame{
	private MyDialog dialog;	
		
	public void showDialogToSearch(MyList list){ 	
		if (notOpenedYet()){
			dialog = new MyDialog(this); //TODO moze skrocic?
			dialog.showSearchWordDialog(list);
		}		
		
	}
	
	private boolean notOpenedYet(){
		return (dialog==null || !dialog.isOpened());	//TODO try to avoid nulls	
	}
	
	public void showDialogToAddWord(MyList list){
		if (notOpenedYet()){
			dialog = new MyDialog(this);
			dialog.showInsertDialog(list);
		}
	}
	
	public void showMessageDialog(String message){
		if (notOpenedYet()){
			dialog = new MyDialog(this);
			dialog.showMsgDialog(message);
		}
	}
	
	public void showLearnStartDialog (){
		if (notOpenedYet()){
			dialog = new MyDialog(this);
			dialog.showLearningStartDialog();
		}
	}
	

}
