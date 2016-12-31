package com.kanji.graphicInterface;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.kanji.rowMaker.HorizontallyFilledRow;
import com.kanji.rowMaker.SimpleRow;

public class MainPanel {
	
	private List<JPanel> rows;
	private JPanel panel;
	private int gapInRow = 5;
	private int gapBetweenRow = 3;
	private final boolean shouldPutRowsHighestAsPossible;

	public MainPanel(Color color) {
		this(color,false);		
	}
	
	public MainPanel(Color color, boolean putRowsHighestAsPossible){
		shouldPutRowsHighestAsPossible = putRowsHighestAsPossible;
		panel = new JPanel();
		panel.setBackground(color);
		panel.setLayout(new GridBagLayout());
		rows = new LinkedList<JPanel>();
	}
	
	
	public HorizontallyFilledRow createHorizontallyFilledRow(JComponent ... components){
		return new HorizontallyFilledRow(false,components);
	}

	public SimpleRow createVerticallyFilledRow(JComponent ... components){
		return new SimpleRow(true,components);
	}
	
	public HorizontallyFilledRow createBothSidesFilledRow(JComponent ... components){
		return new HorizontallyFilledRow(true, components);
	}
	
	public SimpleRow createUnfilledRow(int anchor, JComponent ... components){
		SimpleRow s = new SimpleRow(false, components);
		s.setAnchor(anchor);
		return s;
	}
	
	public JPanel addRow(SimpleRow row){		
		JPanel panel = addComponentsToSinglePanel (row.getComponents(), mapComponentToFilling(row));
		int fill = row.getFillingType();
		System.out.println(row);
		createConstraintsAndAdd(panel, row.getAnchor(), fill );
		updateView();
		return panel;
	}
	
	private Map <JComponent, Integer> mapComponentToFilling(SimpleRow row){
		Map <JComponent, Integer> componentsFilling = new HashMap <JComponent, Integer> ();
		JComponent [] horizontal = row.getHorizontallyFilledElements();
		List <JComponent> vertical = new ArrayList <JComponent> 
			(Arrays.asList(row.getVerticallyFilledElements()));
		
		if (row.getComponents().length==1){
			componentsFilling.put(row.getComponents()[0], row.getFillingType());
		}

		for (JComponent hor: horizontal){
			boolean bothSides=false;
			for (JComponent ver: vertical){
				if (hor==ver){
					componentsFilling.put(hor, GridBagConstraints.BOTH);
					System.out.println("both sides: "+hor.getClass());
					vertical.remove(ver);
					bothSides=true;
					break;
				}
			}
			if (!bothSides){
				componentsFilling.put(hor, GridBagConstraints.HORIZONTAL);
				System.out.println("horizontal: "+hor.getClass());
			}
			
		}
		for (JComponent v: vertical){
			componentsFilling.put(v, GridBagConstraints.VERTICAL);
			System.out.println("vertical: "+v.getClass());
		}
		return componentsFilling;
	}
	
	private JPanel addComponentsToSinglePanel(JComponent [] components, 
			Map<JComponent, Integer> componentsFilling) {
		JPanel p = new JPanel();
		p.setBackground(Color.CYAN);
		p.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;

		int a = gapBetweenRow;
		gbc.insets = new Insets(a, a, a, a);
		for (JComponent compo: components){
			if (componentsFilling.containsKey(compo)){
				gbc.fill = componentsFilling.get(compo);
				if (gbc.fill == GridBagConstraints.VERTICAL){
					gbc.weighty=1;
				}
				else if (gbc.fill == GridBagConstraints.HORIZONTAL){
					gbc.weightx=1;
				}
				else if (gbc.fill == GridBagConstraints.BOTH){
					gbc.weightx=1;
					gbc.weighty=1;
				}
				
			}
			
			p.add(compo,gbc);
		}
		
		return p;
	}

