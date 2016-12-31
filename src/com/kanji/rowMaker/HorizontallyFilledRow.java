package com.kanji.rowMaker;

import javax.swing.JComponent;

public class HorizontallyFilledRow extends SimpleRow{
		
	public HorizontallyFilledRow(boolean verticalFill, JComponent[] components) {		
		super(verticalFill,components);
		enableHorizontalFill();
	}			
	
	public SimpleRow fillHorizontallySomeElements (JComponent ... filledElements){
		fillHorizontally(filledElements);
		return this;
	}
	
	public SimpleRow fillHorizontallyEqually (){
		fillHorizontally(componentsInRow);
		return this;
	}
	
	
	
	
}
