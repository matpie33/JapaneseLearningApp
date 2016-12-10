package com.kanji.dialogs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.sun.glass.events.KeyEvent;

public class ConfirmPanel {



    	private JPanel mainPanel;
    	private GridBagConstraints layoutConstraints;
    	private MyDialog parentDialog;

    	public ConfirmPanel(JPanel panel, MyDialog parent) {
    		mainPanel = panel;
    		parentDialog = parent;
    		layoutConstraints = new GridBagConstraints();
    	}

    	public void setLayoutConstraints(GridBagConstraints c) {
    		layoutConstraints = c;
    	}

    	public JPanel createPanel(String message) {

    		int level = 0;
    		addPromptAtLevel(level, message);

    		JButton yesButton = new JButton ("Tak");
    		AbstractAction al = new AbstractAction (){
    		    @Override
    		    public void actionPerformed (ActionEvent e){
    			parentDialog.dispose();
    			parentDialog.setAccepted(true);
    		    }
    		};
    		yesButton.addActionListener(al);
    		yesButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "enter");

		yesButton.getActionMap().put("enter", al);
    		
    		
    		
    		JButton noButton = new JButton ("Nie");
    		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
			    parentDialog.dispose();
	    			parentDialog.setAccepted(false);
			}
		};
		noButton.addActionListener(action);
		noButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "space");

		noButton.getActionMap().put("space", action);
    		layoutConstraints.gridy++;
    		layoutConstraints.gridx=0;
    		
    		layoutConstraints.fill = GridBagConstraints.NONE;
    		mainPanel.add(yesButton, layoutConstraints);
    		
    		layoutConstraints.gridx++;
    		mainPanel.add(noButton, layoutConstraints);    		

    		return mainPanel;
    	}

    	private void addPromptAtLevel(int level, String message) {
    		layoutConstraints.gridy = level;
		layoutConstraints.anchor = GridBagConstraints.CENTER;
    		layoutConstraints.weightx = 1;
    		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
    		JTextArea elem = new JTextArea(4, 30);

    		elem.setText(message);
    		elem.setLineWrap(true);
    		elem.setWrapStyleWord(true);
    		elem.setOpaque(false);
    		elem.setEditable(false);

    		mainPanel.add(elem, layoutConstraints);
    	}

}

    

