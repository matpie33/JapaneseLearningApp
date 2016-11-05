package com.kanji.fileReading;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
	
	private XSSFWorkbook wb;
	private XSSFSheet sheet;
	private XSSFFont font;
	
	public void load(){
		POIFSFileSystem fs;
		try {
			
			wb = new XSSFWorkbook(new File("rtk.xlsx"));
		    sheet = wb.getSheetAt(0);
		    font = sheet.getRow(1).getCell(0).getCellStyle().getFont();
		} 
		catch (IOException | InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getKanjiById (int id){
		return sheet.getRow(id).getCell(0).toString();
	}
	
	public String getFontName(){
		return font.getFontName();
	}

}
