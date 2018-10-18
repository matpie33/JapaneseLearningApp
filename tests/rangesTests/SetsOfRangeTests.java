//package rangesTests;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
// TODO tests are not compiling anymore
//import com.guimaker.utilities.Range;
//import com.guimaker.utilities.SetOfRanges;
//
//public class SetsOfRangeTests {
//
//	@Test
//	public void inclusiveRangeShouldBeMerged(){
//		Range includedRange = new Range(20,30);
//		Range includingRange = new Range(10,50);
//
//		SetOfRanges set = new SetOfRanges();
//		set.addRange(includingRange);
//		set.addRange(includedRange);
//
//		assertEquals("(20,30) should not be added to set, if it has already (10,50)",
//				new SetOfRanges(includingRange).getRangesAsString(), set.getRangesAsString());
//	}
//
//	@Test
//	public void followingRangesShouldBeMerged(){
//		Range firstRange = new Range(10,20);
//		Range nextRange = new Range(21,30);
//
//		SetOfRanges set = new SetOfRanges();
//		set.addRange(firstRange);
//		set.addRange(nextRange);
//
//		SetOfRanges desiredSet = new SetOfRanges(new Range(10,30));
//		assertEquals("(10,20) and (21,30) should be merged to (10,30)", desiredSet.getRangesAsString(), set.getRangesAsString());
//	}
//
//}
