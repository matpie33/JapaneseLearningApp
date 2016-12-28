package com.kanji.graphicInterface;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.kanji.Row.KanjiWords;
import com.kanji.myList.MyList;
import com.kanji.myList.SearchOptions;
import com.kanji.panels.ConfirmPanel;
import com.kanji.panels.InsertWordPanel;
import com.kanji.panels.LearningStartPanel;
import com.kanji.panelsLogic.LearningStartLogic;

public class ActionMaker {
	
	public static AbstractAction startLearning (final LearningStartLogic logic){
		return new AbstractAction (){
			@Override
			public void actionPerformed(ActionEvent e){
			
					logic.validateInputs();
					logic.addToRepeatsListOrShowError();

//					if (!excelReaderIsLoaded()) {
//						parentFrame.showMessageDialog(ExceptionsMessages.excelNotLoaded,true);
//						waitUntillExcelLoads();
//					} else
//						switchToRepeatingPanel();

				
			}
		};
	}
	
	public static AbstractAction addNewRow(final LearningStartPanel panel){
		return new AbstractAction (){
			@Override
			public void actionPerformed(ActionEvent e){
				panel.addRowToPanel();
				
			}
		};
	}
	
	public static AbstractAction createDeletingRowAction(final MainPanel panel, final JPanel rowToDelete, 
			final LearningStartLogic logic){
		return new AbstractAction (){
			@Override
			public void actionPerformed(ActionEvent e){
				logic.deleteRow(panel, rowToDelete);
			}
		};
	}
	
	public static AbstractAction createConfirmingAction (final ConfirmPanel panel, final boolean chosen){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.setAccepted(chosen);
			}
		};
	}

	public static WindowListener createClosingListener (final SimpleWindow window){
		return new WindowAdapter (){
			@Override
			public void windowClosed(WindowEvent e){
				window.close();
			}
		};
	}
	
    public static AbstractAction createDisposingAction(final Window dialog){
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}
		};
    }
    
    public static AbstractAction createFullWordsSearchOption(final SearchOptions options){
    	return new AbstractAction (){
    		@Override
    		public void actionPerformed(ActionEvent e){
    			options.enableMatchByWordOnly();
    		}
    	};
    }
    
    public static AbstractAction createPerfectMatchSearchOption(final SearchOptions options){
    	return new AbstractAction (){
    		@Override
    		public void actionPerformed(ActionEvent e){
    			options.enableMatchByExpressionOnly();
    		}
    	};
    }
    
    public static AbstractAction createDefaultSearchOption(final SearchOptions options){
    	return new AbstractAction (){
    		@Override
    		public void actionPerformed(ActionEvent e){
    			options.setDefaultOption();
    		}
    	};
    }
    
    public static AbstractAction createActionAddProblematicKanjis (final LearningStartLogic logic){
    	return new AbstractAction (){
    		@Override
    		public void actionPerformed(ActionEvent e){
    			logic.addProblematicKanjis();
    		}
    	};
    }
    
    @SuppressWarnings("serial")
	public static AbstractAction createValidatingAction (final JTextField wordField, final JTextField numberField,
    		final MyList list, final InsertWordPanel panel){
    	return new AbstractAction(){		
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(numberField);
				String wordInput = wordField.getText();
				String numberInput = numberField.getText();
				if (isNumberValid(numberInput)) {
					int number = Integer.parseInt(numberInput);
					if (((KanjiWords) list.getWords()).isInputValid(wordInput, number)) {
						panel.updateGUI(wordInput, number);						
					}

				}
				System.out.println("number invalid");

			}
		};
    }
    
    private static boolean isNumberValid(String number){
    	return number.matches("\\d+");
    }
    
}
