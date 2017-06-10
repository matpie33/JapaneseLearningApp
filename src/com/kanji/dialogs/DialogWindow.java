package com.kanji.dialogs;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.kanji.constants.Titles;
import com.kanji.window.ApplicationWindow;

public class DialogWindow extends JDialog {

	private static final long serialVersionUID = 7484743485658276014L;
	private DialogWindow upper;
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private DialogWindow dialog;
	private Position position;

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

	}

	public DialogWindow(DialogWindow b) {
		parentWindow = b;
		initialize();
	}

	private void initialize() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	public void setPanel(JPanel panel) {
		mainPanel = panel;
	}

	public void showYourself(String title) {
		showYourself(title, false);

	}

	public void showYourself(String title, boolean modal) {
		setContentPane(mainPanel);
		pack();
		setPosition();
		setModal(modal);
		setTitle(title);
		setVisible(true);
	}

	private void setPosition() {
		switch (position) {
		case CENTER:
			setLocationAtCenterOfParent();
			break;
		case LEFT_CORNER:
			setLocationAtLeftUpperCornerOfParent();
			break;
		}
	}

	public void showMsgDialog(String message, boolean modal) {

		MessagePanel dialog = new MessagePanel(mainPanel, this);
		mainPanel = dialog.createPanel(message);

		setLocationAtCenterOfParent();
		showYourself(Titles.messageDialogTitle, true);

	}

	public void addHotkey(KeyStroke k, AbstractAction a) {
		JRootPane root = getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(k, "close");
		root.getActionMap().put("close", a);
		setRootPane(root);
	}

	public boolean showConfirmDialog(String message) {
		ConfirmPanel panel = new ConfirmPanel(mainPanel, this);
		setLocationAtCenterOfParent();
		mainPanel = panel.createPanel(message);
		showYourself("Potwierdź", true);
		return isAccepted;
	}

	public void showErrorDialogInNewWindow(String message) { // TODO jak tego
																// uniknac bo to
																// kopia
		if (upper == null || !upper.isDisplayable()) {
			upper = new DialogWindow(this);
		}
		else
			return;

		upper.showMsgDialog(message, true);

	}

	public void setLocationAtCenterOfParent() {
		position = Position.CENTER;
		setLocationRelativeTo(getParent());

	}

	public void setLocationAtLeftUpperCornerOfParent() {
		position = Position.LEFT_CORNER;
		setLocation(parentWindow.getLocation());
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

	public void closeDialog() {
		dialog.dispose();
	}

}
