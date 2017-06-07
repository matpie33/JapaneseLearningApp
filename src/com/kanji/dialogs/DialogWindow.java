package com.kanji.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.kanji.Row.KanjiWords;
import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.window.ApplicationWindow;
import com.kanji.window.ClassWithDialog;

public class DialogWindow extends JDialog {

	private static final long serialVersionUID = 7484743485658276014L;
	private Insets insets = new Insets(10, 10, 0, 10);
	private Color backgroundColor = Color.GREEN;
	private GridBagConstraints layoutConstraints;
	private DialogWindow upper;
	private JPanel mainPanel;
	private Window parentWindow;
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

	public DialogWindow(Window b) {
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

	private void addHotkey(KeyStroke k, AbstractAction a) {
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

	public JButton createButtonHide(String text, KeyStroke disposeKey,
			final ProblematicKanjiPanel panel) {
		JButton button = new JButton(text);
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				hideProblematics(panel);

			}
		};
		button.addActionListener(action);
		button.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(disposeKey, "space");

		button.getActionMap().put("space", action);
		return button;
	}

	private void hideProblematics(ProblematicKanjiPanel problematicKanjiPanel2) {
		setVisible(false);
		System.out.println("just hide");
		ApplicationWindow parentBaseWindow = ((ApplicationWindow) parentWindow);
		parentBaseWindow.addButtonIcon(problematicKanjiPanel2);
		if (problematicKanjiPanel != null && problematicKanjiPanel.allProblematicKanjisRepeated()) {
			System.out.println("removing");
			parentBaseWindow.removeButtonProblematicsKanji();
			ClassWithDialog c = (ClassWithDialog) parentWindow;
			// c.closeDialog();
			c.closeProblematics();

		}
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
