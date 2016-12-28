package com.kanji.listenersAndAdapters;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kanji.constants.ExceptionsMessages;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panelsLogic.LearningStartLogic;

public class AdaptersMaker {
	
	public static KeyAdapter create(final JTextField from, final JTextField to, LearningStartPanel panel, 
			final JPanel container){
		
		final LearningStartLogic logic = panel.getLogic();		
		KeyAdapter keyAdapter = new KeyAdapter() {

			private String error = "";

			@Override
			public void keyTyped(KeyEvent e) {
				if (!(e.getKeyChar() + "").matches("\\d")) {
					showErrorIfNotExists(ExceptionsMessages.valueIsNotNumber);
					e.consume();
					return;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				int valueFrom = 0;
				int valueTo = 0;
				if (to.getText().isEmpty() || from.getText().isEmpty())
					return;
				else {
					valueFrom = Integer.parseInt(from.getText());
					valueTo = Integer.parseInt(to.getText());
				}

				if (valueTo <= valueFrom) {
					showErrorIfNotExists(ExceptionsMessages.rangeToValueLessThanRangeFromValue);
				} 
				else if (logic.isNumberHigherThanMaximum(valueFrom) || logic.isNumberHigherThanMaximum(valueTo))
					showErrorIfNotExists(ExceptionsMessages.rangeValueTooHigh);
				else {
					removeErrorIfExists();
					logic.recalculateSumOfKanji((JPanel) container.getParent());
				}

			}

			private void showErrorIfNotExists(String message) {
				if (error.equals(message))
					return;
				else
					removeErrorIfExists();

				container.add(new JLabel(message));
				container.repaint();
				container.revalidate();
				error = message;
			}

			private void removeErrorIfExists() {
				if (error.isEmpty())
					return;
				error = "";

				for (Component c : container.getComponents()) {
					if (c instanceof JLabel && ((JLabel) c).getText()
							.matches(ExceptionsMessages.rangeToValueLessThanRangeFromValue + "|"
									+ ExceptionsMessages.valueIsNotNumber + "|"
									+ ExceptionsMessages.rangeValueTooHigh)) {
						container.remove(c);
						container.repaint();
						container.revalidate();
					}
				}
			}

		};
		return keyAdapter;
	}

}
