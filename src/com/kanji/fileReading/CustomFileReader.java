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
	
	
	public Map <String, Integer> readFile(File file) throws Exception{
		
		Map <String, Integer> keywords = new LinkedHashMap <String, Integer>();
		try{
			
			BufferedReader in = new BufferedReader(new InputStreamReader(				   
			                      new FileInputStream(file), "UTF8"));			
		    String line;
		    while ((line = in.readLine()) != null) {
		    	
		    	String wordId=""; 
		    	line = line.trim();
		    	int i=line.length()-1;
		    	while (!(line.charAt(i)+"").matches("\\d"))
		    		i--;
		    	while ((line.charAt(i)+"").matches("\\d")){
		    		wordId=wordId+line.charAt(i);			
		    		i--;
		    	}
		    	wordId = new StringBuilder(wordId).reverse().toString();
		    	
		    	while (!(line.charAt(i)+"").matches("[a-zA-Z¿Ÿæñó³ê¹œ¯Æ¥ŒÊ£ÓÑ]|\\d"))
		    		i--;
		    	String word="";
		    	while (i>=0){
		    		word=word+line.charAt(i);
		    		i--;
		    	}
		    	word=new StringBuilder(word).reverse().toString();
		    	int wordIdInt = Integer.parseInt(wordId);
		    	if (keywords.containsKey(word))	{	    
		    		Desktop.getDesktop().open(file);
		    		throw new Exception (TextValues.duplicatedWordException +
		    				"S³owo to: "+word+"; a numer to "		    	
		    		+wordIdInt + " oraz "+keywords.get(word));
		    	}
		    	keywords.put(word, wordIdInt);
		    }
		    in.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return keywords;
		
	}

}
