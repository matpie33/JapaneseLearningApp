package rangesTests;

import com.kanji.range.Range;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RangesTests {

	@Test public void inclusiveRangeShouldBeIncludedInto() {
		Range inclusiveRange = new Range(10, 100);
		Range includedRange = new Range(30, 40);

		assertEquals("(30,40) should be included into (10,100)", true,
				inclusiveRange.includesRange(includedRange));
		assertEquals("(10,30) should be included into (10,100)", true,
				inclusiveRange.includesRange(new Range(10, 30)));
		assertEquals("(70,100) should be included into (10,100)", true,
				inclusiveRange.includesRange(new Range(70, 100)));
	}

	@Test public void rangesThatFollowsShouldBeMerged() {
		Range firstRange = new Range(30, 100);
		Range followingRange = new Range(101, 200);

		assertEquals("(30,100) and (101,200) should be merged to (30,200)", true,
				firstRange.isFollowedBy(followingRange));
	}

}
