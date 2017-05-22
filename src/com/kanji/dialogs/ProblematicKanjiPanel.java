package com.kanji.dialogs;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.kanji.Row.KanjiWords;
import com.kanji.constants.ButtonsNames;

public class ProblematicKanjiPanel {
	private JPanel mainPanel;
	private GridBagConstraints layoutConstraints;
	private MyDialog parentDialog;
	private KanjiWords kanjiInfos;

	public ProblematicKanjiPanel(JPanel panel, MyDialog parent, KanjiWords kanjis) {
		mainPanel = panel;
		parentDialog = parent;
		layoutConstraints = new GridBagConstraints();
		kanjiInfos = kanjis;
	}

	public void setLayoutConstraints(GridBagConstraints c) {
		layoutConstraints = c;
	}

	public JPanel createPanel(Set<Integer> problematicKanjis) {

		int level = 0;
		layoutConstraints.gridy = 0;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;

		JPanel panelInScrollPane = new JPanel();
		panelInScrollPane.setMaximumSize(new Dimension(30, 30));
		panelInScrollPane.setLayout(new GridBagLayout());
		panelInScrollPane.setOpaque(false);
		JScrollPane pane = new JScrollPane(panelInScrollPane);
		pane.setOpaque(false);
		pane.getViewport().setOpaque(false);
		pane.setPreferredSize(new Dimension(400, 200));
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;

		for (Integer i : problematicKanjis) {

			final JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints c1 = new GridBagConstraints();
			c1.gridx = 0;
			c1.gridy = 0;
			c.weightx = 1;
			panel.setOpaque(false);
			final JLabel id = new JLabel(i.toString());
			JLabel kanji = new JLabel(kanjiInfos.getWordForId(i));

			JButton button = new JButton("Przejdź do źródła");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.setOpaque(true);
					panel.setBackground(Color.red);
					String uriText = "http://kanji.koohii.com/study/kanji/";
					uriText += id.getText();
					URI uriObject = constructUriFromText(uriText, parentDialog);
					if (uriObject != null) {
						openUrlInBrowser(uriObject, parentDialog);
					}

				}
			});
			c1.anchor = GridBagConstraints.WEST;
			int a = 10;
			c1.insets = new Insets(a, a, a, a);
			c1.fill = GridBagConstraints.HORIZONTAL;
			panel.add(kanji, c1);
			c1.fill = GridBagConstraints.NONE;
			c1.gridx++;
			panel.add(id, c1);
			c1.gridx++;
			panel.add(button, c1);

			panelInScrollPane.add(panel, c);
			c.gridy++;

		}
		mainPanel.add(pane);

		JButton button = parentDialog.createButtonDispose(ButtonsNames.buttonApproveText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
		layoutConstraints.fill = GridBagConstraints.NONE;
		layoutConstraints.anchor = GridBagConstraints.CENTER;
		layoutConstraints.gridy++;
		mainPanel.add(button, layoutConstraints);

		return mainPanel;
	}

	private void openUrlInBrowser(URI uriObject, MyDialog frame) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uriObject);
			}
			catch (IOException ex) {
				ex.printStackTrace();
				frame.showMsgDialog("buuu", true);
			}
		}
		else {
			frame.showMsgDialog("buuu", true);
		}
	}

	private URI constructUriFromText(String text, MyDialog frame) {
		URI uriObject = null;
		try {
			uriObject = new URI(text);
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
			frame.showMsgDialog("error", true);
			return null;
		}
		return uriObject;
	}

	private void addPromptAtLevel(int level, String message) {
		layoutConstraints.gridy = level;
		layoutConstraints.anchor = GridBagConstraints.CENTER;
		layoutConstraints.weightx = 1;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		JTextArea elem = new JTextArea(4, 30);

		elem.setText(message);
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(false);
		elem.setEditable(false);

		mainPanel.add(elem, layoutConstraints);
	}
}
