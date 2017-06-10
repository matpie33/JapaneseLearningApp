package com.kanji.dialogs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
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
	private Insets insets = new Insets(10, 10, 0, 10);
	private Color backgroundColor = Color.GREEN;
	private GridBagConstraints layoutConstraints;
	private DialogWindow upper;
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private ProblematicKanjiPanel problematicKanjiPanel;
	private DialogWindow dialog;
	private DialogWindow problematicKanjisDialog;
	private Position position;

	private enum Position {
		CENTER, LEFT_CORNER
	}

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			// if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			// dispose();
			return false;
		}
	}

	public DialogWindow() {

	}

	public DialogWindow(DialogWindow b) {
		// super(b);
		parentWindow = b;
		initialize();
		initializeLayout();
	}

	private void initialize() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}

	private void initializeLayout() {

		mainPanel = new JPanel();
		mainPanel.setBackground(backgroundColor);
		mainPanel.setLayout(new GridBagLayout());

		setContentPane(mainPanel);
		initializeLayoutConstraints();

	}

	private void initializeLayoutConstraints() {
		layoutConstraints = new GridBagConstraints();
		layoutConstraints.insets = insets;
		layoutConstraints.anchor = GridBagConstraints.WEST;
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
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		position = Position.CENTER;
		addHotkey(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), action);

		MessagePanel dialog = new MessagePanel(mainPanel, this);
		mainPanel = dialog.createPanel(message);

		setLocationAtCenterOfParent();
		// setModal(true);
		System.out.println("yoyo aa");
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
		// panel.setLayoutConstraints(layoutConstraints);
		position = Position.CENTER;
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
		// upper.setLocationAtCenterOfParent(this);
		// upper.pack();
		// upper.setMinimumSize(upper.getSize());

	}

	public void setLocationAtCenterOfParent() {
		position = Position.CENTER;
		setLocationRelativeTo(getParent());

	}

	public void setLocationAtLeftUpperCornerOfParent() {
		position = Position.LEFT_CORNER;
		System.out.println("parent position: " + parentWindow.getBounds().x);
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
