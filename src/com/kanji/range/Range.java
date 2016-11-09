package com.kanji.range;

import com.kanji.constants.ExceptionsMessages;

public class Range {
	int rangeStart;
	int rangeEnd;
	
	public Range (int rangeStart, int rangeEnd) throws IllegalArgumentException{
		if (rangeStart>=rangeEnd)
			throw new IllegalArgumentException(ExceptionsMessages.rangeToValueLessThanRangeFromValue);
		this.rangeStart=rangeStart;
		this.rangeEnd=rangeEnd;
	}
	
	public boolean isValueInsideRange(int value){
		return value >= rangeStart && value <= rangeEnd;
	}
	
	public boolean includesRange(Range range){
		return range.rangeStart>=rangeStart && range.rangeEnd <= rangeEnd;
	}
	
	public boolean isFollowedBy(Range range){
		return range.rangeStart==rangeEnd+1;
	}	
	
	public int getRangeStart(){
		return rangeStart;
	}
	
	public int getRangeEnd(){
		return rangeEnd;
	}
	

}
