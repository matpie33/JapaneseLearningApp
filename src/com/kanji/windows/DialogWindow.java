package com.kanji.windows;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import com.guimaker.utilities.CommonActionsMaker;
import com.kanji.strings.Titles;
import com.kanji.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panels.ConfirmPanel;
import com.kanji.panels.MessagePanel;

public class DialogWindow {

	protected DialogWindow childWindow;
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private Position position;
	private JDialog container;
	private AbstractPanelWithHotkeysInfo panelType;

	public enum Position {
		CENTER, LEFT_CORNER, NEXT_TO_PARENT
	}

	public DialogWindow(DialogWindow parent) {
		if (parent instanceof ApplicationWindow) {
			ApplicationWindow w = (ApplicationWindow) parent;
			container = new JDialog(w.getContainer());
		}
		else if (parent != null && parent.getContainer() != null) {
			container = new JDialog(parent.getContainer());
		}
		else {
			container = new JDialog();
		}
		container.setAutoRequestFocus(true);
		parentWindow = parent;
	}

	public void setPanel(JPanel panel) {
		mainPanel = panel;
	}

	public void showYourself(AbstractPanelWithHotkeysInfo panelCreator, String title,
			boolean modal) {
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

		case NEXT_TO_PARENT:
			setChildNextToParent(parentWindow.getContainer(), container);
			// Point parentLocation =
			// parentWindow.getContainer().getLocationOnScreen();
			// Dimension parentSize = parentWindow.getContainer().getSize();
			// container.setLocation(parentLocation.x + parentSize.width,
			// parentLocation.y);
			break;
		}
	}

	protected void setChildNextToParent(Window parentContainer, Window childContainer) {
		Point parentLocation = parentContainer.getLocationOnScreen();
		Dimension parentSize = parentContainer.getSize();
		childContainer.setLocation(parentLocation.x + parentSize.width, parentLocation.y);
	}

	public void showMessageDialog(String message) {
		createDialog(new MessagePanel(message), Titles.MESSAGE_DIALOG, true, Position.CENTER);
	}

	private void makeTheChildFollowThisDialog() {
		container.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				setChildNextToParent(container, childWindow.getContainer());
			}
		});
	}

	public void createDialog(AbstractPanelWithHotkeysInfo panelCreator, String title, boolean modal,
			Position position) {
		if (!isDialogOfSameType(panelCreator) || childWindowIsClosed()) {
			panelType = panelCreator;
			childWindow = new DialogWindow(this);
			panelCreator.setParentDialog(childWindow);
			childWindow.setPosition(position);
			JPanel panel = panelCreator.createPanel();
			childWindow.setPanel(panel);
			childWindow.showYourself(panelCreator, title, modal);
			childWindow.getContainer().setMinimumSize(childWindow.getContainer().getSize());
			panelCreator.afterVisible();
		}
	}

	public boolean isDialogOfSameType(AbstractPanelWithHotkeysInfo panelTypeToCompare) {
		return panelTypeToCompare.getClass().isInstance(panelType);
	}

	public void showReadyPanel(DialogWindow childWindow) {
		this.childWindow = childWindow;
		childWindow.getContainer().setVisible(true);
	}

	private boolean childWindowIsClosed() {
		return childWindow == null || !childWindow.getContainer().isVisible();
	}

	public boolean showConfirmDialog(String message) {
		createDialog(new ConfirmPanel(message), Titles.CONFIRM_DIALOG, true, Position.CENTER);
		return isAccepted();
	}

	public void setPosition(Position position) {
		this.position = position;
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

	public DialogWindow getParent() {
		return parentWindow;
	}

	public void maximize (){
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				container.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().
						getMaximumWindowBounds());
			}
		});

	}

}
