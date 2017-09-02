package com.kanji.panels;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JScrollPane;

import com.guimaker.enums.Anchor;
import com.guimaker.enums.FillType;
import com.guimaker.enums.TextAlignment;
import com.guimaker.panels.GuiMaker;
import com.guimaker.row.SimpleRow;
import com.kanji.constants.ButtonsNames;
import com.kanji.constants.HotkeysDescriptions;

public class MessagePanel extends AbstractPanelWithHotkeysInfo {

	private String message;

	public MessagePanel(String message) {
		// super(true); //TODO this is misleading - we add escape for closing,
		// but we don't call super with true as parameter
		this.message = message;
	}

	@Override
	void createElements() {
		AbstractButton button = createButtonClose();
		JScrollPane scrollPane = GuiMaker.createTextPaneWrappedInScrollPane(message,
				TextAlignment.CENTERED);

		mainPanel.addRow(new SimpleRow(FillType.BOTH, scrollPane));
		addHotkeysPanelHere();
		mainPanel.addRow(new SimpleRow(FillType.NONE, Anchor.CENTER, button));
	}

	private AbstractButton createButtonClose() {
		AbstractAction dispose = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				parentDialog.getContainer().dispose();
			}
		};
		return createButtonWithHotkey(KeyEvent.VK_ESCAPE, dispose, ButtonsNames.APPROVE,
				HotkeysDescriptions.CLOSE_WINDOW);
	}

}
