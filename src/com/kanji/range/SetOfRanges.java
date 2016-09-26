package com.kanji.range;

import java.util.ArrayList;
import java.util.List;

public class SetOfRanges {
	
	private List <Range> ranges;
	
	public SetOfRanges (){
		ranges = new ArrayList <Range> ();
	}	
	
	public boolean addRange (Range newRange){
		boolean isModified = false;
		for (int i=0; i<ranges.size(); i++){
			Range rangeFromSet = ranges.get(i);
			if (rangeFromSet.includesRange(newRange)){
				return false;
			}
				
			if (rangeFromSet.isValueInsideRange(newRange.rangeStart)){
				ranges.remove(rangeFromSet);
				i--;
				newRange = new Range(rangeFromSet.rangeStart, newRange.rangeEnd);
				isModified=true;
				
			}
			if (rangeFromSet.isValueInsideRange(newRange.rangeEnd)){
				ranges.remove(rangeFromSet);
				i--;
				newRange = new Range(newRange.rangeStart, rangeFromSet.rangeEnd);
				isModified=true;
			}
			if (newRange.includesRange(rangeFromSet)){
				ranges.remove(rangeFromSet);
				i--;
				isModified=true;
			}
		}
		ranges.add(newRange);
		return isModified;
	}
	
	public String getRanges(){
		String msg="";
		for (Range r: ranges){
			msg+="od: "+r.rangeStart+" do "+r.rangeEnd;
		}
		return msg;
	}
	

	public int sumRangeInclusive (){
		int sum = 0;
		for (Range range: ranges){
			sum += range.rangeEnd-range.rangeStart+1;			
		}
		return sum;
	}

}
