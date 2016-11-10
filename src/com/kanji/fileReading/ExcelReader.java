package com.kanji.fileReading;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	
	
	private Font font;
	private List <String> words;
	
	public void load(){
		;
		words = new ArrayList <String>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream("kanjis.txt"),"Utf-8"));
			String line="";
			while ((line = br.readLine())!=null){
				words.add(line);
			}
			String first = words.get(0).replace("\uFEFF", "");
			words.remove(0);
			words.add(0,first);
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(words);
		font = new Font("MS PMincho", Font.PLAIN, 60);
	}
	
	private List <String> getColumn (XSSFWorkbook workbook){
		List <String> s = new LinkedList <String> ();
		int i =0;
		XSSFCell c = workbook.getSheetAt(0).getRow(i).getCell(1);
		while (c!=null){
			s.add(c.toString());
			i++;
			c = workbook.getSheetAt(0).getRow(i).getCell(1);			
		}
			
		return s;
		
	}
	
	public String getKanjiById (int id){
		return words.get(id-1);
	}
	
	public String getFontName(){
		return font.getFontName();
	}

}
