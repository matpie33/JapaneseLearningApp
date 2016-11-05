package com.kanji.fileReading;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CustomFileReader
{
  private Map<Integer, String> keywords;
  private File readedFile;
  
  public Map<Integer, String> readFile(File file)
    throws Exception
  {
    this.readedFile = file;
    try
    {
      this.keywords = findWordsAndIDs(file);
      return this.keywords;
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return new HashMap();
  }
  
  private Map<Integer, String> findWordsAndIDs(File file)
    throws Exception
  {
    this.keywords = new LinkedHashMap();
    BufferedReader in = new BufferedReader(new InputStreamReader(
      new FileInputStream(file), "UTF8"));
    String line;
    while ((line = in.readLine()) != null)
    {
      String line1 = line.trim();
      line1 = removeBOMMarker(line1);
      int i = moveCursorToDigit(line1);
      String wordId = getWordId(i, line1);
      i = line1.indexOf(wordId) - 1;
      i = moveCursorToLetterOrDigit(i, line1);
      String word = getWord(i, line1);
      int wordIdInt = Integer.parseInt(wordId);
      if ((!this.keywords.containsKey(Integer.valueOf(wordIdInt))) || (!this.keywords.containsValue(word))) {
        if (this.keywords.containsValue(word)) {
          openDesktopAndShowMessage(word, wordIdInt);
        } else if (this.keywords.containsKey(Integer.valueOf(wordIdInt))) {
          openDesktopAndShowMessage(wordIdInt);
        } else {
          this.keywords.put(Integer.valueOf(wordIdInt), word);
        }
      }
    }
    in.close();
    return this.keywords;
  }
  
  private String removeBOMMarker(String line)
  {
    return line.replace("?", "");
  }
  
  private int moveCursorToDigit(String line)
  {
    int i = line.length() - 1;
    while (!(""+line.charAt(i)).matches("\\d")) {
      i--;
    }
    return i;
  }
  
  private String getWordId(int i, String line)
  {
    int iterator = i;
    while ((""+line.charAt(i)).matches("\\d")) {
      i--;
    }
    return line.substring(i + 1, iterator + 1);
  }
  
  private int moveCursorToLetterOrDigit(int startIndex, String line)
  {
    while (!(""+line.charAt(startIndex)).matches("[a-zA-Z������������������]|\\d")) {
      startIndex--;
    }
    return startIndex;
  }
  
  private String getWord(int i, String line)
  {
    return line.substring(0, i + 1);
  }
  
  private void openDesktopAndShowMessage(int wordId)
    throws Exception
  {
    Desktop.getDesktop().open(this.readedFile);
    throw new Exception("Na li�cie wyst�puje wielokrotnie pewne id.  Nale�y poprawi�.Numer id to: " + 
      wordId);
  }
  
  private void openDesktopAndShowMessage(String duplicatedWord, int wordId)
    throws Exception
  {
    Desktop.getDesktop().open(this.readedFile);
    throw new Exception("Pewne s�owo wyst�puje wielokrotnie na li�cie s��w. Nale�y poprawi�.S�owo to: " + 
      duplicatedWord + "; a numer to " + 
      wordId + " oraz " + getKeywordID(duplicatedWord));
  }
  
  private int getKeywordID(String keyword)
  {
    for (Iterator localIterator = this.keywords.keySet().iterator(); localIterator.hasNext();)
    {
      int id = ((Integer)localIterator.next()).intValue();
      if (((String)this.keywords.get(Integer.valueOf(id))).equals(keyword)) {
        return id;
      }
    }
    return -1;
  }
}
