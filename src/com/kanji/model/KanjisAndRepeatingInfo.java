package com.kanji.model;

import com.kanji.Row.KanjiInformation;
import com.kanji.Row.RepeatingInformation;

import java.util.List;

public class KanjisAndRepeatingInfo {

	private List <KanjiInformation> kanjiInformations;
	private List <RepeatingInformation> repeatingInformations;

	public KanjisAndRepeatingInfo (List <KanjiInformation> kanjiInformations, List <RepeatingInformation> repeatingInformations){
		this.kanjiInformations = kanjiInformations;
		this.repeatingInformations = repeatingInformations;
	}

	public List <KanjiInformation> getKanjiInformations (){
		return kanjiInformations;
	}

	public List <RepeatingInformation> getRepeatingInformations (){
		return repeatingInformations;
	}

}
