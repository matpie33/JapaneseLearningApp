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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.kanji.constants.Prompts;
import com.kanji.row.RepeatingInformation;
import com.kanji.row.RepeatingList;

public class RowInRepeatingList extends RowsCreator<RepeatingInformation> {

	private Color defaultColor = Color.RED;
	private MyList<RepeatingList> list;
	private int rowsCounter;

	public RowInRepeatingList(MyList<RepeatingList> list) {
		this.list = list;
		rowsCounter = 1;
	}

	@Override
	public JPanel addWord(RepeatingInformation rep, int rowsNumber) {
		String word = rep.getRepeatingRange();
		String time = rep.getTimeSpentOnRepeating();
		Date date1 = rep.getRepeatingDate();
		JPanel rowPanel = createPanel();

		String rowNumber = "" + rowsCounter++ + ".";
		JLabel repeatedWords = createLabel(
				rowNumber + " " + Prompts.repeatingWordsRangePrompt + word + ",");
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
		JLabel date = createLabel(Prompts.repeatingDatePrompt + sdf.format(date1) + ",");
		JLabel timeSpent = null;

		List<Component> components = new ArrayList<>();
		components.add(repeatedWords);
		components.add(date);

		if (time != null) {
			timeSpent = createLabel(Prompts.repeatingTimePrompt + time);
			components.add(timeSpent);
		}

		JButton delete = createButtonRemove(rowPanel, rep);
		components.add(delete);
		addComponentsToPanel(rowPanel, components);

		return rowPanel;

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

	private JButton createButtonRemove(final JPanel text, final RepeatingInformation kanji) {
		JButton remove = new JButton("-");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!list.showMessage(String.format(Prompts.deleteElementPrompt,
						Prompts.repeatingElementPrompt))) {
					return;
				}

				list.removeRowContainingTheWord(text);
				list.getWords().remove(kanji);
				list.save();
			}
		});
		return remove;
	}

	@Override
	public void setList(MyList list) {
		this.list = list;
	}

}
