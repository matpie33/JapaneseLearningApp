package com.kanji.dialogs;

import java.awt.Color;
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

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import com.kanji.constants.Prompts;
import com.kanji.constants.Titles;
import com.kanji.myList.MyList;
import com.kanji.window.BaseWindow;

public class MyDialog extends JDialog {

	private static final long serialVersionUID = 7484743485658276014L;
	private Insets insets = new Insets(10, 10, 0, 10);
	private Color backgroundColor = Color.GREEN;
	private GridBagConstraints layoutConstraints;
	private boolean isOpened;
	private MyDialog upper;
	private JPanel mainPanel;
	private Window parentWindow;
	private boolean isAccepted;

	private class MyDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			// if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			// dispose();
			return false;
		}
	}

	public MyDialog(Window b) {
		super(b);
		parentWindow = b;
		initialize();
		initializeLayout();
		addEscapeKeyToCloseTheWindow();
	}

	private void initialize() {
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new MyDispatcher());
		isOpened = true;
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

	private void addEscapeKeyToCloseTheWindow() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				isOpened = false;
			}
		});
	}

	public void showLearningStartDialog(MyList list, int maximumNumber) {
		LearningStartPanel dialog = new LearningStartPanel(mainPanel, this, parentWindow,
				maximumNumber);
		mainPanel = dialog.createPanel(list);
		showYourself(Titles.learnStartDialogTitle);
	}

	private void showYourself(String title) {
		setContentPane(mainPanel);
		pack();
		setLocationRelativeTo(parentWindow);
		// setModal(true);
		setTitle(title);
		// setMinimumSize(getSize());
		setVisible(true);

	}

	public LoadingPanel showProgressDialog() {
		// ModalityType modality;
		// modality = ModalityType.APPLICATION_MODAL;

		LoadingPanel dialog = new LoadingPanel(mainPanel, this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createPanel(Prompts.kanjiLoadingPrompt);
		// setLocationRelativeTo(parentWindow);
		// setModal(true);
		System.out.println("yoyo aa");
		showYourself(Titles.messageDialogTitle);
		return dialog;
	}

	public void showMsgDialog(String message, boolean modal) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				isOpened = false;
			}
		};
		JRootPane root = getRootPane();
		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "close");
		root.getActionMap().put("close", action);
		setRootPane(root);
		ModalityType modality;
		if (modal)
			modality = ModalityType.APPLICATION_MODAL;
		else
			modality = ModalityType.MODELESS;

		MessagePanel dialog = new MessagePanel(mainPanel, this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createPanel(message);
		// setLocationRelativeTo(parentWindow);
		// setModal(true);
		System.out.println("yoyo aa");
		showYourself(Titles.messageDialogTitle);

	}

	public void showSearchWordDialog(MyList list) {
		SearchWordPanel dialog = new SearchWordPanel(mainPanel, this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createPanel(list);
		showYourself(Titles.wordSearchDialogTitle);
	}

	public void showInsertDialog(MyList list) {
		InsertWordPanel dialog = new InsertWordPanel(mainPanel, this);
		dialog.setLayoutConstraints(layoutConstraints);
		mainPanel = dialog.createPanel(list);
		showYourself(Titles.insertWordDialogTitle);
	}

	public void showConfirmDialog(String message) {
		ConfirmPanel panel = new ConfirmPanel(mainPanel, this);
		panel.setLayoutConstraints(layoutConstraints);
		mainPanel = panel.createPanel(message);
		pack();
		setLocationRelativeTo(parentWindow);
		setModal(true);
		setVisible(true);

	}

	public void showErrorDialogInNewWindow(String message) { // TODO jak tego
																// uniknac bo to
																// kopia
		if (upper == null || !upper.isOpened) {
			upper = new MyDialog(this);
		}
		else
			return;

		upper.showMsgDialog(message, true);
		// upper.setLocationAtCenterOfParent(this);
		// upper.pack();
		// upper.setMinimumSize(upper.getSize());

	}

	public boolean isOpened() {
		return isOpened;
	}

	public JButton createButtonDispose(String text, KeyStroke disposeKey) {
		JButton button = new JButton(text);
		AbstractAction action = new AbstractAction() {
			private static final long serialVersionUID = 5504620933205592893L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		button.addActionListener(action);
		button.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).put(disposeKey, "space");

		button.getActionMap().put("space", action);
		return button;
	}

	public void setLocationAtCenterOfParent(Window parent) {
		setLocationRelativeTo(parentWindow);

	}

	public void setLocationAtLeftUpperCornerOfParent(Window parent) {
		setLocation(parent.getLocation());
	}

	public void save() {
		if (parentWindow instanceof BaseWindow) {
			BaseWindow parent = (BaseWindow) parentWindow;
			parent.save();
		}
	}

	public void setAccepted(boolean accepted) {
		this.isAccepted = accepted;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

}
