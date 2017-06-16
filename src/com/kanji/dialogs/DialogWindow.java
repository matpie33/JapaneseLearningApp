package com.kanji.dialogs;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.kanji.constants.Titles;
import com.kanji.window.ApplicationWindow;

public class DialogWindow {

	protected DialogWindow childWindow;
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
		if (b instanceof ApplicationWindow) {
			ApplicationWindow w = (ApplicationWindow) b;
			container = new JDialog(w.getContainer());
		}
		else if (b != null && b.getContainer() != null) {
			container = new JDialog(b.getContainer());
		}
		else {
			container = new JDialog();
		}
		container.setAutoRequestFocus(true);
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
		setCoordinatesBasedOnPosition();
		container.setModal(modal);
		container.setTitle(title);
		container.setVisible(true);
	}

	private void setCoordinatesBasedOnPosition() {
		switch (position) {
		case CENTER:
			container.setLocationRelativeTo(parentWindow.getContainer());
			break;
		case LEFT_CORNER:
			container.setLocation(parentWindow.getContainer().getLocation());
			break;
		}
	}

	public void showMsgDialog(String message) {
		showPanel(new MessagePanel(message), Titles.messageDialogTitle, true, Position.CENTER);
	}

	public void showPanel(PanelCreator panel, String title, boolean modal, Position position) {
		if (childWindowIsClosed()) {
			childWindow = new DialogWindow(this);
			panel.setParentDialog(childWindow);
			childWindow.setPosition(position);
			childWindow.setPanel(panel.createPanel());
			childWindow.showYourself(title, modal);
		}
	}

	private boolean childWindowIsClosed() {
		return childWindow == null || !childWindow.getContainer().isVisible();
	}

	public boolean showConfirmDialog(String message) {
		showPanel(new ConfirmPanel(message), Titles.confirmDialogTitle, true, Position.CENTER);
		return isAccepted();
	}

	public void setPosition(Position position) {
		this.position = position;
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

	public void addHotkey(int keyEvent, AbstractAction a, JComponent component) {
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(keyEvent, 0), "close");
		component.getActionMap().put("close", a);
	}

	public void addHotkeyToWindow(int keyEvent, AbstractAction a) {
		addHotkey(keyEvent, a, container.getRootPane());
	}

	public DialogWindow getParent() {
		return parentWindow;
	}

}
