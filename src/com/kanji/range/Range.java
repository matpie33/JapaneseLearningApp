package com.kanji.range;

import com.kanji.constants.TextValues;

public class Range {
	int rangeStart;
	int rangeEnd;
	
	public Range (int rangeStart, int rangeEnd) throws IllegalArgumentException{
		if (rangeStart>=rangeEnd)
			throw new IllegalArgumentException(TextValues.rangeToValueLessThanRangeFromValue);
		this.rangeStart=rangeStart;
		this.rangeEnd=rangeEnd;
	}
	
	public boolean isValueInsideRange(int value){
		return value >= rangeStart && value <= rangeEnd;
	}
	
	public boolean includesRange(Range range){
		return range.rangeStart>rangeStart && range.rangeEnd < rangeEnd;
	}

}
