package com.kanji.panelSwitching;

import com.guimaker.enums.MoveDirection;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PanelSwitchingHandler {

	private List<JComponent> panels = new ArrayList<>();

	public List<JComponent> getPanels() {
		return panels;
	}

	public void registerPanel(JComponent panel) {
		panels.add(panel);
	}

	public JComponent findClosestPanelBasedOnDirection(
			JComponent referencePanel, MoveDirection moveDirection) {
		JComponent closestPanel = null;
		for (JComponent panel : panels) {
			if (referencePanel == null){
				return panel;
			}
			if (panel == referencePanel || !panel.isVisible()) {
				continue;
			}
			if (arePanelsEdgesAdequateToBeCompared(referencePanel, panel,
					moveDirection) && isPanelOnTheCorrectSide(referencePanel,
					panel, moveDirection)) {
				if (closestPanel == null || isPanelCloserThanPreviouslyFound(
						panel, closestPanel, moveDirection)) {
					closestPanel = panel;
				}
			}
		}
		return closestPanel;
	}

	private boolean isPanelCloserThanPreviouslyFound(JComponent panel,
			JComponent closestPanel, MoveDirection moveDirection) {
		switch (moveDirection) {
		case RIGHT:
			return panel.getLocationOnScreen().getX() < closestPanel
					.getLocationOnScreen().getX();
		case LEFT:
			return panel.getLocationOnScreen().getX() > closestPanel
					.getLocationOnScreen().getX();
		case ABOVE:
			return panel.getLocationOnScreen().getY() > closestPanel
					.getLocationOnScreen().getY();
		case BELOW:
			return panel.getLocationOnScreen().getY() < closestPanel
					.getLocationOnScreen().getY();

		}
		return false;
	}

	private boolean isPanelOnTheCorrectSide(JComponent referencePanel,
			JComponent panel, MoveDirection moveDirection) {

		switch (moveDirection) {
		case RIGHT:
			return panel.getLocationOnScreen().getX() > referencePanel
					.getLocationOnScreen().getX();
		case LEFT:
			return panel.getLocationOnScreen().getX() < referencePanel
					.getLocationOnScreen().getX();
		case ABOVE:
			return panel.getLocationOnScreen().getY() < referencePanel
					.getLocationOnScreen().getY();
		case BELOW:
			return panel.getLocationOnScreen().getY() > referencePanel
					.getLocationOnScreen().getY();

		}
		return false;
	}

	private boolean arePanelsEdgesAdequateToBeCompared(
			JComponent referencePanel, JComponent panel,
			MoveDirection moveDirection) {
		switch (moveDirection) {
		case LEFT:
		case RIGHT:
			return panel.getLocationOnScreen().getY() == referencePanel
					.getLocationOnScreen().getY()
					|| getBottomLocation(panel) == getBottomLocation(
					referencePanel);
		case ABOVE:
		case BELOW:
			return panel.getLocationOnScreen().getX() == referencePanel
					.getLocationOnScreen().getX();
		}
		return false;
	}

	private double getBottomLocation(JComponent component) {
		return component.getLocationOnScreen().getY() + component.getSize()
				.getHeight();
	}

}