	private void createConstraintsAndAdd(JPanel p, int anchor, int fill) {
		GridBagConstraints c = new GridBagConstraints();		
		c.gridy=rows.size();
		c.weightx = 1;
			if (fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL){
				c.weighty=1;
			}
			else{
				c.weighty=0;
			}
		if (shouldPutRowsHighestAsPossible){
			updateRowsAboveMe();
			c.weighty=1;
			c.anchor = GridBagConstraints.NORTHWEST;
			if (c.fill == GridBagConstraints.BOTH){
				c.fill = GridBagConstraints.HORIZONTAL;
			}
		}
		
		c.anchor= anchor;
		c.fill=fill;
		int a = gapInRow;
		c.insets=new Insets(a,a,a,a);
		panel.add(p, c);
		rows.add(p);
	}
	
	private void updateRowsAboveMe(){
		if (rows.size()>0){
			GridBagLayout g = (GridBagLayout)panel.getLayout();
			GridBagConstraints c = g.getConstraints(rows.get(rows.size()-1));
			c.weighty=0;
			panel.remove(rows.get(rows.size()-1));
			panel.add(rows.get(rows.size()-1), c);
		}
	}
	
		
	
	public void setAsLastRow (JComponent ... components){
		setAsRow(rows.size(),components);
	}

	public MainPanel setAsRow(int number, JComponent... components) {
    	if (rows.size()<number+1){
//		    createRow(GridBagConstraints.WEST, 1, components);
		    return this;
		}
	    
		GridBagLayout g = (GridBagLayout) panel.getLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel row = rows.get(number);
		c = g.getConstraints(row);
		
		panel.remove(row);
		rows.remove(row);
		
		JPanel asRow = new JPanel();
//				addComponentsToSinglePanel(GridBagConstraints.NONE, components);
		panel.add(asRow, c);
		rows.add(number, asRow);
		updateView();
		return this;
	}
	
	public void removeRow (int number){
    	JPanel row = rows.get(number);
    	removeAndUpdateRows(row, number);   	
	}
	
	public void addElementsToRow (JPanel row, JComponent ... elements){
		System.out.println("layout: "+row.getLayout());
		System.out.println("last element: "+row.getComponent(0));
		
		GridBagConstraints c = ((GridBagLayout)row.getLayout()).getConstraints(
				row.getComponent(0));
		for (JComponent element: elements){
//			c.gridx++;
			System.out.println("c. gridx: "+c.gridx+ " y "+c.gridy);
		
			row.add(element,c); //TODO why it works?
		}
		
	}
	
	public void removeRow (JPanel row){
		removeAndUpdateRows(row, rows.indexOf(row));
	}
	
	private void removeAndUpdateRows(JPanel row, int lastRowToUpdate){
		movePanels(Direction.BACKWARD, lastRowToUpdate);
    	panel.remove(row);
		rows.remove(row);
		updateView();
	}
	
	private enum Direction {
	    FORWARD,BACKWARD;
	}
	
	private void movePanels (Direction direction, int startIndex){
	    for (int i=rows.size()-1; i>=startIndex; i--){
	    	GridBagLayout g = (GridBagLayout) panel.getLayout();
		JPanel row = rows.get(i);		
		GridBagConstraints c = g.getConstraints(row);
		if (direction.equals(Direction.FORWARD)){
		    c.gridy++;
		}
		else if (direction.equals(Direction.BACKWARD)){
		    c.gridy--;
		}
		
		panel.remove(row);
		panel.add(row, c);
    	    }
	}
	
	public SubPanel divideRow (int number){
		SubPanel p = new SubPanel ();
		rows.add(p.getPanel());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy=number;
		c.weightx=1;
		c.weighty=1;
		panel.add(p.getPanel(), c);
		return p;
	}
	
	
	public void insertRow (int number, JComponent ... components){
	    movePanels(Direction.FORWARD,number);	    	
		JPanel newRow = new JPanel();
//				addComponentsToSinglePanel(GridBagConstraints.NONE, components);
		GridBagConstraints c = new GridBagConstraints();
		c.gridy=number;
		c.weightx=1;
		c.weighty=1;
		panel.add(newRow, c);
		rows.add(number, newRow);	
		updateView();
	}
	
	public int getNumberOfRows(){
	    return rows.size();
	}
	
	public List <JPanel> getRows(){
	    return rows;
	}
	
	private void updateView(){
		panel.repaint();
		panel.revalidate();
	}
	
	public JPanel getPanel(){
		return panel;
	}
	
}
