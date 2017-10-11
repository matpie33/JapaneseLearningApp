//package SearchingTests;
//
//import static org.junit.Assert.*;
//
//import java.io.File;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.kanji.constants.NumberValues;
//import com.kanji.fileReading.CustomFileReader;
//import com.kanji.myList.MyList;
//import com.kanji.myList.RowAsJLabel;
//import com.kanji.myList.SearchOptions;
//import com.kanji.window.BaseWindow;
//
//public class SearchWord {
//	private Map <String, Integer> words;
//	private MyList list;
//
//	@Before
//	public void initiateWordsList (){
//		File file = new File ("./tests/Dummy list.txt");
//		CustomFileReader r = new CustomFileReader();
//		try {
//			words = r.readFile(file);
//			list = new MyList(new BaseWindow(),"Test", new RowAsJLabel());
//			list.setWords(words);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void shouldFindPartOfWord(){
//		SearchOptions options = new SearchOptions ();
//
//		try {
//			boolean isFound = list.findAndHighlightNextOccurence("Ala", NumberValues.FORWARD_DIRECTION, options);
//			assertEquals("Should find in dummy list: Ala as part", true, isFound);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void shouldFindWholeWord(){
//		SearchOptions options = new SearchOptions ();
//		options.enableMatchByWordOnly();
//
//		try {
//			boolean isFound = list.findAndHighlightNextOccurence("Kot", NumberValues.FORWARD_DIRECTION, options);
//			assertEquals ("Should find in dummy list: Kot as word", true, isFound);
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void shouldNotFindCharactersAsWord (){
//		SearchOptions options = new SearchOptions ();
//		options.enableMatchByWordOnly();
//
//		try {
//			boolean isFound = list.findAndHighlightNextOccurence("Ko", NumberValues.FORWARD_DIRECTION, options);
//			assertEquals ("Should not find in dummy list: Ko when searching for word by word match", false, isFound);
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}
//
//}
