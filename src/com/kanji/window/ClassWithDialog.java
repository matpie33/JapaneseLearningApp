package com.kanji.window;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class ClassWithDialog extends JFrame{
	private MyDialog dialog;	
		
	public void showDialogToSearch(MyList list){ 	
		if (notOpenedYet()){
			dialog = new MyDialog(this,list);
			dialog.showSearchWordDialog();
		}		
		
	}
	
	private boolean notOpenedYet(){
		return (dialog==null || !dialog.isOpened());	//TODO try to avoid nulls	
	}
	
	public void showDialogToAddWord(MyList list){
		if (notOpenedYet()){
			dialog = new MyDialog(this,list);
			dialog.showInsertDialog();
		}
	}
	

}
