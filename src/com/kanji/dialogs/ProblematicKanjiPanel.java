package com.kanji.dialogs;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
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

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.constants.ButtonsNames;

public class ProblematicKanjiPanel {
	private MyDialog parentDialog;
	private KanjiWords kanjiInfos;
	private int repeatedProblematics;
	private Set<Integer> problematicKanjis;
	private MainPanel main;

	public ProblematicKanjiPanel(JPanel panel, MyDialog parent, KanjiWords kanjis,
			Set<Integer> problematicKanji) {
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		parentDialog = parent;
		kanjiInfos = kanjis;
		problematicKanjis = problematicKanji;
		System.out.println("in constructor: " + problematicKanjis);
	}

	public JPanel createPanel() {
		if (main.getNumberOfRows() > 0) {
			System.out.println("already exists");
			return main.getPanel();
		}
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER,
				new JLabel("Do powtórzenia")));

		MainPanel panelInScrollPane = new MainPanel(BasicColors.LIGHT_BLUE, true);
		panelInScrollPane.setGapsBetweenRowsTo(5);
		JScrollPane pane = new JScrollPane(panelInScrollPane.getPanel());
		pane.setOpaque(false);
		pane.getViewport().setOpaque(false);
		pane.setPreferredSize(new Dimension(400, 200));
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;

		for (Integer i : problematicKanjis) {
			final MainPanel panel = new MainPanel(BasicColors.DARK_BLUE);
			final JLabel id = new JLabel(i.toString());
			JLabel kanji = new JLabel(kanjiInfos.getWordForId(i));

			JButton button = new JButton("Przejdź do źródła");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					panel.setBackground(BasicColors.OCEAN_BLUE);
					String uriText = "http://kanji.koohii.com/study/kanji/";
					uriText += id.getText();
					URI uriObject = constructUriFromText(uriText, parentDialog);
					if (uriObject != null) {
						openUrlInBrowser(uriObject, parentDialog);
						repeatedProblematics++;
					}

				}
			});

			panel.addRow(RowMaker.createHorizontallyFilledRow(kanji, id, button)
					.fillHorizontallySomeElements(kanji));

			panelInScrollPane.addRow(RowMaker.createHorizontallyFilledRow(panel.getPanel()));
			c.gridy++;

		}
		main.addRow(RowMaker.createBothSidesFilledRow(pane));

		JButton button = parentDialog.createButtonHide(ButtonsNames.buttonApproveText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), this);
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, button));

		return main.getPanel();
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

	public boolean allProblematicKanjisRepeated() {
		System.out.println("rep: " + repeatedProblematics + "  prob " + problematicKanjis.size());
		return repeatedProblematics == problematicKanjis.size();
	}

	public void clear() {
		main.clear();
	}

}
