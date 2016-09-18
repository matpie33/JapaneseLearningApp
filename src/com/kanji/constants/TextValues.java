package com.kanji.constants;

public class TextValues {
	
	public static final String appTitle="Program do nauki kanji i s³ów.";
	
	public static final String addWordDialogTitle="Nowe s³owo";	
	// Buttons
	public static final String buttonStartText = "Rozpocznij";
	public static final String buttonSearchText = "ZnajdŸ s³ówko";
	public static final String buttonAddText = "Dodaj s³ówko";
	public static final String buttonOpenText = "Wczytaj listê";
	
	public static final String [] buttonNames = 
		{buttonStartText, buttonSearchText, buttonAddText, buttonOpenText};	
			
	public static final String buttonApproveText = "OK";
	public static final String buttonCancelText = "Anuluj";
	public static final String buttonPreviousText = "Poprzedni";
	public static final String buttonNextText = "Nastêpny";
	
	public static final String wordAddDialogPrompt = "Podaj s³ówko";	
	public static final String wordAddNumberPrompt = "Podaj numerek";
	
	//word search dialog
	
	public static final String wordSearchDialogTitle="ZnajdŸ s³owo";	
	public static final String wordSearchDialogPrompt="Wpisz szukane s³owo";
	public static final String wordSearchDefaultOption = 
			"Szukaj wszystkich pozycji zawieraj¹cych wpisane s³owa";
	public static final String wordSearchOnlyFullWordsOption = 
			"Szukaj pozycji, które zawieraj¹ dane s³owo i byæ mo¿e coœ wiêcej.";
	public static final String wordSearchPerfectMatchOption =
			"Szukaj pozycji zawieraj¹cych dok³adnie to co wpisa³em.";
	
	public static final String wordSearchExceptionWordNotFound = 
			"Nie znaleziono podanego s³owa. Byæ mo¿e zazaczono z³¹ opcjê," +
			"a mo¿e jest b³¹d we wpisanym s³owie.";
	public static final String duplicatedWordException = "Pewne s³owo wystêpuje wielokrotnie "+
			"na liœcie s³ów. Nale¿y poprawiæ.";
	public static final String numberFormatException = "Niepoprawnie wpisana liczba.";
	public static final String idAlreadyDefinedException = "Takie id juz istnieje.";
	public static final String wordAlreadyDefinedException = "Takie slowo juz istnieje.";
	public static final String wordAlreadyHighlightedException = "To slowo jest juz zaznaczone." +
			"Nie znaleziono innych pozycji.";

}
