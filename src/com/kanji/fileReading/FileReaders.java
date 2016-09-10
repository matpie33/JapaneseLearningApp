package com.kanji.fileReading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileReaders {
	
	
	public Map <Integer,String> readFile(File file){
//		List <String> words = new ArrayList <String> ();
		Map <Integer,String> keywords = new HashMap <Integer,String>();
		try{
			
			BufferedReader in = new BufferedReader(new InputStreamReader(				   
			                      new FileInputStream(file), "UTF8"));			
		    String line;
		    while ((line = in.readLine()) != null) {
		    	
		    	String wordId=""; 
		    	line = line.trim();
		    	int i=line.length()-1;
		    	System.out.println("trimmed: "+line);
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
		    	System.out.println(word);
		    	int wordIdInt = Integer.parseInt(wordId);
		    	keywords.put(wordIdInt, word);
//		    	words.add(word);
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
