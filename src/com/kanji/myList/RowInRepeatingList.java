package com.kanji.myList;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.guimaker.colors.BasicColors;
import com.guimaker.panels.MainPanel;
import com.guimaker.row.Anchor;
import com.guimaker.row.RowMaker;
import com.kanji.Row.RepeatingInformation;
import com.kanji.constants.Prompts;

public class RowInRepeatingList implements RowsCreator<RepeatingInformation> {

	private Color defaultColor = Color.RED;
	private MyList<RepeatingInformation> list;
	private int rowsCounter;
	private MainPanel panel;
	private ListWordsController<RepeatingInformation> controller;

	// TODO refactor this class

	public RowInRepeatingList(MyList<RepeatingInformation> list) {
		this.list = list;
		rowsCounter = 1;
		panel = new MainPanel(BasicColors.VERY_BLUE, true);
		controller = new ListWordsController<>();
	}

	@Override
	public JPanel createRow(RepeatingInformation rep) {
		String word = rep.getRepeatingRange();
		String time = rep.getTimeSpentOnRepeating();
		Date date1 = rep.getRepeatingDate();

		String rowNumber = "" + rowsCounter++ + ".";
		JLabel repeatedWords = createLabel(Prompts.repeatingWordsRangePrompt + word);

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		JLabel date = createLabel(
				rowNumber + " " + Prompts.repeatingDatePrompt + sdf.format(date1) + ".");
		date.setForeground(BasicColors.OCEAN_BLUE);
		JLabel timeSpent = null;

		if (time != null) {
			timeSpent = createLabel(Prompts.repeatingTimePrompt + time);
		}

		JButton delete = createButtonRemove();

		MainPanel panel = new MainPanel(null);
		panel.addRow(RowMaker.createHorizontallyFilledRow(date));
		// TODO add in main panel method for creating a series of rows in one
		// line
		panel.addRow(RowMaker.createHorizontallyFilledRow(repeatedWords));
		if (timeSpent != null) {
			panel.addRow(RowMaker.createHorizontallyFilledRow(timeSpent));
		}

		panel.addRow(RowMaker.createUnfilledRow(Anchor.WEST, delete));
		JPanel wrappingPanel = this.panel
				.addRow(RowMaker.createHorizontallyFilledRow(panel.getPanel()));
		wrappingPanel
				.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BasicColors.LIGHT_BLUE));
		addActionListener(delete, wrappingPanel, rep);
		return this.panel.getPanel();

	}

	private JPanel createPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBackground(defaultColor);
		return panel;
	}

	private JLabel createLabel(String word) {
		JLabel l1 = new JLabel(word);
		l1.setForeground(Color.WHITE);
		return l1;
	}

	private JTextArea createTextArea(String text) {

		JTextArea elem = new JTextArea(text);
		elem.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					FileOutputStream fout = new FileOutputStream("hi.txt");
					ObjectOutputStream oos = new ObjectOutputStream(fout);
					oos.writeObject(list.getWords());
					fout.close();
					System.out.println("save");
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		elem.setLineWrap(true);
		elem.setWrapStyleWord(true);
		elem.setOpaque(true);
		return elem;
	}

	private JTextArea createDateArea(Date date) {
		JTextArea textArea = createTextArea("");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();

		textArea.setText(sdf.format(date));
		// textArea.setText(date+"");
		textArea.setEditable(false);

		return textArea;
	}

	private void addComponentsToPanel(JPanel panel, List<Component> components) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		int a = 5;
		c.insets = new Insets(a, a, a, a);
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;

		int componentNumber = 1;
		for (Component component : components) {
			c.anchor = setPosition(componentNumber, components.size());
			panel.add(component, c);
			c.gridy++;
			componentNumber++;
		}
	}

	private int setPosition(int componentNumber, int numberOfComponents) {
		int anchor = 0;
		// if (componentNumber == 1)
		anchor = GridBagConstraints.WEST;
		// else if (componentNumber == numberOfComponents)
		// anchor = GridBagConstraints.EAST;
		// else
		// anchor = GridBagConstraints.CENTER;
		return anchor;
	}

	private JButton createButtonRemove() {
		JButton remove = new JButton("-");
		return remove;
	}

	private void addActionListener(JButton button, JPanel panel, final RepeatingInformation kanji) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!list.showMessage(String.format(Prompts.deleteElementPrompt,
						Prompts.repeatingElementPrompt))) {
					return;
				}

				removeRow(panel);
				list.getWords().remove(kanji);
				list.save();
			}
		});
	}

	@Override
	public void setList(MyList<RepeatingInformation> list) {
		this.list = list;
	}

	private void removeRow(JPanel row) {
		panel.removeRow(row);
	}

	@Override
	public ListWordsController<RepeatingInformation> getController() {
		return controller;
	}

	public JPanel getPanel() {
		return panel.getPanel();
	}

	@Override
	public void highlightRowAndScroll(int rowNumber) {
		panel.getRows().get(rowNumber).setBackground(Color.red);
	}

	@Override
	public int getHighlightedRowNumber() {
		return -1000;
	}

	@Override
	public void scrollToBottom() {

	}

	@Override
	public JScrollPane getScrollPane() {
		return new JScrollPane(panel.getPanel());
	}

}
