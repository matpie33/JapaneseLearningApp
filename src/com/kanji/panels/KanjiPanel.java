package com.kanji.panels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.windows.DialogWindow;
import com.sun.glass.events.KeyEvent;

public class KanjiPanel implements PanelCreator {
	private MainPanel main;
	private DialogWindow parentDialog;
	private String message;

	public KanjiPanel(String message) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		this.message = message;
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		parentDialog = parent;
	}

	@Override
	public JPanel createPanel() {

		JTextArea prompt = addPromptAtLevel(message);
		main.addRow(RowMaker.createBothSidesFilledRow(prompt));

		JButton okButton = new JButton("Ok");
		AbstractAction al = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
				parentDialog.setAccepted(true);
			}
		};
		okButton.addActionListener(al);
		okButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "enter");

		okButton.getActionMap().put("enter", al);

		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, okButton));

		return main.getPanel();
	}

	private JTextArea addPromptAtLevel(String message) {
		JTextArea elem = new JTextArea(1, 1);
		Font f = new Font("MS PMincho", 1, 80);
		elem.setFont(f);
		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setEditable(false);
		return elem;

	}

}
