package com.kanji.dialogs;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.kanji.constants.Titles;
import com.kanji.window.ApplicationWindow;

public class DialogWindow {

	private DialogWindow newDialog;
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private DialogWindow dialog;
	private Position position;
	private JDialog container;

	private enum Position {
		CENTER, LEFT_CORNER
	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			return false;
		}
	}

	public DialogWindow() {
		container = new JDialog();
	}

	public DialogWindow(DialogWindow b) {
		this();
		parentWindow = b;
		initialize();
	}

	private void initialize() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
	}

	public void setPanel(JPanel panel) {
		mainPanel = panel;
	}

	public void showYourself(String title) {
		showYourself(title, false);
	}

	public void showYourself(String title, boolean modal) {
		container.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		container.setContentPane(mainPanel);
		container.pack();
		setPosition();
		container.setModal(modal);
		container.setTitle(title);
		container.setVisible(true);
	}

	private void setPosition() {
		switch (position) {
		case CENTER:
			container.setLocationRelativeTo(container.getParent());
			break;
		case LEFT_CORNER:
			container.setLocation(parentWindow.getContainer().getLocation());
			break;
		}
	}

	public void showMsgDialog(String message) {
		MessagePanel dialog = new MessagePanel(mainPanel, this);
		mainPanel = dialog.createPanel(message);
		setLocationAtCenterOfParent();
		showYourself(Titles.messageDialogTitle, true);
	}

	public void addHotkey(KeyStroke k, AbstractAction a) {
		JRootPane root = container.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(k, "close");
		root.getActionMap().put("close", a);
	}

	public boolean showConfirmDialog(String message) {
		ConfirmPanel panel = new ConfirmPanel(mainPanel, this);
		setLocationAtCenterOfParent();
		mainPanel = panel.createPanel(message);
		showYourself("Potwierdź", true);
		return isAccepted;
	}

	public void showErrorDialogInNewWindow(String message) {

		if (newDialog == null) {
			newDialog = new DialogWindow(this);
		}
		newDialog.showMsgDialog(message);

	}

	public void setLocationAtCenterOfParent() {
		position = Position.CENTER;
	}

	public void setLocationAtLeftUpperCornerOfParent() {
		position = Position.LEFT_CORNER;
	}

	public void save() {
		if (parentWindow instanceof ApplicationWindow) {
			ApplicationWindow parent = (ApplicationWindow) parentWindow;
			parent.save();
		}
	}

	public void setAccepted(boolean accepted) {
		this.isAccepted = accepted;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public Window getContainer() {
		return container;
	}

}
