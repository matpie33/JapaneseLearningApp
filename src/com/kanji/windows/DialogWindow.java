package com.kanji.windows;

import com.guimaker.application.ApplicationConfiguration;
import com.kanji.application.ApplicationChangesManager;
import com.kanji.constants.strings.Titles;
import com.guimaker.customPositioning.CustomPositioner;
import com.kanji.list.myList.MyList;
import com.kanji.panelsAndControllers.panels.AbstractPanelWithHotkeysInfo;
import com.kanji.panelsAndControllers.panels.ConfirmPanel;
import com.kanji.panelsAndControllers.panels.InsertWordPanel;
import com.kanji.panelsAndControllers.panels.MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class DialogWindow {

	protected DialogWindow childWindow;
	protected CustomPositioner customPositioner;
	private JPanel mainPanel;
	private DialogWindow parentWindow;
	private boolean isAccepted;
	private Position position;
	private JDialog container;
	private AbstractPanelWithHotkeysInfo panel;
	protected ApplicationChangesManager applicationChangesManager;

	private void setCustomPositioner(CustomPositioner customPositioner) {
		this.customPositioner = customPositioner;
	}

	public enum Position {
		CENTER, LEFT_CORNER, CUSTOM, NEXT_TO_PARENT
	}

	public DialogWindow(DialogWindow parent,
			ApplicationChangesManager applicationChangesManager) {
		this.applicationChangesManager = applicationChangesManager;
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
		container.getInputContext().selectInputMethod(Locale.getDefault());
		parentWindow = parent;
	}

	public void setPanel(JPanel panel) {
		mainPanel = panel;
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

		case NEXT_TO_PARENT:
			setChildNextToParent(parentWindow.getContainer(), container);
			break;
		case CUSTOM:
			container.setLocation(customPositioner.getPosition());
		}
	}

	protected void setChildNextToParent(Window parentContainer,
			Window childContainer) {
		Point parentLocation = parentContainer.getLocationOnScreen();
		Dimension parentSize = parentContainer.getSize();
		childContainer.setLocation(parentLocation.x + parentSize.width,
				parentLocation.y);
	}

	public void showMessageDialog(String message) {
		showMessageDialog(message, true);
	}

	public void showMessageDialog(String message, boolean modal) {
		createDialog(new MessagePanel(message), Titles.MESSAGE_DIALOG, modal,
				Position.CENTER);
	}

	public void createDialog(AbstractPanelWithHotkeysInfo panelCreator,
			String title, boolean modal, Position position) {
		if (!isDialogOfSameType(panelCreator) || childWindowIsClosed()) {
			if (!getContainer().isVisible()) {
				return;
			}
			childWindow = new DialogWindow(this, applicationChangesManager);
			childWindow.setPanel(panelCreator);

			if (position.equals(Position.CUSTOM)) {
				childWindow.setCustomPositioner(customPositioner);
			}
			panelCreator.setParentDialog(childWindow);
			childWindow.setPosition(position);
			JPanel panel = panelCreator.createPanel();
			childWindow.setPanel(panel);
			if (panelCreator.isMaximized()) {
				childWindow.maximize();
			}
			childWindow.showYourself(title, modal);
			childWindow.getContainer()
					.setMinimumSize(childWindow.getContainer().getSize());
			panelCreator.afterVisible();
		}
	}

	private boolean isDialogOfSameType(
			AbstractPanelWithHotkeysInfo panelTypeToCompare) {
		return panelTypeToCompare.getClass().isInstance(panel);
	}

	private boolean childWindowIsClosed() {
		return childWindow == null || !childWindow.getContainer().isVisible();
	}

	public void showInsertWordDialog(MyList myList,
			CustomPositioner customPositioner) {
		AbstractPanelWithHotkeysInfo panel = new InsertWordPanel<>(myList,
				applicationChangesManager);
		setCustomPositioner(customPositioner);
		createDialog(panel, Titles.INSERT_WORD_DIALOG, false, Position.CUSTOM);
	}

	public boolean showConfirmDialog(String message) {
		createDialog(new ConfirmPanel(message), Titles.CONFIRM_DIALOG, true,
				Position.CENTER);
		return isAccepted();
	}

	private void setPosition(Position position) {
		this.position = position;
	}

	public void setAccepted(boolean accepted) {
		this.isAccepted = accepted;
	}

	private boolean isAccepted() {
		return childWindow.isAccepted;
	}

	public Window getContainer() {
		return container;
	}

	private void maximize() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				container.setBounds(
						GraphicsEnvironment.getLocalGraphicsEnvironment().
								getMaximumWindowBounds());
			}
		});

	}

	public AbstractPanelWithHotkeysInfo getPanel() {
		return panel;
	}

	public void setPanel(AbstractPanelWithHotkeysInfo panel) {
		this.panel = panel;
	}

	public ApplicationConfiguration getParentConfiguration (){
		if (this instanceof ApplicationWindow){
			return ((ApplicationWindow)this).getApplicationConfiguration();
		}
		DialogWindow parent = parentWindow;
		while (!(parent instanceof ApplicationWindow)){
			parent = parent.parentWindow;
		}
		return ((ApplicationWindow)parent).getApplicationConfiguration();

	}

}
