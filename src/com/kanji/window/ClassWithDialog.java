package com.kanji.window;

import java.io.Serializable;

import javax.swing.JFrame;

import com.kanji.dialogs.MyDialog;
import com.kanji.fileReading.ExcelReader;
import com.kanji.myList.MyList;

@SuppressWarnings("serial")
public abstract class ClassWithDialog extends JFrame implements Serializable{
	private MyDialog dialog;
	private boolean isExcelReaderLoaded;
		
	public void showDialogToSearch(MyList list){ 	
		if (notOpenedYet()){
			dialog = new MyDialog(this); //TODO moze skrocic?
			dialog.showSearchWordDialog(list);
			dialog.setLocationAtLeftUpperCornerOfParent(this);
		}		
		
	}
	
	private boolean notOpenedYet(){
		return (dialog==null || !dialog.isOpened());	//TODO try to avoid nulls	
	}
	
	public void showDialogToAddWord(MyList list){
		if (notOpenedYet()){
			dialog = new MyDialog(this);
			dialog.showInsertDialog(list);
			dialog.setLocationAtLeftUpperCornerOfParent(this);
		}
	}
	
	public void showMessageDialog(String message){
		if (notOpenedYet()){
			dialog = new MyDialog(this);
			dialog.showMsgDialog(message);
			dialog.setLocationAtCenterOfParent(this);
		}
	}
	
	public void showLearnStartDialog (MyList list, int maximumNumber){
		if (notOpenedYet()){			
			dialog = new MyDialog(this);
			dialog.showLearningStartDialog(list, maximumNumber);
			dialog.setLocationAtLeftUpperCornerOfParent(this);				
		}
	}
	
	
	

}
