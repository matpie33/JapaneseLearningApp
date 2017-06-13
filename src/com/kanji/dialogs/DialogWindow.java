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

	private DialogWindow childWindow; // TODO so how to initialize it?
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private Position position;
	private JDialog container;

	public enum Position {
		CENTER, LEFT_CORNER
	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			return false;
		}
	}

	public DialogWindow(DialogWindow b) {
		container = new JDialog();
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
		childWindow = new DialogWindow(this);
		MessagePanel dialog = new MessagePanel(mainPanel, childWindow); // TODO
																		// main
																		// panel
																		// not
																		// needed
																		// here
		showPanel(dialog.createPanel(message), Titles.messageDialogTitle, true, Position.CENTER);
	}

	public void showPanel(JPanel panel, String title, boolean modal, Position position) {
		childWindow.setPosition(position);
		childWindow.setPanel(panel);
		childWindow.showYourself(title, modal);
	}

	public void addHotkey(KeyStroke k, AbstractAction a) {
		JRootPane root = container.getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(k, "close");
		root.getActionMap().put("close", a);
	}

	public boolean showConfirmDialog(String message) {
		childWindow = new DialogWindow(this);
		ConfirmPanel panel = new ConfirmPanel(mainPanel, childWindow);
		showPanel(panel.createPanel(message), Titles.confirmDialogTitle, true, Position.CENTER);
		return isAccepted();
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setLocationAtCenterOfParent() {
		position = Position.CENTER;
	}

	public void setLocationAtLeftUpperCornerOfParent() {
		position = Position.LEFT_CORNER;
	}

	public void save() { // TODO this should go to application window to avoid
							// cast
		if (parentWindow instanceof ApplicationWindow) {
			ApplicationWindow parent = (ApplicationWindow) parentWindow;
			parent.save();
		}
	}

	public void setAccepted(boolean accepted) {
		this.isAccepted = accepted;
	}

	public boolean isAccepted() {
		return childWindow.isAccepted;
	}

	public Window getContainer() {
		return container;
	}

}
