package com.kanji.fileReading;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kanji.constants.TextValues;

public class CustomFileReader {
	
	private Map <String, Integer> keywords;
	private File readedFile;
	
	public Map <String, Integer> readFile(File file) throws Exception{
		
		readedFile=file;
		try{
			keywords = findWordsAndIDs(file);
			return keywords;			
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}		
		return new HashMap <String,Integer>();
		
	}
	
	private Map <String, Integer> findWordsAndIDs(File file) throws Exception{
		
		keywords = new LinkedHashMap <String, Integer>();
		BufferedReader in = new BufferedReader(new InputStreamReader(				   
                new FileInputStream(file), "UTF8"));		

		String line;
		while ((line = in.readLine()) != null) {		    	
		
			line = line.trim();
			int i=moveCursorToDigit(line);
			String wordId = getWordId(i, line);
			i=moveCursorToLetterOrDigit(i,line);		    	
			String word=getWord(i,line);
			int wordIdInt = Integer.parseInt(wordId);
			
			if (keywords.containsKey(word))	    
				openDesktopAndShowMessage(word, wordIdInt);
			else
				keywords.put(word, wordIdInt);
		}
			
		in.close();
		return keywords;
	}
	
	private int moveCursorToDigit(String line){
		int i=line.length()-1;
		while (!(line.charAt(i)+"").matches("\\d"))
    		i--;
		return i;
	}
	
	private String getWordId(int i, String line){
		int iterator=i;
		while ((line.charAt(i)+"").matches("\\d")){    				
    		i--;
    	}
		return line.substring(i+1, iterator+1);
	}
	
	private int moveCursorToLetterOrDigit(int startIndex, String line){
		
		while (!(line.charAt(startIndex)+"").matches("[a-zA-ZøüÊÒÛ≥ÍπúØè∆•å £”—]|\\d"))			
    		startIndex--;		
		return startIndex;
	}
	
	private String getWord (int i, String line){
		return line.substring(0,i);
	}
	
	private void openDesktopAndShowMessage(String duplicatedWord, int wordId) throws Exception{
		Desktop.getDesktop().open(readedFile);		
		throw new Exception (TextValues.duplicatedWordException +
				"S≥owo to: "+duplicatedWord+"; a numer to "		    	
		+wordId + " oraz "+keywords.get(duplicatedWord));
	}
	

}
