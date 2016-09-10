package com.kanji.window;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class ClassWithDialog extends JFrame{
	private MyDialog dialog;
	
	
	public void showDialogToSearch(MyList list){ //TODO try to avoid nulls
		if (dialog==null || !dialog.isOpened())
			dialog = new MyDialog(this,list);
		else {
			System.out.println("not null");
			return;
		}
		
		dialog.showInputDialog();
		
	}
	

}
