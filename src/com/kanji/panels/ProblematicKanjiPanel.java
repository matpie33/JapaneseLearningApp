package com.kanji.panels;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.RowMaker;
import com.kanji.Row.KanjiWords;
import com.kanji.actions.CommonActionsMaker;
import com.kanji.constants.ButtonsNames;
import com.kanji.fileReading.ExcelReader;
import com.kanji.windows.ApplicationWindow;
import com.kanji.windows.DialogWindow;

public class ProblematicKanjiPanel implements PanelCreator {
	private DialogWindow parentDialog;
	private KanjiWords kanjiInfos;
	private int repeatedProblematics;
	private Set<Integer> problematicKanjis;
	private MainPanel main;
	private List<KanjiRow> kanjisToBrowse;
	private JScrollPane scrollPane;
	private boolean useInternet;
	private ExcelReader excel;

	private class KanjiRow {
		private MainPanel panel;
		private int id;

		private KanjiRow(MainPanel p, int id) {
			panel = p;
			this.id = id;
		}

		private MainPanel getPanel() {
			return panel;
		}

		private int getId() {
			return id;
		}
	}

	public ProblematicKanjiPanel(KanjiWords kanjis, Set<Integer> problematicKanji) {
		kanjisToBrowse = new ArrayList<>();
		main = new MainPanel(BasicColors.OCEAN_BLUE);
		kanjiInfos = kanjis;
		problematicKanjis = problematicKanji;
		useInternet = true;
		this.excel = new ExcelReader();
		excel.load();
		// TODO better use existing excel instead of
		// creating new here
	}

	@Override
	public void setParentDialog(DialogWindow parent) {
		if (parentDialog == null) {
			parentDialog = parent;
		}

	}

	@Override
	public JPanel createPanel() {
		if (main.getNumberOfRows() > 0) {
			System.out.println("already exists");
			return main.getPanel();
		}

		JRadioButton withInternet = new JRadioButton("Z internetem");
		JRadioButton withoutInternet = new JRadioButton("Bez internetu");
		ButtonGroup group = new ButtonGroup();
		group.add(withInternet);
		group.add(withoutInternet);
		withInternet.setFocusable(false);
		withoutInternet.setFocusable(false);
		withInternet.setSelected(true);
		withInternet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useInternet = true;
			}
		});
		withoutInternet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				useInternet = false;
			}
		});
		main.addRow(
				RowMaker.createUnfilledRow(GridBagConstraints.WEST, withInternet, withoutInternet));
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
			KanjiRow k = new KanjiRow(panel, i);

			JButton button = new JButton("Przejdź do źródła");
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					spaceBarPressed(k);

				}
			});
			button.setFocusable(false);

			panel.addRow(RowMaker.createHorizontallyFilledRow(kanji, id, button)
					.fillHorizontallySomeElements(kanji));

			kanjisToBrowse.add(k);

			panelInScrollPane.addRow(RowMaker.createHorizontallyFilledRow(panel.getPanel()));
			c.gridy++;

		}

		main.addRow(RowMaker.createBothSidesFilledRow(scrollPane));

		JButton button = CommonActionsMaker.createButtonDispose(ButtonsNames.buttonApproveText,
				java.awt.event.KeyEvent.VK_ESCAPE, parentDialog);

		AbstractAction a = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!kanjisToBrowse.isEmpty()) {
					spaceBarPressed(kanjisToBrowse.get(0));
				}
				else {
					parentDialog.showMsgDialog("Koniec");
				}
			}
		};
		parentDialog.addHotkeyToWindow(KeyEvent.VK_SPACE, a);
		// TODO create a variable how many rows should be initially then just
		// add so many rows and use that size as preferred,then remove the rows
		parentDialog.getContainer().setPreferredSize(new Dimension(600, 400));
		parentDialog.getContainer().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				hideProblematics();
			}
		});

		main.addRow(RowMaker.createUnfilledRow(GridBagConstraints.CENTER, button));

		return main.getPanel();
	}

	private void hideProblematics() {
		assert (parentDialog.getParent() instanceof ApplicationWindow);
		ApplicationWindow parent = (ApplicationWindow) parentDialog.getParent();
		parent.addButtonIcon();
		if (allProblematicKanjisRepeated()) {
			parent.removeButtonProblematicsKanji();
			parentDialog.getContainer().dispose();
		}

	}

	private void browseKanji(MainPanel panelWithKanji) {
		// TODO dont extract id from panel use my private class kanji row

		String uriText = "http://kanji.koohii.com/study/kanji/";
		JLabel id = (JLabel) panelWithKanji.getElementFromRow(0, 1);
		uriText += id.getText();
		URI uriObject = constructUriFromText(uriText, parentDialog);
		if (uriObject != null) {
			openUrlInBrowser(uriObject, parentDialog);

		}
	}

	private void highlightRow(KanjiRow row) {
		MainPanel panelWithKanji = row.getPanel();
		kanjisToBrowse.remove(row);
		scrollPane.getVerticalScrollBar().setValue(
				(int) Math.floor(panelWithKanji.getPanel().getParent().getBounds().getY()));
		panelWithKanji.setBackground(BasicColors.OCEAN_BLUE);
	}

	public void spaceBarPressed(KanjiRow k) {

		repeatedProblematics++;
		highlightRow(k);
		if (useInternet) {
			browseKanji(k.getPanel());
		}
		else {
			parentDialog.showKanjiDialog(excel.getKanjiById(k.getId()));
		}

	}

	private void openUrlInBrowser(URI uriObject, DialogWindow frame) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uriObject);
			}
			catch (IOException ex) {
				ex.printStackTrace();
				frame.showMsgDialog("buuu");
			}
		}
		else {
			frame.showMsgDialog("buuu"); // TODO change the message properly
		}
	}

	private URI constructUriFromText(String text, DialogWindow frame) {
		URI uriObject = null;
		try {
			uriObject = new URI(text);
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
			frame.showMsgDialog("error");
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
