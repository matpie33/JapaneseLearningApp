//package SearchingTests;
//
//import com.kanji.constants.strings.NumberValues;
//import com.kanji.fileReading.WordsListReadWrite;
//import com.guimaker.list.myList.MyList;
//import com.guimaker.list.myList.RowAsJLabel;
//import com.guimaker.list.myList.SearchCriteria;
//import com.kanji.window.BaseWindow;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.util.Map;
//
//import static org.junit.Assert.assertEquals;
//
//public class SearchWord {
//	private Map <String, Integer> words;
//	private MyList list;
//
//	@Before
//	public void initiateWordsList (){
//		File file = new File ("./tests/Dummy list.txt");
//		WordsListReadWrite r = new WordsListReadWrite();
//		try {
//			words = r.readFile(file);
//
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
//		SearchCriteria options = new SearchCriteria ();
//
//		try {
//			boolean isFound = list.findAndHighlightNextOccurence("Ala", NumberValues.FORWARD_DIRECTION, options);
//			assertEquals("Should find in dummy list: Ala as part", true, isFound);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	public void shouldFindWholeWord(){
//		SearchCriteria options = new SearchCriteria ();
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
//		SearchCriteria options = new SearchCriteria ();
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
