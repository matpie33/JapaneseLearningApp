
var selection = window.getSelection().toString().toLowerCase();
var heisigKanjiId = getKanjiKeywords().indexOf(selection) + 1;
if (heisigKanjiId >= 0){
    var polishKeyword = ApplicationController.getKanjiKeywordById(heisigKanjiId);
    createTooltipDiv(polishKeyword);
}




