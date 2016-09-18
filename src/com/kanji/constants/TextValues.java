package com.kanji.constants;

public class TextValues {
	
	public static final String appTitle="Program do nauki kanji i s��w.";
	
	public static final String addWordDialogTitle="Nowe s�owo";	
	// Buttons
	public static final String buttonStartText = "Rozpocznij";
	public static final String buttonSearchText = "Znajd� s��wko";
	public static final String buttonAddText = "Dodaj s��wko";
	public static final String buttonOpenText = "Wczytaj list�";
	
	public static final String [] buttonNames = 
		{buttonStartText, buttonSearchText, buttonAddText, buttonOpenText};	
			
	public static final String buttonApproveText = "OK";
	public static final String buttonCancelText = "Anuluj";
	public static final String buttonPreviousText = "Poprzedni";
	public static final String buttonNextText = "Nast�pny";
	
	public static final String wordAddDialogPrompt = "Podaj s��wko";	
	public static final String wordAddNumberPrompt = "Podaj numerek";
	
	//word search dialog
	
	public static final String wordSearchDialogTitle="Znajd� s�owo";	
	public static final String wordSearchDialogPrompt="Wpisz szukane s�owo";
	public static final String wordSearchDefaultOption = 
			"Szukaj wszystkich pozycji zawieraj�cych wpisane s�owa";
	public static final String wordSearchOnlyFullWordsOption = 
			"Szukaj pozycji, kt�re zawieraj� dane s�owo i by� mo�e co� wi�cej.";
	public static final String wordSearchPerfectMatchOption =
			"Szukaj pozycji zawieraj�cych dok�adnie to co wpisa�em.";
	
	public static final String wordSearchExceptionWordNotFound = 
			"Nie znaleziono podanego s�owa. By� mo�e zazaczono z�� opcj�," +
			"a mo�e jest b��d we wpisanym s�owie.";
	public static final String duplicatedWordException = "Pewne s�owo wyst�puje wielokrotnie "+
			"na li�cie s��w. Nale�y poprawi�.";
	public static final String numberFormatException = "Niepoprawnie wpisana liczba.";
	public static final String idAlreadyDefinedException = "Takie id juz istnieje.";
	public static final String wordAlreadyDefinedException = "Takie slowo juz istnieje.";
	public static final String wordAlreadyHighlightedException = "To slowo jest juz zaznaczone." +
			"Nie znaleziono innych pozycji.";

}
