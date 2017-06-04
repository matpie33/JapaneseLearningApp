package com.kanji.dialogs;

import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
	private List<MainPanel> kanjisToBrowse;
	private JScrollPane scrollPane;

	public ProblematicKanjiPanel(JPanel panel, MyDialog parent, KanjiWords kanjis,
			Set<Integer> problematicKanji) {
		kanjisToBrowse = new ArrayList<>();
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
		scrollPane = new JScrollPane(panelInScrollPane.getPanel());
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		// pane.setPreferredSize(new Dimension(400, 200));
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;

		for (Integer i : problematicKanjis) {
			final MainPanel panel = new MainPanel(BasicColors.DARK_BLUE);
			final JLabel id = new JLabel(i.toString());
			JTextArea kanji = new JTextArea(1, 30);
			kanji.setLineWrap(true);
			kanji.setOpaque(false);
			kanji.setEditable(false);
			kanji.setWrapStyleWord(true);
			kanji.setText(kanjiInfos.getWordForId(i));

			JButton button = new JButton("Przejdź do źródła");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					browseKanji(panel);
				}
			});
			button.setFocusable(false);

			panel.addRow(RowMaker.createHorizontallyFilledRow(kanji, id, button)
					.fillHorizontallySomeElements(kanji));
			kanjisToBrowse.add(panel);

			panelInScrollPane.addRow(RowMaker.createHorizontallyFilledRow(panel.getPanel()));
			c.gridy++;

		}

		main.addRow(RowMaker.createBothSidesFilledRow(scrollPane));

		JButton button = parentDialog.createButtonHide(ButtonsNames.buttonApproveText,
				javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), this);
		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, button));

		return main.getPanel();
	}

	private void browseKanji(MainPanel panelWithKanji) {
		kanjisToBrowse.remove(panelWithKanji);

		scrollPane.getVerticalScrollBar().setValue(
				(int) Math.floor(panelWithKanji.getPanel().getParent().getBounds().getY()));

		panelWithKanji.setBackground(BasicColors.OCEAN_BLUE);
		String uriText = "http://kanji.koohii.com/study/kanji/";
		JLabel id = (JLabel) panelWithKanji.getElementFromRow(0, 1);
		uriText += id.getText();
		URI uriObject = constructUriFromText(uriText, parentDialog);
		if (uriObject != null) {
			openUrlInBrowser(uriObject, parentDialog);
			repeatedProblematics++;
		}
	}

	public void spaceBarPressed() {
		if (!kanjisToBrowse.isEmpty())
			browseKanji(kanjisToBrowse.get(0));
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
