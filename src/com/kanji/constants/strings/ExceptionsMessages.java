package com.kanji.constants.strings;

public class ExceptionsMessages {

	public static final String NO_SELECTED_WORD_TO_SEARCH_IN_DICTIONARY =
			"Nie zaznaczono słowa, które można by wyszukać w słowniku "
					+ "japońsko - angielskim.";
	public static final String DUPLICATED_WORD_EXCEPTION =
			"Pewne słowo występuje wielokrotnie "
					+ "na liście słów. Należy poprawić.";
	public static final String DUPLICATED_ID_EXCEPTION = "Istnieje już słowo z id: %s";
	public static final String NUMBER_FORMAT_EXCEPTION = "Niepoprawnie wpisana liczba.";
	public static final String ID_ALREADY_DEFINED_EXCEPTION =
			"Id o wartości %d juz istnieje" + "w wierszu %d.";
	public static final String DUPLICATED_KEYWORD_EXCEPTION = "Kanji o słowie kluczowym \"%s\" już istnieje.";

	public static final String ILLEGAL_LIST_FILE_FORMAT = "Niepoprawny format pliku z listą.";
	public static final String WORDS_IN_KANJI_LIST_NOT_GIVEN =
			"Nie podano listy słów " + "zapisanych za pomocą kanji.";
	public static final String WORDS_IN_KANA_LIST_NOT_GIVEN =
			"Nie podano listy słów "
					+ "zapisanych za pomocą hiragany/katakany.";
	public static final String WORDS_MEANING_LIST_NOT_GIVEN =
			"Nie podano listy z tłumaczeniami " + "japońskich słów na polski";
	public static final String INCORRECT_JAPANESE_WORDS_LISTS_SIZES =
			"Długości list słów zapisanych kaną, "
					+ "kanji oraz przetłumaczonych na polski nie są jednakowe - a powinny.";
	public static final String JAPANESE_WORD_WRITINGS_ALREADY_DEFINED = "Słowo z takimi zapisami: %s już istnieje.";
	public static final String DUPLICATED_WORD_MEANING = "Słowo o znaczeniu \"%s\" już istnieje.";
	public static final String KANA_WRITING_INCORRECT = "Niepoprawny zapis kaną: %s. Powinien zawierać tylko hiraganę lub katakanę.";
	public static final String KANJI_WRITING_INCORRECT = "Niepoprawny zapis kanji: %s. Powinien zawierać kanji i hiraganę lub katakanę.";
	public static final String KANA_OR_KANJI_WRITING_INCORRECT =
			"Niepoprawny zapis kany lub kanji. Powinien zawierać hiraganę lub katakanę "
					+ "lub kanji.";
	public static final String DUPLICATED_KANJI_WRITING_WITHIN_ROW = "Kanji %s już wystepuje w tym wierszu.";
	public static final String EXCEPTION_WHILE_FILLING_WORDS_LISTS =
			"Wystąpił błąd przy wypełnianiu list: %s.";
	public static final String EXCEPTION_WHILE_LOADING_KANJI_PROJECT =
			"Wystąpił błąd przy wczytywaniu projektu z pliku. Ale głowa do "
					+ "góry! Zapisałem przyczynę błędu w pliku %s, a "
					+ "aplikację zamykam aby się nie pogorszyło :)";
	public static final String DUPLICATED_WRITINGS_IN_WORD = "Wpisano identyczne zapisy słowa w dwóch wierszach.";
	public static final String KANA_INPUT_EMPTY = "Pole \"kana\" musi być wypełnione.";
	public static final String ERROR_IN_ROW = "Błąd w wierszu %d: %s";
}
