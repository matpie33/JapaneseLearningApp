var selection = window.getSelection().toString().toLowerCase();
var meanings = EnglishDictionaryCaller.callDictionaryForEnglishWord(selection);
createTooltipDiv(meanings);



