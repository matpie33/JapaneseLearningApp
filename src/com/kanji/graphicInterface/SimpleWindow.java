package com.kanji.graphicInterface;

import java.awt.Dialog.ModalityType;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.kanji.constants.Titles;
import com.kanji.panels.ConfirmPanel;
import com.kanji.panels.MessagePanel;

public class SimpleWindow {
	private JDialog window;
	protected SimpleWindow newDialog;
	protected boolean isOpened;
	
	public SimpleWindow(){
		window = new JDialog();
		isOpened = false;
	}
	
	public void setProperties(JPanel contentPane) {		
		window.setContentPane(contentPane);
		window.pack();
		window.setMinimumSize(window.getSize());
		window.setTitle(Titles.appTitle);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setVisible(true);	
		window.addWindowListener(ActionMaker.createClosingListener(this));
		isOpened = true;
	}
	
	public void setMenuBar(JMenuBar menu){
		window.setJMenuBar(menu);
	}
	
	protected boolean notOpenedYet() {
		return (newDialog == null || !newDialog.isOpened()); 
	}

	public void showMessageDialog(String message, boolean modal) {
		if (notOpenedYet()) {
			newDialog = new SimpleWindow();
			MessagePanel dialog = new MessagePanel(newDialog);
			newDialog.setProperties(dialog.createPanel(message));
		}
	}
			
	public boolean showConfirmDialog (String prompt){
	    if (notOpenedYet()){
	    	newDialog = new SimpleWindow();
	    	newDialog.getWindow().setModalityType(ModalityType.APPLICATION_MODAL);
	    	newDialog.setEscapeOnClose();
	    	ConfirmPanel panel = new ConfirmPanel(newDialog);
	    	newDialog.setProperties(panel.createPanel(prompt));
	    	
	    	System.out.println("done");
	    	return panel.accepted();
	    }
	    return false;
	}
	
	public JDialog getWindow(){
		return window;
	}
	
	private void setRootToDisposeOnKey(KeyStroke key){
		KeyBindingsMaker.makeBindings(window.getRootPane(), key, ActionMaker.createDisposingAction(this));
	}
	
	public void setEscapeOnClose(){
		setRootToDisposeOnKey((KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)));
	}
	
	public boolean isOpened(){
		return isOpened;		
	}
	
	public void close(){
		isOpened = false;
		window.dispose();
	}
	
	public void dispose(){
		System.out.println(window);
		window.dispose();
	}
	
	public SimpleWindow getNewDialog(){
		return newDialog;
	}
		
}
