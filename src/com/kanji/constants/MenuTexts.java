package com.kanji.constants;

public class TextValues {
	
	public static final String appTitle="Program do nauki kanji i słów.";
	
	public static final String insertWordDialogTitle="Nowe słowo";	
	public static final String messageDialogTitle="Informacja";	
	public static final String learnStartDialogTitle="Rozpoczecie nauki";	
	public static final String wordSearchDialogTitle="Znajdź słowo";
	
	public static final String wordsListTitle="Slowka";	
	public static final String repeatedWordsListTitle="Powtorzone";
	
	// Buttons
	public static final String buttonStartText = "Rozpocznij";
	public static final String buttonSearchText = "Znajdź słówko";
	public static final String buttonAddText = "Dodaj słówko";
	public static final String buttonOpenText = "Wczytaj listę";
  public static final String buttonSaveText = "Zapisz";
  public static final String buttonSaveListText = "Zapisz listę";
	
	public static final String [] buttonNames = 
		{buttonStartText, buttonSearchText, buttonAddText, buttonOpenText, buttonSaveText,
				buttonSaveListText};	
			
	public static final String buttonApproveText = "OK";
	public static final String buttonCancelText = "Anuluj";
	public static final String buttonPreviousText = "Poprzedni";
	public static final String buttonNextText = "Następny";
	public static final String buttonAddRowText = "Dodaj wiersz";
	public static final String buttonRemoveRowText = "Usuń wiersz";
	
	public static final String wordAddDialogPrompt = "Podaj słówko";	
	public static final String wordAddNumberPrompt = "Podaj numerek";
	public static final String problematicKanji = "Problematyczne";
	
	//word search dialog	
	public static final String wordSearchDialogPrompt="Wpisz szukane słowo";
	public static final String wordSearchDefaultOption = 
			"Szukaj wszystkich pozycji zawierających wpisane słowa";
	public static final String wordSearchOnlyFullWordsOption = 
			"Szukaj pozycji, które zawierają dane słowo i być może coś więcej.";
	public static final String wordSearchPerfectMatchOption =
			"Szukaj pozycji zawierających dokładnie to co wpisałem.";
	
	public static final String learnStartPrompt = "Proszę wpisać numery słówek, które chcesz powtórzyć. " +
			"Zewnetrzne numery slowek rowniez beda uwzglednione.";
	
	public static final String wordNotFoundMessage = 
			"Nie znaleziono podanego słowa. Być może zazaczono złą opcję," +
			"a może jest błąd we wpisanym słowie.";
			
	public static final String duplicatedWordException = "Pewne słowo występuje wielokrotnie "+
			"na liście słów. Należy poprawić.";
	public static final String numberFormatException = "Niepoprawnie wpisana liczba.";
	public static final String idAlreadyDefinedException = "Takie id juz istnieje.";
	public static final String wordAlreadyDefinedException = "Takie slowo juz istnieje.";
	public static final String wordAlreadyHighlightedException = "To slowo jest juz zaznaczone." +
			"Nie znaleziono innych pozycji.";
	public static final String rangeToValueLessThanRangeFromValue = "Za mała liczba";
	public static final String rangeValueTooHigh = "Wpisana liczba jest za duża";
	public static final String valueIsNotNumber = "Proszę wpisać liczbę";
	public static final String noInputSupplied = "Prosze cokolwiek wpisac!";
	public static final String sumRangePrompt = "Łączna liczba kanji: ";
	public static final String learningFinished = "Koniec słów do powtórzenia.";
	public static final String excelNotLoaded = "Musisz zaczekac. Klasa wczytujaca kanji" +
			" z excela nie zaladowala sie jeszcze.";

	public static final String problematicKanjiPrompt = "Problematycznych";


}
